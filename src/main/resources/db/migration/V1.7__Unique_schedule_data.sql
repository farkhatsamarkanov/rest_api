ALTER TABLE schedule_of_classes
    ADD CONSTRAINT unique_schedule_data UNIQUE (student_id, lecturer_id, course_id, "time", location, semester_id);