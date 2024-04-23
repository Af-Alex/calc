-- liquibase formatted sql
-- changeset alexaf:add_user_table

CREATE TABLE app_user
(
    id       UUID                        NOT NULL,
    created  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    username VARCHAR(100)                NOT NULL,
    password VARCHAR(72),
    role     VARCHAR(10)                 NOT NULL,
    enabled  BOOLEAN                     NOT NULL,
    CONSTRAINT pk_app_user PRIMARY KEY (id),
    CONSTRAINT uc_app_user_username UNIQUE (username)
);

