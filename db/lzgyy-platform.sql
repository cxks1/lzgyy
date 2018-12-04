/*
Navicat MySQL Data Transfer

Source Server         : (mysql年底临时)114.215.71.72
Source Server Version : 50625
Source Host           : 192.168.0.114:3306
Source Database       : lzgyy-platform

Target Server Type    : MYSQL
Target Server Version : 50625
File Encoding         : 65001

Date: 2018-10-26 15:28:06
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for demo
-- ----------------------------
DROP TABLE IF EXISTS `demo`;
CREATE TABLE `demo` (
  `id` bigint(40) NOT NULL COMMENT 'id',
  `name` varchar(80) DEFAULT NULL,
  `createUser` bigint(40) DEFAULT NULL,
  `createDate` datetime DEFAULT NULL,
  `updateUser` bigint(40) DEFAULT NULL,
  `updateDate` datetime DEFAULT NULL,
  `deleteState` char(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of demo
-- ----------------------------
INSERT INTO `demo` VALUES ('2', 'd1', '222', '2018-10-25 14:02:04', '2222', '2018-10-25 14:02:14', '1');

-- ----------------------------
-- Table structure for test
-- ----------------------------
DROP TABLE IF EXISTS `test`;
CREATE TABLE `test` (
  `id` bigint(40) NOT NULL COMMENT 'id',
  `name` varchar(80) DEFAULT NULL,
  `createUser` bigint(40) DEFAULT NULL,
  `createDate` datetime DEFAULT NULL,
  `updateUser` bigint(40) DEFAULT NULL,
  `updateDate` datetime DEFAULT NULL,
  `deleteState` char(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of test
-- ----------------------------
INSERT INTO `test` VALUES ('1', 't1', '1111', '2018-10-25 14:02:04', '1111', '2018-10-25 14:02:14', '1');
INSERT INTO `test` VALUES ('2', 't2', '2222', '2018-10-25 14:03:11', '2222', '2018-10-25 14:03:16', '1');
