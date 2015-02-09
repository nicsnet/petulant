CREATE TABLE permissions (
  id serial primary key,
  users_id int not null,
  name varchar(255) not null
);

CREATE unique index name_users_id_idx on permissions (users_id, name);
