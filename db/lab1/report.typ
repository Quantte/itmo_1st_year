#set document(title: "Лабораторная работа №1 по дисциплине «Базы данных»")
#set page(
  paper: "a4",
  margin: (top: 20mm, bottom: 20mm, left: 30mm, right: 15mm),
  numbering: "1",
)
#set text(font: "New Computer Modern", size: 12pt, lang: "ru")
#set par(justify: true, leading: 1.2em)
#set heading(numbering: "1.")

// ─── Title page ───────────────────────────────────────────────
#align(center)[
  #text(size: 14pt, weight: "bold")[
    Санкт-Петербургский национальный исследовательский университет\
    информационных технологий, механики и оптики
  ]

  #v(1cm)
  #text(size: 12pt)[Факультет информационных технологий и программирования]

  #v(3cm)
  #text(size: 16pt, weight: "bold")[
    Лабораторная работа №1\
    по дисциплине «Базы данных»
  ]

  #v(0.5cm)
  #text(size: 14pt, weight: "bold")[
    Тема: Проектирование реляционной базы данных\
    на основе предметной области
  ]

  #v(3cm)
  #align(right)[
    #table(
      columns: (auto, auto),
      stroke: none,
      align: left,
      [*Выполнил:*], [студент гр. P3215],
      [],            [Гераcюто Фадей],
      [*Схема БД:*], [s501650],
      [*Проверил:*], [\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_],
    )
  ]

  #v(4cm)
  #text(size: 12pt)[Санкт-Петербург, 2026]
]

#pagebreak()

// ─── TOC ──────────────────────────────────────────────────────
#outline(title: "Содержание", indent: 1.5em)

#pagebreak()

// ═════════════════════════════════════════════════════════════
= Текст задания
// ═════════════════════════════════════════════════════════════

На основе предложенной предметной области (текста) необходимо:

+ Составить описание предметной области. Выделить сущности, их атрибуты и связи.
+ Составить инфологическую модель (ER-диаграмма).
+ Составить даталогическую модель с типами данных PostgreSQL.
+ Реализовать даталогическую модель в PostgreSQL с ограничениями целостности.
+ Заполнить таблицы тестовыми данными.

*Исходный текст предметной области:*

#block(
  fill: luma(240),
  inset: 10pt,
  radius: 4pt,
  [
    _Подросток зарычал и, наклонив голову, сбил Реджиса с ног. Реджис закричал и попытался встать, но тиранозавр снова налетел на него и, очевидно, приплюснул своей задней лапой, потому что Реджис уже не пробовал встать, а лишь орал на динозавра, сидя на тропинке, и махал руками, словно это могло напугать зверя. Юный динозавр, похоже, был озадачен поведением своей крошечной жертвы и звуками, которые она издавала. Зверь наклонил голову и с любопытством принюхивался, а Реджис заколотил кулаками по его морде._
  ]
)

// ═════════════════════════════════════════════════════════════
= Описание предметной области
// ═════════════════════════════════════════════════════════════

Предметная область описывает *столкновения динозавров с людьми* в некоем мире, где обе формы жизни сосуществуют. Центральным событием является нападение молодого тираннозавра (Tyrannosaurus rex) на человека по имени Реджис на лесной тропинке.

На основе текста предметная область обобщается до системы учёта и анализа столкновений динозавров с людьми, включающей:

- *Виды динозавров* — таксономическая принадлежность, средние физические параметры, тип питания.
- *Конкретные особи динозавров* — возрастная категория, пол, индивидуальные особенности.
- *Люди* — участники столкновений с личными данными и родом деятельности.
- *Места* — географические локации, где происходят события.
- *Столкновения* — зафиксированные инциденты между конкретным динозавром и конкретным человеком в конкретном месте.
- *Действия в столкновении* — хронологически упорядоченные действия участников в рамках одного столкновения.

// ═════════════════════════════════════════════════════════════
= Список сущностей и их классификация
// ═════════════════════════════════════════════════════════════

В данной предметной области выделены следующие сущности:

#table(
  columns: (2fr, 1.5fr, 3fr),
  stroke: 0.5pt,
  align: (left, center, left),
  fill: (_, row) => if row == 0 { luma(220) } else { white },
  [*Сущность*], [*Тип*], [*Пояснение*],

  [`dinosaur_species`\ (Вид динозавра)],
  [Стержневая],
  [Существует независимо. Описывает биологический вид.],

  [`dinosaur`\ (Динозавр)],
  [Стержневая],
  [Конкретная особь. Может существовать без столкновений.],

  [`person`\ (Человек)],
  [Стержневая],
  [Конкретный человек. Может существовать без столкновений.],

  [`location`\ (Место)],
  [Стержневая],
  [Географическая локация. Может существовать без столкновений.],

  [`encounter`\ (Столкновение)],
  [Ассоциация],
  [Связывает динозавра, человека и место. Реализует связь M:N между `dinosaur` и `person`.],

  [`encounter_action`\ (Действие)],
  [Характеристика],
  [Описывает конкретное действие в рамках столкновения. Существование зависит от `encounter`.],
)

// ═════════════════════════════════════════════════════════════
= Инфологическая модель
// ═════════════════════════════════════════════════════════════

ER-диаграмма отражает структуру предметной области. Ниже приведено текстовое описание в расширенном виде с указанием атрибутов и ключей.

#v(0.4cm)

*dinosaur_species* (#underline[id], name, latin_name#super[U], avg_height_m, avg_weight_kg, diet)

#h(2cm) ↓ 1:N (species\_id)

*dinosaur* (#underline[id], species_id→, nickname, age_category, gender, estimated_age)

#h(2cm) ↓ 1:N (dinosaur\_id)\ #h(2cm) ← 1:N (person\_id) ← *person* (#underline[id], first_name, last_name, age, occupation)\
#h(2cm) ← 1:N (location\_id) ← *location* (#underline[id], name, loc_type, latitude, longitude, description)

*encounter* (#underline[id], dinosaur_id→, person_id→, location_id→, encounter_ts, outcome, notes)

#h(2cm) ↓ 1:N (encounter\_id)

*encounter_action* (#underline[id], encounter_id→, actor, action_desc, seq_number)

#v(0.4cm)

*Связи:*

#table(
  columns: (2.5fr, 1fr, 1fr, 3fr),
  stroke: 0.5pt,
  align: (left, center, center, left),
  fill: (_, row) => if row == 0 { luma(220) } else { white },
  [*Связь*], [*Мощность*], [*Тип*], [*Описание*],

  [`dinosaur_species` → `dinosaur`],  [1:N], [обязат.], [Один вид — много особей],
  [`dinosaur` → `encounter`],          [1:N], [обязат.], [Один динозавр — много столкновений],
  [`person` → `encounter`],            [1:N], [обязат.], [Один человек — много столкновений],
  [`location` → `encounter`],          [1:N], [обязат.], [Одно место — много столкновений],
  [`encounter` → `encounter_action`],  [1:N], [обязат.], [Одно столкновение — много действий],
  [`dinosaur` ↔ `person`],             [M:N], [—],       [Реализована через таблицу `encounter`],
)

// ═════════════════════════════════════════════════════════════
= Даталогическая модель
// ═════════════════════════════════════════════════════════════

== Пользовательские типы (ENUM)

#table(
  columns: (1.8fr, 3fr),
  stroke: 0.5pt,
  fill: (_, row) => if row == 0 { luma(220) } else { white },
  [*Тип*], [*Допустимые значения*],
  [`predator_type`],    [`herbivore`, `carnivore`, `omnivore`],
  [`age_category`],     [`juvenile`, `adolescent`, `adult`, `elder`],
  [`gender_type`],      [`male`, `female`, `unknown`],
  [`location_type`],    [`path`, `clearing`, `forest`, `settlement`, `riverbank`, `cave`],
  [`encounter_outcome`],[`escaped`, `injured`, `fatal`, `standoff`, `ongoing`],
  [`actor_type`],       [`dinosaur`, `person`],
)

== Таблица `dinosaur_species`

#table(
  columns: (2fr, 1.8fr, 1.2fr, 3fr),
  stroke: 0.5pt,
  fill: (_, row) => if row == 0 { luma(220) } else { white },
  [*Атрибут*], [*Тип PostgreSQL*], [*Огр-я*], [*Описание*],
  [`id`],            [`SERIAL`],         [PK],          [Суррогатный ключ],
  [`name`],          [`VARCHAR(100)`],   [NOT NULL],    [Название вида на русском],
  [`latin_name`],    [`VARCHAR(150)`],   [NOT NULL, UNIQUE], [Латинское название],
  [`avg_height_m`],  [`NUMERIC(5,2)`],   [NOT NULL, >0],[Средняя высота, м],
  [`avg_weight_kg`], [`NUMERIC(8,2)`],   [NOT NULL, >0],[Средняя масса, кг],
  [`diet`],          [`predator_type`],  [NOT NULL],    [Тип питания],
)

== Таблица `dinosaur`

#table(
  columns: (2fr, 1.8fr, 1.2fr, 3fr),
  stroke: 0.5pt,
  fill: (_, row) => if row == 0 { luma(220) } else { white },
  [*Атрибут*], [*Тип PostgreSQL*], [*Огр-я*], [*Описание*],
  [`id`],             [`SERIAL`],        [PK],              [Суррогатный ключ],
  [`species_id`],     [`INTEGER`],       [FK, NOT NULL],    [Ссылка на вид],
  [`nickname`],       [`VARCHAR(100)`],  [—],               [Кличка (необязательно)],
  [`age_category`],   [`age_category`],  [NOT NULL],        [Возрастная категория],
  [`gender`],         [`gender_type`],   [NOT NULL],        [Пол],
  [`estimated_age`],  [`SMALLINT`],      [≥ 0],             [Примерный возраст, лет],
)

== Таблица `person`

#table(
  columns: (2fr, 1.8fr, 1.2fr, 3fr),
  stroke: 0.5pt,
  fill: (_, row) => if row == 0 { luma(220) } else { white },
  [*Атрибут*], [*Тип PostgreSQL*], [*Огр-я*], [*Описание*],
  [`id`],          [`SERIAL`],        [PK],          [Суррогатный ключ],
  [`first_name`],  [`VARCHAR(100)`],  [NOT NULL],    [Имя],
  [`last_name`],   [`VARCHAR(100)`],  [—],           [Фамилия],
  [`age`],         [`SMALLINT`],      [0–150],       [Возраст],
  [`occupation`],  [`VARCHAR(150)`],  [—],           [Род деятельности],
)

== Таблица `location`

#table(
  columns: (2fr, 1.8fr, 1.2fr, 3fr),
  stroke: 0.5pt,
  fill: (_, row) => if row == 0 { luma(220) } else { white },
  [*Атрибут*], [*Тип PostgreSQL*], [*Огр-я*], [*Описание*],
  [`id`],          [`SERIAL`],        [PK],                  [Суррогатный ключ],
  [`name`],        [`VARCHAR(150)`],  [NOT NULL],            [Название места],
  [`loc_type`],    [`location_type`], [NOT NULL],            [Тип местности],
  [`latitude`],    [`NUMERIC(9,6)`],  [−90…90],              [Широта],
  [`longitude`],   [`NUMERIC(9,6)`],  [−180…180],            [Долгота],
  [`description`], [`TEXT`],          [—],                   [Текстовое описание],
)

== Таблица `encounter` (ассоциация, M:N)

#table(
  columns: (2fr, 2.2fr, 1.2fr, 3fr),
  stroke: 0.5pt,
  fill: (_, row) => if row == 0 { luma(220) } else { white },
  [*Атрибут*], [*Тип PostgreSQL*], [*Огр-я*], [*Описание*],
  [`id`],           [`SERIAL`],                    [PK],             [Суррогатный ключ],
  [`dinosaur_id`],  [`INTEGER`],                   [FK, NOT NULL],   [Динозавр],
  [`person_id`],    [`INTEGER`],                   [FK, NOT NULL],   [Человек],
  [`location_id`],  [`INTEGER`],                   [FK, NOT NULL],   [Место],
  [`encounter_ts`], [`TIMESTAMP WITH TIME ZONE`],  [NOT NULL],       [Время столкновения],
  [`outcome`],      [`encounter_outcome`],          [NOT NULL],       [Исход],
  [`notes`],        [`TEXT`],                       [—],              [Примечания],
)

== Таблица `encounter_action` (характеристика)

#table(
  columns: (2fr, 1.8fr, 1.2fr, 3fr),
  stroke: 0.5pt,
  fill: (_, row) => if row == 0 { luma(220) } else { white },
  [*Атрибут*], [*Тип PostgreSQL*], [*Огр-я*], [*Описание*],
  [`id`],            [`SERIAL`],        [PK],           [Суррогатный ключ],
  [`encounter_id`],  [`INTEGER`],       [FK, NOT NULL], [Столкновение],
  [`actor`],         [`actor_type`],    [NOT NULL],     [Кто совершил действие],
  [`action_desc`],   [`TEXT`],          [NOT NULL],     [Описание действия],
  [`seq_number`],    [`SMALLINT`],      [NOT NULL, ≥1], [Порядковый номер],
)

*Уникальное ограничение:* `(encounter_id, seq_number)` — в одном столкновении не может быть двух действий с одним номером.

// ═════════════════════════════════════════════════════════════
= Реализация даталогической модели на SQL
// ═════════════════════════════════════════════════════════════

Файл `schema.sql` содержит полный DDL. Ниже приведён листинг.

```sql
SET search_path TO s501650;

-- ENUM-типы
CREATE TYPE predator_type     AS ENUM ('herbivore','carnivore','omnivore');
CREATE TYPE age_category      AS ENUM ('juvenile','adolescent','adult','elder');
CREATE TYPE gender_type       AS ENUM ('male','female','unknown');
CREATE TYPE location_type     AS ENUM ('path','clearing','forest',
                                        'settlement','riverbank','cave');
CREATE TYPE encounter_outcome AS ENUM ('escaped','injured','fatal',
                                        'standoff','ongoing');
CREATE TYPE actor_type        AS ENUM ('dinosaur','person');

-- 1. dinosaur_species
CREATE TABLE dinosaur_species (
    id              SERIAL         PRIMARY KEY,
    name            VARCHAR(100)   NOT NULL,
    latin_name      VARCHAR(150)   NOT NULL UNIQUE,
    avg_height_m    NUMERIC(5,2)   NOT NULL CHECK (avg_height_m > 0),
    avg_weight_kg   NUMERIC(8,2)   NOT NULL CHECK (avg_weight_kg > 0),
    diet            predator_type  NOT NULL
);

-- 2. dinosaur
CREATE TABLE dinosaur (
    id             SERIAL        PRIMARY KEY,
    species_id     INTEGER       NOT NULL
                   REFERENCES dinosaur_species(id) ON DELETE RESTRICT,
    nickname       VARCHAR(100),
    age_category   age_category  NOT NULL,
    gender         gender_type   NOT NULL DEFAULT 'unknown',
    estimated_age  SMALLINT      CHECK (estimated_age >= 0)
);

-- 3. person
CREATE TABLE person (
    id          SERIAL       PRIMARY KEY,
    first_name  VARCHAR(100) NOT NULL,
    last_name   VARCHAR(100),
    age         SMALLINT     CHECK (age >= 0 AND age <= 150),
    occupation  VARCHAR(150)
);

-- 4. location
CREATE TABLE location (
    id          SERIAL         PRIMARY KEY,
    name        VARCHAR(150)   NOT NULL,
    loc_type    location_type  NOT NULL,
    latitude    NUMERIC(9,6)   CHECK (latitude  BETWEEN -90  AND  90),
    longitude   NUMERIC(9,6)   CHECK (longitude BETWEEN -180 AND 180),
    description TEXT
);

-- 5. encounter  (ассоциация M:N)
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

-- 6. encounter_action  (характеристика)
CREATE TABLE encounter_action (
    id           SERIAL     PRIMARY KEY,
    encounter_id INTEGER    NOT NULL
                 REFERENCES encounter(id) ON DELETE CASCADE,
    actor        actor_type NOT NULL,
    action_desc  TEXT       NOT NULL,
    seq_number   SMALLINT   NOT NULL CHECK (seq_number >= 1),
    UNIQUE (encounter_id, seq_number)
);
```

// ═════════════════════════════════════════════════════════════
= Выводы по работе
// ═════════════════════════════════════════════════════════════

В ходе выполнения лабораторной работы была спроектирована и реализована реляционная база данных на основе предметной области, описывающей столкновения динозавров с людьми.

*Основные результаты:*

+ Из текстового описания предметной области выделено *6 сущностей*: 4 стержневых (`dinosaur_species`, `dinosaur`, `person`, `location`), 1 ассоциация (`encounter`) и 1 характеристика (`encounter_action`).

+ Составлена инфологическая модель, отражающая связи типов 1:N и M:N. Связь M:N между `dinosaur` и `person` реализована через таблицу-ассоциацию `encounter`.

+ Даталогическая модель разработана с использованием типов PostgreSQL: `SERIAL`, `VARCHAR`, `TEXT`, `NUMERIC`, `SMALLINT`, `TIMESTAMP WITH TIME ZONE`, пользовательских `ENUM`.

+ Реализованы ограничения целостности: первичные ключи (`PRIMARY KEY`), внешние ключи (`FOREIGN KEY` с `ON DELETE RESTRICT / CASCADE`), ограничения на значения (`CHECK`), уникальность (`UNIQUE`), обязательность (`NOT NULL`).

+ Таблицы заполнены тестовыми данными: 5 видов динозавров, 5 особей, 5 людей, 5 мест, 5 столкновений и 20 действий.

Работа позволила освоить этапы проектирования баз данных: концептуальный (инфологическая модель), логический (даталогическая модель) и физический (реализация на PostgreSQL).
