CREATE USER 'dbfiubaar'@'localhost' IDENTIFIED BY 'tpfinal';
CREATE DATABASE fiubaar_webapp;
USE fiubaar_webapp;
GRANT ALL PRIVILEGES ON fiubaar_webapp.* TO 'dbfiubaar'@'localhost';
FLUSH PRIVILEGES;