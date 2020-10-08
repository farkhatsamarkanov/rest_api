ALTER TABLE courses
ADD CONSTRAINT unique_course_title UNIQUE (course_title);