--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.3
-- Dumped by pg_dump version 9.5.3

-- Started on 2016-06-20 19:24:17 UTC

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'SQL_ASCII';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 1 (class 3079 OID 12362)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2109 (class 0 OID 0)
-- Dependencies: 1
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 181 (class 1259 OID 16389)
-- Name: auth; Type: TABLE; Schema: public; Owner: many_tasks_user
--

CREATE TABLE auth (
    username character varying(100) NOT NULL,
    hash bytea NOT NULL
);


ALTER TABLE auth OWNER TO many_tasks_user;

--
-- TOC entry 2101 (class 0 OID 16389)
-- Dependencies: 181
-- Data for Name: auth; Type: TABLE DATA; Schema: public; Owner: many_tasks_user
--

COPY auth (username, hash) FROM stdin;
\.


--
-- TOC entry 1986 (class 2606 OID 16396)
-- Name: auth_pkey; Type: CONSTRAINT; Schema: public; Owner: many_tasks_user
--

ALTER TABLE ONLY auth
    ADD CONSTRAINT auth_pkey PRIMARY KEY (username);


--
-- TOC entry 2108 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2016-06-20 19:24:17 UTC

--
-- PostgreSQL database dump complete
--

