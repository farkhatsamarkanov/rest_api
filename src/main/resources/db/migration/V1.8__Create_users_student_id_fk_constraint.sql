ALTER TABLE users
ADD CONSTRAINT fk_student_id
FOREIGN KEY (student_id)
REFERENCES students (student_id)
ON DELETE CASCADE;