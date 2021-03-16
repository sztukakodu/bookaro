create sequence hibernate_sequence start 1 increment 1;
create table author
(
    id         int8 not null,
    uuid       varchar(255),
    version    int8 not null,
    created_at timestamp,
    name       varchar(255),
    primary key (id)
);
create table book
(
    id        int8 not null,
    uuid      varchar(255),
    version   int8 not null,
    available int8,
    cover_id  int8,
    price     numeric(19, 2),
    title     varchar(255),
    year      int4,
    primary key (id)
);
create table book_authors
(
    books_id   int8 not null,
    authors_id int8 not null,
    primary key (books_id, authors_id)
);
create table order_item
(
    id       int8 not null,
    uuid     varchar(255),
    version  int8 not null,
    quantity int4 not null,
    book_id  int8,
    order_id int8,
    primary key (id)
);
create table orders
(
    id           int8 not null,
    uuid         varchar(255),
    version      int8 not null,
    created_at   timestamp,
    delivery     varchar(255),
    status       varchar(255),
    updated_at   timestamp,
    recipient_id int8,
    primary key (id)
);
create table recipient
(
    id       int8 not null,
    uuid     varchar(255),
    version  int8 not null,
    city     varchar(255),
    email    varchar(255),
    name     varchar(255),
    phone    varchar(255),
    street   varchar(255),
    zip_code varchar(255),
    primary key (id)
);
create table upload
(
    id           int8 not null,
    uuid         varchar(255),
    version      int8 not null,
    content_type varchar(255),
    created_at   timestamp,
    file         bytea,
    filename     varchar(255),
    primary key (id)
);
create table users
(
    id         int8 not null,
    uuid       varchar(255),
    version    int8 not null,
    created_at timestamp,
    password   varchar(255),
    updated_at timestamp,
    username   varchar(255),
    primary key (id)
);
create table users_roles
(
    user_id int8 not null,
    role    varchar(255)
);
alter table book
    add constraint UK_g0286ag1dlt4473st1ugemd0m unique (title);
alter table book_authors
    add constraint FK551i3sllw1wj7ex6nir16blsm foreign key (authors_id) references author;
alter table book_authors
    add constraint FKmuhqocx8etx13u6jrtutnumek foreign key (books_id) references book;
alter table order_item
    add constraint FKb033an1f8qmpbnfl0a6jb5njs foreign key (book_id) references book;
alter table order_item
    add constraint FKt4dc2r9nbvbujrljv3e23iibt foreign key (order_id) references orders;
alter table orders
    add constraint FKcxwo1jbmo15jih4b5qjclvye8 foreign key (recipient_id) references recipient;
alter table users_roles
    add constraint FK2o0jvgh89lemvvo17cbqvdxaa foreign key (user_id) references users;
