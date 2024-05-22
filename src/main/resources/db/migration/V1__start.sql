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
\.


--
-- Data for Name: description; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.description (id, file_resources, task_id) FROM stdin;
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
\.

--
-- Data for Name: man_hours; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.man_hours (id, created_at, hours_spent, comment, taskid, projectid, activityid) FROM stdin;
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
482	1	\N	\N	5	521	\N
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

SELECT pg_catalog.setval('public.description_id_seq', 505, true);


--
-- Name: excel_file_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.excel_file_id_seq', 1, false);


--
-- Name: file_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.file_id_seq', 56, true);


--
-- Name: man_hours_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.man_hours_id_seq', 61, true);


--
-- Name: person_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.person_id_seq', 107, true);


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

SELECT pg_catalog.setval('public.task_id_seq', 523, true);


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

SELECT pg_catalog.setval('public.usersroleproject_id_seq', 482, true);


--
-- Name: usser_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.usser_id_seq', 109, true);


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
    ADD CONSTRAINT file_description_fk FOREIGN KEY (descriptionid) REFERENCES public.description(id) ON DELETE CASCADE;


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

