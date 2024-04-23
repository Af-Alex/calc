-- liquibase formatted sql

-- changeset alexaf:set_user_password_not_null
ALTER TABLE app_user
    ALTER COLUMN password SET NOT NULL;

