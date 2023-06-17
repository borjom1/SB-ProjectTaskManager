drop table if exists user_roles;
drop table if exists roles;
drop table if exists users;

create table users
(
    id         bigserial primary key,
    first_name varchar(64)        not null,
    last_name  varchar(64)        not null,
    login      varchar(64) unique not null,
    password   text               not null,
    created_at timestamptz        not null default now(),
    avatar     text               not null,
    position   varchar(128)       not null,
    projects   bigint             not null default 0
);

create table roles
(
    id   bigserial primary key,
    name varchar(32) unique not null
);

create table user_roles
(
    user_id bigint references users (id),
    role_id bigint references roles (id),
    primary key (user_id, role_id)
);