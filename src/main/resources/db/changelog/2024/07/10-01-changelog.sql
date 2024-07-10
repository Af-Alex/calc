-- liquibase formatted sql

-- changeset Alex:create_table_goal_save_request
CREATE TABLE goal_save_request
(
    id             UUID                        NOT NULL,
    created        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id        UUID                        NOT NULL,
    name           VARCHAR(50),
    priority       INTEGER,
    type           VARCHAR(50),
    balance        DECIMAL(12, 2),
    target_amount  DECIMAL(12, 2),
    deadline       date,
    monthly_amount DECIMAL(12, 2),
    CONSTRAINT pk_goal_save_request PRIMARY KEY (id),
    CONSTRAINT uc_goal_save_request_user UNIQUE (user_id),
    CONSTRAINT fk_goal_save_request_on_user FOREIGN KEY (user_id) REFERENCES "user" (id)
);
