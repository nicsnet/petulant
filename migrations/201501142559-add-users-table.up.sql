CREATE OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS '
  BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
  END;
' LANGUAGE 'plpgsql';

CREATE TABLE users (
  id serial PRIMARY KEY,
  email varchar(40) NOT NULL UNIQUE,
  token varchar(100),
  resource_name varchar(30),
  resource_action varchar(30),
  password varchar(100),
  created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER update_updated_at_users
  BEFORE UPDATE ON users FOR EACH ROW EXECUTE
  PROCEDURE update_updated_at_column();
