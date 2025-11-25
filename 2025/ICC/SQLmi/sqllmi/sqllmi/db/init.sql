CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(32) NOT NULL UNIQUE,
  password VARCHAR(64) NOT NULL
);

INSERT INTO users (username, password) VALUES
  ('guest', 'guest'),
  ('test', 'test'),
  ('admin', 'REDACTED');
