-- DROP TABLE IF EXISTS `userlike` cascade;
-- DROP TABLE IF EXISTS `user` cascade;
-- DROP TABLE IF EXISTS `goods` cascade;

CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100) NOT NULL UNIQUE,
    gender VARCHAR(20),
    address VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS goods (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pic_img LONGTEXT NOT NULL,
    product_link LONGTEXT NOT NULL,
    product_title VARCHAR(255) NOT NULL,
    product_price VARCHAR(50) NOT NULL,
    keyword VARCHAR(255) NOT NULL,
    platform VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS userlike (
    username VARCHAR(50) NOT NULL,
    goods_link LONGTEXT NOT NULL
);