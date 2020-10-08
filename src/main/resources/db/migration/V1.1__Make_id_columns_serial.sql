DROP VIEW task_1;

ALTER TABLE courses DROP COLUMN course_id;

ALTER TABLE courses
    ADD COLUMN course_id serial NOT NULL;
ALTER TABLE public.courses
    ADD PRIMARY KEY (course_id);