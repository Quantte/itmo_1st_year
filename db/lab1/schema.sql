-- Lab 1: Dinosaur-Human Encounter Database
-- Schema: s501650
-- psql -h pg -d studs -f schema.sql

SET search_path TO s501650;

-- ============================================================
-- ENUM types
-- ============================================================

CREATE TYPE predator_type AS ENUM ('herbivore', 'carnivore', 'omnivore');
CREATE TYPE age_category   AS ENUM ('juvenile', 'adolescent', 'adult', 'elder');
CREATE TYPE gender_type    AS ENUM ('male', 'female', 'unknown');
CREATE TYPE location_type  AS ENUM ('path', 'clearing', 'forest', 'settlement', 'riverbank', 'cave');
CREATE TYPE encounter_outcome AS ENUM ('escaped', 'injured', 'fatal', 'standoff', 'ongoing');
CREATE TYPE actor_type     AS ENUM ('dinosaur', 'person');

-- ============================================================
-- 1. dinosaur_species  (стержневая)
--    Справочник видов динозавров
-- ============================================================

CREATE TABLE dinosaur_species (
    id              SERIAL          PRIMARY KEY,
    name            VARCHAR(100)    NOT NULL,
    latin_name      VARCHAR(150)    NOT NULL UNIQUE,
    avg_height_m    NUMERIC(5, 2)   NOT NULL CHECK (avg_height_m > 0),
    avg_weight_kg   NUMERIC(8, 2)   NOT NULL CHECK (avg_weight_kg > 0),
    diet            predator_type   NOT NULL
);

-- ============================================================
-- 2. dinosaur  (стержневая)
--    Конкретная особь динозавра
-- ============================================================

CREATE TABLE dinosaur (
    id              SERIAL          PRIMARY KEY,
    species_id      INTEGER         NOT NULL REFERENCES dinosaur_species(id) ON DELETE RESTRICT,
    nickname        VARCHAR(100),
    age_category    age_category    NOT NULL,
    gender          gender_type     NOT NULL DEFAULT 'unknown',
    estimated_age   SMALLINT        CHECK (estimated_age >= 0)
);

-- ============================================================
-- 3. person  (стержневая)
--    Человек, участвовавший в столкновении
-- ============================================================

CREATE TABLE person (
    id              SERIAL          PRIMARY KEY,
    first_name      VARCHAR(100)    NOT NULL,
    last_name       VARCHAR(100),
    age             SMALLINT        CHECK (age >= 0 AND age <= 150),
    occupation      VARCHAR(150)
);

-- ============================================================
-- 4. location  (стержневая)
--    Место, где произошло столкновение
-- ============================================================

CREATE TABLE location (
    id              SERIAL          PRIMARY KEY,
    name            VARCHAR(150)    NOT NULL,
    loc_type        location_type   NOT NULL,
    latitude        NUMERIC(9, 6)   CHECK (latitude  BETWEEN -90  AND  90),
    longitude       NUMERIC(9, 6)   CHECK (longitude BETWEEN -180 AND 180),
    description     TEXT
);

-- ============================================================
-- 5. encounter  (ассоциация: dinosaur M:N person, через location)
--    Столкновение конкретного динозавра с конкретным человеком
-- ============================================================

CREATE TABLE encounter (
    id              SERIAL                      PRIMARY KEY,
    dinosaur_id     INTEGER                     NOT NULL REFERENCES dinosaur(id)  ON DELETE RESTRICT,
    person_id       INTEGER                     NOT NULL REFERENCES person(id)    ON DELETE RESTRICT,
    location_id     INTEGER                     NOT NULL REFERENCES location(id)  ON DELETE RESTRICT,
    encounter_ts    TIMESTAMP WITH TIME ZONE    NOT NULL DEFAULT NOW(),
    outcome         encounter_outcome           NOT NULL DEFAULT 'ongoing',
    notes           TEXT
);

-- ============================================================
-- 6. encounter_action  (характеристика encounter)
--    Конкретное действие, совершённое в ходе столкновения
-- ============================================================

CREATE TABLE encounter_action (
    id              SERIAL          PRIMARY KEY,
    encounter_id    INTEGER         NOT NULL REFERENCES encounter(id) ON DELETE CASCADE,
    actor           actor_type      NOT NULL,
    action_desc     TEXT            NOT NULL,
    seq_number      SMALLINT        NOT NULL CHECK (seq_number >= 1),
    UNIQUE (encounter_id, seq_number)
);
