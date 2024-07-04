-- liquibase formatted sql

-- changeset Alex:1719944305532-1
CREATE TABLE account_entity
(
    id      UUID                        NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    balance DECIMAL(10, 2),
    user_id BIGINT                      NOT NULL,
    CONSTRAINT pk_account_entity PRIMARY KEY (id),
    CONSTRAINT fk_account_on_user FOREIGN KEY (user_id) REFERENCES tg_user (id)
);
