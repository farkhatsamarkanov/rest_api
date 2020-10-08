ALTER TABLE lecturers
ADD CONSTRAINT fk_academic_rank
FOREIGN KEY (numeric_academic_rank)
REFERENCES academic_ranks (numeric_rank)
ON DELETE SET NULL
ON UPDATE CASCADE;