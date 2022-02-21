create table if not exists app_user
(
    id                serial not null primary key,
    email             varchar(255),
    last_name         varchar(255),
    name              varchar(255),
    organization_name varchar(255),
    password          varchar(255)
);

alter table app_user
    owner to postgres;

create table if not exists organization
(
    id           serial not null primary key,
    address      varchar(255),
    name         varchar(255),
    phone_number varchar(255)
);

alter table organization
    owner to postgres;

create table if not exists role
(
    id   serial not null primary key,
    name varchar(255)
);

alter table role
    owner to postgres;

create table task
(
    id          serial not null primary key,
    deadline    varchar(255),
    description varchar(255),
    is_done     boolean not null,
    title       varchar(255)
);

alter table task
    owner to postgres;

create table app_user_roles
(
    app_user_id bigint not null
        constraint fkkwxexnudtp5gmt82j0qtytnoe
            references app_user,
    roles_id    bigint not null
        constraint fk23e7b5jyl3ql41rk3566gywdd
            references role
);

alter table app_user_roles
    owner to postgres;

create table app_user_tasks
(
    app_user_id bigint not null
        constraint fko759frcqmcskpolcsua4e2lqp
            references app_user,
    tasks_id    bigint not null
        constraint fk60ct2yn74181kbuvnn8eolgye
            references task
);

alter table app_user_tasks
    owner to postgres;

create table organization_app_users
(
    organization_id bigint not null
        constraint fksn2mh47mqy10usv55nh23rqu8
            references organization,
    app_users_id    bigint not null
        constraint uk_bxape02douelko1n9g7p9d50e
            unique
        constraint fkk8hovkifui7v6syt7n2ceupax
            references app_user
);

alter table organization_app_users
    owner to postgres;