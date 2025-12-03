CREATE ROLE lugus_role WITH
  NOLOGIN
  NOSUPERUSER
  INHERIT
  NOCREATEDB
  NOCREATEROLE
  NOREPLICATION
  NOBYPASSRLS;
  
CREATE ROLE lugus_readonly WITH
  NOLOGIN
  NOSUPERUSER
  INHERIT
  NOCREATEDB
  NOCREATEROLE
  NOREPLICATION
  NOBYPASSRLS;

CREATE ROLE lugus_admin WITH
  NOLOGIN
  NOSUPERUSER
  INHERIT
  NOCREATEDB
  NOCREATEROLE
  NOREPLICATION
  NOBYPASSRLS;
  
CREATE ROLE lugus_usr WITH
  LOGIN
  NOSUPERUSER
  INHERIT
  NOCREATEDB
  NOCREATEROLE
  NOREPLICATION
  NOBYPASSRLS
  ENCRYPTED PASSWORD '<TO BE CHANGED>';
  
CREATE ROLE lugus_usr_ro WITH
  LOGIN
  NOSUPERUSER
  INHERIT
  NOCREATEDB
  NOCREATEROLE
  NOREPLICATION
  NOBYPASSRLS
  CONNECTION LIMIT 3
  ENCRYPTED PASSWORD '<TO BE CHANGED>';

GRANT lugus_readonly TO lugus_usr_ro;  

GRANT lugus_role TO lugus_usr;  

CREATE TABLESPACE tb_lugus
  OWNER lugus_admin
  LOCATION 'D:\\tb_lugus_data';

ALTER TABLESPACE tb_lugus
  OWNER TO lugus_admin;
  
CREATE TABLESPACE tb_lugus_index
  OWNER lugus_admin
  LOCATION E'D:\\tb_lugus_index';

ALTER TABLESPACE tb_lugus_index
  OWNER TO lugus_admin;

-- extensiones 
-- Extension: unaccent

-- DROP EXTENSION unaccent;

CREATE EXTENSION IF NOT EXISTS unaccent
    SCHEMA public
    VERSION "1.1";

-- Extension: plpgsql

-- DROP EXTENSION plpgsql;

CREATE EXTENSION IF NOT EXISTS plpgsql
    SCHEMA pg_catalog
    VERSION "1.0";

-- Extension: pg_trgm

-- DROP EXTENSION pg_trgm;

CREATE EXTENSION IF NOT EXISTS pg_trgm
    SCHEMA public
    VERSION "1.6";
	
-- schemas


CREATE SCHEMA IF NOT EXISTS lugus
    AUTHORIZATION lugus_admin;

GRANT ALL ON SCHEMA lugus TO lugus_admin;

GRANT USAGE ON SCHEMA lugus TO lugus_role;

GRANT USAGE ON SCHEMA lugus TO lugus_readonly;

CREATE SCHEMA IF NOT EXISTS imdb
    AUTHORIZATION lugus_admin;

GRANT ALL ON SCHEMA lugus TO lugus_admin;

GRANT USAGE ON SCHEMA lugus TO lugus_readonly;

GRANT USAGE ON SCHEMA lugus TO lugus_role;

CREATE SCHEMA IF NOT EXISTS imdb
    AUTHORIZATION lugus_admin;

GRANT ALL ON SCHEMA imdb TO lugus_admin;

GRANT ALL ON SCHEMA imdb TO lugus_role;

ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA imdb
GRANT DELETE, INSERT, SELECT, UPDATE ON TABLES TO lugus_role;
  
-- sequences

-- SEQUENCE: lugus.enlaces_id_seq

-- DROP SEQUENCE IF EXISTS lugus.enlaces_id_seq;

CREATE SEQUENCE IF NOT EXISTS lugus.enlaces_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE lugus.enlaces_id_seq
    OWNER TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.enlaces_id_seq TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.enlaces_id_seq TO lugus_role;

-- SEQUENCE: lugus.fotos_id_seq

-- DROP SEQUENCE IF EXISTS lugus.fotos_id_seq;

CREATE SEQUENCE IF NOT EXISTS lugus.fotos_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE lugus.fotos_id_seq
    OWNER TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.fotos_id_seq TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.fotos_id_seq TO lugus_role;

-- SEQUENCE: lugus.fuentes_id_seq

-- DROP SEQUENCE IF EXISTS lugus.fuentes_id_seq;

CREATE SEQUENCE IF NOT EXISTS lugus.fuentes_id_seq
    INCREMENT 1
    START 1
    MINVALUE 0
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE lugus.fuentes_id_seq
    OWNER TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.fuentes_id_seq TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.fuentes_id_seq TO lugus_role;

-- SEQUENCE: lugus.historico_id_seq

-- DROP SEQUENCE IF EXISTS lugus.historico_id_seq;

CREATE SEQUENCE IF NOT EXISTS lugus.historico_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE lugus.historico_id_seq
    OWNER TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.historico_id_seq TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.historico_id_seq TO lugus_role;

-- SEQUENCE: lugus.log_id_seq

-- DROP SEQUENCE IF EXISTS lugus.log_id_seq;

CREATE SEQUENCE IF NOT EXISTS lugus.log_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE lugus.log_id_seq
    OWNER TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.log_id_seq TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.log_id_seq TO lugus_role;

-- SEQUENCE: lugus.operaciones_id_seq

-- DROP SEQUENCE IF EXISTS lugus.operaciones_id_seq;

CREATE SEQUENCE IF NOT EXISTS lugus.operaciones_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE lugus.operaciones_id_seq
    OWNER TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.operaciones_id_seq TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.operaciones_id_seq TO lugus_role;

-- SEQUENCE: lugus.otros_id_seq

-- DROP SEQUENCE IF EXISTS lugus.otros_id_seq;

CREATE SEQUENCE IF NOT EXISTS lugus.otros_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE lugus.otros_id_seq
    OWNER TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.otros_id_seq TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.otros_id_seq TO lugus_role;

-- SEQUENCE: lugus.peliculas_id_seq

-- DROP SEQUENCE IF EXISTS lugus.peliculas_id_seq;

CREATE SEQUENCE IF NOT EXISTS lugus.peliculas_id_seq
    INCREMENT 1
    START 1
    MINVALUE 0
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE lugus.peliculas_id_seq
    OWNER TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.peliculas_id_seq TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.peliculas_id_seq TO lugus_role;

-- SEQUENCE: lugus.personal_id_seq

-- DROP SEQUENCE IF EXISTS lugus.personal_id_seq;

CREATE SEQUENCE IF NOT EXISTS lugus.personal_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE lugus.personal_id_seq
    OWNER TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.personal_id_seq TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.personal_id_seq TO lugus_role;

-- SEQUENCE: lugus.personas_id_seq

-- DROP SEQUENCE IF EXISTS lugus.personas_id_seq;

CREATE SEQUENCE IF NOT EXISTS lugus.personas_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE lugus.personas_id_seq
    OWNER TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.personas_id_seq TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.personas_id_seq TO lugus_role;

-- SEQUENCE: lugus.peticiones_id_seq

-- DROP SEQUENCE IF EXISTS lugus.peticiones_id_seq;

CREATE SEQUENCE IF NOT EXISTS lugus.peticiones_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE lugus.peticiones_id_seq
    OWNER TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.peticiones_id_seq TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.peticiones_id_seq TO lugus_role;

-- SEQUENCE: lugus.profesiones_id_seq

-- DROP SEQUENCE IF EXISTS lugus.profesiones_id_seq;

CREATE SEQUENCE IF NOT EXISTS lugus.profesiones_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE lugus.profesiones_id_seq
    OWNER TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.profesiones_id_seq TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.profesiones_id_seq TO lugus_role;

-- SEQUENCE: lugus.roles_id_seq

-- DROP SEQUENCE IF EXISTS lugus.roles_id_seq;

CREATE SEQUENCE IF NOT EXISTS lugus.roles_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE lugus.roles_id_seq
    OWNER TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.roles_id_seq TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.roles_id_seq TO lugus_role;

-- SEQUENCE: lugus.tipo_oper_id_seq

-- DROP SEQUENCE IF EXISTS lugus.tipo_oper_id_seq;

CREATE SEQUENCE IF NOT EXISTS lugus.tipo_oper_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE lugus.tipo_oper_id_seq
    OWNER TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.tipo_oper_id_seq TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.tipo_oper_id_seq TO lugus_role;

-- SEQUENCE: lugus.series_id_seq

-- DROP SEQUENCE IF EXISTS lugus.series_id_seq;

CREATE SEQUENCE IF NOT EXISTS lugus.series_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE lugus.series_id_seq
    OWNER TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.series_id_seq TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.series_id_seq TO lugus_role;

-- SEQUENCE: lugus.fotoser_id_seq

-- DROP SEQUENCE IF EXISTS lugus.fotoser_id_seq;

CREATE SEQUENCE IF NOT EXISTS lugus.fotoser_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE lugus.fotoser_id_seq
    OWNER TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.fotoser_id_seq TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.fotoser_id_seq TO lugus_role;

-- SEQUENCE: lugus.seasons_id_seq

-- DROP SEQUENCE IF EXISTS lugus.seasons_id_seq;

CREATE SEQUENCE IF NOT EXISTS lugus.seasons_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE lugus.seasons_id_seq
    OWNER TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.seasons_id_seq TO lugus_admin;

GRANT ALL ON SEQUENCE lugus.seasons_id_seq TO lugus_role;

-- SEQUENCE: lugus.tipoUbic_id_seq

-- DROP SEQUENCE IF EXISTS lugus."tipoUbic_id_seq";

CREATE SEQUENCE IF NOT EXISTS lugus."tipo_ubic_id_seq"
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

ALTER SEQUENCE lugus."tipo_ubic_id_seq"
    OWNER TO lugus_admin;

GRANT ALL ON SEQUENCE lugus."tipo_ubic_id_seq" TO lugus_admin;

GRANT ALL ON SEQUENCE lugus."tipo_ubic_id_seq" TO lugus_role;


-- tables
-- Table: lugus.fuentes

-- DROP TABLE IF EXISTS lugus.fuentes;

CREATE TABLE IF NOT EXISTS lugus.fuentes
(
    id integer NOT NULL DEFAULT nextval('lugus.fuentes_id_seq'::regclass),
    descripcion character varying(255) COLLATE pg_catalog."default" NOT NULL,
    suggest character varying(10) COLLATE pg_catalog."default",
    CONSTRAINT fuentes_pkey PRIMARY KEY (id)
)

TABLESPACE tb_lugus;

ALTER TABLE IF EXISTS lugus.fuentes
    OWNER to lugus_admin;

REVOKE ALL ON TABLE lugus.fuentes FROM lugus_readonly;
REVOKE ALL ON TABLE lugus.fuentes FROM lugus_role;

GRANT ALL ON TABLE lugus.fuentes TO lugus_admin;

GRANT SELECT ON TABLE lugus.fuentes TO lugus_readonly;

GRANT INSERT, DELETE, SELECT, UPDATE ON TABLE lugus.fuentes TO lugus_role;

-- Table: lugus.tipos_ubicacion

-- DROP TABLE IF EXISTS lugus.tipos_ubicacion;

CREATE TABLE IF NOT EXISTS lugus.tipos_ubicacion
(
    id integer NOT NULL DEFAULT nextval('lugus.tipo_ubic_id_seq'::regclass),
    description character varying(50) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT tipos_ubicacion_pkey PRIMARY KEY (id)
)

TABLESPACE tb_lugus;

ALTER TABLE IF EXISTS lugus.tipos_ubicacion
    OWNER to lugus_admin;

REVOKE ALL ON TABLE lugus.tipos_ubicacion FROM lugus_readonly;
REVOKE ALL ON TABLE lugus.tipos_ubicacion FROM lugus_role;

GRANT ALL ON TABLE lugus.tipos_ubicacion TO lugus_admin;

GRANT SELECT ON TABLE lugus.tipos_ubicacion TO lugus_readonly;

GRANT INSERT, DELETE, SELECT, UPDATE ON TABLE lugus.tipos_ubicacion TO lugus_role;

COMMENT ON TABLE lugus.tipos_ubicacion
    IS 'Keeps the ubication types';
	
-- Table: lugus.localizaciones

-- DROP TABLE IF EXISTS lugus.localizaciones;

CREATE TABLE IF NOT EXISTS lugus.localizaciones
(
    codigo character varying(12) COLLATE pg_catalog."default" NOT NULL,
    descripcion character varying(255) COLLATE pg_catalog."default" NOT NULL,
    ubicacion_tipo_cod integer NOT NULL,
    CONSTRAINT localizaciones_pkey PRIMARY KEY (codigo),
    CONSTRAINT ubication_fk1 FOREIGN KEY (ubicacion_tipo_cod)
        REFERENCES lugus.tipos_ubicacion (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)

TABLESPACE tb_lugus;

ALTER TABLE IF EXISTS lugus.localizaciones
    OWNER to lugus_admin;

REVOKE ALL ON TABLE lugus.localizaciones FROM lugus_readonly;
REVOKE ALL ON TABLE lugus.localizaciones FROM lugus_role;

GRANT ALL ON TABLE lugus.localizaciones TO lugus_admin;

GRANT SELECT ON TABLE lugus.localizaciones TO lugus_readonly;

GRANT INSERT, DELETE, SELECT, UPDATE ON TABLE lugus.localizaciones TO lugus_role;

-- Table: lugus.log

-- DROP TABLE IF EXISTS lugus.log;

CREATE TABLE IF NOT EXISTS lugus.log
(
    id integer NOT NULL DEFAULT nextval('lugus.log_id_seq'::regclass),
    log character varying(4000) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT log_pkey PRIMARY KEY (id)
)

TABLESPACE tb_lugus;

ALTER TABLE IF EXISTS lugus.log
    OWNER to lugus_admin;

REVOKE ALL ON TABLE lugus.log FROM lugus_readonly;
REVOKE ALL ON TABLE lugus.log FROM lugus_role;

GRANT ALL ON TABLE lugus.log TO lugus_admin;

GRANT SELECT ON TABLE lugus.log TO lugus_readonly;

GRANT INSERT, DELETE, SELECT, TRIGGER, UPDATE ON TABLE lugus.log TO lugus_role;

-- Table: lugus.operaciones

-- DROP TABLE IF EXISTS lugus.operaciones;

CREATE TABLE IF NOT EXISTS lugus.operaciones
(
    id integer NOT NULL DEFAULT nextval('lugus.operaciones_id_seq'::regclass),
    tipo_oper_id integer NOT NULL,
    log character varying COLLATE pg_catalog."default",
    ts_start date NOT NULL,
    ts_end date,
    status_code smallint,
    CONSTRAINT operaciones_pkey PRIMARY KEY (id)
)

TABLESPACE tb_lugus;

ALTER TABLE IF EXISTS lugus.operaciones
    OWNER to lugus_admin;

REVOKE ALL ON TABLE lugus.operaciones FROM lugus_readonly;
REVOKE ALL ON TABLE lugus.operaciones FROM lugus_role;

GRANT ALL ON TABLE lugus.operaciones TO lugus_admin;

GRANT SELECT ON TABLE lugus.operaciones TO lugus_readonly;

GRANT INSERT, DELETE, SELECT, UPDATE ON TABLE lugus.operaciones TO lugus_role;

-- Table: lugus.usuarios

-- DROP TABLE IF EXISTS lugus.usuarios;

CREATE TABLE IF NOT EXISTS lugus.usuarios
(
    login character varying(10) COLLATE pg_catalog."default" NOT NULL,
    password character varying(255) COLLATE pg_catalog."default" NOT NULL,
    admin boolean NOT NULL,
    CONSTRAINT usuarios_pkey PRIMARY KEY (login)
)

TABLESPACE tb_lugus;

ALTER TABLE IF EXISTS lugus.usuarios
    OWNER to lugus_admin;

REVOKE ALL ON TABLE lugus.usuarios FROM lugus_readonly;
REVOKE ALL ON TABLE lugus.usuarios FROM lugus_role;

GRANT ALL ON TABLE lugus.usuarios TO lugus_admin;

GRANT SELECT ON TABLE lugus.usuarios TO lugus_readonly;

GRANT INSERT, DELETE, SELECT, UPDATE ON TABLE lugus.usuarios TO lugus_role;

-- Table: lugus.roles

-- DROP TABLE IF EXISTS lugus.roles;

CREATE TABLE IF NOT EXISTS lugus.roles
(
    id integer NOT NULL DEFAULT nextval('lugus.roles_id_seq'::regclass),
    login character varying(50) COLLATE pg_catalog."default",
    role character varying(50) COLLATE pg_catalog."default",
    CONSTRAINT roles_login_fkey FOREIGN KEY (login)
        REFERENCES lugus.usuarios (login) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE tb_lugus;

ALTER TABLE IF EXISTS lugus.roles
    OWNER to lugus_admin;

REVOKE ALL ON TABLE lugus.roles FROM lugus_usr;
REVOKE ALL ON TABLE lugus.roles FROM lugus_usr_ro;

GRANT ALL ON TABLE lugus.roles TO lugus_admin;

GRANT INSERT, DELETE, SELECT, UPDATE ON TABLE lugus.roles TO lugus_usr;

GRANT SELECT ON TABLE lugus.roles TO lugus_usr_ro;

-- Table: lugus.personas

-- DROP TABLE IF EXISTS lugus.personas;

CREATE TABLE IF NOT EXISTS lugus.personas
(
    id integer NOT NULL DEFAULT nextval('lugus.personas_id_seq'::regclass),
    nombre character varying(150) COLLATE pg_catalog."default" NOT NULL,
    nacionalidad integer,
    bio character varying(4000) COLLATE pg_catalog."default",
    nconst character varying(50) COLLATE pg_catalog."default",
    nacimiento integer,
    fallecimiento integer,
    CONSTRAINT personas_pkey PRIMARY KEY (id)
)

TABLESPACE tb_lugus;

ALTER TABLE IF EXISTS lugus.personas
    OWNER to lugus_admin;

REVOKE ALL ON TABLE lugus.personas FROM lugus_readonly;
REVOKE ALL ON TABLE lugus.personas FROM lugus_role;

GRANT ALL ON TABLE lugus.personas TO lugus_admin;

GRANT SELECT ON TABLE lugus.personas TO lugus_readonly;

GRANT INSERT, DELETE, SELECT, UPDATE ON TABLE lugus.personas TO lugus_role;

-- Table: lugus.peticiones

-- DROP TABLE IF EXISTS lugus.peticiones;

CREATE TABLE IF NOT EXISTS lugus.peticiones
(
    id integer NOT NULL DEFAULT nextval('lugus.peticiones_id_seq'::regclass),
    peticion character varying(4000) COLLATE pg_catalog."default" NOT NULL,
    usr_alta character varying(10) COLLATE pg_catalog."default" NOT NULL,
    ts_alta date NOT NULL,
    lg_atendida boolean NOT NULL,
    lg_borrada boolean NOT NULL,
    CONSTRAINT peticiones_pkey PRIMARY KEY (id),
    CONSTRAINT peticiones_fk1 FOREIGN KEY (usr_alta)
        REFERENCES lugus.usuarios (login) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE tb_lugus;

ALTER TABLE IF EXISTS lugus.peticiones
    OWNER to lugus_admin;

REVOKE ALL ON TABLE lugus.peticiones FROM lugus_readonly;
REVOKE ALL ON TABLE lugus.peticiones FROM lugus_role;

GRANT ALL ON TABLE lugus.peticiones TO lugus_admin;

GRANT SELECT ON TABLE lugus.peticiones TO lugus_readonly;

GRANT INSERT, DELETE, SELECT, UPDATE ON TABLE lugus.peticiones TO lugus_role;

-- Table: lugus.peliculas

-- DROP TABLE IF EXISTS lugus.peliculas;

CREATE TABLE IF NOT EXISTS lugus.peliculas
(
    id integer NOT NULL DEFAULT nextval('lugus.peliculas_id_seq'::regclass),
    titulo character varying(255) COLLATE pg_catalog."default" NOT NULL,
    anyo integer NOT NULL,
    formato smallint NOT NULL,
    genero character varying(3) COLLATE pg_catalog."default" NOT NULL,
    localizacion_codigo character varying(12) COLLATE pg_catalog."default",
    codigo character varying(50) COLLATE pg_catalog."default" NOT NULL,
    notas text COLLATE pg_catalog."default",
    pack boolean DEFAULT false,
    comprado boolean DEFAULT false,
    steelbook boolean DEFAULT false,
    funda boolean DEFAULT false,
    padre_id integer,
    usr_alta character varying(10) COLLATE pg_catalog."default" NOT NULL,
    usr_modif character varying(10) COLLATE pg_catalog."default",
    ts_alta timestamp without time zone NOT NULL,
    ts_modif timestamp without time zone,
    titulo_gest character varying(255) COLLATE pg_catalog."default" NOT NULL,
    ts_baja timestamp without time zone,
    CONSTRAINT peliculas_pkey PRIMARY KEY (id),
    CONSTRAINT fk_localizacion FOREIGN KEY (localizacion_codigo)
        REFERENCES lugus.localizaciones (codigo) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_padre FOREIGN KEY (padre_id)
        REFERENCES lugus.peliculas (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_usr_alta_p FOREIGN KEY (usr_alta)
        REFERENCES lugus.usuarios (login) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_usr_modif_p FOREIGN KEY (usr_modif)
        REFERENCES lugus.usuarios (login) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE tb_lugus;

ALTER TABLE IF EXISTS lugus.peliculas
    OWNER to lugus_admin;

REVOKE ALL ON TABLE lugus.peliculas FROM lugus_readonly;
REVOKE ALL ON TABLE lugus.peliculas FROM lugus_role;

GRANT ALL ON TABLE lugus.peliculas TO lugus_admin;

GRANT SELECT ON TABLE lugus.peliculas TO lugus_readonly;

GRANT INSERT, DELETE, SELECT, UPDATE ON TABLE lugus.peliculas TO lugus_role;

-- Trigger: trg_auditoria_peliculas

-- Index: idx_pso_text_trgm

-- DROP INDEX IF EXISTS lugus.idx_pso_text_trgm;

CREATE INDEX IF NOT EXISTS idx_pso_text_trgm
    ON lugus.peliculas USING gin
    (titulo COLLATE pg_catalog."default" gin_trgm_ops)
    WITH (fastupdate=True, gin_pending_list_limit=4194304)
    TABLESPACE tb_lugus_index;
	
-- DROP TRIGGER IF EXISTS trg_auditoria_peliculas ON lugus.peliculas;

CREATE OR REPLACE TRIGGER trg_auditoria_peliculas
    BEFORE DELETE OR UPDATE 
    ON lugus.peliculas
    FOR EACH ROW
    EXECUTE FUNCTION lugus.trg_peliculas_auditoria();
	

-- Table: lugus.peliculas_h

-- DROP TABLE IF EXISTS lugus.peliculas_h;

CREATE TABLE IF NOT EXISTS lugus.peliculas_h
(
    id integer NOT NULL,
    id_historico integer NOT NULL DEFAULT nextval('lugus.historico_id_seq'::regclass),
    titulo character varying(255) COLLATE pg_catalog."default" NOT NULL,
    anyo integer NOT NULL,
    formato smallint NOT NULL,
    genero character varying(3) COLLATE pg_catalog."default" NOT NULL,
    localizacion_codigo character varying(12) COLLATE pg_catalog."default",
    codigo character varying(50) COLLATE pg_catalog."default" NOT NULL,
    notas text COLLATE pg_catalog."default",
    pack boolean DEFAULT false,
    comprado boolean DEFAULT false,
    steelbook boolean DEFAULT false,
    funda boolean DEFAULT false,
    padre_id integer,
    usr_alta character varying(10) COLLATE pg_catalog."default" NOT NULL,
    ts_alta date NOT NULL,
    usr_modif character varying(10) COLLATE pg_catalog."default",
    ts_modif date,
    usr_historico character varying(10) COLLATE pg_catalog."default" NOT NULL,
    ts_historico date NOT NULL,
    titulo_gest character varying(255) COLLATE pg_catalog."default",
    ts_baja timestamp without time zone,
    CONSTRAINT peliculas_h_pkey PRIMARY KEY (id, id_historico)
)

TABLESPACE tb_lugus;

ALTER TABLE IF EXISTS lugus.peliculas_h
    OWNER to lugus_admin;

REVOKE ALL ON TABLE lugus.peliculas_h FROM lugus_readonly;
REVOKE ALL ON TABLE lugus.peliculas_h FROM lugus_role;

GRANT ALL ON TABLE lugus.peliculas_h TO lugus_admin;

GRANT SELECT ON TABLE lugus.peliculas_h TO lugus_readonly;

GRANT INSERT, DELETE, SELECT, UPDATE ON TABLE lugus.peliculas_h TO lugus_role;

-- FUNCTION: lugus.trg_peliculas_auditoria()

-- DROP FUNCTION IF EXISTS lugus.trg_peliculas_auditoria();

CREATE OR REPLACE FUNCTION lugus.trg_peliculas_auditoria()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
BEGIN
    -- Para UPDATE y DELETE guardamos la fila anterior (OLD)
    IF TG_OP = 'UPDATE' OR TG_OP = 'DELETE' THEN
		INSERT INTO
	LUGUS.PELICULAS_H (
		ID,	ID_HISTORICO, TITULO,	ANYO,	FORMATO,	GENERO,
		LOCALIZACION_CODIGO,	CODIGO,	NOTAS,	PACK,	COMPRADO,
		STEELBOOK,	FUNDA,	PADRE_ID,	USR_ALTA,	TS_ALTA,
		USR_MODIF,	TS_MODIF,	USR_HISTORICO,	TS_HISTORICO,
		TITULO_GEST, TS_BAJA
	)
VALUES
	( OLD.id,	nextval('lugus.historico_id_seq'::regclass), OLD.TITULO, OLD.ANYO,	OLD.FORMATO, OLD.GENERO,
		OLD.LOCALIZACION_CODIGO,	OLD.CODIGO,	OLD.NOTAS,	OLD.PACK,	OLD.COMPRADO,
		OLD.STEELBOOK,	OLD.FUNDA,	OLD.PADRE_ID,	OLD.USR_ALTA,	OLD.TS_ALTA,
		OLD.USR_MODIF,	OLD.TS_MODIF,	CURRENT_USER,	 now(),
		OLD.TITULO_GEST, OLD.TS_BAJA);

    END IF;

    -- IMPORTANTE: para BEFORE triggers debemos devolver la fila que seguirá procesándose.
    -- En un BEFORE UPDATE/DELETE devolvemos OLD (la fila original).
    -- En un BEFORE INSERT devolvemos NEW (la fila que se insertará).
    IF TG_OP = 'DELETE' THEN
        RETURN OLD;   -- la fila será eliminada después de este trigger
    ELSE
        RETURN NEW;   -- la fila continuará su proceso (INSERT o UPDATE)
    END IF;
END;
$BODY$;

ALTER FUNCTION lugus.trg_peliculas_auditoria()
    OWNER TO lugus_admin;

-- Table: lugus.peliculas_enlaces

-- DROP TABLE IF EXISTS lugus.peliculas_enlaces;

CREATE TABLE IF NOT EXISTS lugus.peliculas_enlaces
(
    id integer NOT NULL DEFAULT nextval('lugus.enlaces_id_seq'::regclass),
    pelicula_id integer NOT NULL,
    fuente_id integer NOT NULL,
    url character varying(255) COLLATE pg_catalog."default" NOT NULL,
    usr_alta character varying(10) COLLATE pg_catalog."default" NOT NULL,
    ts_alta date NOT NULL,
    usr_modif character varying COLLATE pg_catalog."default",
    ts_modif date,
    CONSTRAINT peliculas_enlaces_pkey PRIMARY KEY (id),
    CONSTRAINT pelicula_enlace_fk1 FOREIGN KEY (pelicula_id)
        REFERENCES lugus.peliculas (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT pelicula_enlace_fk2 FOREIGN KEY (fuente_id)
        REFERENCES lugus.fuentes (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT pelicula_enlace_fk3 FOREIGN KEY (usr_alta)
        REFERENCES lugus.usuarios (login) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT pelicula_enlace_fk4 FOREIGN KEY (usr_modif)
        REFERENCES lugus.usuarios (login) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE tb_lugus;

ALTER TABLE IF EXISTS lugus.peliculas_enlaces
    OWNER to lugus_admin;

REVOKE ALL ON TABLE lugus.peliculas_enlaces FROM lugus_readonly;
REVOKE ALL ON TABLE lugus.peliculas_enlaces FROM lugus_role;

GRANT ALL ON TABLE lugus.peliculas_enlaces TO lugus_admin;

GRANT SELECT ON TABLE lugus.peliculas_enlaces TO lugus_readonly;

GRANT INSERT, DELETE, SELECT, UPDATE ON TABLE lugus.peliculas_enlaces TO lugus_role;


-- Table: lugus.peliculas_fotos

-- DROP TABLE IF EXISTS lugus.peliculas_fotos;

CREATE TABLE IF NOT EXISTS lugus.peliculas_fotos
(
    id integer NOT NULL DEFAULT nextval('lugus.fotos_id_seq'::regclass),
    pelicula_id integer NOT NULL,
    url character varying(255) COLLATE pg_catalog."default" NOT NULL,
    fuente_id integer NOT NULL,
    foto bytea NOT NULL,
    thumb bytea,
    caratula boolean NOT NULL DEFAULT false,
    CONSTRAINT peliculas_fotos_pkey PRIMARY KEY (id),
    CONSTRAINT peliculas_foto_fk1 FOREIGN KEY (pelicula_id)
        REFERENCES lugus.peliculas (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT peliculas_foto_fk2 FOREIGN KEY (fuente_id)
        REFERENCES lugus.fuentes (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE tb_lugus;

ALTER TABLE IF EXISTS lugus.peliculas_fotos
    OWNER to lugus_admin;

REVOKE ALL ON TABLE lugus.peliculas_fotos FROM lugus_readonly;
REVOKE ALL ON TABLE lugus.peliculas_fotos FROM lugus_role;

GRANT ALL ON TABLE lugus.peliculas_fotos TO lugus_admin;

GRANT SELECT ON TABLE lugus.peliculas_fotos TO lugus_readonly;

GRANT INSERT, DELETE, SELECT, UPDATE ON TABLE lugus.peliculas_fotos TO lugus_role;
	
-- Table: lugus.peliculas_otros

-- DROP TABLE IF EXISTS lugus.peliculas_otros;

CREATE TABLE IF NOT EXISTS lugus.peliculas_otros
(
    id integer NOT NULL DEFAULT nextval('lugus.otros_id_seq'::regclass),
    pelicula_id integer NOT NULL,
    idmb_id character varying(20) COLLATE pg_catalog."default",
    CONSTRAINT otros_fk1 FOREIGN KEY (pelicula_id)
        REFERENCES lugus.peliculas (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE tb_lugus;

ALTER TABLE IF EXISTS lugus.peliculas_otros
    OWNER to lugus_admin;

REVOKE ALL ON TABLE lugus.peliculas_otros FROM lugus_readonly;
REVOKE ALL ON TABLE lugus.peliculas_otros FROM lugus_role;

GRANT ALL ON TABLE lugus.peliculas_otros TO lugus_admin;

GRANT SELECT ON TABLE lugus.peliculas_otros TO lugus_readonly;

GRANT INSERT, DELETE, SELECT, UPDATE ON TABLE lugus.peliculas_otros TO lugus_role;

-- Table: lugus.peliculas_personal

-- DROP TABLE IF EXISTS lugus.peliculas_personal;

CREATE TABLE IF NOT EXISTS lugus.peliculas_personal
(
    id integer NOT NULL DEFAULT nextval('lugus.personal_id_seq'::regclass),
    pelicula_id integer NOT NULL,
    persona_id integer NOT NULL,
    notas character varying(4000) COLLATE pg_catalog."default",
    personaje character varying(1000) COLLATE pg_catalog."default",
    orden integer,
    trabajo character varying COLLATE pg_catalog."default",
    CONSTRAINT peliculas_personal_pkey PRIMARY KEY (id),
    CONSTRAINT personal_fk1 FOREIGN KEY (pelicula_id)
        REFERENCES lugus.peliculas (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT personal_fk2 FOREIGN KEY (persona_id)
        REFERENCES lugus.personas (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)

TABLESPACE tb_lugus;

ALTER TABLE IF EXISTS lugus.peliculas_personal
    OWNER to lugus_admin;

REVOKE ALL ON TABLE lugus.peliculas_personal FROM lugus_readonly;
REVOKE ALL ON TABLE lugus.peliculas_personal FROM lugus_role;

GRANT ALL ON TABLE lugus.peliculas_personal TO lugus_admin;

GRANT SELECT ON TABLE lugus.peliculas_personal TO lugus_readonly;

GRANT INSERT, DELETE, SELECT, UPDATE ON TABLE lugus.peliculas_personal TO lugus_role;
-- Index: fki_personal_fk2

-- DROP INDEX IF EXISTS lugus.fki_personal_fk2;

CREATE INDEX IF NOT EXISTS fki_personal_fk2
    ON lugus.peliculas_personal USING btree
    (persona_id ASC NULLS LAST)
    WITH (fillfactor=100, deduplicate_items=True)
    TABLESPACE tb_lugus_index;
	
-- Table: lugus.series

-- DROP TABLE IF EXISTS lugus.series;

CREATE TABLE IF NOT EXISTS lugus.series
(
    id integer NOT NULL DEFAULT nextval('lugus.series_id_seq'::regclass),
    titulo character varying(255) COLLATE pg_catalog."default" NOT NULL,
    titulo_gest character varying(255) COLLATE pg_catalog."default" NOT NULL,
    anyo_inicio integer NOT NULL,
    anyo_fin integer,
    formato smallint NOT NULL,
    genero character varying(3) COLLATE pg_catalog."default" NOT NULL,
    localizacion_codigo character varying(12) COLLATE pg_catalog."default",
    codigo character varying(50) COLLATE pg_catalog."default" NOT NULL,
    notas text COLLATE pg_catalog."default",
    comprado boolean DEFAULT false,
    usr_alta character varying(10) COLLATE pg_catalog."default" NOT NULL,
    usr_modif character varying(10) COLLATE pg_catalog."default",
    ts_alta timestamp without time zone NOT NULL,
    ts_modif timestamp without time zone,
    ts_baja timestamp without time zone,
    completa boolean NOT NULL DEFAULT false,
    CONSTRAINT series_pkey PRIMARY KEY (id),
    CONSTRAINT fk_local_series FOREIGN KEY (localizacion_codigo)
        REFERENCES lugus.localizaciones (codigo) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_usr_alta_s FOREIGN KEY (usr_alta)
        REFERENCES lugus.usuarios (login) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_usr_modif_s FOREIGN KEY (usr_modif)
        REFERENCES lugus.usuarios (login) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE tb_lugus;

ALTER TABLE IF EXISTS lugus.series
    OWNER to lugus_admin;

REVOKE ALL ON TABLE lugus.series FROM lugus_readonly;
REVOKE ALL ON TABLE lugus.series FROM lugus_role;

GRANT ALL ON TABLE lugus.series TO lugus_admin;

GRANT SELECT ON TABLE lugus.series TO lugus_readonly;

GRANT INSERT, DELETE, SELECT, UPDATE ON TABLE lugus.series TO lugus_role;
-- Table: lugus.series_fotos

-- DROP TABLE IF EXISTS lugus.series_fotos;

CREATE TABLE IF NOT EXISTS lugus.series_fotos
(
    id integer NOT NULL DEFAULT nextval('lugus.fotoser_id_seq'::regclass),
    serie_id integer NOT NULL,
    url character varying(255) COLLATE pg_catalog."default" NOT NULL,
    fuente_id integer NOT NULL,
    foto bytea NOT NULL,
    caratula boolean NOT NULL DEFAULT false,
    CONSTRAINT series_foto_pkey PRIMARY KEY (id),
    CONSTRAINT series_foto_fk1 FOREIGN KEY (serie_id)
        REFERENCES lugus.series (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT series_foto_fk2 FOREIGN KEY (fuente_id)
        REFERENCES lugus.fuentes (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE tb_lugus;

ALTER TABLE IF EXISTS lugus.series_fotos
    OWNER to lugus_admin;

REVOKE ALL ON TABLE lugus.series_fotos FROM lugus_readonly;
REVOKE ALL ON TABLE lugus.series_fotos FROM lugus_role;

GRANT ALL ON TABLE lugus.series_fotos TO lugus_admin;

GRANT SELECT ON TABLE lugus.series_fotos TO lugus_readonly;

GRANT INSERT, DELETE, SELECT, UPDATE ON TABLE lugus.series_fotos TO lugus_role;

-- Table: lugus.seasons

-- DROP TABLE IF EXISTS lugus.seasons;

CREATE TABLE IF NOT EXISTS lugus.seasons
(
    id integer NOT NULL DEFAULT nextval('lugus.seasons_id_seq'::regclass),
    series_id integer NOT NULL,
    "desc" character varying(150) COLLATE pg_catalog."default" NOT NULL,
    year integer NOT NULL,
    "order" integer NOT NULL,
    purchased boolean NOT NULL DEFAULT false,
    wanted boolean NOT NULL DEFAULT false,
    published_version character varying(4) COLLATE pg_catalog."default" NOT NULL,
    purchased_version character varying(4) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT seasons_pkey PRIMARY KEY (id),
    CONSTRAINT fk_seasons FOREIGN KEY (series_id)
        REFERENCES lugus.series (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE tb_lugus;

ALTER TABLE IF EXISTS lugus.seasons
    OWNER to lugus_admin;
	
-- views
-- View: lugus.actores

-- DROP VIEW lugus.actores;

CREATE OR REPLACE VIEW lugus.actores
 AS
 SELECT pp.id,
    pp.pelicula_id,
    pp.persona_id,
    p.nombre,
    pp.orden,
    pp.trabajo,
    replace(replace(pp.personaje::text, '['::text, ''::text), ']'::text, ''::text) AS personaje
   FROM lugus.peliculas_personal pp
     JOIN lugus.personas p ON pp.persona_id = p.id
  WHERE pp.trabajo::text = ANY (ARRAY['actor'::character varying::text, 'actress'::character varying::text, 'self'::character varying::text]);

ALTER TABLE lugus.actores
    OWNER TO lugus_admin;

GRANT ALL ON TABLE lugus.actores TO lugus_admin;
GRANT SELECT ON TABLE lugus.actores TO lugus_readonly;
GRANT SELECT ON TABLE lugus.actores TO lugus_role;

-- View: lugus.directores

-- DROP VIEW lugus.directores;

CREATE OR REPLACE VIEW lugus.directores
 AS
 SELECT pp.id,
    pp.pelicula_id,
    pp.persona_id,
    p.nombre,
    pp.orden,
    pp.trabajo
   FROM lugus.peliculas_personal pp
     JOIN lugus.personas p ON pp.persona_id = p.id
  WHERE pp.trabajo::text = 'director'::text;

ALTER TABLE lugus.directores
    OWNER TO lugus_admin;

GRANT ALL ON TABLE lugus.directores TO lugus_admin;
GRANT SELECT ON TABLE lugus.directores TO lugus_readonly;
GRANT SELECT ON TABLE lugus.directores TO lugus_role;

-- View: lugus.imdbDirectorFilm

-- DROP VIEW lugus."imdbDirectorFilm";

CREATE OR REPLACE VIEW lugus."imdbDirectorFilm"
 AS
 SELECT inb.nconst,
    inb.primaryname,
    inb.birthyear,
    inb.deathyear,
    inb.primaryprofession,
    itp.tconst,
    tae.title
   FROM imdb.imdb_name_basics inb
     JOIN imdb.imdb_title_principals itp ON inb.nconst::text = itp.nconst::text
     JOIN imdb.imdb_title_akas tae ON tae.titleid::text = itp.tconst::text
  WHERE ('director'::text = ANY (inb.primaryprofession::text[])) AND COALESCE(itp.category, itp.job)::text = 'director'::text AND upper(tae.language::text) = 'ES'::text;

ALTER TABLE lugus."imdbDirectorFilm"
    OWNER TO lugus_admin;

GRANT ALL ON TABLE lugus."imdbDirectorFilm" TO lugus_admin;
GRANT SELECT ON TABLE lugus."imdbDirectorFilm" TO lugus_readonly;
GRANT SELECT ON TABLE lugus."imdbDirectorFilm" TO lugus_role;

-- View: lugus.imdbDirectors

-- DROP VIEW lugus."imdbDirectors";

CREATE OR REPLACE VIEW lugus."imdbDirectors"
 AS
 SELECT nconst,
    primaryname,
    birthyear,
    deathyear,
    primaryprofession,
    knownfortitles
   FROM imdb.imdb_name_basics
  WHERE 'director'::text = ANY (primaryprofession::text[]);

ALTER TABLE lugus."imdbDirectors"
    OWNER TO lugus_admin;

GRANT SELECT ON TABLE lugus."imdbDirectors" TO lugus_readonly;
GRANT SELECT ON TABLE lugus."imdbDirectors" TO lugus_role;
GRANT ALL ON TABLE lugus."imdbDirectors" TO postgres;
-- PROCEDURE: lugus.insertar_datos_personales(integer, character varying)

-- DROP PROCEDURE IF EXISTS lugus.insertar_datos_personales(integer, character varying);

CREATE OR REPLACE PROCEDURE lugus.insertar_datos_personales(
	IN p_id integer,
	IN p_imdb character varying)
LANGUAGE 'plpgsql'
AS $BODY$
BEGIN
	delete from lugus.PELICULAS_PERSONAL where pelicula_id = p_id;
	delete from lugus.peliculas_otros where pelicula_id = p_id;
	
	INSERT INTO lugus.peliculas_otros(
	 pelicula_id, idmb_id)
	VALUES ( p_id, p_imdb);

INSERT INTO
	LUGUS.PELICULAS_PERSONAL (
		PELICULA_ID,
		PERSONA_ID,
		PERSONAJE,
		ORDEN,
		TRABAJO
	)
SELECT
	PELICULA_ID,
	PER.ID,
	ITP.characters,
	itp.ordering,
	coalesce (itp.category, itp.job)
FROM
	LUGUS.PELICULAS_OTROS PO
	JOIN IMDB.IMDB_TITLE_PRINCIPALS ITP ON (PO.IDMB_ID = ITP.TCONST)
	JOIN LUGUS.PERSONAS PER ON (PER.NCONST = ITP.NCONST)
	where PELICULA_ID not in (select PELICULA_ID FROM LUGUS.PELICULAS_PERSONAL );

  --  COMMIT;            
EXCEPTION
    WHEN OTHERS THEN
      
        ROLLBACK;
        RAISE;           
END;
$BODY$;
ALTER PROCEDURE lugus.insertar_datos_personales(integer, character varying)
    OWNER TO lugus_admin;

GRANT EXECUTE ON PROCEDURE lugus.insertar_datos_personales(integer, character varying) TO lugus_admin;

GRANT EXECUTE ON PROCEDURE lugus.insertar_datos_personales(integer, character varying) TO lugus_role;

REVOKE ALL ON PROCEDURE lugus.insertar_datos_personales(integer, character varying) FROM PUBLIC;

