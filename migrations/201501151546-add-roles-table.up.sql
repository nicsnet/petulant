CREATE TABLE roles (
  id serial primary key,
  application_id int not null,
  name varchar(255) not null,

  foreign key (application_id) references applications (id)
);
