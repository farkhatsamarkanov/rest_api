ALTER TABLE lecturers DROP CONSTRAINT fk_academic_rank;

ALTER TABLE public.academic_ranks DROP CONSTRAINT academic_ranks_pkey;

ALTER TABLE public.academic_ranks
    ADD CONSTRAINT "academic_ranks_PK" PRIMARY KEY (entry_id);

ALTER TABLE public.academic_ranks
    ADD CONSTRAINT academic_ranks_unique_numeric_rank UNIQUE (numeric_rank);

ALTER TABLE lecturers
ADD CONSTRAINT fk_academic_rank
FOREIGN KEY (numeric_academic_rank)
REFERENCES academic_ranks (numeric_rank);
