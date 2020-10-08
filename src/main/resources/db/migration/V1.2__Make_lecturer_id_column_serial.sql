DROP VIEW course_maxlecturerrank_lecturer_rankname;

DROP VIEW course_maxlecturerrank_rankname;

DROP VIEW course_maxlecturerrank;

DROP VIEW course_lecturer_rank;

ALTER TABLE lecturers DROP COLUMN lecturer_id;

ALTER TABLE lecturers
    ADD COLUMN lecturer_id serial NOT NULL;
ALTER TABLE lecturers
    ADD PRIMARY KEY (lecturer_id);