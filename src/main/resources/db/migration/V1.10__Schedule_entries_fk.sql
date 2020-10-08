ALTER TABLE schedule_of_classes
ADD CONSTRAINT fk_lecturer_id
FOREIGN KEY (lecturer_id)
REFERENCES lecturers (lecturer_id)
ON DELETE SET NULL;

ALTER TABLE schedule_of_classes
ADD CONSTRAINT fk_student_id
FOREIGN KEY (student_id)
REFERENCES students (student_id)
ON DELETE CASCADE;

ALTER TABLE schedule_of_classes
ADD CONSTRAINT fk_course_id
FOREIGN KEY (course_id)
REFERENCES courses (course_id)
ON DELETE CASCADE;

ALTER TABLE schedule_of_classes
ADD CONSTRAINT fk_semester_id
FOREIGN KEY (semester_id)
REFERENCES semesters (semester_id)
ON DELETE CASCADE;

