-- liquibase formatted sql

-- changeset Alex:create_table_user
CREATE TABLE "user"
(
    id                UUID                        NOT NULL,
    created           TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_login        TIMESTAMP WITHOUT TIME ZONE,
    telegram_id       BIGINT,
    telegram_nickname VARCHAR(100),
    email             VARCHAR(100),
    password          VARCHAR(100),
    active            BOOLEAN                     NOT NULL,
    firstname         VARCHAR(100),
    lastname          VARCHAR(100),
    chat_state        VARCHAR(50),
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT uc_user_email UNIQUE (email),
    CONSTRAINT uc_user_telegram UNIQUE (telegram_id)
);
