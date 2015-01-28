CREATE TABLE user_roles (
  id serial PRIMARY KEY,
  user_id int not null,
  role_id int not null,

  foreign key (role_id) references roles (id),
  foreign key (user_id) references users (id)
 );
