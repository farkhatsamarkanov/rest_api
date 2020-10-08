ALTER TABLE public.academic_ranks
    ADD COLUMN entry_id serial;

ALTER TABLE public.semesters
    ADD COLUMN entry_id serial;

ALTER TABLE public.users
    ADD COLUMN entry_id serial