INSERT INTO academic_ranks (numeric_rank, name) VALUES (1, 'Professor');
INSERT INTO courses (course_title, course_description) VALUES ('Computer Science 101', 'Prerequisites: Mathematics, Calculus');
INSERT INTO courses (course_title, course_description) VALUES ('Computer Science 102', 'Prerequisites: Computer Science 101');
INSERT INTO courses (course_title, course_description) VALUES ('Computer Science 103', 'Prerequisites: Computer Science 102');
INSERT INTO courses (course_title, course_description) VALUES ('Computer Science 104', 'Prerequisites: Mathematics');
INSERT INTO courses (course_title, course_description) VALUES ('Computer Science 105', 'Prerequisites: Calculus');
INSERT INTO courses (course_title, course_description) VALUES ('Computer Science 106', 'Prerequisites: ');
INSERT INTO lecturers (name, date_of_birth, numeric_academic_rank) VALUES ('John Doe', 461885632, 1);
INSERT INTO students(name, date_of_birth) VALUES ('Student one', 463226420);
INSERT INTO users(login, password, is_active, student_id) VALUES ('user_1', 'pa$$word', false, 1);
INSERT INTO semesters(semester_id, name, year, start_time, end_time) VALUES ('FAL2020', 'Fall semester of 2020', 2020, 1599300020, 1608890420);
INSERT INTO semesters(semester_id, name, year, start_time, end_time) VALUES ('SUM2020', 'Summer semester of 2020', 2020, 456132789, 123456789);
INSERT INTO schedule_of_classes(student_id, lecturer_id, course_id, time, location, semester_id) VALUES (1, 1, 1, 1609855628000, 'Room A1', 'FAL2020');
INSERT INTO schedule_of_classes(student_id, lecturer_id, course_id, time, location, semester_id) VALUES (1, 1, 2, 1609855628000, 'Room A1', 'FAL2020');
INSERT INTO schedule_of_classes(student_id, lecturer_id, course_id, time, location, semester_id) VALUES (1, 1, 3, 1609855628000, 'Room A1', 'FAL2020');
INSERT INTO schedule_of_classes(student_id, lecturer_id, course_id, time, location, semester_id) VALUES (1, 1, 4, 1609855628000, 'Room A1', 'FAL2020');
INSERT INTO schedule_of_classes(student_id, lecturer_id, course_id, time, location, semester_id) VALUES (1, 1, 5, 1609855628000, 'Room A1', 'FAL2020');
