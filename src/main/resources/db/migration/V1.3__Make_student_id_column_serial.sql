ALTER TABLE students DROP COLUMN student_id;

ALTER TABLE students
    ADD COLUMN student_id serial NOT NULL;
ALTER TABLE students
    ADD PRIMARY KEY (student_id);