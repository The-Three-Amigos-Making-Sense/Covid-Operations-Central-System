CREATE DATABASE cocsdb;
CREATE USER 'cocsadmin'@'localhost' IDENTIFIED BY '12345678';
GRANT all privileges ON cocsdb.* TO 'cocsadmin'@'localhost';