ALTER TABLE public.users DROP CONSTRAINT users_pkey;

ALTER TABLE public.users
    ADD CONSTRAINT "users_PK" PRIMARY KEY (entry_id);

ALTER TABLE public.users
    ADD CONSTRAINT users_unique_login UNIQUE (login);
