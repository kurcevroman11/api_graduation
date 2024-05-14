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
    content text,
    file_resources text
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
    start_data timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    descriptionid integer,
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
479	481
35	30
\.


--
-- Data for Name: description; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.description (id, content, file_resources) FROM stdin;
18	\N	src\\main\\resources\\media\\18\\file\\
19	\N	src\\main\\resources\\media\\19\\file\\
20	\N	src\\main\\resources\\media\\20\\file\\
21	\N	src\\main\\resources\\media\\23\\file\\
22	\N	src\\main\\resources\\media\\24\\file\\
23	\N	src\\main\\resources\\media\\25\\file\\
24	\N	src\\main\\resources\\media\\27\\file\\
25	\N	src\\main\\resources\\media\\28\\file\\
26	\N	src\\main\\resources\\media\\29\\file\\
27	\N	src\\main\\resources\\media\\30\\file\\
30	\N	src\\main\\resources\\media\\33\\file\\
32	\N	src\\main\\resources\\media\\35\\file\\
33	\N	src\\main\\resources\\media\\36\\file\\
34	\N	src\\main\\resources\\media\\37\\file\\
35	\N	src\\main\\resources\\media\\38\\
36	\N	src\\main\\resources\\media\\39\\
37	\N	src\\main\\resources\\media\\40\\
38	\N	src\\main\\resources\\media\\41\\
43	\N	src\\main\\resources\\media\\50\\
44	\N	src\\main\\resources\\media\\51\\
45	\N	src\\main\\resources\\media\\53\\
46	\N	src\\main\\resources\\media\\54\\
47	\N	src/main/resources/media/55/
48	\N	src//main//resources//media//56/
49	\N	src/main/resources/media/57/
51	\N	src/main/resources/media/59/
52	\N	src/main/resources/media/60/
53	\N	src/main/resources/media/61/
54	\N	src/main/resources/media/63/
107	\N	src/main/resources/media/118/
40	Переделанное описание проекта	src\\main\\resources\\media\\43\\
108	\N	src/main/resources/media/119/
56	\N	src/main/resources/media/66/
57	\N	src/main/resources/media/67/
58	\N	src/main/resources/media/68/
59	\N	src/main/resources/media/69/
60	\N	src/main/resources/media/70/
61	\N	src/main/resources/media/71/
62	\N	src/main/resources/media/72/
63	\N	src/main/resources/media/73/
64	\N	src/main/resources/media/74/
65	\N	src/main/resources/media/75/
66	\N	src/main/resources/media/76/
67	\N	src/main/resources/media/77/
68	\N	src/main/resources/media/78/
69	\N	src/main/resources/media/79/
70	\N	src/main/resources/media/80/
71	\N	src/main/resources/media/81/
72	\N	src/main/resources/media/82/
73	\N	src/main/resources/media/84/
74	\N	src/main/resources/media/85/
75	\N	src/main/resources/media/86/
76	\N	src/main/resources/media/87/
77	\N	src/main/resources/media/88/
78	\N	src/main/resources/media/89/
79	\N	src/main/resources/media/90/
80	\N	src/main/resources/media/91/
81	\N	src/main/resources/media/92/
82	\N	src/main/resources/media/93/
83	\N	src/main/resources/media/94/
84	\N	src/main/resources/media/95/
85	\N	src/main/resources/media/96/
86	\N	src/main/resources/media/97/
87	\N	src/main/resources/media/98/
88	\N	src/main/resources/media/99/
89	\N	src/main/resources/media/100/
90	\N	src/main/resources/media/101/
91	\N	src/main/resources/media/102/
92	\N	src/main/resources/media/103/
93	\N	src/main/resources/media/104/
94	\N	src/main/resources/media/105/
95	\N	src/main/resources/media/106/
96	\N	src/main/resources/media/107/
97	\N	src/main/resources/media/108/
98	\N	src/main/resources/media/109/
99	\N	src/main/resources/media/110/
100	\N	src/main/resources/media/111/
101	\N	src/main/resources/media/112/
102	\N	src/main/resources/media/113/
103	\N	src/main/resources/media/114/
104	\N	src/main/resources/media/115/
105	\N	src/main/resources/media/116/
106	\N	src/main/resources/media/117/
109	\N	src/main/resources/media/120/
110	\N	src/main/resources/media/121/
111	\N	src/main/resources/media/122/
112	\N	src/main/resources/media/123/
113	\N	src/main/resources/media/124/
114	\N	src/main/resources/media/125/
115	\N	src/main/resources/media/126/
116	\N	src/main/resources/media/127/
117	\N	src/main/resources/media/128/
118	\N	src/main/resources/media/129/
119	\N	src/main/resources/media/130/
120	\N	src/main/resources/media/131/
121	\N	src/main/resources/media/132/
122	\N	src/main/resources/media/133/
123	\N	src/main/resources/media/134/
124	\N	src/main/resources/media/135/
125	\N	src/main/resources/media/136/
126	\N	src/main/resources/media/137/
127	\N	src/main/resources/media/138/
128	\N	src/main/resources/media/139/
129	\N	src/main/resources/media/140/
130	\N	src/main/resources/media/141/
131	\N	src/main/resources/media/142/
132	\N	src/main/resources/media/143/
133	\N	src/main/resources/media/144/
134	\N	src/main/resources/media/145/
135	\N	src/main/resources/media/146/
136	\N	src/main/resources/media/147/
137	\N	src/main/resources/media/148/
138	\N	src/main/resources/media/149/
145	\N	src/main/resources/media/156/
146	\N	src/main/resources/media/157/
147	\N	src/main/resources/media/158/
148	\N	src/main/resources/media/159/
149	\N	src/main/resources/media/160/
150	\N	src/main/resources/media/161/
151	\N	src/main/resources/media/162/
152	\N	src/main/resources/media/163/
153	\N	src/main/resources/media/164/
154	\N	src/main/resources/media/165/
158	\N	src/main/resources/media/170/
159	\N	src/main/resources/media/171/
160	\N	src/main/resources/media/172/
161	\N	src/main/resources/media/173/
162	\N	src/main/resources/media/174/
163	\N	src/main/resources/media/175/
164	\N	src/main/resources/media/176/
165	\N	src/main/resources/media/177/
166	\N	src/main/resources/media/178/
167	\N	src/main/resources/media/179/
168	\N	src/main/resources/media/180/
173	\N	src/main/resources/media/185/
174	\N	src/main/resources/media/186/
175	\N	src/main/resources/media/187/
176	\N	src/main/resources/media/188/
177	\N	src/main/resources/media/189/
178	\N	src/main/resources/media/190/
179	\N	src/main/resources/media/191/
180	\N	src/main/resources/media/192/
185	\N	src/main/resources/media/197/
186	\N	src/main/resources/media/198/
191	\N	src/main/resources/media/203/
192	\N	src/main/resources/media/204/
193	\N	src/main/resources/media/205/
194	\N	src/main/resources/media/206/
195	\N	src/main/resources/media/207/
196	\N	src/main/resources/media/208/
201	\N	src/main/resources/media/213/
202	\N	src/main/resources/media/214/
203	\N	src/main/resources/media/215/
204	\N	src/main/resources/media/216/
205	\N	src/main/resources/media/217/
206	\N	src/main/resources/media/218/
215	\N	src/main/resources/media/227/
216	\N	src/main/resources/media/228/
217	\N	src/main/resources/media/229/
218	\N	src/main/resources/media/230/
219	\N	src/main/resources/media/231/
220	\N	src/main/resources/media/232/
221	\N	src/main/resources/media/233/
222	\N	src/main/resources/media/234/
223	\N	src/main/resources/media/235/
224	\N	src/main/resources/media/236/
225	\N	src/main/resources/media/237/
226	\N	src/main/resources/media/238/
232	\N	src/main/resources/media/244/
233	\N	src/main/resources/media/245/
234	\N	src/main/resources/media/246/
235	\N	src/main/resources/media/247/
236	\N	src/main/resources/media/248/
237	\N	src/main/resources/media/249/
238	\N	src/main/resources/media/250/
239	\N	src/main/resources/media/251/
240	\N	src/main/resources/media/252/
241	\N	src/main/resources/media/253/
242	\N	src/main/resources/media/254/
243	\N	src/main/resources/media/255/
244	\N	src/main/resources/media/256/
245	\N	src/main/resources/media/257/
246	\N	src/main/resources/media/258/
247	\N	src/main/resources/media/259/
248	\N	src/main/resources/media/260/
249	\N	src/main/resources/media/261/
250	\N	src/main/resources/media/262/
263	\N	src/main/resources/media/275/
264	\N	src/main/resources/media/276/
265	\N	src/main/resources/media/277/
266	\N	src/main/resources/media/278/
267	\N	src/main/resources/media/279/
280	\N	src/main/resources/media/292/
281	\N	src/main/resources/media/293/
282	\N	src/main/resources/media/294/
283	\N	src/main/resources/media/295/
284	\N	src/main/resources/media/296/
285	\N	src/main/resources/media/297/
286	\N	src/main/resources/media/298/
287	\N	src/main/resources/media/299/
288	\N	src/main/resources/media/300/
289	\N	src/main/resources/media/301/
290	\N	src/main/resources/media/302/
291	\N	src/main/resources/media/303/
308	\N	src/main/resources/media/320/
309	\N	src/main/resources/media/321/
310	\N	src/main/resources/media/322/
311	\N	src/main/resources/media/323/
312	\N	src/main/resources/media/324/
313	\N	src/main/resources/media/325/
314	\N	src/main/resources/media/326/
315	\N	src/main/resources/media/327/
316	\N	src/main/resources/media/328/
317	\N	src/main/resources/media/329/
318	\N	src/main/resources/media/330/
319	\N	src/main/resources/media/331/
320	\N	src/main/resources/media/332/
321	\N	src/main/resources/media/333/
322	\N	src/main/resources/media/334/
323	\N	src/main/resources/media/335/
324	\N	src/main/resources/media/336/
325	\N	src/main/resources/media/337/
326	\N	src/main/resources/media/338/
327	\N	src/main/resources/media/339/
352	\N	src/main/resources/media/364/
353	\N	src/main/resources/media/365/
354	\N	src/main/resources/media/366/
355	\N	src/main/resources/media/367/
356	\N	src/main/resources/media/368/
357	\N	src/main/resources/media/369/
358	\N	src/main/resources/media/370/
359	\N	src/main/resources/media/371/
336	\N	src/main/resources/media/348/
337	\N	src/main/resources/media/349/
338	\N	src/main/resources/media/350/
339	\N	src/main/resources/media/351/
340	\N	src/main/resources/media/352/
341	\N	src/main/resources/media/353/
342	\N	src/main/resources/media/354/
343	\N	src/main/resources/media/355/
360	\N	src/main/resources/media/372/
361	\N	src/main/resources/media/373/
362	\N	src/main/resources/media/374/
363	\N	src/main/resources/media/375/
364	\N	src/main/resources/media/376/
365	\N	src/main/resources/media/377/
366	\N	src/main/resources/media/378/
367	\N	src/main/resources/media/379/
368	\N	src/main/resources/media/380/
369	\N	src/main/resources/media/381/
370	\N	src/main/resources/media/382/
428	\N	src/main/resources/media/440/
429	\N	src/main/resources/media/441/
430	\N	src/main/resources/media/442/
435	\N	src/main/resources/media/447/
456	\N	src/main/resources/media/468/
457	\N	src/main/resources/media/469/
461	\N	src/main/resources/media/473/
462	\N	src/main/resources/media/474/
465	\N	src/main/resources/media/477/
466	\N	src/main/resources/media/478/
467	\N	src/main/resources/media/479/
468	\N	src/main/resources/media/480/
469	\N	src/main/resources/media/481/
470	\N	src/main/resources/media/482/
471	\N	src/main/resources/media/483/
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
10	xkf_besa_doberdoll-120x128.png.pagespeed.ic.prz58CJU5o (2)	\N	png
11	Снимок экрана 2024-04-03 в 18.11.52	\N	png
12	Снимок экрана 2024-04-03 в 18.11.52	\N	png
13	Снимок экрана 2024-04-03 в 18.11.52	\N	png
20	Снимок экрана 2024-04-01 в 17.36.46	49	png
21	Снимок экрана 2024-04-03 в 18.34.50	49	png
22	backup	49	sql
23	backup	49	sql
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

COPY public.task (id, name, status, start_data, descriptionid, parent, score, generation, typeofactivityid, "position", content) FROM stdin;
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
30	Разработать get запрос	2	2023-06-16 12:08:08.067671+03	27	29	9	3	1	\N	\N
468	Test15	2	2024-05-07 12:42:46.526637+03	456	29	15	3	3	\N	Тестовое описание
35	Разработать что-то	2	2023-06-16 13:45:10.080295+03	32	33	6	3	2	\N	\N
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
346	1	\N	\N	5	361	\N
399	1	\N	\N	5	422	\N
404	1	\N	\N	5	426	\N
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

SELECT pg_catalog.setval('public.description_id_seq', 471, true);


--
-- Name: excel_file_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.excel_file_id_seq', 1, false);


--
-- Name: file_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.file_id_seq', 44, true);


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

SELECT pg_catalog.setval('public.task_id_seq', 483, true);


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

SELECT pg_catalog.setval('public.usersroleproject_id_seq', 442, true);


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
-- Name: task task_fk1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.task
    ADD CONSTRAINT task_fk1 FOREIGN KEY (descriptionid) REFERENCES public.description(id);


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

