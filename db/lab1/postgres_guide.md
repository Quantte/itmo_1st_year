# PostgreSQL: Подробный гайд для защиты Лаб. работы №1

---

## Содержание

1. [Что такое PostgreSQL и как с ним работать](#1-что-такое-postgresql-и-как-с-ним-работать)
2. [Схемы и пространства имён](#2-схемы-и-пространства-имён)
3. [Типы данных PostgreSQL](#3-типы-данных-postgresql)
4. [DDL — создание объектов](#4-ddl--создание-объектов)
5. [Ограничения целостности](#5-ограничения-целостности)
6. [DML — работа с данными](#6-dml--работа-с-данными)
7. [SELECT и JOIN — выборки](#7-select-и-join--выборки)
8. [Транзакции](#8-транзакции)
9. [Индексы](#9-индексы)
10. [Архитектура ANSI-SPARC в контексте PostgreSQL](#10-архитектура-ansi-sparc-в-контексте-postgresql)
11. [Модель ER — теория с примерами из лабы](#11-модель-er--теория-с-примерами-из-лабы)
12. [Частые вопросы на защите с ответами](#12-частые-вопросы-на-защите-с-ответами)

---

## 1. Что такое PostgreSQL и как с ним работать

### Что такое СУБД

**СУБД (Система Управления Базами Данных)** — программное обеспечение для хранения, организации и управления данными. Обеспечивает:
- хранение данных на диске
- доступ к данным через SQL
- поддержку транзакций (ACID)
- управление параллельным доступом
- ограничения целостности

**PostgreSQL** — объектно-реляционная СУБД с открытым исходным кодом. Поддерживает стандарт SQL, расширяемые типы, транзакции ACID, MVCC.

### Подключение

```bash
# Подключиться к базе studs (наш случай)
psql -h pg -d studs

# Общий синтаксис
psql -h <хост> -p <порт> -U <пользователь> -d <база>

# Выполнить файл
psql -h pg -d studs -f schema.sql
```

### Мета-команды psql (не SQL, работают только в терминале psql)

```
\l          -- список баз данных
\dn         -- список схем
\dt         -- список таблиц в текущей схеме
\dt s501650.*  -- таблицы в схеме s501650
\d table_name  -- описание таблицы (столбцы, типы, ограничения)
\dT         -- список типов (в т.ч. ENUM)
\c dbname   -- переключиться на другую базу
\q          -- выйти
\i file.sql -- выполнить SQL файл
\timing     -- включить показ времени выполнения запросов
```

---

## 2. Схемы и пространства имён

### Что такое схема

**Схема (schema)** — пространство имён внутри базы данных. Аналог папки: в разных схемах могут быть таблицы с одинаковыми именами.

```
База данных studs
├── схема public      (общая)
├── схема s501650     (твоя, для лабы)
├── схема s501651     (другого студента)
└── ...
```

### Зачем нужна схема

- Изоляция объектов разных пользователей
- Организация логических групп таблиц
- Управление правами доступа на уровне схемы

### Работа со схемой

```sql
-- Создать схему (уже создана для тебя)
CREATE SCHEMA s501650;

-- Указать схему явно при обращении
SELECT * FROM s501650.dinosaur;

-- Установить search_path — тогда можно писать просто имя таблицы
SET search_path TO s501650;
SELECT * FROM dinosaur;   -- то же самое

-- Посмотреть текущий search_path
SHOW search_path;
```

**Важно:** `SET search_path` работает только в текущей сессии. Чтобы не забывать — ставь в начале каждого SQL-файла.

---

## 3. Типы данных PostgreSQL

### Числовые типы

| Тип | Размер | Диапазон | Когда использовать |
|-----|--------|----------|--------------------|
| `SMALLINT` | 2 байта | −32 768..32 767 | Маленькие целые (возраст, счётчик) |
| `INTEGER` / `INT` | 4 байта | −2.1B..2.1B | Обычные целые, FK, id |
| `BIGINT` | 8 байт | ±9.2×10¹⁸ | Очень большие числа |
| `SERIAL` | 4 байта | 1..2.1B | Автоинкремент (id) — синтаксический сахар над sequence |
| `BIGSERIAL` | 8 байт | 1..9.2×10¹⁸ | Автоинкремент для больших таблиц |
| `NUMERIC(p, s)` | переменный | точное | Деньги, физ. величины (точный, без потерь) |
| `REAL` / `FLOAT4` | 4 байта | ~6 знаков | Приближённое (не для денег!) |
| `DOUBLE PRECISION` / `FLOAT8` | 8 байт | ~15 знаков | Приближённое |

**Разница NUMERIC vs FLOAT:**
```sql
-- FLOAT неточный:
SELECT 0.1 + 0.2::float;   -- 0.30000000000000004

-- NUMERIC точный:
SELECT 0.1::numeric + 0.2::numeric;   -- 0.3
```
Для весов, высот, координат — используй `NUMERIC`. Для научных вычислений — `FLOAT`.

**NUMERIC(p, s):**
- `p` (precision) — всего цифр
- `s` (scale) — цифр после запятой
- `NUMERIC(8, 2)` → до 999999.99

**SERIAL** — это не настоящий тип, а сокращение:
```sql
-- Это:
id SERIAL PRIMARY KEY
-- Эквивалентно:
id INTEGER NOT NULL DEFAULT nextval('table_id_seq') PRIMARY KEY
-- (PostgreSQL сам создаёт sequence)
```

### Строковые типы

| Тип | Описание |
|-----|----------|
| `CHAR(n)` | Фиксированная длина, дополняется пробелами |
| `VARCHAR(n)` | До n символов, хранит реально |
| `TEXT` | Неограниченная длина |

**В PostgreSQL `VARCHAR` и `TEXT` одинаково эффективны** — под капотом одно и то же. Используй `VARCHAR(n)` когда хочешь задокументировать ограничение длины (и оно будет проверяться), `TEXT` — когда длина не важна.

### Дата и время

| Тип | Описание |
|-----|----------|
| `DATE` | Только дата (2026-04-15) |
| `TIME` | Только время (14:23:00) |
| `TIMESTAMP` | Дата + время без часового пояса |
| `TIMESTAMP WITH TIME ZONE` / `TIMESTAMPTZ` | Дата + время с часовым поясом |
| `INTERVAL` | Промежуток времени ('3 hours', '2 days') |

**Почему `TIMESTAMP WITH TIME ZONE`?**  
При хранении PostgreSQL переводит время в UTC, при чтении — обратно в timezone сессии. Безопаснее при работе с разными часовыми поясами.

```sql
-- Примеры
SELECT NOW();                              -- текущее время с tz
SELECT CURRENT_DATE;                       -- сегодня
SELECT '2026-04-15'::date + INTERVAL '7 days';  -- через неделю
```

### Булевый тип

```sql
col BOOLEAN   -- TRUE / FALSE / NULL
-- Принимает: true, false, 'yes', 'no', 'on', 'off', 1, 0
```

### Пользовательские типы — ENUM

```sql
-- Создание
CREATE TYPE encounter_outcome AS ENUM ('escaped', 'injured', 'fatal', 'standoff', 'ongoing');

-- Использование
outcome encounter_outcome NOT NULL DEFAULT 'ongoing'

-- Посмотреть значения
SELECT enumlabel FROM pg_enum
JOIN pg_type ON pg_type.oid = pg_enum.enumtypid
WHERE pg_type.typname = 'encounter_outcome';

-- Добавить значение (нельзя удалить без пересоздания!)
ALTER TYPE encounter_outcome ADD VALUE 'unknown' AFTER 'ongoing';
```

**Преимущества ENUM:** ограничивает допустимые значения на уровне СУБД, понятен в коде, компактно хранится.

**Недостаток:** сложно изменить набор значений.

### Другие полезные типы

| Тип | Описание |
|-----|----------|
| `UUID` | UUID (универсально уникальный id) |
| `JSON` / `JSONB` | JSON-документы (JSONB — бинарный, с индексами) |
| `ARRAY` | Массив любого типа (`INTEGER[]`) |
| `BYTEA` | Бинарные данные |

---

## 4. DDL — создание объектов

### CREATE TABLE

```sql
CREATE TABLE table_name (
    -- определения столбцов
    col_name  data_type  [column_constraints],
    ...
    -- ограничения на уровне таблицы
    [table_constraints]
);
```

**Полный пример — наша таблица encounter:**
```sql
CREATE TABLE encounter (
    id           SERIAL                   PRIMARY KEY,
    dinosaur_id  INTEGER                  NOT NULL
                 REFERENCES dinosaur(id)  ON DELETE RESTRICT,
    person_id    INTEGER                  NOT NULL
                 REFERENCES person(id)    ON DELETE RESTRICT,
    location_id  INTEGER                  NOT NULL
                 REFERENCES location(id)  ON DELETE RESTRICT,
    encounter_ts TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    outcome      encounter_outcome        NOT NULL DEFAULT 'ongoing',
    notes        TEXT
);
```

### ALTER TABLE

```sql
-- Добавить столбец
ALTER TABLE dinosaur ADD COLUMN weight_kg NUMERIC(8, 2);

-- Удалить столбец
ALTER TABLE dinosaur DROP COLUMN weight_kg;

-- Изменить тип
ALTER TABLE dinosaur ALTER COLUMN estimated_age TYPE INTEGER;

-- Переименовать столбец
ALTER TABLE dinosaur RENAME COLUMN nickname TO individual_name;

-- Добавить ограничение
ALTER TABLE dinosaur ADD CONSTRAINT chk_age CHECK (estimated_age >= 0);

-- Удалить ограничение
ALTER TABLE dinosaur DROP CONSTRAINT chk_age;

-- Добавить NOT NULL
ALTER TABLE dinosaur ALTER COLUMN nickname SET NOT NULL;

-- Убрать NOT NULL
ALTER TABLE dinosaur ALTER COLUMN nickname DROP NOT NULL;
```

### DROP

```sql
DROP TABLE dinosaur;                -- ошибка если есть FK из других таблиц
DROP TABLE dinosaur CASCADE;        -- удалить и все зависимые объекты
DROP TABLE IF EXISTS dinosaur;      -- без ошибки если не существует
DROP TABLE IF EXISTS dinosaur CASCADE;

-- Удалить ENUM тип
DROP TYPE encounter_outcome;
DROP TYPE encounter_outcome CASCADE;  -- удалит и столбцы, использующие его
```

### CREATE INDEX

```sql
-- Обычный индекс
CREATE INDEX idx_dinosaur_species ON dinosaur(species_id);

-- Уникальный индекс (аналог UNIQUE constraint)
CREATE UNIQUE INDEX idx_species_latin ON dinosaur_species(latin_name);

-- Удалить
DROP INDEX idx_dinosaur_species;
```

### Порядок создания объектов (важно!)

При создании нужно соблюдать порядок зависимостей:
1. ENUM-типы
2. Таблицы без FK (стержневые: `dinosaur_species`, `person`, `location`)
3. Таблицы с FK на уже созданные (`dinosaur`)
4. Ассоциации (`encounter`)
5. Характеристики (`encounter_action`)

При удалении — в обратном порядке (или CASCADE).

---

## 5. Ограничения целостности

### PRIMARY KEY

```sql
-- Вариант 1: inline
id SERIAL PRIMARY KEY

-- Вариант 2: отдельно (нужен для составного PK)
CONSTRAINT pk_encounter_action PRIMARY KEY (encounter_id, seq_number)
```

- Автоматически создаёт уникальный индекс
- Не допускает NULL
- В таблице может быть только один PK

**Составной PK** — когда уникальность задаётся комбинацией столбцов:
```sql
PRIMARY KEY (encounter_id, seq_number)
-- Пара (encounter_id=1, seq_number=1) уникальна
-- encounter_id может повторяться отдельно
```

### FOREIGN KEY

```sql
-- Inline
dinosaur_id INTEGER NOT NULL REFERENCES dinosaur(id)

-- С явным именем и поведением
CONSTRAINT fk_encounter_dinosaur
    FOREIGN KEY (dinosaur_id)
    REFERENCES dinosaur(id)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
```

**ON DELETE / ON UPDATE варианты:**

| Вариант | Что происходит при удалении/изменении родителя |
|---------|------------------------------------------------|
| `RESTRICT` | Запрещает операцию (ошибка) |
| `NO ACTION` | То же что RESTRICT, но проверка отложена до конца транзакции |
| `CASCADE` | Автоматически удаляет/обновляет дочерние записи |
| `SET NULL` | Ставит NULL в FK (столбец должен допускать NULL) |
| `SET DEFAULT` | Ставит значение по умолчанию |

**Выбор в нашей лабе:**
- `encounter` → `dinosaur/person/location`: `RESTRICT` — не дать удалить динозавра, если есть история столкновений
- `encounter_action` → `encounter`: `CASCADE` — действия без столкновения бессмысленны, удаляем вместе

### UNIQUE

```sql
-- На один столбец
latin_name VARCHAR(150) NOT NULL UNIQUE

-- На комбинацию столбцов (нет двух действий с одним seq_number в одном encounter)
UNIQUE (encounter_id, seq_number)

-- С именем
CONSTRAINT uq_species_latin UNIQUE (latin_name)
```

### NOT NULL

```sql
name VARCHAR(100) NOT NULL   -- значение обязательно
notes TEXT                   -- NULL допускается (необязательное поле)
```

### CHECK

```sql
-- На один столбец
age SMALLINT CHECK (age >= 0 AND age <= 150)
avg_height_m NUMERIC(5,2) CHECK (avg_height_m > 0)
latitude NUMERIC(9,6) CHECK (latitude BETWEEN -90 AND 90)

-- На уровне таблицы (с именем)
CONSTRAINT chk_coordinates CHECK (
    (latitude IS NULL AND longitude IS NULL) OR
    (latitude IS NOT NULL AND longitude IS NOT NULL)
)
```

### DEFAULT

```sql
outcome encounter_outcome NOT NULL DEFAULT 'ongoing'
encounter_ts TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
gender gender_type NOT NULL DEFAULT 'unknown'
```

### Просмотр ограничений

```sql
-- Все ограничения таблицы
\d encounter

-- Через системный каталог
SELECT conname, contype, pg_get_constraintdef(oid)
FROM pg_constraint
WHERE conrelid = 'encounter'::regclass;
```

---

## 6. DML — работа с данными

### INSERT

```sql
-- Одна строка
INSERT INTO person (first_name, last_name, age, occupation)
VALUES ('Реджис', NULL, 35, 'путешественник');

-- Несколько строк
INSERT INTO dinosaur_species (name, latin_name, avg_height_m, avg_weight_kg, diet)
VALUES
    ('Тираннозавр', 'Tyrannosaurus rex', 6.10, 8000.00, 'carnivore'),
    ('Велоцираптор', 'Velociraptor mongoliensis', 0.50, 15.00, 'carnivore');

-- Получить id вставленной строки
INSERT INTO person (first_name) VALUES ('Анна') RETURNING id;

-- Вставить только если нет конфликта (upsert)
INSERT INTO dinosaur_species (name, latin_name, avg_height_m, avg_weight_kg, diet)
VALUES ('Тираннозавр', 'Tyrannosaurus rex', 6.10, 8000.00, 'carnivore')
ON CONFLICT (latin_name) DO NOTHING;
```

### UPDATE

```sql
-- Обновить конкретную строку
UPDATE encounter SET outcome = 'injured' WHERE id = 1;

-- Обновить несколько столбцов
UPDATE person
SET age = 36, occupation = 'исследователь'
WHERE first_name = 'Реджис';

-- Обновить на основе данных из другой таблицы
UPDATE dinosaur d
SET estimated_age = 4
FROM dinosaur_species s
WHERE d.species_id = s.id AND s.latin_name = 'Tyrannosaurus rex';

-- ВАЖНО: UPDATE без WHERE обновит все строки!
UPDATE person SET occupation = 'неизвестно';   -- все записи изменятся
```

### DELETE

```sql
-- Удалить конкретные строки
DELETE FROM encounter_action WHERE encounter_id = 1;

-- Удалить по условию
DELETE FROM encounter WHERE outcome = 'ongoing';

-- Удалить с возвратом удалённых строк
DELETE FROM encounter WHERE id = 5 RETURNING *;

-- ВАЖНО: DELETE без WHERE удалит все строки (таблица останется)
DELETE FROM encounter;

-- TRUNCATE — быстрее для очистки всей таблицы (нельзя откатить в большинстве случаев)
TRUNCATE TABLE encounter CASCADE;
```

---

## 7. SELECT и JOIN — выборки

### Базовый SELECT

```sql
SELECT *                           FROM dinosaur;
SELECT id, nickname, age_category  FROM dinosaur;
SELECT id AS dinosaur_id, nickname AS name FROM dinosaur;   -- псевдонимы
SELECT DISTINCT age_category       FROM dinosaur;           -- уникальные значения
```

### WHERE — фильтрация

```sql
WHERE age_category = 'adolescent'
WHERE estimated_age > 5
WHERE estimated_age BETWEEN 3 AND 10
WHERE nickname IS NULL
WHERE nickname IS NOT NULL
WHERE nickname LIKE 'Р%'           -- начинается с Р
WHERE nickname ILIKE 'р%'          -- то же, без учёта регистра
WHERE age_category IN ('juvenile', 'adolescent')
WHERE age_category NOT IN ('elder')
WHERE estimated_age > 3 AND age_category = 'adolescent'
WHERE estimated_age > 10 OR age_category = 'elder'
WHERE NOT (age_category = 'elder')
```

### ORDER BY

```sql
SELECT * FROM dinosaur ORDER BY estimated_age;           -- по возрастанию
SELECT * FROM dinosaur ORDER BY estimated_age DESC;      -- по убыванию
SELECT * FROM dinosaur ORDER BY species_id, estimated_age DESC;  -- несколько полей
```

### LIMIT / OFFSET

```sql
SELECT * FROM encounter ORDER BY encounter_ts LIMIT 10;        -- первые 10
SELECT * FROM encounter ORDER BY encounter_ts LIMIT 10 OFFSET 20;  -- страница 3
```

### Агрегатные функции

```sql
COUNT(*)              -- количество строк
COUNT(col)            -- количество строк где col IS NOT NULL
SUM(col)              -- сумма
AVG(col)              -- среднее
MIN(col), MAX(col)    -- минимум, максимум

-- Примеры
SELECT COUNT(*) FROM encounter;
SELECT AVG(estimated_age) FROM dinosaur;
SELECT species_id, COUNT(*) AS cnt FROM dinosaur GROUP BY species_id;
SELECT species_id, COUNT(*) AS cnt FROM dinosaur
GROUP BY species_id
HAVING COUNT(*) > 1;   -- только виды с >1 особью
```

### JOIN — соединения таблиц

```
Таблица A    Таблица B
┌───┐        ┌───┐
│ 1 │        │ 1 │ ← совпадает
│ 2 │        │ 3 │ ← совпадает
│ 4 │        │ 5 │
└───┘        └───┘
```

| Тип JOIN | Что возвращает |
|----------|----------------|
| `INNER JOIN` / `JOIN` | Только совпадающие строки |
| `LEFT JOIN` | Все из левой + совпадающие из правой (NULL если нет) |
| `RIGHT JOIN` | Все из правой + совпадающие из левой |
| `FULL OUTER JOIN` | Все строки из обеих таблиц |
| `CROSS JOIN` | Декартово произведение (все со всеми) |

```sql
-- INNER JOIN: динозавры с их видом (только у кого есть вид — у всех)
SELECT d.nickname, s.name AS species
FROM dinosaur d
INNER JOIN dinosaur_species s ON d.species_id = s.id;

-- LEFT JOIN: все виды, даже без особей
SELECT s.name, d.nickname
FROM dinosaur_species s
LEFT JOIN dinosaur d ON d.species_id = s.id;
-- Виды без особей: nickname будет NULL

-- Цепочка JOIN — полный запрос по нашей лабе:
SELECT
    s.name           AS species,
    d.age_category,
    p.first_name     AS person,
    l.name           AS location,
    e.outcome,
    e.encounter_ts
FROM encounter e
JOIN dinosaur d         ON e.dinosaur_id = d.id
JOIN dinosaur_species s ON d.species_id  = s.id
JOIN person p           ON e.person_id   = p.id
JOIN location l         ON e.location_id = l.id
ORDER BY e.encounter_ts;
```

### Подзапросы (subqueries)

```sql
-- В WHERE
SELECT * FROM dinosaur
WHERE species_id = (SELECT id FROM dinosaur_species WHERE latin_name = 'Tyrannosaurus rex');

-- В FROM (derived table)
SELECT avg_age FROM (
    SELECT AVG(estimated_age) AS avg_age FROM dinosaur
) AS stats;

-- EXISTS
SELECT * FROM dinosaur_species s
WHERE EXISTS (SELECT 1 FROM dinosaur d WHERE d.species_id = s.id);
-- Только виды, у которых есть хотя бы одна особь
```

---

## 8. Транзакции

### Что такое транзакция

Транзакция — последовательность операций, выполняемых как единое целое. Либо все операции выполняются успешно, либо ни одна.

### ACID свойства

| Свойство | Название | Смысл |
|----------|----------|-------|
| **A** | Atomicity (Атомарность) | Транзакция — всё или ничего |
| **C** | Consistency (Согласованность) | После транзакции данные согласованы (ограничения соблюдены) |
| **I** | Isolation (Изолированность) | Транзакции не мешают друг другу |
| **D** | Durability (Долговечность) | После COMMIT данные сохранены даже при сбое |

### Синтаксис

```sql
BEGIN;                  -- начать транзакцию (или START TRANSACTION)

INSERT INTO encounter (dinosaur_id, person_id, location_id, outcome)
VALUES (1, 1, 1, 'ongoing');

UPDATE dinosaur SET estimated_age = 4 WHERE id = 1;

COMMIT;                 -- зафиксировать изменения

-- или

ROLLBACK;               -- откатить все изменения в транзакции
```

### Savepoints

```sql
BEGIN;
INSERT INTO person (first_name) VALUES ('Тест1');

SAVEPOINT sp1;
INSERT INTO person (first_name) VALUES ('Тест2');

ROLLBACK TO SAVEPOINT sp1;  -- откат только до точки (Тест2 отменён, Тест1 остался)

COMMIT;   -- фиксируем (только Тест1)
```

**Важно:** В PostgreSQL DDL (CREATE, DROP, ALTER) тоже транзакционны! Можно откатить создание таблицы.

```sql
BEGIN;
CREATE TABLE test (id INTEGER);
ROLLBACK;  -- таблица не создана
```

---

## 9. Индексы

### Зачем нужны

Индекс — отдельная структура данных (B-Tree по умолчанию), ускоряющая поиск строк. Как содержание книги — вместо чтения всей таблицы (seq scan) PostgreSQL идёт напрямую к нужным строкам (index scan).

**Цена:** место на диске + замедление INSERT/UPDATE/DELETE (нужно обновлять индекс).

### Когда создаются автоматически

- PRIMARY KEY → уникальный B-Tree индекс
- UNIQUE → уникальный B-Tree индекс

### Ручное создание

```sql
-- Обычный индекс (ускоряет поиск по FK)
CREATE INDEX idx_dinosaur_species_id ON dinosaur(species_id);
CREATE INDEX idx_encounter_dinosaur  ON encounter(dinosaur_id);
CREATE INDEX idx_encounter_person    ON encounter(person_id);

-- Составной
CREATE INDEX idx_encounter_composite ON encounter(dinosaur_id, person_id);

-- Частичный (только по нужным строкам)
CREATE INDEX idx_active_encounters ON encounter(outcome)
WHERE outcome = 'ongoing';
```

### EXPLAIN — план выполнения запроса

```sql
EXPLAIN SELECT * FROM encounter WHERE dinosaur_id = 1;
-- Покажет Seq Scan или Index Scan

EXPLAIN ANALYZE SELECT * FROM encounter WHERE dinosaur_id = 1;
-- То же + реальное время выполнения
```

---

## 10. Архитектура ANSI-SPARC в контексте PostgreSQL

Стандарт описывает три уровня абстракции для любой СУБД.

```
┌─────────────────────────────────────────────┐
│           ВНЕШНИЙ УРОВЕНЬ                   │
│  (Views, пользовательские представления)    │
│  CREATE VIEW encounters_today AS ...        │
└──────────────────┬──────────────────────────┘
                   │ внешнее/концептуальное отображение
┌──────────────────▼──────────────────────────┐
│         КОНЦЕПТУАЛЬНЫЙ УРОВЕНЬ              │
│  (Таблицы, связи, ограничения)              │
│  encounter, dinosaur, person, ...           │
└──────────────────┬──────────────────────────┘
                   │ концептуальное/внутреннее отображение
┌──────────────────▼──────────────────────────┐
│           ВНУТРЕННИЙ УРОВЕНЬ                │
│  (Файлы, страницы, индексы, WAL)            │
│  heap files, B-Tree indexes, TOAST, ...     │
└─────────────────────────────────────────────┘
```

**Пример применения в нашей лабе:**

```sql
-- Внешний уровень — представление для аналитика
CREATE VIEW v_encounters AS
SELECT
    s.name AS species, d.age_category,
    p.first_name AS person, l.name AS location,
    e.outcome, e.encounter_ts
FROM encounter e
JOIN dinosaur d ON e.dinosaur_id = d.id
JOIN dinosaur_species s ON d.species_id = s.id
JOIN person p ON e.person_id = p.id
JOIN location l ON e.location_id = l.id;

-- Аналитик видит только это представление,
-- не зная о внутренней структуре таблиц
SELECT * FROM v_encounters WHERE outcome = 'injured';
```

**Логическая независимость:** можно добавить столбец `notes2` в `encounter`, представление `v_encounters` не сломается — аналитик ничего не заметит.

**Физическая независимость:** можно добавить индекс на `encounter(outcome)`, запросы ускорятся, но SQL код менять не нужно.

---

## 11. Модель ER — теория с примерами из лабы

### Сущности

```
Стержневая — независимая:
  dinosaur_species  →  существует сама по себе
  dinosaur          →  существует без столкновений
  person            →  существует без столкновений
  location          →  существует без столкновений

Ассоциация — связывает M:N:
  encounter         →  реализует связь dinosaur M:N person
                        без dinosaur и person смысла нет
  
Характеристика — уточняет другую:
  encounter_action  →  описывает encounter
                        без encounter существовать не может
```

### Как M:N превращается в реляционную модель

**Проблема:** реляционная таблица не может хранить M:N напрямую.

**Решение:** таблица-ассоциация (junction table).

```
dinosaur (1) ──< encounter >── (1) person
                     |
                 (1) location
```

```sql
-- Вместо:
-- dinosaur.person_ids = {1, 2, 3}   -- так нельзя в реляционной модели

-- Делаем:
encounter:
  id=1, dinosaur_id=1, person_id=1, location_id=1
  id=2, dinosaur_id=1, person_id=4, location_id=4   -- тот же динозавр, другой человек
  id=3, dinosaur_id=2, person_id=2, location_id=2   -- другой динозавр
```

### Нормальные формы (кратко)

| Форма | Требование |
|-------|------------|
| **1НФ** | Все атрибуты атомарны (нет множественных значений в ячейке) |
| **2НФ** | 1НФ + каждый неключевой атрибут зависит от всего PK (актуально при составном PK) |
| **3НФ** | 2НФ + нет транзитивных зависимостей (неключ→неключ→PK) |

Наша схема находится в **3НФ**: все атрибуты зависят только от PK своей таблицы.

---

## 12. Частые вопросы на защите с ответами

**Q: Объясни, что такое первичный ключ.**
> Атрибут (или набор атрибутов), однозначно идентифицирующий каждую строку таблицы. Должен быть NOT NULL и уникальным. В наших таблицах — `SERIAL id` (суррогатный ключ).

**Q: Чем суррогатный ключ отличается от естественного?**
> Суррогатный — искусственный (SERIAL), не имеет смысла в предметной области. Естественный — из предметной области (latin_name вида). Суррогатный надёжнее: не меняется, гарантированно уникален.

**Q: Почему `encounter` — ассоциация?**
> Потому что она реализует связь M:N между `dinosaur` и `person`. Один динозавр может напасть на нескольких людей, один человек — столкнуться с несколькими динозаврами. Таблица `encounter` содержит FK на обе стороны и на `location`, без них запись теряет смысл.

**Q: Чем RESTRICT отличается от CASCADE?**
> `RESTRICT` — при попытке удалить родительскую запись выдаёт ошибку, если есть дочерние. `CASCADE` — автоматически удаляет дочерние вместе с родительской. В нашей схеме: удаление динозавра при наличии столкновений запрещено (RESTRICT), удаление столкновения каскадно удаляет все его действия (CASCADE).

**Q: Что такое ссылочная целостность?**
> Гарантия, что значение FK в дочерней таблице всегда ссылается на существующую строку в родительской таблице (или равно NULL). PostgreSQL обеспечивает это через FOREIGN KEY constraints.

**Q: Зачем CHECK(latitude BETWEEN -90 AND 90)?**
> Это ограничение доменной целостности. Широта физически не может быть меньше −90 или больше 90. Без CHECK можно было бы вставить бессмысленное значение 9999.

**Q: Что такое ENUM и когда его использовать?**
> Пользовательский тип с фиксированным набором допустимых значений. Используется когда множество значений заранее известно и редко меняется (статусы, категории). Проще `VARCHAR` + CHECK, понятнее в схеме. Минус — сложно изменить набор значений.

**Q: Что такое транзакция? Что такое ACID?**
> Транзакция — последовательность операций, выполняемых атомарно. ACID: Atomicity (всё или ничего), Consistency (данные остаются согласованными), Isolation (транзакции изолированы), Durability (после COMMIT данные сохранены).

**Q: Чем VIEW отличается от таблицы?**
> VIEW — виртуальная таблица, хранящая SQL-запрос, а не данные. При обращении к VIEW PostgreSQL выполняет сохранённый запрос. Данных на диске не занимает (в отличие от materialized view).

**Q: Зачем нужен search_path?**
> PostgreSQL ищет объекты (таблицы, типы) в схемах по порядку из `search_path`. Без установки пришлось бы писать `s501650.dinosaur` вместо просто `dinosaur`.

**Q: В чём разница DDL и DML?**
> DDL (Data Definition Language) — определяет структуру: CREATE, ALTER, DROP. DML (Data Manipulation Language) — работает с данными: INSERT, UPDATE, DELETE, SELECT.

**Q: Почему не использовать FLOAT для координат?**
> FLOAT — приближённое вещественное число с ошибками округления. NUMERIC — точное. Для координат важна точность, иначе точка может сдвинуться на метры.

**Q: Что произойдёт если вставить NULL в поле NOT NULL?**
> PostgreSQL выдаст ошибку: `ERROR: null value in column "X" of relation "Y" violates not-null constraint`.

**Q: Что такое UNIQUE(encounter_id, seq_number)?**
> Составное ограничение уникальности: пара значений должна быть уникальной в таблице. `encounter_id=1, seq_number=1` и `encounter_id=2, seq_number=1` — допустимо. `encounter_id=1, seq_number=1` дважды — ошибка.

---

## Полезные запросы для демонстрации на защите

```sql
SET search_path TO s501650;

-- 1. Посмотреть структуру таблицы
\d encounter

-- 2. Все столкновения с деталями
SELECT s.name, d.age_category, p.first_name, l.name, e.outcome
FROM encounter e
JOIN dinosaur d ON e.dinosaur_id = d.id
JOIN dinosaur_species s ON d.species_id = s.id
JOIN person p ON e.person_id = p.id
JOIN location l ON e.location_id = l.id;

-- 3. Хронология конкретного столкновения
SELECT actor, action_desc, seq_number
FROM encounter_action
WHERE encounter_id = 1
ORDER BY seq_number;

-- 4. Сколько столкновений у каждого вида
SELECT s.name, COUNT(e.id) AS encounters
FROM dinosaur_species s
LEFT JOIN dinosaur d ON d.species_id = s.id
LEFT JOIN encounter e ON e.dinosaur_id = d.id
GROUP BY s.name
ORDER BY encounters DESC;

-- 5. Исходы столкновений
SELECT outcome, COUNT(*) FROM encounter GROUP BY outcome;

-- 6. Проверить FK: нельзя удалить динозавра у которого есть столкновения
DELETE FROM dinosaur WHERE id = 1;
-- ERROR: update or delete on table "dinosaur" violates foreign key constraint

-- 7. Проверить CHECK: нельзя вставить невалидную широту
INSERT INTO location (name, loc_type, latitude, longitude)
VALUES ('Тест', 'path', 999, 0);
-- ERROR: new row for relation "location" violates check constraint
```
