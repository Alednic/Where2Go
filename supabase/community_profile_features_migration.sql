-- Where2Go: profil permissions, avatar, favorites, check-in XP/count, and reviews.
-- Safe to rerun. Run in Supabase SQL Editor.
begin;

alter table public.profiles
  add column if not exists points integer not null default 0,
  add column if not exists avatar_url text;
alter table public.places
  add column if not exists check_in_count integer not null default 0;
alter table public.place_submissions
  add column if not exists card_tags text[] not null default '{}';
alter table public.check_ins
  add column if not exists points_earned integer not null default 10,
  add column if not exists place_rating smallint;
do $$ begin
  if not exists (
    select 1 from pg_constraint
    where conname = 'check_ins_points_earned_fixed'
      and conrelid = 'public.check_ins'::regclass
  ) then
    alter table public.check_ins
      add constraint check_ins_points_earned_fixed check (points_earned = 10);
  end if;
  if not exists (
    select 1 from pg_constraint
    where conname = 'check_ins_place_rating_range'
      and conrelid = 'public.check_ins'::regclass
  ) then
    alter table public.check_ins
      add constraint check_ins_place_rating_range check (place_rating is null or place_rating between 1 and 5);
  end if;
end $$;

-- Repair profile rows for users created before the profile trigger existed.
insert into public.profiles (id, display_name, email)
select u.id,
  coalesce(nullif(u.raw_user_meta_data ->> 'display_name', ''), split_part(u.email, '@', 1), 'Pengguna Where2Go'),
  coalesce(u.email, '')
from auth.users u
on conflict (id) do nothing;

-- Existing check-ins are included in counts and points.
update public.places p
set check_in_count = coalesce((select count(*)::integer from public.check_ins c where c.place_id = p.id), 0);
update public.places p
set rating = (select round(avg(c.place_rating)::numeric, 2)
              from public.check_ins c where c.place_id = p.id and c.place_rating is not null)
where exists (select 1 from public.check_ins c where c.place_id = p.id and c.place_rating is not null);
update public.profiles p
set points = coalesce((select sum(c.points_earned)::integer from public.check_ins c where c.user_id = p.id), 0);

alter table public.profiles enable row level security;
grant usage on schema public to authenticated;
grant select, insert on table public.profiles to authenticated;
revoke update on table public.profiles from authenticated;
grant update (display_name, username, bio, avatar_url, updated_at) on public.profiles to authenticated;
drop policy if exists profiles_read_self on public.profiles;
create policy profiles_read_self on public.profiles
  for select to authenticated using ((select auth.uid()) = id);
drop policy if exists profiles_insert_self on public.profiles;
create policy profiles_insert_self on public.profiles
  for insert to authenticated with check ((select auth.uid()) = id and role = 'user');
drop policy if exists profiles_update_self on public.profiles;
create policy profiles_update_self on public.profiles
  for update to authenticated using ((select auth.uid()) = id)
  with check ((select auth.uid()) = id);

grant select, insert, update on table public.user_preferences to authenticated;
drop policy if exists preferences_own on public.user_preferences;
create policy preferences_own on public.user_preferences
  for all to authenticated using ((select auth.uid()) = user_id)
  with check ((select auth.uid()) = user_id);

alter table public.places enable row level security;
grant select on table public.places to anon, authenticated;
drop policy if exists places_read_published on public.places;
create policy places_read_published on public.places
  for select to anon, authenticated using (true);

create or replace function public.review_place_submission(submission_id bigint, approved boolean)
returns void language plpgsql security definer set search_path = '' as $$
declare s public.place_submissions%rowtype;
begin
  if not exists (select 1 from public.profiles p where p.id = auth.uid() and p.role = 'admin') then
    raise exception 'Akses hanya untuk admin.' using errcode = '42501';
  end if;
  select * into s from public.place_submissions
  where id = submission_id and status = 'PENDING' for update;
  if not found then raise exception 'Pendaftaran tidak ditemukan atau sudah ditinjau.'; end if;
  update public.place_submissions
  set status = case when approved then 'APPROVED' else 'REJECTED' end, updated_at = now()
  where id = submission_id;
  if approved then
    insert into public.places (
      name, address, latitude, longitude, description, opening_hours,
      image_url, facilities, card_tags, owner_id, category, phone, vibe, price_level, rating, min_order
    ) values (
      s.name, s.address, s.latitude, s.longitude, s.description, s.opening_hours,
      s.image_url, s.facilities, array[s.category] || coalesce(s.card_tags, '{}'::text[]),
      s.owner_id, s.category, s.phone, 'QUIET', 'MEDIUM', 0, s.min_order
    );
  end if;
end;
$$;
grant execute on function public.review_place_submission(bigint, boolean) to authenticated;
revoke all on function public.review_place_submission(bigint, boolean) from public, anon;

-- Backfill place rows for approved requests accepted before the publishing RPC was installed.
insert into public.places (
  name, address, latitude, longitude, description, opening_hours,
  image_url, facilities, card_tags, owner_id, category, phone, vibe, price_level, rating, min_order
)
select s.name, s.address, s.latitude, s.longitude, s.description, s.opening_hours,
  s.image_url, s.facilities, array[s.category] || coalesce(s.card_tags, '{}'::text[]),
  s.owner_id, s.category, s.phone, 'QUIET', 'MEDIUM', 0, s.min_order
from public.place_submissions s
where s.status = 'APPROVED'
  and not exists (select 1 from public.places p where p.owner_id = s.owner_id and p.name = s.name);

create table if not exists public.favorite_places (
  user_id uuid not null references auth.users(id) on delete cascade,
  place_id integer not null references public.places(id) on delete cascade,
  created_at timestamptz not null default now(),
  primary key (user_id, place_id)
);
alter table public.favorite_places enable row level security;
grant select, insert, delete on table public.favorite_places to authenticated;
drop policy if exists favorites_read_own on public.favorite_places;
create policy favorites_read_own on public.favorite_places
  for select to authenticated using ((select auth.uid()) = user_id);
drop policy if exists favorites_insert_own on public.favorite_places;
create policy favorites_insert_own on public.favorite_places
  for insert to authenticated with check ((select auth.uid()) = user_id);
drop policy if exists favorites_delete_own on public.favorite_places;
create policy favorites_delete_own on public.favorite_places
  for delete to authenticated using ((select auth.uid()) = user_id);

create or replace function public.award_checkin_points_and_count()
returns trigger language plpgsql security definer set search_path = '' as $$
begin
  update public.profiles
  set points = coalesce(points, 0) + coalesce(new.points_earned, 10), updated_at = now()
  where id = new.user_id;
  update public.places
  set check_in_count = coalesce(check_in_count, 0) + 1,
      rating = case when new.place_rating is null then rating else (
        select round(avg(c.place_rating)::numeric, 2)
        from public.check_ins c
        where c.place_id = new.place_id and c.place_rating is not null
      ) end
  where id = new.place_id;
  return new;
end;
$$;
drop trigger if exists checkin_award_points_and_count on public.check_ins;
create trigger checkin_award_points_and_count
  after insert on public.check_ins
  for each row execute function public.award_checkin_points_and_count();

create or replace function public.place_reviews(requested_place_id integer)
returns table (review_id bigint, author text, vibe text, comment text, created_at timestamptz)
language sql stable security definer set search_path = '' as $$
  select c.id,
    coalesce(nullif(pr.username, ''), nullif(pr.display_name, ''), 'Pengguna Where2Go')::text,
    c.vibe::text, c.comment::text, c.created_at
  from public.check_ins c
  left join public.profiles pr on pr.id = c.user_id
  where c.place_id = requested_place_id and nullif(btrim(c.comment), '') is not null
  order by c.created_at desc
  limit 100;
$$;
grant execute on function public.place_reviews(integer) to authenticated;
revoke all on function public.place_reviews(integer) from public, anon;

insert into storage.buckets (id, name, public, file_size_limit, allowed_mime_types)
values ('profile-photos', 'profile-photos', true, 5242880, array['image/jpeg', 'image/png'])
on conflict (id) do update set public = true, file_size_limit = 5242880,
  allowed_mime_types = array['image/jpeg', 'image/png'];
drop policy if exists profile_photo_public_read on storage.objects;
create policy profile_photo_public_read on storage.objects
  for select to anon, authenticated using (bucket_id = 'profile-photos');
drop policy if exists profile_photo_owner_insert on storage.objects;
create policy profile_photo_owner_insert on storage.objects
  for insert to authenticated with check (
    bucket_id = 'profile-photos' and (storage.foldername(name))[1] = (select auth.uid())::text
  );

notify pgrst, 'reload schema';
commit;
