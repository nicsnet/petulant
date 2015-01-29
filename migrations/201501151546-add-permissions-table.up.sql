CREATE TABLE permissions (
  id serial primary key,
  users_id int not null,
  name varchar(255) not null,

  foreign key (users_id) references users (id)
);
