-- user insertion

insert into roles(name)
values ('ROLE_USER'),
       ('ROLE_MANAGER'),
       ('ROLE_ADMIN');

insert into users(first_name, last_name, login, password, avatar, position)
values ('Mark', 'Zuckerberg', 'mark31', '$2a$12$6kHi8D0MDtARuXCRxZeRAuK5X5jojeahinZvBiHPGpkmuaK6F.rxG', '', '');

insert into user_roles(user_id, role_id)
values (1, 1),
       (1, 2),
       (1, 3);