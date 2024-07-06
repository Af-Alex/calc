-- liquibase formatted sql

-- changeset Alex:create_table_transaction_entity
CREATE TABLE transaction_entity
(
    id           UUID                        NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id      BIGINT                      NOT NULL,
    account_id   UUID                        NOT NULL,
    amount       DECIMAL(10, 2)              NOT NULL,
    completed_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_transaction_entity PRIMARY KEY (id),
    CONSTRAINT fk_transaction_on_account_id FOREIGN KEY (account_id) REFERENCES account_entity (id),
    CONSTRAINT fk_transaction_on_tguser_id FOREIGN KEY (user_id) REFERENCES tg_user (id)
);

