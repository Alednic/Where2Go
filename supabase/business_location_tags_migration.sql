-- Jalankan satu kali di Supabase SQL Editor untuk mendukung tag usaha.
-- Aman dijalankan ulang: hanya menambah kolom bila belum ada dan mengganti RPC review.
begin;

alter table public.place_submissions
  add column if not exists card_tags text[] not null default '{}';

-- App Explore and Map read only published rows from public.places.
alter table public.places enable row level security;
grant select on table public.places to anon, authenticated;
drop policy if exists places_read_published on public.places;
create policy places_read_published on public.places
  for select to anon, authenticated using (true);

create or replace function public.review_place_submission(submission_id bigint, approved boolean)
returns void
language plpgsql
security definer
set search_path = ''
as $$
declare
  s public.place_submissions%rowtype;
begin
  if not exists (
    select 1 from public.profiles p
    where p.id = auth.uid() and p.role = 'admin'
  ) then
    raise exception 'Akses hanya untuk admin.' using errcode = '42501';
  end if;

  select * into s
  from public.place_submissions
  where id = submission_id and status = 'PENDING'
  for update;

  if not found then
    raise exception 'Pendaftaran tidak ditemukan atau sudah ditinjau.';
  end if;

  update public.place_submissions
  set status = case when approved then 'APPROVED' else 'REJECTED' end,
      updated_at = now()
  where id = submission_id;

  if approved then
    insert into public.places (
      name, address, latitude, longitude, description, opening_hours,
      image_url, facilities, card_tags, owner_id, category, phone,
      vibe, price_level, rating, min_order
    ) values (
      s.name, s.address, s.latitude, s.longitude, s.description, s.opening_hours,
      s.image_url, s.facilities, array[s.category] || coalesce(s.card_tags, '{}'::text[]),
      s.owner_id, s.category, s.phone, 'QUIET', 'MEDIUM', 0, s.min_order
    );
  end if;
end;
$$;

-- Publish any previously approved submission that has no matching place yet.
insert into public.places (
  name, address, latitude, longitude, description, opening_hours,
  image_url, facilities, card_tags, owner_id, category, phone,
  vibe, price_level, rating, min_order
)
select
  s.name, s.address, s.latitude, s.longitude, s.description, s.opening_hours,
  s.image_url, s.facilities, array[s.category] || coalesce(s.card_tags, '{}'::text[]),
  s.owner_id, s.category, s.phone, 'QUIET', 'MEDIUM', 0, s.min_order
from public.place_submissions s
where s.status = 'APPROVED'
  and not exists (
    select 1 from public.places p
    where p.owner_id = s.owner_id and p.name = s.name
  );

grant execute on function public.review_place_submission(bigint, boolean) to authenticated;
revoke all on function public.review_place_submission(bigint, boolean) from public, anon;
notify pgrst, 'reload schema';
commit;
