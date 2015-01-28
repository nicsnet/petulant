CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS '
  BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
  END;
' LANGUAGE 'plpgsql';

CREATE TABLE users (
  id serial PRIMARY KEY,
  email varchar(40) not null unique,
  password varchar(255) not null,
  created_at timestamp not null default CURRENT_TIMESTAMP,
  updated_at timestamp not null default CURRENT_TIMESTAMP
);

CREATE TRIGGER update_updated_at_users
  BEFORE UPDATE ON users FOR EACH ROW EXECUTE
  PROCEDURE update_updated_at_column();
