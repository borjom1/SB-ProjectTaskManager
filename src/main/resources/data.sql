-- user insertion

insert into roles(name)
values ('ROLE_USER'),
       ('ROLE_MANAGER'),
       ('ROLE_ADMIN');

insert into users(first_name, last_name, login, password, avatar, position)
values ('Mark', 'Zuckerberg', 'mark31', '$2a$12$6kHi8D0MDtARuXCRxZeRAuK5X5jojeahinZvBiHPGpkmuaK6F.rxG', DEFAULT, 'Java Software Engineer');

insert into user_roles(user_id, role_id)
values (1, 1),
       (1, 2),
       (1, 3);

insert into projects(name, description, tasks, tasks_done)
values (
        'Mobile Banking App',
        'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry''s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.',
        12,
        7
);

insert into members(user_id, project_id) values (1, 1);