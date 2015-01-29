CREATE TABLE permissions (
  id serial primary key,
  user_id int not null,
  name varchar(255) not null,

  foreign key (user_id) references users (id)
);
