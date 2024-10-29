CREATE TABLE IF NOT EXISTS categories
(
  id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  CONSTRAINT categories_name_ux2 UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS events
(
  id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  category_id BIGINT NOT NULL,
  CONSTRAINT events_category_id_fk FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE TABLE IF NOT EXISTS compilations
(
  id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  pinned BOOLEAN      NOT NULL,
  title  VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name  VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL
);