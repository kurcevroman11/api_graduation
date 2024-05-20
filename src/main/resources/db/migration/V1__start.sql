--
-- PostgreSQL database dump
--

-- Dumped from database version 15.6 (Homebrew)
-- Dumped by pg_dump version 15.6 (Homebrew)

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

SET default_tablespace = '';


--
-- Name: activity; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.activity (
    id integer NOT NULL,
    name character varying
);


ALTER TABLE public.activity OWNER TO postgres;

--
-- Name: activity_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.activity_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.activity_id_seq OWNER TO postgres;

--
-- Name: activity_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.activity_id_seq OWNED BY public.activity.id;


--
-- Name: dependence; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.dependence (
    depends_on integer,
    dependent integer
);


ALTER TABLE public.dependence OWNER TO postgres;

--
-- Name: description; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.description (
    id integer NOT NULL,
    file_resources text,
    task_id integer
);


ALTER TABLE public.description OWNER TO postgres;

--
-- Name: description_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.description_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.description_id_seq OWNER TO postgres;

--
-- Name: description_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.description_id_seq OWNED BY public.description.id;


--
-- Name: excel_file; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.excel_file (
    id integer NOT NULL,
    name_plan character varying,
    path character varying,
    projectid integer
);


ALTER TABLE public.excel_file OWNER TO postgres;

--
-- Name: excel_file_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.excel_file_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.excel_file_id_seq OWNER TO postgres;

--
-- Name: excel_file_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.excel_file_id_seq OWNED BY public.excel_file.id;


--
-- Name: file; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.file (
    id bigint NOT NULL,
    orig_filename text,
    descriptionid integer,
    type text
);


ALTER TABLE public.file OWNER TO postgres;

--
-- Name: file_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.file_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.file_id_seq OWNER TO postgres;

--
-- Name: file_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.file_id_seq OWNED BY public.file.id;


--
-- Name: man_hours; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.man_hours (
    id integer NOT NULL,
    created_at date,
    hours_spent text,
    comment text,
    taskid integer,
    projectid integer,
    activityid integer
);


ALTER TABLE public.man_hours OWNER TO postgres;

--
-- Name: man_hours_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.man_hours_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.man_hours_id_seq OWNER TO postgres;

--
-- Name: man_hours_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.man_hours_id_seq OWNED BY public.man_hours.id;


--
-- Name: person; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.person (
    id integer NOT NULL,
    surname character varying NOT NULL,
    name character varying NOT NULL,
    patronymic character varying,
    role text
);


ALTER TABLE public.person OWNER TO postgres;

--
-- Name: person_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.person_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.person_id_seq OWNER TO postgres;

--
-- Name: person_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.person_id_seq OWNED BY public.person.id;


--
-- Name: role; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.role (
    id integer NOT NULL,
    name character varying
);


ALTER TABLE public.role OWNER TO postgres;

--
-- Name: role_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.role_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.role_id_seq OWNER TO postgres;

--
-- Name: role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.role_id_seq OWNED BY public.role.id;


--
-- Name: status; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.status (
    id integer NOT NULL,
    name text
);


ALTER TABLE public.status OWNER TO postgres;

--
-- Name: status_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.status_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.status_id_seq OWNER TO postgres;

--
-- Name: status_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.status_id_seq OWNED BY public.status.id;


--
-- Name: task; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.task (
    id integer NOT NULL,
    name character varying,
    status integer,
    start_data date DEFAULT CURRENT_TIMESTAMP,
    parent integer,
    score integer,
    generation integer,
    typeofactivityid integer,
    "position" integer,
    content text
);


ALTER TABLE public.task OWNER TO postgres;

--
-- Name: task_backup; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.task_backup (
    id integer,
    name character varying,
    status integer,
    start_data timestamp with time zone,
    descriptionid integer,
    parent integer,
    score integer,
    generation integer,
    typeofactivityid integer,
    "position" integer,
    content text
);


ALTER TABLE public.task_backup OWNER TO postgres;

--
-- Name: task_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.task_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.task_id_seq OWNER TO postgres;

--
-- Name: task_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.task_id_seq OWNED BY public.task.id;


--
-- Name: type_of_activity; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.type_of_activity (
    id integer NOT NULL,
    name character varying
);


ALTER TABLE public.type_of_activity OWNER TO postgres;

--
-- Name: type_of_activity_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.type_of_activity_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.type_of_activity_id_seq OWNER TO postgres;

--
-- Name: type_of_activity_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.type_of_activity_id_seq OWNED BY public.type_of_activity.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id integer NOT NULL,
    username character varying(128) NOT NULL,
    password character varying(128) NOT NULL
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO postgres;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: usersroleproject; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.usersroleproject (
    id integer NOT NULL,
    userid integer,
    projectid integer,
    type_of_activityid integer,
    score integer,
    current_task_id integer,
    creater_project integer
);


ALTER TABLE public.usersroleproject OWNER TO postgres;

--
-- Name: usersroleproject_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.usersroleproject_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.usersroleproject_id_seq OWNER TO postgres;

--
-- Name: usersroleproject_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.usersroleproject_id_seq OWNED BY public.usersroleproject.id;


--
-- Name: usser; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.usser (
    id integer NOT NULL,
    login character varying NOT NULL,
    password character varying NOT NULL,
    personid integer
);


ALTER TABLE public.usser OWNER TO postgres;

--
-- Name: usser_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.usser_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.usser_id_seq OWNER TO postgres;

--
-- Name: usser_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.usser_id_seq OWNED BY public.usser.id;


--
-- Name: activity id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.activity ALTER COLUMN id SET DEFAULT nextval('public.activity_id_seq'::regclass);


--
-- Name: description id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.description ALTER COLUMN id SET DEFAULT nextval('public.description_id_seq'::regclass);


--
-- Name: excel_file id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.excel_file ALTER COLUMN id SET DEFAULT nextval('public.excel_file_id_seq'::regclass);


--
-- Name: file id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.file ALTER COLUMN id SET DEFAULT nextval('public.file_id_seq'::regclass);


--
-- Name: man_hours id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.man_hours ALTER COLUMN id SET DEFAULT nextval('public.man_hours_id_seq'::regclass);


--
-- Name: person id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.person ALTER COLUMN id SET DEFAULT nextval('public.person_id_seq'::regclass);


--
-- Name: role id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.role ALTER COLUMN id SET DEFAULT nextval('public.role_id_seq'::regclass);


--
-- Name: status id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.status ALTER COLUMN id SET DEFAULT nextval('public.status_id_seq'::regclass);


--
-- Name: task id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.task ALTER COLUMN id SET DEFAULT nextval('public.task_id_seq'::regclass);


--
-- Name: type_of_activity id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.type_of_activity ALTER COLUMN id SET DEFAULT nextval('public.type_of_activity_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Name: usersroleproject id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usersroleproject ALTER COLUMN id SET DEFAULT nextval('public.usersroleproject_id_seq'::regclass);


--
-- Name: usser id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usser ALTER COLUMN id SET DEFAULT nextval('public.usser_id_seq'::regclass);


--
-- Data for Name: activity; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.activity (id, name) FROM stdin;
1	Проектирование
2	Разработка
3	Дизайн
4	Расследование
5	Обсуждение
\.


--
-- Data for Name: dependence; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.dependence (depends_on, dependent) FROM stdin;
35	30
\.


--
-- Data for Name: description; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.description (id, file_resources, task_id) FROM stdin;
484	src/main/resources/media/488/	488
485	src/main/resources/media/495/	495
\.


--
-- Data for Name: excel_file; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.excel_file (id, name_plan, path, projectid) FROM stdin;
\.


--
-- Data for Name: file; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.file (id, orig_filename, descriptionid, type) FROM stdin;
48	Без названия	485	png
49	Otchet_4_kurs (3)	485	pdf
50	backup	485	sql
\.


--
-- Data for Name: man_hours; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.man_hours (id, created_at, hours_spent, comment, taskid, projectid, activityid) FROM stdin;
12	2024-04-01	02:00:00	Работа над задачей A	\N	\N	\N
13	2024-04-01	01:15:00	Обеденный перерыв	\N	\N	\N
14	2024-04-01	03:00:00	Работа над задачей B	\N	\N	\N
15	2024-04-01	00:45:00	Вечернее подведение итогов дня	\N	\N	\N
16	2023-06-16	01:30	Test	43	41	1
17	2023-06-16	01:30	Test	43	41	1
48	2024-05-10	00:45	Tes23	442	28	3
18	2023-06-16	01:30	Test	43	41	1
19	2023-06-16	01:30	Tes2	43	41	3
20	2023-06-16	01:30	Tes3	43	41	3
21	2023-06-16	01:30	Tes2dgsda4	35	28	2
22	2023-06-16	00:45	Tes23	43	41	3
38	2023-06-16	00:45	Tes23	43	41	3
39	2023-06-16	00:45	Tes23	43	41	3
45	2023-06-16	00:45	Tes23	29	28	3
47	2023-06-16	00:45	Tes23	29	28	3
7	2024-06-16	\N	Test	43	41	1
8	2024-06-16	\N	Test	43	41	1
9	2024-06-16	\N	Test	43	41	1
10	2024-06-16	\N	Test	43	41	1
\.


--
-- Data for Name: person; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.person (id, surname, name, patronymic, role) FROM stdin;
3	Сидоров	Михаил		\N
4	Ми	Сидо		\N
5	Сидо	Ми		\N
7				\N
8	test	test		\N
9				\N
1	Иванов	Иван	Иванович	Админ
2	Петров	Пётр		Исполнитель
6	Тестов	Тест	Тестович	Исполнитель
\.


--
-- Data for Name: role; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.role (id, name) FROM stdin;
2	Исполнитель
3	Админ
4	Проект-менеджер
\.


--
-- Data for Name: status; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.status (id, name) FROM stdin;
1	Готово
2	В работе
3	В ожиданнии
\.


--
-- Data for Name: task; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.task (id, name, status, start_data, parent, score, generation, typeofactivityid, "position", content) FROM stdin;
37	Разработка KMM по отрисовки задач	2	2023-06-16	\N	0	1	2	\N	\N
38	Test	2	2024-03-29	\N	0	1	\N	\N	\N
61	Тест7	2	2024-04-15	\N	0	1	\N	\N	\N
28	Приложение список дел	2	2023-06-16	\N	16	1	\N	\N	\N
57		1	2024-04-09	\N	\N	1	\N	\N	\N
60	Тест7	2	2024-04-15	\N	0	1	\N	\N	\N
62	Проверка	\N	2024-04-23	\N	\N	1	\N	\N	\N
59	Test24	2	2024-04-15	28	16	2	3	\N	\N
64	Test24	\N	2024-04-23	\N	10	1	3	\N	\N
126	TestTask	2	2024-04-24	122	10	2	1	\N	\N
94	Test	2	2024-04-24	\N	0	1	\N	\N	\N
29	Разработать API	2	2023-06-16	28	15	2	1	\N	\N
33	Разработать дизайн	2	2023-06-16	28	12	2	2	\N	\N
95	Test	2	2024-04-24	80	10	1	1	\N	\N
63	Проверка	2	2024-04-23	\N	10	1	\N	\N	\N
66	Test12	2	2024-04-23	63	10	1	3	\N	Ваня - лошара!
491	Test	2	2024-05-20	\N	0	1	\N	\N	\N
468	Test15	2	2024-05-07	29	15	3	3	\N	Тестовое описание
30	Разработать get запрос	2	2023-06-16	29	15	3	1	\N	\N
35	Разработать что-то	2	2023-06-16	33	18	3	2	\N	\N
41	ФК Черноморец	2	2024-04-04	\N	0	1	\N	\N	\N
43	Разработать прототип	2	2024-04-04	41	0	2	3	\N	\N
469	Test0084	2	2024-05-10	\N	25	1	\N	\N	\N
473	Test30	2	2024-05-13	469	13	2	3	\N	Тестовое описание
198	Test12	2	2024-04-29	197	10	2	3	\N	Тестовое описание
197	Test84	2	2024-04-29	\N	10	1	\N	\N	\N
275	Test0084	2	2024-05-02	\N	0	1	\N	\N	\N
479	TestDependeOn	2	2024-05-13	469	12	2	3	\N	Тестовое описание
481	TestDependeOn3	2	2024-05-13	469	25	2	3	\N	Тестовое описание
483	TestDependeOn5	2	2024-05-13	469	6	2	3	\N	Тестовое описание
474	Test10	2	2024-05-13	473	13	3	3	\N	Тестовое описание
477	Test13	2	2024-05-13	474	13	4	3	\N	Тестовое описание
480	TestDependeOn2	2	2024-05-13	469	14	2	3	\N	Тестовое описание
482	TestDependeOn4	2	2024-05-13	469	6	2	3	\N	Тестовое описание
478	Test13477	2	2024-05-13	477	13	5	3	\N	Тестовое описание
36	продумывние прототипа	2	2023-06-16	33	12	3	2	\N	\N
440	Test0007	2	2024-05-06	29	15	3	3	\N	Тестовое описание
447	Test15	2	2024-05-06	440	15	4	3	\N	Тестовое описание
441	Test0007	2	2024-05-06	440	6	4	3	\N	Тестовое описание
442	Test0007	2	2024-05-06	440	6	4	3	\N	Тестовое описание
488	Test	2	2024-05-20	\N	20	1	\N	\N	\N
492	TestTask	2	2024-05-20	488	10	2	1	\N	\N
490	TestTask2	2	2024-05-20	488	20	2	1	\N	\N
489	TestTask	2	2024-05-20	488	10	2	1	\N	TestDescription
493	TestFiles	2	2024-05-20	488	15	2	3	\N	Тестовое описание
494	TestFiles2	2	2024-05-20	488	15	2	3	\N	Тестовое описание
495	TestFiles3	2	2024-05-20	488	15	2	3	\N	Тестовое описание
\.


--
-- Data for Name: task_backup; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.task_backup (id, name, status, start_data, descriptionid, parent, score, generation, typeofactivityid, "position", content) FROM stdin;
37	Разработка KMM по отрисовки задач	2	2023-06-16 23:52:52.584454+03	34	\N	0	1	2	\N	\N
38	Test	2	2024-03-29 21:55:49.499668+03	35	\N	0	1	\N	\N	\N
61	Тест7	2	2024-04-15 11:50:10.397819+03	53	\N	0	1	\N	\N	\N
28	Приложение список дел	2	2023-06-16 11:57:11.168978+03	25	\N	16	1	\N	\N	\N
57		1	2024-04-09 16:55:41.570365+03	\N	\N	\N	1	\N	\N	\N
60	Тест7	2	2024-04-15 11:46:05.433221+03	52	\N	0	1	\N	\N	\N
62	Проверка	\N	2024-04-23 10:40:36.633316+03	\N	\N	\N	1	\N	\N	\N
59	Test24	2	2024-04-15 11:34:44.592853+03	51	28	16	2	3	\N	\N
64	Test24	\N	2024-04-23 17:58:41.480446+03	\N	\N	10	1	3	\N	\N
126	TestTask	2	2024-04-24 18:31:09.330375+03	115	122	10	2	1	\N	\N
94	Test	2	2024-04-24 17:26:52.096177+03	83	\N	0	1	\N	\N	\N
29	Разработать API	2	2023-06-16 12:04:29.661011+03	26	28	15	2	1	\N	\N
33	Разработать дизайн	2	2023-06-16 12:34:05.15122+03	30	28	12	2	2	\N	\N
95	Test	2	2024-04-24 17:26:52.372004+03	84	80	10	1	1	\N	\N
63	Проверка	2	2024-04-23 10:43:13.094319+03	54	\N	10	1	\N	\N	\N
66	Test12	2	2024-04-23 18:35:00.635972+03	56	63	10	1	3	\N	Ваня - лошара!
491	Test	2	2024-05-20 10:37:26.522291+03	479	\N	0	1	\N	\N	\N
468	Test15	2	2024-05-07 12:42:46.526637+03	456	29	15	3	3	\N	Тестовое описание
30	Разработать get запрос	2	2023-06-16 12:08:08.067671+03	27	29	15	3	1	\N	\N
35	Разработать что-то	2	2023-06-16 13:45:10.080295+03	32	33	18	3	2	\N	\N
488	Test	2	2024-05-20 07:38:14.789645+03	476	\N	20	1	\N	\N	\N
492	TestTask	2	2024-05-20 10:37:27.437164+03	480	488	10	2	1	\N	\N
490	TestTask2	2	2024-05-20 07:38:15.841269+03	478	488	20	2	1	\N	\N
489	TestTask	2	2024-05-20 07:38:15.414834+03	477	488	10	2	1	\N	TestDescription
41	ФК Черноморец	2	2024-04-04 15:43:34.844193+03	38	\N	0	1	\N	\N	\N
43	Разработать прототип	2	2024-04-04 16:00:33.931895+03	40	41	0	2	3	\N	\N
469	Test0084	2	2024-05-10 18:04:51.115719+03	457	\N	25	1	\N	\N	\N
473	Test30	2	2024-05-13 10:54:05.28395+03	461	469	13	2	3	\N	Тестовое описание
198	Test12	2	2024-04-29 16:59:21.195795+03	186	197	10	2	3	\N	Тестовое описание
197	Test84	2	2024-04-29 16:57:47.881078+03	185	\N	10	1	\N	\N	\N
275	Test0084	2	2024-05-02 15:02:35.440381+03	263	\N	0	1	\N	\N	\N
479	TestDependeOn	2	2024-05-13 12:26:37.462797+03	467	469	12	2	3	\N	Тестовое описание
481	TestDependeOn3	2	2024-05-13 12:41:10.161713+03	469	469	25	2	3	\N	Тестовое описание
483	TestDependeOn5	2	2024-05-13 12:43:26.987125+03	471	469	6	2	3	\N	Тестовое описание
474	Test10	2	2024-05-13 10:55:24.905995+03	462	473	13	3	3	\N	Тестовое описание
477	Test13	2	2024-05-13 11:57:19.543298+03	465	474	13	4	3	\N	Тестовое описание
480	TestDependeOn2	2	2024-05-13 12:39:40.916151+03	468	469	14	2	3	\N	Тестовое описание
482	TestDependeOn4	2	2024-05-13 12:42:42.129164+03	470	469	6	2	3	\N	Тестовое описание
478	Test13477	2	2024-05-13 12:01:29.945325+03	466	477	13	5	3	\N	Тестовое описание
36	продумывние прототипа	2	2023-06-16 13:47:52.232819+03	33	33	12	3	2	\N	\N
440	Test0007	2	2024-05-06 13:46:57.887226+03	428	29	15	3	3	\N	Тестовое описание
447	Test15	2	2024-05-06 16:34:13.258408+03	435	440	15	4	3	\N	Тестовое описание
441	Test0007	2	2024-05-06 13:47:25.076308+03	429	440	6	4	3	\N	Тестовое описание
442	Test0007	2	2024-05-06 13:52:29.441572+03	430	440	6	4	3	\N	Тестовое описание
\.


--
-- Data for Name: type_of_activity; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.type_of_activity (id, name) FROM stdin;
1	Backend
2	Frontend
4	UI/UX дизайн
3	Тестирование
5	Проектный менеджмент
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, username, password) FROM stdin;
1	nSzF6bGPAh	$2a$10$O6RcP.PQbovGVgven4OQSehe9Dpin9vBQlKM4Ci8c8EcmAIrPD1vi
2	ajvB1xBe4w	$2a$10$AFx5Z5dNroVMLkAKILGXruo68lbiZMbnXXBaU0vH3qqcIZ5w6tgMK
3	OtfkSdkq5f	$2a$10$xDK/Jz.95ZM1IDK6pQqlF.Uaf9JFpE8l2CAoBaxWpjXIb4JIaBb7u
4	OYq8Du67c9	$2a$10$eO25Sd4D7yJF6pbMERPtiuU5ffQfLgUIt5YkKHJ.ubj43EHSPS.MW
5	ZuyzTGN6x5	$2a$10$9R7Fb966a1U0CIJEBNRMFuipe2xvUrmjomR4eND/4lQIThCUHRccO
6	qr3VUxQuxB	$2a$10$9jRC.P1mq3zzvJsXag2diOYt9tSyr1b1SQZWxErC7mMg4VOE9TI62
7	yryhuhbA1A	$2a$10$3O.fdZNTlA87PD0gjX3x5ek2f4z/nRXdXBUrNvep98LhLWcdTvTQm
8	lm5pm62S4F	$2a$10$ANvJnNwvoJiUqw.DpvwjAuUcpAnnDSXfiREk5g3neqQyd4igMCC9.
9	5Pu6PIRYmy	$2a$10$5ZdteYGlpOLJrB5trBK/Xu3N9.oWRUQM.xV9i0ZFYJ/9/txLrQye.
\.


--
-- Data for Name: usersroleproject; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.usersroleproject (id, userid, projectid, type_of_activityid, score, current_task_id, creater_project) FROM stdin;
38	3	\N	\N	\N	\N	\N
40	3	\N	\N	\N	29	\N
41	3	\N	\N	\N	34	\N
103	3	\N	\N	\N	59	\N
104	3	\N	\N	\N	28	\N
105	\N	60	\N	\N	\N	4
106	\N	61	\N	\N	\N	3
61	7	\N	\N	\N	34	\N
62	7	\N	\N	\N	33	\N
63	7	\N	\N	\N	36	\N
78	4	\N	\N	\N	34	\N
90	4	\N	\N	\N	44	\N
91	4	\N	\N	\N	43	\N
92	7	\N	\N	\N	44	\N
93	7	\N	\N	\N	43	\N
95	7	\N	\N	\N	45	\N
100	3	\N	\N	\N	45	\N
296	3	\N	\N	\N	122	\N
410	1	\N	\N	\N	43	\N
411	5	\N	\N	\N	43	\N
416	1	\N	\N	5	449	\N
359	1	\N	\N	5	390	\N
421	1	\N	\N	5	453	\N
364	1	\N	\N	5	394	\N
426	1	\N	\N	5	457	\N
101	3	\N	\N	5	43	\N
369	1	\N	\N	5	398	\N
107	\N	63	\N	\N	\N	\N
109	2	63	\N	\N	\N	\N
110	5	63	\N	\N	\N	\N
111	8	63	\N	\N	\N	\N
112	4	63	\N	\N	\N	\N
374	1	\N	\N	5	402	\N
321	1	\N	\N	5	341	\N
33	3	28	1	7	32	\N
34	4	28	1	4	31	\N
35	5	28	1	5	30	\N
36	6	28	2	3	34	\N
37	7	28	2	6	35	\N
290	1	28	\N	\N	\N	\N
431	1	\N	\N	5	461	\N
160	\N	197	\N	\N	\N	\N
177	\N	197	\N	\N	\N	\N
81	\N	41	\N	\N	\N	\N
88	4	41	\N	\N	\N	\N
94	7	41	\N	\N	\N	\N
99	3	41	\N	\N	\N	\N
291	1	41	\N	\N	\N	\N
379	1	\N	\N	5	406	\N
326	1	\N	\N	5	345	\N
436	1	\N	\N	5	465	\N
384	1	\N	\N	5	410	\N
331	1	\N	\N	5	349	\N
437	2	\N	\N	\N	29	\N
438	6	\N	\N	\N	29	\N
389	1	\N	\N	5	414	\N
439	\N	469	\N	\N	\N	\N
440	1	469	\N	\N	\N	\N
336	1	\N	\N	5	353	\N
441	4	\N	\N	\N	59	\N
442	1	\N	\N	\N	473	\N
394	1	\N	\N	5	418	\N
341	1	\N	\N	5	357	\N
443	4	\N	\N	\N	30	\N
444	1	\N	\N	5	30	\N
346	1	\N	\N	5	361	\N
399	1	\N	\N	5	422	\N
446	1	\N	\N	3	485	\N
404	1	\N	\N	5	426	\N
447	1	\N	\N	5	486	\N
448	1	\N	\N	4	487	\N
449	\N	488	\N	\N	\N	1
450	\N	491	\N	\N	\N	1
409	1	\N	\N	5	430	\N
\.


--
-- Data for Name: usser; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.usser (id, login, password, personid) FROM stdin;
8	user10	666	6
5	user3	44	3
3	user1	22	1
6	user4	55	4
7	user5	66	5
4	user2	33	2
2	admin	admin123	\N
9	test	00	7
\.


--
-- Name: activity_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.activity_id_seq', 5, true);


--
-- Name: description_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.description_id_seq', 485, true);


--
-- Name: excel_file_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.excel_file_id_seq', 1, false);


--
-- Name: file_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.file_id_seq', 50, true);


--
-- Name: man_hours_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.man_hours_id_seq', 48, true);


--
-- Name: person_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.person_id_seq', 103, true);


--
-- Name: role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.role_id_seq', 1, false);


--
-- Name: status_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.status_id_seq', 1, false);


--
-- Name: task_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.task_id_seq', 495, true);


--
-- Name: type_of_activity_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.type_of_activity_id_seq', 2, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_id_seq', 9, true);


--
-- Name: usersroleproject_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.usersroleproject_id_seq', 450, true);


--
-- Name: usser_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.usser_id_seq', 105, true);


--
-- Name: activity activity_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.activity
    ADD CONSTRAINT activity_pkey PRIMARY KEY (id);


--
-- Name: description descriptions_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.description
    ADD CONSTRAINT descriptions_pk PRIMARY KEY (id);


--
-- Name: excel_file excel_file_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.excel_file
    ADD CONSTRAINT excel_file_pkey PRIMARY KEY (id);


--
-- Name: file file_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.file
    ADD CONSTRAINT file_pkey PRIMARY KEY (id);



--
-- Name: man_hours man_hours_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.man_hours
    ADD CONSTRAINT man_hours_pkey PRIMARY KEY (id);


--
-- Name: person person_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.person
    ADD CONSTRAINT person_pk PRIMARY KEY (id);


--
-- Name: role role_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.role
    ADD CONSTRAINT role_pk PRIMARY KEY (id);


--
-- Name: status status_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.status
    ADD CONSTRAINT status_pk PRIMARY KEY (id);


--
-- Name: task task_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.task
    ADD CONSTRAINT task_pk PRIMARY KEY (id);


--
-- Name: type_of_activity type_of_activity_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.type_of_activity
    ADD CONSTRAINT type_of_activity_pk PRIMARY KEY (id);


--
-- Name: usersroleproject unique_user_task; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usersroleproject
    ADD CONSTRAINT unique_user_task UNIQUE (userid, current_task_id);


--
-- Name: usser users_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usser
    ADD CONSTRAINT users_pk PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: users users_username_unique; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_unique UNIQUE (username);


--
-- Name: usersroleproject usersroleproject_pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usersroleproject
    ADD CONSTRAINT usersroleproject_pk PRIMARY KEY (id);




--
-- Name: idx_unique_dependence; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX idx_unique_dependence ON public.dependence USING btree (depends_on, dependent);


--
-- Name: description description_task_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.description
    ADD CONSTRAINT description_task_fk FOREIGN KEY (task_id) REFERENCES public.task(id);


--
-- Name: excel_file excel_file_projectid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.excel_file
    ADD CONSTRAINT excel_file_projectid_fkey FOREIGN KEY (projectid) REFERENCES public.task(id);


--
-- Name: file file_description_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.file
    ADD CONSTRAINT file_description_fk FOREIGN KEY (descriptionid) REFERENCES public.description(id);


--
-- Name: man_hours fk_activity; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.man_hours
    ADD CONSTRAINT fk_activity FOREIGN KEY (activityid) REFERENCES public.activity(id);


--
-- Name: dependence fk_dependent; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dependence
    ADD CONSTRAINT fk_dependent FOREIGN KEY (dependent) REFERENCES public.task(id) ON DELETE CASCADE;


--
-- Name: dependence fk_depends_on; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.dependence
    ADD CONSTRAINT fk_depends_on FOREIGN KEY (depends_on) REFERENCES public.task(id) ON DELETE CASCADE;


--
-- Name: man_hours fk_task; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.man_hours
    ADD CONSTRAINT fk_task FOREIGN KEY (taskid) REFERENCES public.task(id);


--
-- Name: man_hours fk_task_projectid; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.man_hours
    ADD CONSTRAINT fk_task_projectid FOREIGN KEY (projectid) REFERENCES public.task(id);


--
-- Name: task task_fk0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.task
    ADD CONSTRAINT task_fk0 FOREIGN KEY (status) REFERENCES public.status(id);


--
-- Name: task task_typeofactivityid_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.task
    ADD CONSTRAINT task_typeofactivityid_fkey FOREIGN KEY (typeofactivityid) REFERENCES public.type_of_activity(id);


--
-- Name: usser users_fk0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usser
    ADD CONSTRAINT users_fk0 FOREIGN KEY (personid) REFERENCES public.person(id);


--
-- Name: usersroleproject usersroleproject_fk0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usersroleproject
    ADD CONSTRAINT usersroleproject_fk0 FOREIGN KEY (userid) REFERENCES public.person(id);


--
-- Name: usersroleproject usersroleproject_fk2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usersroleproject
    ADD CONSTRAINT usersroleproject_fk2 FOREIGN KEY (projectid) REFERENCES public.task(id);


--
-- Name: usersroleproject usersroleproject_fk3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usersroleproject
    ADD CONSTRAINT usersroleproject_fk3 FOREIGN KEY (type_of_activityid) REFERENCES public.type_of_activity(id);


--
-- PostgreSQL database dump complete
--

