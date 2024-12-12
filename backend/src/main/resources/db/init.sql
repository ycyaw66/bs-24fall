DROP TABLE IF EXISTS `user` cascade;

CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    gender TINYINT NOT NULL,  -- 0: 不愿透露, 1: 男, 2: 女
    address VARCHAR(255) NOT NULL
);
