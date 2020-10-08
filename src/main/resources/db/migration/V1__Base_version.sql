--
-- PostgreSQL database dump
--

-- Dumped from database version 10.13
-- Dumped by pg_dump version 10.13

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


--
-- Name: pgstattuple; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS pgstattuple WITH SCHEMA public;


--
-- Name: EXTENSION pgstattuple; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION pgstattuple IS 'show tuple-level statistics';


--
-- Name: get_max_semester_for_course(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_max_semester_for_course(input_course_id integer) RETURNS text
    LANGUAGE plpgsql
    AS $$
declare semester_id_to_return text;

BEGIN
select array_to_string(array_agg(second_ss.semester_id), '') into semester_id_to_return from
(select ss.semester_id, MAX(ss.count) as max_times_in_single_semester
FROM
(SELECT schedule_of_classes.semester_id, count(schedule_of_classes.semester_id) as "count"
FROM schedule_of_classes
WHERE course_id = input_course_id
group by semester_id
order by count desc) as ss
group by semester_id
limit 1) as second_ss;

return semester_id_to_return;
end;
$$;


ALTER FUNCTION public.get_max_semester_for_course(input_course_id integer) OWNER TO postgres;

--
-- Name: get_most_picked_semester_for_specific_course(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_most_picked_semester_for_specific_course(input_course integer) RETURNS TABLE(course_id integer, semester_id character, max_times_in_single_semester bigint)
    LANGUAGE plpgsql
    AS $$BEGIN
	RETURN QUERY
	select ss.course_id_1, ss.semester_id_1, MAX(ss.count) as max_times_in_single_semester
	FROM
	(SELECT schedule_of_classes.course_id as course_id_1, schedule_of_classes.semester_id as semester_id_1, count(schedule_of_classes.semester_id) as "count"
	FROM schedule_of_classes
	WHERE schedule_of_classes.course_id = input_course
	group by semester_id_1, course_id_1
	order by count desc) as ss
	group by semester_id_1, course_id_1
	limit 1;
END
$$;


ALTER FUNCTION public.get_most_picked_semester_for_specific_course(input_course integer) OWNER TO postgres;

--
-- Name: get_most_picked_semesters(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.get_most_picked_semesters() RETURNS TABLE(course_id integer, semester_id character, max_times_in_single_semester bigint)
    LANGUAGE plpgsql
    AS $$DECLARE
   elem integer;
BEGIN
   FOR elem IN
      SELECT courses.course_id FROM courses
    
   LOOP
      RETURN QUERY SELECT * from get_most_picked_semester_for_specific_course(elem);
   END LOOP;
END
$$;


ALTER FUNCTION public.get_most_picked_semesters() OWNER TO postgres;

--
-- Name: list_courses_for_lecturer_and_semester(character varying, character varying); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.list_courses_for_lecturer_and_semester(lecturer_name character varying, input_semester_id character varying) RETURNS text
    LANGUAGE sql
    AS $$

	SELECT array_to_string(array_agg(course_id), ', ')
	FROM schedule_of_classes
	LEFT JOIN lecturers ON schedule_of_classes.lecturer_id = lecturers.lecturer_id
	WHERE name = lecturer_name AND semester_id = input_semester_id;
    
$$;


ALTER FUNCTION public.list_courses_for_lecturer_and_semester(lecturer_name character varying, input_semester_id character varying) OWNER TO postgres;

--
-- Name: log_insert_academic_ranks(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.log_insert_academic_ranks() RETURNS trigger
    LANGUAGE plpgsql
    AS $$ BEGIN
INSERT INTO public.logging
(record_insert_datetime,
referenced_table_name,
description)
VALUES (CURRENT_TIMESTAMP,
'academic_ranks',
CONCAT('numeric_rank: "', NEW.numeric_rank, '";', ' name: "', TRIM(NEW.name), '"'));
RETURN NEW;
END;
$$;


ALTER FUNCTION public.log_insert_academic_ranks() OWNER TO postgres;

--
-- Name: log_insert_courses(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.log_insert_courses() RETURNS trigger
    LANGUAGE plpgsql
    AS $$ BEGIN
INSERT INTO public.logging
(record_insert_datetime,
referenced_table_name,
description)
VALUES (CURRENT_TIMESTAMP,
'courses',
CONCAT('course_id: "', NEW.course_id, '";', ' course_title: "', TRIM(NEW.course_title), '";', ' course_description: "', TRIM(NEW.course_description), '"'));
RETURN NEW;
END;
$$;


ALTER FUNCTION public.log_insert_courses() OWNER TO postgres;

--
-- Name: log_insert_lecturers(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.log_insert_lecturers() RETURNS trigger
    LANGUAGE plpgsql
    AS $$ BEGIN
INSERT INTO public.logging
(record_insert_datetime,
referenced_table_name,
description)
VALUES (CURRENT_TIMESTAMP,
'lecturers',
CONCAT('lecturer_id: "', NEW.lecturer_id, '";', ' name: "', TRIM(NEW.name), '";', ' date_of_birth: "', NEW.date_of_birth, '";', ' numeric_academic_rank: "', NEW.numeric_academic_rank, '"'));
RETURN NEW;
END;
$$;


ALTER FUNCTION public.log_insert_lecturers() OWNER TO postgres;

--
-- Name: log_insert_schedule_of_classes(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.log_insert_schedule_of_classes() RETURNS trigger
    LANGUAGE plpgsql
    AS $$ BEGIN
INSERT INTO public.logging
(record_insert_datetime,
referenced_table_name,
description)
VALUES (CURRENT_TIMESTAMP,
'schedule_of_classes',
CONCAT('student_id: "', NEW.student_id, '";', ' lecturer_id: "', NEW.lecturer_id, '";', ' course_id: "', NEW.course_id, '";', ' time: "', NEW.time, '";', ' location: "', TRIM(NEW.location), '";', ' semester_id: "', NEW.semester_id, '"'));
RETURN NEW;
END;
$$;


ALTER FUNCTION public.log_insert_schedule_of_classes() OWNER TO postgres;

--
-- Name: log_insert_semesters(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.log_insert_semesters() RETURNS trigger
    LANGUAGE plpgsql
    AS $$ BEGIN
INSERT INTO public.logging
(record_insert_datetime,
referenced_table_name,
description)
VALUES (CURRENT_TIMESTAMP,
'semesters',
CONCAT('semester_id: "', NEW.semester_id, '";', ' name: "', TRIM(NEW.name), '";', ' year: "', NEW.year, '";', ' start: "', NEW.start_time, '";', ' end: "', NEW.end_time, '"'));
RETURN NEW;
END;
$$;


ALTER FUNCTION public.log_insert_semesters() OWNER TO postgres;

--
-- Name: log_insert_students(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.log_insert_students() RETURNS trigger
    LANGUAGE plpgsql
    AS $$ BEGIN
INSERT INTO public.logging
(record_insert_datetime,
referenced_table_name,
description)
VALUES (CURRENT_TIMESTAMP,
'students',
CONCAT('student_id: "', NEW.student_id, '";', ' name: "', TRIM(NEW.name), '";', ' date_of_birth: "', NEW.date_of_birth, '"'));
RETURN NEW;
END;
$$;


ALTER FUNCTION public.log_insert_students() OWNER TO postgres;

--
-- Name: log_insert_users(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.log_insert_users() RETURNS trigger
    LANGUAGE plpgsql
    AS $$ BEGIN
INSERT INTO public.logging
(record_insert_datetime,
referenced_table_name,
description)
VALUES (CURRENT_TIMESTAMP,
'users',
CONCAT('login: "', TRIM(NEW.login), '";', ' password: "', TRIM(NEW.password), '";', ' is_active: "', NEW.is_active, '";', ' student_id: "', NEW.student_id, '"'));
RETURN NEW;
END;
$$;


ALTER FUNCTION public.log_insert_users() OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: academic_ranks; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.academic_ranks (
    numeric_rank integer NOT NULL,
    name character(45)
);


ALTER TABLE public.academic_ranks OWNER TO postgres;

--
-- Name: schedule_of_classes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.schedule_of_classes (
    student_id integer,
    lecturer_id integer,
    course_id integer,
    "time" bigint,
    location character(45),
    semester_id character(7),
    entry_id integer NOT NULL
);


ALTER TABLE public.schedule_of_classes OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    login character(45) NOT NULL,
    password character(45),
    is_active boolean,
    student_id integer
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: course_inactiveusersnumber; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.course_inactiveusersnumber AS
 SELECT ss.course_id,
    count(ss.student_id) AS count
   FROM ( SELECT schedule_of_classes.course_id,
            schedule_of_classes.student_id,
            users.is_active
           FROM (public.schedule_of_classes
             LEFT JOIN public.users ON ((schedule_of_classes.student_id = users.student_id)))) ss
  WHERE (ss.is_active = false)
  GROUP BY ss.course_id;


ALTER TABLE public.course_inactiveusersnumber OWNER TO postgres;

--
-- Name: lecturers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.lecturers (
    lecturer_id integer NOT NULL,
    name character(45),
    date_of_birth bigint,
    numeric_academic_rank integer
);


ALTER TABLE public.lecturers OWNER TO postgres;

--
-- Name: course_lecturer_rank; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.course_lecturer_rank AS
 SELECT schedule_of_classes.course_id,
    schedule_of_classes.lecturer_id,
    lecturers.numeric_academic_rank
   FROM (public.schedule_of_classes
     JOIN public.lecturers ON ((schedule_of_classes.lecturer_id = lecturers.lecturer_id)));


ALTER TABLE public.course_lecturer_rank OWNER TO postgres;

--
-- Name: course_maxlecturerrank; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.course_maxlecturerrank AS
 SELECT course_lecturer_rank.course_id,
    max(course_lecturer_rank.numeric_academic_rank) AS max_rank
   FROM public.course_lecturer_rank
  GROUP BY course_lecturer_rank.course_id;


ALTER TABLE public.course_maxlecturerrank OWNER TO postgres;

--
-- Name: course_maxlecturerrank_rankname; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.course_maxlecturerrank_rankname AS
 SELECT course_maxlecturerrank.course_id,
    course_maxlecturerrank.max_rank,
    academic_ranks.name
   FROM (public.course_maxlecturerrank
     LEFT JOIN public.academic_ranks ON ((course_maxlecturerrank.max_rank = academic_ranks.numeric_rank)));


ALTER TABLE public.course_maxlecturerrank_rankname OWNER TO postgres;

--
-- Name: course_maxlecturerrank_lecturer_rankname; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.course_maxlecturerrank_lecturer_rankname AS
 SELECT course_maxlecturerrank_rankname.course_id,
    course_maxlecturerrank_rankname.max_rank,
    course_lecturer_rank.lecturer_id,
    course_maxlecturerrank_rankname.name
   FROM (public.course_maxlecturerrank_rankname
     LEFT JOIN public.course_lecturer_rank ON (((course_maxlecturerrank_rankname.course_id = course_lecturer_rank.course_id) AND (course_maxlecturerrank_rankname.max_rank = course_lecturer_rank.numeric_academic_rank))));


ALTER TABLE public.course_maxlecturerrank_lecturer_rankname OWNER TO postgres;

--
-- Name: courses; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.courses (
    course_id integer NOT NULL,
    course_title character(45),
    course_description character(60)
);


ALTER TABLE public.courses OWNER TO postgres;

--
-- Name: get_table_sizes; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.get_table_sizes AS
 SELECT c.relname AS relation,
    pg_size_pretty(pg_total_relation_size((c.oid)::regclass)) AS total_size
   FROM (pg_class c
     LEFT JOIN pg_namespace n ON ((n.oid = c.relnamespace)))
  WHERE ((n.nspname <> ALL (ARRAY['pg_catalog'::name, 'information_schema'::name])) AND (c.relkind <> 'i'::"char") AND (n.nspname !~ '^pg_toast'::text))
  ORDER BY (pg_total_relation_size((c.oid)::regclass)) DESC;


ALTER TABLE public.get_table_sizes OWNER TO postgres;

--
-- Name: get_tablespace_size; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.get_tablespace_size AS
 SELECT pg_size_pretty(pg_tablespace_size('university_registrar_tablespace'::name)) AS pg_size_pretty;


ALTER TABLE public.get_tablespace_size OWNER TO postgres;

--
-- Name: logging; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.logging (
    record_insert_datetime timestamp without time zone,
    referenced_table_name character(45),
    description text
);


ALTER TABLE public.logging OWNER TO postgres;

--
-- Name: schedule_of_classes_entry_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.schedule_of_classes_entry_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.schedule_of_classes_entry_id_seq OWNER TO postgres;

--
-- Name: schedule_of_classes_entry_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.schedule_of_classes_entry_id_seq OWNED BY public.schedule_of_classes.entry_id;


--
-- Name: semesters; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.semesters (
    semester_id character(7) NOT NULL,
    name character(30),
    year integer,
    start_time bigint,
    end_time bigint
);


ALTER TABLE public.semesters OWNER TO postgres;

--
-- Name: students; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.students (
    student_id integer NOT NULL,
    name character(45),
    date_of_birth bigint
);


ALTER TABLE public.students OWNER TO postgres;

--
-- Name: task_1; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW public.task_1 AS
 SELECT courses.course_title AS "Course title",
    count(DISTINCT schedule_of_classes.student_id) AS "Number of Students",
    count(DISTINCT schedule_of_classes.lecturer_id) AS "Number of Lecturers",
    course_maxlecturerrank_lecturer_rankname.name AS "Highest lecturer rank",
    course_inactiveusersnumber.count AS "Inactive users",
    t.semester_id AS "Most picked semester"
   FROM ((((public.courses
     LEFT JOIN public.schedule_of_classes ON ((schedule_of_classes.course_id = courses.course_id)))
     LEFT JOIN public.course_maxlecturerrank_lecturer_rankname ON ((courses.course_id = course_maxlecturerrank_lecturer_rankname.course_id)))
     LEFT JOIN public.course_inactiveusersnumber ON ((courses.course_id = course_inactiveusersnumber.course_id)))
     LEFT JOIN public.get_most_picked_semesters() t(course_id, semester_id, max_times_in_single_semester) ON ((courses.course_id = t.course_id)))
  GROUP BY courses.course_title, course_maxlecturerrank_lecturer_rankname.name, course_inactiveusersnumber.count, t.semester_id;


ALTER TABLE public.task_1 OWNER TO postgres;

--
-- Name: schedule_of_classes entry_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule_of_classes ALTER COLUMN entry_id SET DEFAULT nextval('public.schedule_of_classes_entry_id_seq'::regclass);


--
-- Data for Name: academic_ranks; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.academic_ranks (numeric_rank, name) FROM stdin;
1	Instructor                                   
2	Master instructor                            
3	Associate professor                          
4	Professor                                    
5	super professor                              
\.


--
-- Data for Name: courses; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.courses (course_id, course_title, course_description) FROM stdin;
101	Computer science 101                         	Prerequisites: Mathematics 1061                             
123	Calculus 123                                 	                                                            
423	Economics 423                                	Prerequisites: 1061, 123                                    
777	Algorithms and data structures 777           	Prerequisites: CS101                                        
1061	Mathematics 1061                             	Prerequisites: Calculus 123                                 
\.


--
-- Data for Name: lecturers; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.lecturers (lecturer_id, name, date_of_birth, numeric_academic_rank) FROM stdin;
21	Michael Turing                               	1596304800	4
22	Sergey Lobachevsky                           	92944800000	3
23	Heinrich Neubauer                            	452538000000	2
24	Milly Sanderson                              	452624400000	1
25	Severus Snape                                	29786400000	4
\.


--
-- Data for Name: logging; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.logging (record_insert_datetime, referenced_table_name, description) FROM stdin;
2020-07-20 00:00:00	semesters                                    	semester_id: "SPR2020"; name: "Spring semester of 2020                      "; year: "2020"; start: "111"; end: "111"
2020-07-20 00:00:00	students                                     	student_id: "6"; name: "Fred Smith                                   "; date_of_birth: "813283200"
2020-07-20 00:00:00	students                                     	student_id: "7"; name: "Frank Montana"; date_of_birth: "813283211"
2020-07-20 00:00:00	students                                     	login: "freddys"; password: "fredsmith1995"; is_active: "t"; student_id: "6"
2020-07-20 00:00:00	students                                     	login: "frankMontana"; password: "frankmontana777"; is_active: "t"; student_id: "7"
2020-07-20 00:00:00	schedule_of_classes                          	student_id: "6"; lecturer_id: "4"; course_id: "423"; time: "1589306400"; location: "room E3"; semester_id: "SUM2020"
2020-07-20 00:00:00	schedule_of_classes                          	student_id: "7"; lecturer_id: "2"; course_id: "123"; time: "1583080200"; location: "room C1"; semester_id: "FAL2020"
2020-07-20 00:00:00	schedule_of_classes                          	student_id: "7"; lecturer_id: "2"; course_id: "101"; time: "1599751800"; location: "room R3"; semester_id: "WIN2020"
2020-07-20 00:00:00	schedule_of_classes                          	student_id: "7"; lecturer_id: "2"; course_id: "101"; time: "1599751800"; location: "room R3"; semester_id: "WIN2020"
2020-07-20 00:00:00	schedule_of_classes                          	student_id: "7"; lecturer_id: "2"; course_id: "101"; time: "1599751800"; location: "room R3"; semester_id: "WIN2020"
2020-07-23 00:00:00	academic_ranks                               	numeric_rank: "5"; name: "super professor"
2020-08-02 00:00:00	courses                                      	course_id: "254"; course_title: "Physical Education 254"; course_description: "Prerequisites: none"
2020-08-03 15:23:38.003954	lecturers                                    	lecturer_id: "6"; name: "C-3PO"; date_of_birth: "1596304800"; numeric_academic_rank: "2"
2020-08-03 16:03:24.896102	semesters                                    	semester_id: "SUM2021"; name: "Summer semester of 2021"; year: "2021"; start: "1619949611000"; end: "1629885611000"
2020-08-03 18:40:14.262028	schedule_of_classes                          	student_id: "55"; lecturer_id: "55"; course_id: "55"; time: "55"; location: "asdasd"; semester_id: "asdasd "
\.


--
-- Data for Name: schedule_of_classes; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.schedule_of_classes (student_id, lecturer_id, course_id, "time", location, semester_id, entry_id) FROM stdin;
11	21	1061	1599300000	room R1                                      	FAL2020	1
12	22	1061	1599300000	room R1                                      	FAL2020	2
11	23	101	1599751800	room R3                                      	WIN2020	3
12	24	101	1599751800	room R3                                      	WIN2020	4
13	21	123	1583080200	room C1                                      	FAL2020	5
14	23	1061	1610280000	room B2                                      	WIN2020	6
16	24	423	1589306400	room E3                                      	SUM2020	7
17	22	123	1583080200	room C1                                      	FAL2020	8
17	22	101	1599751800	room R3                                      	WIN2020	9
15	25	777	1588681800	room F6                                      	SUM2020	10
\.


--
-- Data for Name: semesters; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.semesters (semester_id, name, year, start_time, end_time) FROM stdin;
SUM2021	Summer semester of 2021       	2021	1619949611000	1629885611000
FAL2020	Fall semester of 2020         	2020	1577815200000	1608832800000
SUM2020	Summer semester of 2020       	2020	1588269600000	1596218400000
WIN2020	Winter semester of 2020       	2020	1578592800000	1587751200000
\.


--
-- Data for Name: students; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.students (student_id, name, date_of_birth) FROM stdin;
11	John Doe                                     	758570400000
12	Jane Doe                                     	818532000000
13	Harry Potter                                 	946663200000
14	Michael Michaels                             	694551600000
15	Random Newcommer                             	510170400000
16	Fred Smith                                   	813261600000
17	Frank Montana                                	839696400000
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (login, password, is_active, student_id) FROM stdin;
janedoe2020                                  	janedoe*$1                                   	f	2
john_doe                                     	johndoe456                                   	t	1
micmic1982                                   	mm123456                                     	f	4
rannew111                                    	password                                     	f	5
scarface_boy                                 	hp123456789                                  	f	3
freddys                                      	fredsmith1995                                	t	6
frankMontana                                 	frankmontana777                              	f	7
\.


--
-- Name: schedule_of_classes_entry_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.schedule_of_classes_entry_id_seq', 11, true);


--
-- Name: academic_ranks academic_ranks_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.academic_ranks
    ADD CONSTRAINT academic_ranks_pkey PRIMARY KEY (numeric_rank);


--
-- Name: courses courses_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.courses
    ADD CONSTRAINT courses_pkey PRIMARY KEY (course_id);


--
-- Name: lecturers lecturers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.lecturers
    ADD CONSTRAINT lecturers_pkey PRIMARY KEY (lecturer_id);


--
-- Name: schedule_of_classes schedule_of_classes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.schedule_of_classes
    ADD CONSTRAINT schedule_of_classes_pkey PRIMARY KEY (entry_id);


--
-- Name: semesters semesters_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.semesters
    ADD CONSTRAINT semesters_pkey PRIMARY KEY (semester_id);


--
-- Name: students students_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.students
    ADD CONSTRAINT students_pkey PRIMARY KEY (student_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (login);


--
-- Name: academic_ranks tr_ins_academic_ranks; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER tr_ins_academic_ranks BEFORE INSERT ON public.academic_ranks FOR EACH ROW EXECUTE PROCEDURE public.log_insert_academic_ranks();


--
-- Name: courses tr_ins_courses; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER tr_ins_courses BEFORE INSERT ON public.courses FOR EACH ROW EXECUTE PROCEDURE public.log_insert_courses();


--
-- Name: lecturers tr_ins_lecturers; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER tr_ins_lecturers BEFORE INSERT ON public.lecturers FOR EACH ROW EXECUTE PROCEDURE public.log_insert_lecturers();


--
-- Name: schedule_of_classes tr_ins_schedule_of_classes; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER tr_ins_schedule_of_classes BEFORE INSERT ON public.schedule_of_classes FOR EACH ROW EXECUTE PROCEDURE public.log_insert_schedule_of_classes();


--
-- Name: semesters tr_ins_semesters; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER tr_ins_semesters BEFORE INSERT ON public.semesters FOR EACH ROW EXECUTE PROCEDURE public.log_insert_semesters();


--
-- Name: students tr_ins_students; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER tr_ins_students BEFORE INSERT ON public.students FOR EACH ROW EXECUTE PROCEDURE public.log_insert_students();


--
-- Name: users tr_ins_users; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER tr_ins_users BEFORE INSERT ON public.users FOR EACH ROW EXECUTE PROCEDURE public.log_insert_users();


--
-- PostgreSQL database dump complete
--

