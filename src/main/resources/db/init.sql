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

DROP TABLE if EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_name` varchar(50) NOT NULL COMMENT '用户名',
  `pass_wd` varchar(40) NOT NULL COMMENT '密码',
  `pass_count` int(11) DEFAULT '0' COMMENT '登陆成功次数',
  `fail_count` int(11) DEFAULT '0' COMMENT '登陆失败次数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';