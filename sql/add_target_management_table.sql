/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 50717
 Source Host           : localhost:3306
 Source Schema         : steel_linux

 Target Server Type    : MySQL
 Target Server Version : 50717
 File Encoding         : 65001

 Date: 12/20/2019
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for target_management
-- ----------------------------
DROP TABLE IF EXISTS `target_management`;
CREATE TABLE `target_management`  (
  `id` bigint(30) NOT NULL AUTO_INCREMENT COMMENT '指标编号，主键',
  `parent_id` bigint(30) NULL DEFAULT NULL COMMENT '父编号,用于构造节点树',
  `target_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '指标名',
  `written_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '书面名',
  `target_formula` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '指标公式',
  `normal_range` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '正常值范围',
  `default_value` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '默认值',
  `unit` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '单位',
  `scale` int(11) NULL DEFAULT NULL COMMENT '保留小数位数',
  `default_width` bigint(30) NULL DEFAULT NULL COMMENT '默认列宽',
  `default_height` bigint(30) NULL DEFAULT NULL COMMENT '默认行高',
  `is_leaf` int(1) NOT NULL COMMENT '是否是叶子 0否 1是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_target_name`(`target_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 329 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
