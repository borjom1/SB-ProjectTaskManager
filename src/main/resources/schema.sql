drop table if exists task_marks;
drop table if exists tasks;
drop table if exists stories;
drop table if exists members;
drop table if exists projects;
drop table if exists user_roles;
drop table if exists roles;
drop table if exists users;

create table users
(
    id            bigserial primary key,
    first_name    varchar(64)        not null,
    last_name     varchar(64)        not null,
    login         varchar(64) unique not null,
    password      text               not null,
    created_at    timestamptz        not null default now(),
    avatar        text               not null default 'C:\Users\Igor\Desktop\project-task-manager\target\classes\avatars\default',
    position      varchar(128)       not null,
    projects      bigint             not null default 0,
    refresh_token text
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

create table projects
(
    id          bigserial primary key,
    name        varchar(128) not null,
    description text         not null,
    tasks       bigint       not null default 0,
    tasks_done  bigint       not null default 0
);

create table members
(
    user_id    bigint references users (id),
    project_id bigint references projects (id),
    primary key (user_id, project_id)
);

create table stories
(
    id         bigserial primary key,
    project_id bigint references projects (id) not null,
    name       varchar(32)                     not null,
    start_date date                            not null,
    end_date   date                            not null,
    created_at timestamptz                     not null
);

create table tasks
(
    id               bigserial primary key,
    story_id         bigint references stories (id) not null,
    assigned_user_id bigint references users (id),
    title            varchar(128)                   not null default '',
    status           varchar(32)                    not null default ''
);

create table task_marks
(
    task_id bigint references tasks (id),
    mark    varchar(32),
    primary key (task_id, mark)
);