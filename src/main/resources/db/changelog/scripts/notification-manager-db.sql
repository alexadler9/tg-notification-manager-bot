-- liquibase formatted sql

-- changeSet alexadler:1

CREATE TABLE users
(
    id        bigint        PRIMARY KEY,
    name      text          NOT NULL
);

CREATE TABLE notifications
(
    id        bigserial     PRIMARY KEY,
    id_user   bigint        REFERENCES users (id),
    date_time timestamp     NOT NULL,
    message   text          NOT NULL
);

-- changeSet alexadler:2

ALTER TABLE users
ADD COLUMN last_message text;