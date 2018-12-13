/*
Navicat MySQL Data Transfer

Source Server         : 192.168.0.114
Source Server Version : 50625
Source Host           : 192.168.0.114:3306
Source Database       : lzgyy-platform

Target Server Type    : MYSQL
Target Server Version : 50625
File Encoding         : 65001

Date: 2018-10-30 16:08:47
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for job_entity
-- ----------------------------
DROP TABLE IF EXISTS `job_entity`;
CREATE TABLE `job_entity` (
  `id` bigint(40) NOT NULL COMMENT 'job主键',
  `name` varchar(255) DEFAULT NULL COMMENT 'job名称',
  `group` varchar(255) DEFAULT NULL COMMENT 'job组名',
  `cron` varchar(255) DEFAULT NULL COMMENT '执行的cron',
  `parameter` varchar(255) DEFAULT NULL COMMENT 'job的参数',
  `description` varchar(255) DEFAULT NULL COMMENT 'job描述信息',
  `vm_param` varchar(255) DEFAULT NULL COMMENT 'vm参数',
  `jar_path` varchar(255) DEFAULT NULL COMMENT 'job的jar路径',
  `status` varchar(80) DEFAULT NULL COMMENT 'job的执行状态,这里我设置为OPEN/CLOSE且只有该值为OPEN才会执行该Job',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of job_entity
-- ----------------------------
