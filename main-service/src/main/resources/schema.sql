CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT categories_name_ux2 UNIQUE (name)
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
    name  VARCHAR(250) NOT NULL,
    email VARCHAR(254) NOT NULL,
    CONSTRAINT users_email_ux UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    initiator_id       BIGINT                      NOT NULL,
    title              VARCHAR(120)                NOT NULL,
    category_id        BIGINT                      NOT NULL,
    event_date         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    lat                REAL,
    lon                REAL,
    annotation         VARCHAR(2000)               NOT NULL,
    description        VARCHAR(7000)               NOT NULL,
    participant_limit  BIGINT                      NOT NULL,
    paid               BOOLEAN                     NOT NULL,
    request_moderation BOOLEAN                     NOT NULL,
    created_on         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    state              VARCHAR(10)                 NOT NULL,
    CONSTRAINT events_initiator_id_fk FOREIGN KEY (initiator_id) REFERENCES users (id),
    CONSTRAINT events_category_id_fk FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT state_values CHECK (state IN ('PENDING', 'PUBLISHED', 'CANCELED'))
);

CREATE TABLE IF NOT EXISTS compilation_event
(
    compilation_id BIGINT NOT NULL,
    event_id       BIGINT NOT NULL,
    CONSTRAINT compilation_event_compilation_id_fk FOREIGN KEY (compilation_id) REFERENCES compilations (id),
    CONSTRAINT compilation_event_event_id_fk FOREIGN KEY (event_id) REFERENCES events (id)
);
