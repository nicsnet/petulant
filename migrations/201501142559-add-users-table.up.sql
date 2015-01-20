CREATE TABLE users
(id INT PRIMARY KEY,
 email VARCHAR(30),
 token VARCHAR(100),
 resource_name VARCHAR(30),
 resource_action VARCHAR(30),
 password VARCHAR(100));
