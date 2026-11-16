-- Konfigurasi baca untuk daftar tempat yang ditampilkan di Explore dan Map.
-- Koordinat dan card_tags dimiliki public.places; jarak pengguna dihitung lokal di Android.

begin;

alter table public.places
    add column if not exists latitude double precision,
    add column if not exists longitude double precision,
    add column if not exists card_tags text[] not null default '{}';

grant select on table public.places to anon, authenticated;

alter table public.places enable row level security;

do $$
begin
    if not exists (
        select 1
        from pg_policies
        where schemaname = 'public'
          and tablename = 'places'
          and policyname = 'places_map_select'
    ) then
        create policy places_map_select
            on public.places
            for select
            to anon, authenticated
            using (true);
    end if;
end
$$;

commit;
