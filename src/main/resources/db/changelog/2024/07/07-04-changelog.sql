-- liquibase formatted sql

-- changeset Alex:create_table_contribution
CREATE TABLE contribution
(
    id                    UUID                        NOT NULL,
    created               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    goal_id               UUID                        NOT NULL,
    amount                DECIMAL(12, 2)              NOT NULL,
    contribution_datetime TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_contribution PRIMARY KEY (id),
    CONSTRAINT fk_contribution_on_goal FOREIGN KEY (goal_id) REFERENCES goal (id)
);

