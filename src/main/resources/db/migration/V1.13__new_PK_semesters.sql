ALTER TABLE schedule_of_classes DROP CONSTRAINT fk_semester_id;

ALTER TABLE public.semesters DROP CONSTRAINT semesters_pkey;

ALTER TABLE public.semesters
    ADD CONSTRAINT "semesters_PK" PRIMARY KEY (entry_id);

ALTER TABLE public.semesters
    ADD CONSTRAINT semesters_unique_semester_id UNIQUE (semester_id);

ALTER TABLE schedule_of_classes
ADD CONSTRAINT fk_semester_id
FOREIGN KEY (semester_id)
REFERENCES semesters (semester_id);
