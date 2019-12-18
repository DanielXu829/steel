/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 50728
 Source Host           : localhost:3306
 Source Schema         : steel_linux

 Target Server Type    : MySQL
 Target Server Version : 50728
 File Encoding         : 65001

 Date: 11/12/2019 18:56:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for report_template_config
-- ----------------------------
DROP TABLE IF EXISTS `report_template_config`;
CREATE TABLE `report_template_config`  (
  `id` bigint(30) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `template_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模板的名称',
  `template_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板生成所在临时路径',
  `time_divide_type` tinyint(4) NULL DEFAULT NULL COMMENT '时间划分方式',
  `start_timeslot` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '开始时间',
  `end_timeslot` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '结束时间',
  `timeslot_interval` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '时间间隔',
  `is_add_avg` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '是否添加平均值',
  `avg_divide_type` tinyint(4) NULL DEFAULT NULL COMMENT '平均值计算方式',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 318 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '报表动态模板配置' ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;


-- ----------------------------
-- Table structure for report_template_tags
-- ----------------------------
DROP TABLE IF EXISTS `report_template_tags`;
CREATE TABLE `report_template_tags`  (
  `id` bigint(30) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `template_config_id` bigint(30) NULL DEFAULT NULL COMMENT '外键，对应report_tempalte_config中的主键',
  `target_id` bigint(30) NULL DEFAULT NULL COMMENT '外键，对应target_management中的主键',
  `sequence` tinyint(255) NULL DEFAULT NULL COMMENT '参数排序，定义了excel中的顺序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 318 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '报表动态模板 - 参数列表' ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;
