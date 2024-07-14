-- liquibase formatted sql

-- changeset Alex:create_table_salary
CREATE TABLE salary
(
    id             UUID                        NOT NULL,
    created        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id        UUID                        NOT NULL,
    amount         DECIMAL(12, 2)              NOT NULL,
    effective_date date                        NOT NULL,
    CONSTRAINT pk_salary PRIMARY KEY (id),
    CONSTRAINT fk_salary_on_user FOREIGN KEY (user_id) REFERENCES "user" (id)
);
