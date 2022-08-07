-- liquibase formatted sql

-- changeSet alexadler:1
CREATE TABLE notification_tasks (
    id        bigserial PRIMARY KEY,
    user_id   bigint    NOT NULL CHECK (user_id >= 0),
    date_time timestamp NOT NULL,
    message   text      NOT NULL
);

-- changeSet alexadler:2
CREATE index date_time_index ON notification_tasks (date_time);