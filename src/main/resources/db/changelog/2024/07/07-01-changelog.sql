-- liquibase formatted sql

-- changeset Alex:1720361074122-1
CREATE TABLE contribution
(
    id                    UUID                        NOT NULL,
    created               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    goal_id               UUID                        NOT NULL,
    amount                DECIMAL(12, 2)              NOT NULL,
    contribution_datetime TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_contribution PRIMARY KEY (id)
);

-- changeset Alex:1720361074122-2
CREATE TABLE goal
(
    id             UUID                        NOT NULL,
    created        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id        UUID                        NOT NULL,
    name           VARCHAR(50)                 NOT NULL,
    priority       INTEGER                     NOT NULL,
    type           VARCHAR(50)                 NOT NULL,
    balance        DECIMAL(12, 2),
    target_amount  DECIMAL(12, 2),
    deadline       date,
    active         BOOLEAN,
    monthly_amount DECIMAL(12, 2),
    CONSTRAINT pk_goal PRIMARY KEY (id)
);

-- changeset Alex:1720361074122-3
CREATE TABLE salary
(
    id             UUID                        NOT NULL,
    created        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id        UUID                        NOT NULL,
    amount         DECIMAL(12, 2)              NOT NULL,
    effective_date date                        NOT NULL,
    CONSTRAINT pk_salary PRIMARY KEY (id)
);

-- changeset Alex:1720361074122-4
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
    CONSTRAINT pk_user PRIMARY KEY (id)
);

-- changeset Alex:1720361074122-5
ALTER TABLE "user"
    ADD CONSTRAINT uc_user_email UNIQUE (email);

-- changeset Alex:1720361074122-6
ALTER TABLE "user"
    ADD CONSTRAINT uc_user_telegram UNIQUE (telegram_id);

-- changeset Alex:1720361074122-7
ALTER TABLE contribution
    ADD CONSTRAINT FK_CONTRIBUTION_ON_GOAL FOREIGN KEY (goal_id) REFERENCES goal (id);

-- changeset Alex:1720361074122-8
ALTER TABLE goal
    ADD CONSTRAINT FK_GOAL_ON_USER FOREIGN KEY (user_id) REFERENCES "user" (id);

-- changeset Alex:1720361074122-9
ALTER TABLE salary
    ADD CONSTRAINT FK_SALARY_ON_USER FOREIGN KEY (user_id) REFERENCES "user" (id);

