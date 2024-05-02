-- liquibase formatted sql

-- changeset Alex:1714550826691-1
CREATE TABLE IF NOT EXISTS app_user
(
    id       UUID                        NOT NULL,
    created  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    username VARCHAR(100)                NOT NULL,
    password VARCHAR(72)                 NOT NULL,
    role     VARCHAR(10)                 NOT NULL,
    enabled  BOOLEAN                     NOT NULL,
    telegram_id BIGINT,
    CONSTRAINT pk_app_user PRIMARY KEY (id),
    CONSTRAINT uc_app_user_username UNIQUE (username),
    CONSTRAINT uc_app_user_telegram UNIQUE (telegram_id)
);

INSERT INTO app_user(id,created,username,password,role,enabled, telegram_id)
VALUES (
        'afaca8a7-bc1f-4c16-b767-555a50859f32',
        now(),
        'admin',
        '$2a$10$FBS8d6vs/n3/mZPLDYbZC.v2VghfJh7/Hh3aZdpyV6Cve2lUGGrDy',
        'ADMIN',
        true,
        371923388
       )

