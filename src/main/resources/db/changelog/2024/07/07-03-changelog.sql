-- liquibase formatted sql

-- changeset Alex:create_table_goal
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
    CONSTRAINT pk_goal PRIMARY KEY (id),
    CONSTRAINT fk_goal_on_user FOREIGN KEY (user_id) REFERENCES "user" (id)
);
