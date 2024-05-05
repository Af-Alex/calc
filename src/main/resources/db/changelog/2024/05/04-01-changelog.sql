-- liquibase formatted sql

-- changeset Alex:1714807299777-1
CREATE TABLE tg_user
(
    id BIGINT                               NOT NULL,
    created     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    username    VARCHAR(100)                NOT NULL,
    active      BOOLEAN                     NOT NULL,
    salary      DECIMAL(10, 2),
    chat_state  VARCHAR(50)                 NOT NULL,
    first_name  VARCHAR(100),
    last_name   VARCHAR(100),
    CONSTRAINT pk_tg_user PRIMARY KEY (id),
    CONSTRAINT uc_tg_user_username UNIQUE (username)
);

