DROP DATABASE IF EXISTS `xy-cms`;
CREATE DATABASE `xy-cms` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

USE `xy-cms`;

DROP TABLE if EXISTS `test`;
CREATE TABLE `test` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `pid` int(11) DEFAULT NULL,
  `count` int(11) DEFAULT NULL COMMENT '数量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;