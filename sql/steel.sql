/*
 Navicat Premium Data Transfer

 Source Server         : cisdi
 Source Server Type    : MySQL
 Source Server Version : 50724
 Source Host           : 10.11.11.36:3306
 Source Schema         : steel

 Target Server Type    : MySQL
 Target Server Version : 50724
 File Encoding         : 65001

 Date: 17/01/2019 16:04:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for QRTZ_BLOB_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_BLOB_TRIGGERS`;
CREATE TABLE `QRTZ_BLOB_TRIGGERS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `BLOB_DATA` blob NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `QRTZ_BLOB_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for QRTZ_CALENDARS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_CALENDARS`;
CREATE TABLE `QRTZ_CALENDARS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `CALENDAR_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `CALENDAR_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for QRTZ_CRON_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_CRON_TRIGGERS`;
CREATE TABLE `QRTZ_CRON_TRIGGERS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `CRON_EXPRESSION` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TIME_ZONE_ID` varchar(80) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `QRTZ_CRON_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of QRTZ_CRON_TRIGGERS
-- ----------------------------
INSERT INTO `QRTZ_CRON_TRIGGERS` VALUES ('schedulerFactoryBean', 'triggertest', '高炉', '0 0/1 * * * ?', 'Asia/Shanghai');

-- ----------------------------
-- Table structure for QRTZ_FIRED_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_FIRED_TRIGGERS`;
CREATE TABLE `QRTZ_FIRED_TRIGGERS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `ENTRY_ID` varchar(95) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `INSTANCE_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `FIRED_TIME` bigint(13) NOT NULL,
  `SCHED_TIME` bigint(13) NOT NULL,
  `PRIORITY` int(11) NOT NULL,
  `STATE` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `JOB_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `JOB_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `IS_NONCONCURRENT` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `REQUESTS_RECOVERY` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`, `ENTRY_ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for QRTZ_JOB_DETAILS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_JOB_DETAILS`;
CREATE TABLE `QRTZ_JOB_DETAILS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `JOB_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `JOB_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `DESCRIPTION` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `IS_DURABLE` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `IS_NONCONCURRENT` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `IS_UPDATE_DATA` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `JOB_DATA` blob NULL,
  PRIMARY KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of QRTZ_JOB_DETAILS
-- ----------------------------
INSERT INTO `QRTZ_JOB_DETAILS` VALUES ('schedulerFactoryBean', 'test', '高炉', '', 'com.cisdi.steel.module.quartz.job.TestJob', '0', '0', '0', '0', 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787000737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F40000000000010770800000010000000007800);

-- ----------------------------
-- Table structure for QRTZ_LOCKS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_LOCKS`;
CREATE TABLE `QRTZ_LOCKS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `LOCK_NAME` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `LOCK_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of QRTZ_LOCKS
-- ----------------------------
INSERT INTO `QRTZ_LOCKS` VALUES ('quartzScheduler', 'STATE_ACCESS');
INSERT INTO `QRTZ_LOCKS` VALUES ('quartzScheduler', 'TRIGGER_ACCESS');
INSERT INTO `QRTZ_LOCKS` VALUES ('schedulerFactoryBean', 'TRIGGER_ACCESS');

-- ----------------------------
-- Table structure for QRTZ_PAUSED_TRIGGER_GRPS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_PAUSED_TRIGGER_GRPS`;
CREATE TABLE `QRTZ_PAUSED_TRIGGER_GRPS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_GROUP`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for QRTZ_SCHEDULER_STATE
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_SCHEDULER_STATE`;
CREATE TABLE `QRTZ_SCHEDULER_STATE`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `INSTANCE_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `LAST_CHECKIN_TIME` bigint(13) NOT NULL,
  `CHECKIN_INTERVAL` bigint(13) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `INSTANCE_NAME`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of QRTZ_SCHEDULER_STATE
-- ----------------------------
INSERT INTO `QRTZ_SCHEDULER_STATE` VALUES ('quartzScheduler', 'DESKTOP-16A9FFA1540384021845', 1540389288688, 10000);

-- ----------------------------
-- Table structure for QRTZ_SIMPLE_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_SIMPLE_TRIGGERS`;
CREATE TABLE `QRTZ_SIMPLE_TRIGGERS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `REPEAT_COUNT` bigint(7) NOT NULL,
  `REPEAT_INTERVAL` bigint(12) NOT NULL,
  `TIMES_TRIGGERED` bigint(10) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `QRTZ_SIMPLE_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for QRTZ_SIMPROP_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_SIMPROP_TRIGGERS`;
CREATE TABLE `QRTZ_SIMPROP_TRIGGERS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `STR_PROP_1` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `STR_PROP_2` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `STR_PROP_3` varchar(512) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `INT_PROP_1` int(11) NULL DEFAULT NULL,
  `INT_PROP_2` int(11) NULL DEFAULT NULL,
  `LONG_PROP_1` bigint(20) NULL DEFAULT NULL,
  `LONG_PROP_2` bigint(20) NULL DEFAULT NULL,
  `DEC_PROP_1` decimal(13, 4) NULL DEFAULT NULL,
  `DEC_PROP_2` decimal(13, 4) NULL DEFAULT NULL,
  `BOOL_PROP_1` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `BOOL_PROP_2` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  CONSTRAINT `QRTZ_SIMPROP_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for QRTZ_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_TRIGGERS`;
CREATE TABLE `QRTZ_TRIGGERS`  (
  `SCHED_NAME` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `JOB_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `JOB_GROUP` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `DESCRIPTION` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint(13) NULL DEFAULT NULL,
  `PREV_FIRE_TIME` bigint(13) NULL DEFAULT NULL,
  `PRIORITY` int(11) NULL DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `TRIGGER_TYPE` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `START_TIME` bigint(13) NOT NULL,
  `END_TIME` bigint(13) NULL DEFAULT NULL,
  `CALENDAR_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `MISFIRE_INSTR` smallint(2) NULL DEFAULT NULL,
  `JOB_DATA` blob NULL,
  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
  INDEX `SCHED_NAME`(`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) USING BTREE,
  CONSTRAINT `QRTZ_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `QRTZ_JOB_DETAILS` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of QRTZ_TRIGGERS
-- ----------------------------
INSERT INTO `QRTZ_TRIGGERS` VALUES ('schedulerFactoryBean', 'triggertest', '高炉', 'test', '高炉', NULL, 1547707140000, 1547707080000, 5, 'WAITING', 'CRON', 1542609734000, 0, NULL, 0, '');

-- ----------------------------
-- Table structure for report_category
-- ----------------------------
DROP TABLE IF EXISTS `report_category`;
CREATE TABLE `report_category`  (
  `id` bigint(30) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` bigint(30) NULL DEFAULT 0 COMMENT '父类id',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  `code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '编码唯一',
  `leaf_node` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '0 父 1叶子',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注（预留字段）',
  `attr1` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `attr2` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `attr3` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `attr4` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `attr5` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 67 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '报表分类' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of report_category
-- ----------------------------
INSERT INTO `report_category` VALUES (1, 0, '高炉', 'gl_', '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (2, 0, '焦化', 'jh_', '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (3, 0, '烧结', 'sj_', '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (4, 0, '原供料', 'ygl_', '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (5, 0, '能介', 'nj_', '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (6, 1, '出铁作业 日报', 'gl_chutiezuoye_day', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (7, 1, '出铁作业 月报', 'gl_chutiezuoye_month', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (8, 1, '高炉本体炉身静压 日报', 'gl_bentilushenjingya_day', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (9, 1, '高炉本体炉身静压 月报', 'gl_bentilushenjingya_month', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (10, 1, '高炉本体温度日报表', 'gl_bentiwendu_day', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (11, 1, '高炉本体温度月报表', 'gl_bentiwendu_month', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (12, 1, '高炉冷却壁温度 日报', 'gl_lengquebiwendu_day', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (13, 1, '高炉冷却壁温度 月报', 'gl_lengquebiwendu_month', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (14, 1, '高炉炉顶装料作业 日报1', 'gl_ludingzhuangliaozuoye_day1', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (15, 1, '高炉炉顶装料作业 日报2', 'gl_ludingzhuangliaozuoye_day2', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (16, 1, '热风炉 日报', 'gl_refenglu_day', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (17, 1, '热风炉 月报', 'gl_refenglu_month', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (18, 1, '高炉 日报', 'gl_jswgaolu_day', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (19, 1, '高炉 月报', 'gl_taisu1_month', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (20, 1, 'JSW质能平衡计算报表', 'gl_jswzhinengpinghengjisuan', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (21, 4, '原料供料日报', 'ygl_yuanliaogongliao_day', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (22, 4, '供料异常跟踪表', 'gl_yichanggenzong', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (23, 4, '供料车间集控中心交接班记录', 'gl_chejianjikongzhongxinjioajieban', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (24, 4, '供料车间物料外排统计表', 'gl_chejianwuliaowaipai', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (25, 4, '原料堆混匀矿粉配比通知单', 'yl_duihunyunkuangfenpeibi', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (26, 4, '原料数据记录表', 'yl_shujujilu', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (27, 4, '原料混匀矿粉A4干转湿配比换算计算表', 'yl_hunyunkuangfenA4ganzhuanshi', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (28, 4, '原料炼焦煤每日库存动态表', 'yl_lianjiaomeikucundongtai_day', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (29, 4, '原料进厂物资精煤化验记录表', 'yl_jinchangwuzijingmeihuayan', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (30, 4, '原料车间生产交班表', 'yl_chejianshengchanjiaoban', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (31, 4, '原料车间生产运行记录表', 'yl_chejianshengchanyunxing', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (32, 4, '原料生产卸车登记表', 'yl_shengchanxiechedegji', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (33, 4, '原料车间中控室原始记录表', 'yl_chejianzhongkongshiyuanshijilu', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (34, 2, '配煤作业区报表设计', 'jh_peimeizuoyequ', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (35, 2, '化产报表设计', 'jh_huachan', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (36, 2, '干熄焦报表设计', 'jh_ganxijiao', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (37, 3, '炼焦报表设计', 'jh_shaojiao', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (38, 3, '五烧降低主抽电耗跟踪表', 'sj_gengzongbiao', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (39, 3, '熔剂燃料质量管控', 'sj_rongjiranliao', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (40, 3, '脱硫脱硝工艺参数采集', 'sj_tuoliutuoxiaogongyicaiji', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (41, 3, '5#脱硫系统运行日报', 'sj_tuoliu', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (42, 5, '二空压站运行记录表', 'nj_twokong', '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (43, 5, '三空压站运行记录表', 'nj_threekong', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (44, 5, '四空压站运行记录表', 'nj_fourkong', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (45, 5, '新一空压站运行记录表', 'nj_xinyikong', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (46, 1, '高炉炉顶布料作业日报', 'gl_ludingbuliao_day', '1', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (47, 4, '筛下粉统计', 'ygl_shaixiafentongji_day', '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (48, 4, '中焦外排记录', 'ygl_zhongjiaowaipai_month', '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (49, 4, '煤头外排记录', 'ygl_meitouwaipai_month', '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (50, 4, '供料车间运输车辆统计_录入', 'ygl_gongliaochejian_month', '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (51, 4, '炼焦煤每日库存动态表', 'ygl_Liaojiaomei_day', '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (52, 3, '6#脱硫系统运行日报', 'sj_tuoliu', '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (53, 5, '三四柜区运行记录表', 'nj_sansigui_day', '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (54, 4, '成品仓出入记录', 'ygl_chengpincang', '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (55, 4, '进厂物资（精煤）化验记录表', 'ygl_jinchangwuzi', '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (56, 3, '6#脱硝运行记录表月报', 'sj_tuoxiaoyunxingjilu', '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (66, 0, 'gl_peiliaodan', 'gl_peiliaodan', '1', NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for report_category_template
-- ----------------------------
DROP TABLE IF EXISTS `report_category_template`;
CREATE TABLE `report_category_template`  (
  `id` bigint(30) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `report_category_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板的编码',
  `sequence` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '序号名称',
  `template_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板的名称',
  `template_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板所在路径',
  `template_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板类型名称',
  `template_lang` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板语言',
  `excel_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件生成目录',
  `build` int(11) NULL DEFAULT NULL COMMENT '周期',
  `build_unit` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '单位',
  `build_delay` int(11) NULL DEFAULT NULL COMMENT '文件生成延迟',
  `build_delay_unit` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件生成延迟单位',
  `cron` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'cron表达式',
  `forbid` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '是否禁止 1是 0否',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `attr1` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `attr2` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `attr3` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `attr4` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `attr5` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 133 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '分类模板配置' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of report_category_template
-- ----------------------------
INSERT INTO `report_category_template` VALUES (1, 'gl_chutiezuoye_day', '6高炉', '出铁作业 日报', 'D:\\template\\6高炉\\出铁作业日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (2, 'gl_chutiezuoye_month', '8高炉', '出铁作业 月报', 'D:\\template\\8高炉\\出铁作业月报表.xlsx', 'report_month', 'cn_zh', NULL, NULL, NULL, 5, 'MINUTE', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (3, 'gl_bentilushenjingya_day', '6高炉', '高炉本体炉身静压 日报', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (4, 'gl_bentilushenjingya_month', '6高炉', '高炉本体炉身静压 月报', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (5, 'gl_bentiwendu_day', '6高炉', '高炉本体温度日报表', 'D:\\template\\6高炉\\高炉本体温度日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (6, 'gl_bentiwendu_month', '6高炉', '高炉本体温度月报表', 'D:\\template\\6高炉\\高炉本体温度月报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (7, 'gl_lengquebiwendu_day', '6高炉', '高炉冷却壁温度 日报', 'D:\\template\\6高炉\\高炉冷却壁温度日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (8, 'gl_lengquebiwendu_month', '6高炉', '高炉冷却壁温度 月报', 'D:\\template\\6高炉\\高炉冷却壁温度月报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (9, 'gl_ludingzhuangliaozuoye_day1', '6高炉', '高炉炉顶装料作业 日报1', 'D:\\template\\6高炉\\炉顶装料作业日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (10, 'gl_ludingzhuangliaozuoye_day2', '6高炉', '高炉炉顶装料作业 日报2', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (11, 'gl_refenglu_day', '6高炉', '热风炉 日报', 'D:\\template\\6高炉\\热风炉日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (12, 'gl_refenglu_month', '6高炉', '热风炉 月报', 'D:\\template\\6高炉\\热风炉月报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (13, 'gl_jswgaolu_day', '6高炉', '高炉 日报', 'D:\\template\\6高炉\\高炉日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (14, 'gl_taisu1_month', '6高炉', '高炉 月报', 'D:\\template\\6高炉\\高炉月报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (15, 'gl_jswzhinengpinghengjisuan', '6高炉', 'JSW质能平衡计算报表', 'D:\\template\\cn_zh\\bf5\\质能平衡报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (17, 'ygl_yuanliaogongliao_day', 'ygl1', '原料供料日报', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (18, 'ygl_yichanggenzong', '原供料', '供料异常跟踪表', 'D:\\template\\原供料\\7.供料车间异常用仓信息记录_录入.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (19, 'ygl_chejianjikongzhongxinjioajieban', '原供料', '供料车间集控中心交接班记录', 'D:\\template\\原供料\\1.供料车间集控中心交接班记录.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (20, 'gl_chejianwuliaowaipai', '原供料', '供料车间物料外排统计表', '', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (21, 'yl_duihunyunkuangfenpeibi', 'yl1', '原料堆混匀矿粉配比通知单', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (22, 'yl_shujujilu', 'yl1', '原料数据记录表', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (23, 'yl_hunyunkuangfenA4ganzhuanshi', 'yl1', '原料混匀矿粉A4干转湿配比换算计算表', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (24, 'yl_lianjiaomeikucundongtai_day', 'yl1', '原料炼焦煤每日库存动态表', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (25, 'yl_jinchangwuzijingmeihuayan', 'yl1', '原料进厂物资精煤化验记录表', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (26, 'yl_chejianshengchanjiaoban', '原供料', '原料车间生产交班表', 'D:\\template\\原供料\\9.原料车间生产交班表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (27, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表', 'D:\\template\\原供料\\12.原料车间生产运行记录表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (28, 'ygl_shengchanxiechedegji', '原供料', '原料生产卸车登记表', 'D:\\template\\原供料\\10.各作业班生产卸车登记表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (29, 'yl_chejianzhongkongshiyuanshijilu', 'yl1', '原料车间中控室原始记录表', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (35, 'sj_gengzongbiao', '烧结', '五六烧主抽电耗跟踪表', 'D:\\template\\烧结\\5烧6烧主抽电耗跟踪表.xlsx', 'report_day', 'cn_zh', NULL, 1, 'HOUR', 1, 'MINUTE', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (36, 'sj_rongjiranliao', 'sj1', '熔剂燃料质量管控', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (37, 'sj_tuoliutuoxiaogongyicaiji', '烧结', '6#脱硫脱硝工艺参数采集', 'D:\\template\\烧结\\6#脱硫脱硝工艺参数采集.xlsx', 'report_day', 'cn_zh', NULL, 1, 'HOUR', 1, 'MINUTE', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (38, 'sj_tuoliu', '烧结', '5#脱硫系统运行日报', 'D:\\template\\烧结\\5#脱硫系统运行日报.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (39, 'nj_twokong', '能介', '二空压站运行记录表', 'D:\\template\\能介\\二空压站运行记录表.xlsx', 'report_day', 'cn_zh', NULL, 1, 'HOUR', 5, 'MINUTE', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (40, 'nj_threekong', '能介', '三空压站运行记录表', 'D:\\template\\能介\\三空压站运行记录表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, 5, 'MINUTE', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (41, 'nj_fourkong', '能介', '四空压站运行记录表', 'D:\\template\\能介\\四空压站运行记录表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (42, 'nj_xinyikong', '能介', '新一空压站运行记录表', 'D:\\template\\能介\\一空压站运行记录表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (44, 'gl_ludingbuliao_day', '6高炉', '高炉炉顶布料作业日报', 'D:\\template\\6高炉\\炉顶布料作业日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (45, 'ygl_shaixiafentongji_day', '原供料', '筛下粉统计', 'D:\\template\\原供料\\2.筛下粉统计.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (46, 'ygl_zhongjiaowaipai_month', '原供料', '中焦外排记录', 'D:\\template\\原供料\\3.中焦外排记录.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (47, 'ygl_meitouwaipai_month', '原供料', '煤头外排记录', 'D:\\template\\原供料\\4.煤头外排记录.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (48, 'ygl_gongliaochejian_month', '原供料', '供料车间运输车辆统计_录入', 'D:\\template\\原供料\\5.供料车间运输车辆统计_录入.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (49, 'ygl_Liaojiaomei_day', '原供料', '炼焦煤每日库存动态表', 'D:\\template\\原供料\\8.炼焦煤每日库存动态表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (50, 'sj_tuoliu5', '烧结', '5#脱硫系统运行日报', 'D:\\template\\烧结\\5#脱硫系统运行日报.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (51, 'sj_tuoliu6', '烧结', '6#脱硫系统运行日报', 'D:\\template\\烧结\\6#脱硫系统运行日报.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (52, 'nj_sansigui_day', '能介', '三四柜区运行记录表', 'D:\\template\\能介\\三四柜区运行记录表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (54, 'ygl_chengpincang', '原供料', '成品仓出入记录', 'D:\\template\\原供料\\6.成品仓出入记录.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (55, 'ygl_jinchangwuzi', '原供料', '进厂物资（精煤）化验记录表', 'D:\\template\\原供料\\11.进厂物资（精煤）化验记录表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (56, 'sj_tuoxiaoyunxingjilu', '烧结', '6#脱硝运行记录表月报', 'D:\\template\\烧结\\6#脱硝运行记录表月报.xlsx', 'report_month', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (57, 'sj_shaojieji_day', '6烧结', '6#烧结机生产日报', 'D:\\template\\烧结\\6#烧结机生产日报.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (58, 'sj_shaojieji_day', '5烧结', '5#烧结机生产日报', 'D:\\template\\烧结\\5#烧结机生产日报.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (59, 'nj_onekongcount', '能介', '一空压站启停次数表', 'D:\\template\\能介\\一空压站启停次数表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (60, 'nj_twokongcount', '能介', '二空压站启停次数表', 'D:\\template\\能介\\二空压站启停次数表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (61, 'nj_threekongcount', '能介', '三空压站启停次数表', 'D:\\template\\能介\\三空压站启停次数表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (62, 'nj_fourkongcount', '能介', '四空压站启停次数表', 'D:\\template\\能介\\四空压站启停次数表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (63, 'nj_yasuokongqi', '能介', '压缩空气生产情况汇总表', 'D:\\template\\能介\\压缩空气生产情况汇总表.xlsx', 'report_day', 'cn_zh', NULL, 1, 'HOUR', 1, 'DATE', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (64, 'nj_meiqihunhemei', '能介', '煤气柜作业区混合煤气情况表', 'D:\\template\\能介\\煤气柜作业区混合煤气情况表.xlsx', 'report_day', 'cn_zh', NULL, 1, 'HOUR', 1, 'DATE', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (65, 'nj_guifengjimeiyaji', '能介', '柜区风机煤压机时间统计表', 'D:\\template\\能介\\柜区风机煤压机时间统计表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (67, 'jh_zidongpeimei', '焦化', 'CK67-配煤-自动配煤报表（班）', 'D:\\template\\焦化\\CK67-配煤-自动配煤报表（班）.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (68, 'jh_fensuixidu', '焦化', 'CK67-配煤-粉碎细度报表（月）', 'D:\\template\\焦化\\CK67-配煤-粉碎细度报表（月）.xlsx', 'report_month', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (69, 'jh_cdqcaozuoa', '焦化', 'CK67-干熄焦-CDQ操作运行报表A（日）', 'D:\\template\\焦化\\CK67-干熄焦-CDQ操作运行报表A（日）.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (70, 'jh_chujiaochuchen', '焦化', 'CK67-干熄焦-出焦除尘报表（日）', 'D:\\template\\焦化\\CK67-干熄焦-出焦除尘报表（日）.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (71, 'jh_zhuangmeichuchen', '焦化', 'CK67-干熄焦-装煤除尘报表（日）', 'D:\\template\\焦化\\CK67-干熄焦-装煤除尘报表（日）.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (72, 'jh_cdqchuchen', '焦化', 'CK67-干熄焦-CDQ除尘报表（日）', 'D:\\template\\焦化\\CK67-干熄焦-CDQ除尘报表（日）.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (73, 'jh_shaijiaochuchen', '焦化', 'CK67-干熄焦-筛焦除尘报表（日）', 'D:\\template\\焦化\\CK67-干熄焦-筛焦除尘报表（日）.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (74, 'jh_gufenglengning2', '焦化', 'CK67-化产-鼓风冷凝报表（二）（日）', 'D:\\template\\焦化\\CK67-化产-鼓风冷凝报表（二）（日）.xlsx', 'report_day', 'cn_zh', '', NULL, NULL, NULL, '', NULL, '0', '', '', '', '', '', '');
INSERT INTO `report_category_template` VALUES (75, 'jh_zhilengxunhuanshui', '焦化', 'CK67-化产-制冷循环水报表（日）', 'D:\\template\\焦化\\CK67-化产-制冷循环水报表（日）.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (76, 'jh_zhengan', '焦化', 'CK67-化产-蒸氨报表（日）', 'D:\\template\\焦化\\CK67-化产-蒸氨报表（日）.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (77, 'jh_liuan', '焦化', 'CK67-化产-硫铵报表（日）', 'D:\\template\\焦化\\CK67-化产-硫铵报表（日）.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (78, 'jh_chubenzhengliu', '焦化', 'CK67-化产-粗苯蒸馏报表（日）', 'D:\\template\\焦化\\CK67-化产-粗苯蒸馏报表（日）.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (79, 'jh_zhonglengxiben', '焦化', 'CK67-化产-终冷洗苯报表（日）', 'D:\\template\\焦化\\CK67-化产-终冷洗苯报表（日）.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (80, 'jh_tuoliujiexi', '焦化', 'CK67-化产-脱硫解吸（日）报表设计', 'D:\\template\\焦化\\CK67-化产-脱硫解吸（日）报表设计.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (81, 'jh_zhisuancaozuo', '焦化', 'CK67-化产-制酸操作报表（日）', 'D:\\template\\焦化\\CK67-化产-制酸操作报表（日）.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (82, 'jh_lianjiaoribao', '焦化', 'CK67-炼焦-日报表报表（日&月）', 'D:\\template\\焦化\\CK67-炼焦-日报表报表（日&月）.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, 5, 'MINUTE', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (83, 'jh_jiaolujiare6', '焦化', 'CK67-炼焦-6#焦炉加热制度报表（日）', 'D:\\template\\焦化\\CK67-炼焦-6#焦炉加热制度报表（日）.xlsx', 'report_day', 'cn_zh', NULL, 1, 'HOUR', 5, 'MINUTE', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (84, 'jh_jiaolujiare7', '焦化', 'CK67-炼焦-7#焦炉加热制度报表（日）', 'D:\\template\\焦化\\CK67-炼焦-7#焦炉加热制度报表（日）.xlsx', 'report_day', 'cn_zh', NULL, 1, 'HOUR', 5, 'MINUTE', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (85, 'jh_luwenjilu6', '焦化', 'CK67-炼焦-6#炉温记录报表（日）', 'D:\\template\\焦化\\CK67-炼焦-6#炉温记录报表（日）.xlsx', 'report_day', 'cn_zh', NULL, 1, 'HOUR', 5, 'MINUTE', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (86, 'jh_luwenjilu7', '焦化', 'CK67-炼焦-7#炉温记录报表（日）', 'D:\\template\\焦化\\CK67-炼焦-7#炉温记录报表（日）.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (87, 'sj_liushaogycanshu', '6烧结', '4小时发布-六烧主要工艺参数及实物质量情况日报', 'D:\\template\\烧结\\4小时发布-六烧主要工艺参数及实物质量情况日报.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (88, 'gl_chutiezuoye_day', '8高炉', '出铁作业 日报', 'D:\\template\\8高炉\\出铁作业日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, 5, 'MINUTE', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (89, 'gl_bentiwendu_day', '8高炉', '高炉耐材温度日报表', 'D:\\template\\8高炉\\高炉耐材温度日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, 20, 'MINUTE', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (90, 'gl_bentiwendu_month', '8高炉', '高炉耐材温度月报表', 'D:\\template\\8高炉\\高炉耐材温度月报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (91, 'gl_lengquebiwendu_day', '8高炉', '高炉冷却壁温度 日报', 'D:\\template\\8高炉\\高炉冷却壁温度日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (92, 'gl_lengquebiwendu_month', '8高炉', '高炉冷却壁温度 月报', 'D:\\template\\8高炉\\高炉冷却壁温度月报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (93, 'gl_ludingzhuangliaozuoye_day1', '8高炉', '高炉炉顶装料作业 日报1', 'D:\\template\\8高炉\\炉顶装料作业日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (94, 'gl_jswgaolu_day', '8高炉', '高炉 日报', 'D:\\template\\8高炉\\高炉日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (95, 'gl_taisu1_month', '8高炉', '高炉 月报', 'D:\\template\\8高炉\\高炉月报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (96, 'gl_ludingbuliao_day', '8高炉', '高炉炉顶布料作业日报', 'D:\\template\\8高炉\\炉顶布料作业日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (97, 'gl_ludingzhuangliaozuoye_day1', '8高炉', '高炉炉顶装料作业 日报1', 'D:\\template\\8高炉\\炉顶装料作业日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (98, 'sj_liushaogycanshu', '5烧结', '4小时发布-五烧主要工艺参数及实物质量情况日报', 'D:\\template\\烧结\\4小时发布-五烧主要工艺参数及实物质量情况日报.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (100, 'gl_refenglu_day', '8高炉', '热风炉 日报', 'D:\\template\\8高炉\\热风炉日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (101, 'gl_refenglu_month', '8高炉', '热风炉 月报', 'D:\\template\\8高炉\\热风炉月报表.xlsx', 'report_month', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (102, 'nj_dongli_month', '能介', '动力分厂主要设备开停机信息表', 'D:\\template\\能介\\动力分厂主要设备开停机信息表.xlsx', 'report_month', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (103, 'jh_cdqcaozuob', '焦化', 'CK67-干熄焦-CDQ操作运行报表B（日）', 'D:\\template\\焦化\\CK67-干熄焦-CDQ操作运行报表B（日）.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (106, 'jh_gufenglengning1', '焦化', 'CK67-化产-鼓风冷凝报表（一）（日）', 'D:\\template\\焦化\\CK67-化产-鼓风冷凝报表（一）（日）.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (107, 'gl_peiliaodan', '8高炉', '配料单报表', 'D:\\配料单报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (108, 'jh_jlguanjianzhibiao', '焦化', '炼焦-6#-7#焦炉关键指标统计', 'D:\\template\\焦化\\炼焦-6#-7#焦炉关键指标统计.xlsx', 'report_month', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (109, 'nj_qiguidianjian', '能介', '气柜点检表', 'D:\\template\\能介\\气柜点检表.xlsx', 'report_day', 'cn_zh', NULL, 1, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (110, 'jh_peimeiliang', '焦化', '配煤-配煤量月报表', 'D:\\template\\焦化\\配煤-配煤量月报表.xlsx', 'report_month', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (111, 'gl_xiaohao_day', '8高炉', '高炉消耗月报表', 'D:\\template\\8高炉\\高炉消耗月报表.xlsx', 'report_month', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (112, 'gl_lugangwendu_day', '8高炉', '炉缸温度日报', 'D:\\template\\8高炉\\炉缸温度日报.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (113, 'nj_diaojianoneKong_day', '能介', '能源环保部一空压站设备日点检表', 'D:\\template\\能介\\能源环保部一空压站设备日点检表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (114, 'nj_diaojiantwoKong_day', '能介', '能源环保部二空压站设备日点检表', 'D:\\template\\能介\\能源环保部二空压站设备日点检表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (115, 'nj_diaojianthreeKong_day', '能介', '能源环保部三空压站设备日点检表', 'D:\\template\\能介\\能源环保部三空压站设备日点检表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (116, 'nj_diaojianfourKong_day', '能介', '能源环保部四空压站设备日点检表', 'D:\\template\\能介\\能源环保部四空压站设备日点检表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (117, 'nj_qiguidianjianruihua_month', '能介', '气柜区润滑台帐表格', 'D:\\template\\能介\\气柜区润滑台帐表格.xlsx', 'report_month', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (118, 'nj_kongqiya_month', '能介', '空压站设备给油脂标准及加油记录', 'D:\\template\\能介\\空压站设备给油脂标准及加油记录.xlsx', 'report_month', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (119, 'gl_chutiezuoye_month', '6高炉', '出铁作业 月报', 'D:\\template\\6高炉\\出铁作业月报表.xlsx', 'report_month', 'cn_zh', NULL, NULL, NULL, 5, 'MINUTE', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (120, 'gl_zhongdianbuweicanshu', '8高炉', '重点部位参数监控报表', 'D:\\template\\8高炉\\重点部位参数监控报表.xlsx', 'report_month', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (121, 'jh_zhibiaoguankong', '焦化', '炼焦-关键指标管控', 'D:\\template\\焦化\\炼焦-关键指标管控.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (122, 'gl_zhongdianbuweicanshutubiao', '8高炉', '重点部位参数监控-图表', 'D:\\template\\8高炉\\重点部位参数监控-图表.xlsx', 'report_year', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (123, 'gl_gaolubuliao', '8高炉', '高炉布料报表', 'D:\\template\\8高炉\\重点部位参数监控-图表.xlsx', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (124, 'gl_gaolupenmei', '8高炉', '高炉喷煤运行报表', 'D:\\template\\8高炉\\高炉喷煤运行报表v1.xlsx', 'report_month', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (125, 'jh_zhuyaogycs', '焦化', '炼焦-主要工艺参数', 'D:\\template\\焦化\\炼焦-主要工艺参数.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (126, 'gl_gaolubuliao', '8高炉', '高炉布料报表', 'D:\\template\\8高炉\\高炉布料报表.xlsx', 'report_year', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (127, 'sj_rongji', '烧结', '熔剂燃料质量管控', 'D:\\template\\烧结\\熔剂燃料质量管控.xlsx', 'report_month', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (128, 'hb_6bftrt', '环保', '6BF-TRT日报表', 'D:\\template\\环保\\6BF-TRT日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (129, 'hb_7bftrt', '环保', '7BF-TRT日报表', 'D:\\template\\环保\\7BF-TRT日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (130, 'hb_8bftrt', '环保', '8BF-TRT日报表', 'D:\\template\\环保\\8BF-TRT日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (131, 'gl_gaolupenmei', '6高炉', '高炉喷煤运行报表', 'D:\\template\\6高炉\\高炉喷煤运行报表v1.xlsx', 'report_month', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (132, 'hb_meiqichuchen6bf', '环保', '6BF-煤气布袋除尘报表', 'D:\\template\\环保\\6BF-煤气布袋除尘报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for report_index
-- ----------------------------
DROP TABLE IF EXISTS `report_index`;
CREATE TABLE `report_index`  (
  `id` bigint(30) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `report_category_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分类编码',
  `sequence` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '序号',
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件名称',
  `path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件路径',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `index_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
  `index_lang` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '语言',
  `is_hidden` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '是否隐藏',
  `attr1` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `attr2` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `attr3` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `attr4` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `record_date` datetime(0) NULL DEFAULT NULL COMMENT '报表时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8087 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '报表文件-索引' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of report_index
-- ----------------------------
INSERT INTO `report_index` VALUES (8075, 'gl_zhongdianbuweicanshu', '8高炉', '重点部位参数监控报表_2019-01-17.xlsx', 'D:\\excel\\cn_zh\\8高炉\\月表报\\重点部位参数监控报表_2019-01-17.xlsx', '2019-01-17 09:59:10', '2019-01-17 15:14:31', NULL, 'report_month', 'cn_zh', '0', NULL, NULL, NULL, NULL, '2019-01-17 15:14:27');
INSERT INTO `report_index` VALUES (8079, 'gl_ludingzhuangliaozuoye_day1', '6高炉', '高炉炉顶装料作业 日报1_2019-01-17_16.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉炉顶装料作业 日报1_2019-01-17_16.xlsx', '2019-01-17 10:25:09', '2019-01-17 15:38:49', NULL, 'report_day', 'cn_zh', '0', NULL, NULL, NULL, NULL, '2019-01-17 15:38:47');
INSERT INTO `report_index` VALUES (8080, 'gl_ludingzhuangliaozuoye_day1', '8高炉', '高炉炉顶装料作业 日报1_2019-01-17_16.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉炉顶装料作业 日报1_2019-01-17_16.xlsx', '2019-01-17 10:25:12', '2019-01-17 15:38:51', NULL, 'report_day', 'cn_zh', '0', NULL, NULL, NULL, NULL, '2019-01-17 15:38:50');
INSERT INTO `report_index` VALUES (8082, 'gl_gaolupenmei', '8高炉', '高炉喷煤运行报表_2019-01-17.xlsx', 'D:\\excel\\cn_zh\\8高炉\\月表报\\高炉喷煤运行报表_2019-01-17.xlsx', '2019-01-17 10:43:35', '2019-01-17 10:43:35', NULL, 'report_month', 'cn_zh', '0', NULL, NULL, NULL, NULL, '2019-01-17 10:43:19');
INSERT INTO `report_index` VALUES (8083, 'hb_6bftrt', '环保', '6BF-TRT日报表_2019-01-17_15.xlsx', 'D:\\excel\\cn_zh\\环保\\日报表\\6BF-TRT日报表_2019-01-17_15.xlsx', '2019-01-17 14:34:00', '2019-01-17 14:34:00', NULL, 'report_day', 'cn_zh', '0', NULL, NULL, NULL, NULL, '2019-01-17 14:32:21');
INSERT INTO `report_index` VALUES (8084, 'hb_meiqichuchen6bf', '环保', '6BF-煤气布袋除尘报表_2019-01-17_15.xlsx', 'D:\\excel\\cn_zh\\环保\\日报表\\6BF-煤气布袋除尘报表_2019-01-17_15.xlsx', '2019-01-17 14:38:40', '2019-01-17 14:38:40', NULL, 'report_day', 'cn_zh', '0', NULL, NULL, NULL, NULL, '2019-01-17 14:36:54');
INSERT INTO `report_index` VALUES (8085, 'gl_ludingbuliao_day', '6高炉', '高炉炉顶布料作业日报_2019-01-17_16.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉炉顶布料作业日报_2019-01-17_16.xlsx', '2019-01-17 15:51:04', '2019-01-17 15:51:04', NULL, 'report_day', 'cn_zh', '0', NULL, NULL, NULL, NULL, '2019-01-17 15:51:00');
INSERT INTO `report_index` VALUES (8086, 'gl_ludingbuliao_day', '8高炉', '高炉炉顶布料作业日报_2019-01-17_16.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉炉顶布料作业日报_2019-01-17_16.xlsx', '2019-01-17 15:51:06', '2019-01-17 15:51:06', NULL, 'report_day', 'cn_zh', '0', NULL, NULL, NULL, NULL, '2019-01-17 15:51:04');

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `id` bigint(30) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '编码',
  `action` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '值',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 69 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '配置' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, 'LANGUAGE_CODE', 'cn_zh', '所属语言');
INSERT INTO `sys_config` VALUES (2, 'gl_bentiwendu_day', 'com.cisdi.steel.module.job.a1.BentiwenduDayJob', '高炉本体温度日报表');
INSERT INTO `sys_config` VALUES (3, 'gl_bentiwendu_month', 'com.cisdi.steel.module.job.a1.BentiwenduMonthJob', ' 高炉本体温度 月报');
INSERT INTO `sys_config` VALUES (4, 'gl_chutiezuoye_day', 'com.cisdi.steel.module.job.a1.ChutiezuoyeDayJob', '出铁作业日报表');
INSERT INTO `sys_config` VALUES (5, 'gl_jswgaolu_day', 'com.cisdi.steel.module.job.a1.GaoLuDayJob', '6高炉 日报');
INSERT INTO `sys_config` VALUES (6, 'gl_taisu1_month', 'com.cisdi.steel.module.job.a1.GaoLuMonthJob', '6高炉 月报');
INSERT INTO `sys_config` VALUES (7, 'gl_lengquebiwendu_day', 'com.cisdi.steel.module.job.a1.LengquebiwenduDayJob', '高炉冷却壁温度日报表');
INSERT INTO `sys_config` VALUES (8, 'gl_lengquebiwendu_month', 'com.cisdi.steel.module.job.a1.LengquebiwenduMonthJob', '高炉冷却壁温度 月报');
INSERT INTO `sys_config` VALUES (9, 'gl_ludingbuliao_day', 'com.cisdi.steel.module.job.a1.LudingbuliaoJob', '炉顶装料作业日报表');
INSERT INTO `sys_config` VALUES (10, 'gl_ludingzhuangliaozuoye_day1', 'com.cisdi.steel.module.job.a1.LudingzhuangliaoDayJob', '炉顶装料作业日报表');
INSERT INTO `sys_config` VALUES (11, 'jh_cdqcaozuo', 'com.cisdi.steel.module.job.a2.CDQcaozuoJob', 'CK67-CDQ操作运行报表（日）报表');
INSERT INTO `sys_config` VALUES (12, 'jh_cdqchuchen', 'com.cisdi.steel.module.job.a2.CDQchuchenJob', 'CK67-CDQ除尘报表');
INSERT INTO `sys_config` VALUES (13, 'jh_chubenzhengliu', 'com.cisdi.steel.module.job.a2.ChubenzhengliuJob', 'CK67-粗苯蒸馏（日）报表');
INSERT INTO `sys_config` VALUES (14, 'jh_chujiaochuchen', 'com.cisdi.steel.module.job.a2.ChujiaochuchenJob', 'CK67-出焦除尘报表');
INSERT INTO `sys_config` VALUES (15, 'jh_fensuixidu', 'com.cisdi.steel.module.job.a2.FensuixiduJob', 'CK67-粉碎细度（月）报表');
INSERT INTO `sys_config` VALUES (16, 'jh_ganxijiao', 'com.cisdi.steel.module.job.a2.GanxijiaoJob', '干熄焦报表设计');
INSERT INTO `sys_config` VALUES (17, 'jh_gufenglengning', 'com.cisdi.steel.module.job.a2.GufenglengningJob', 'CK67-鼓风冷凝(日)报表');
INSERT INTO `sys_config` VALUES (18, 'jh_huachan', 'com.cisdi.steel.module.job.a2.HuachanJob', '化产报表设计');
INSERT INTO `sys_config` VALUES (19, 'jh_jiaolujiare6', 'com.cisdi.steel.module.job.a2.Jiaolujiare6Job', 'CK67-6#焦炉加热制度表（日）报表');
INSERT INTO `sys_config` VALUES (20, 'jh_jiaolujiare7', 'com.cisdi.steel.module.job.a2.Jiaolujiare7Job', 'CK67-7#焦炉加热制度表（日）报表');
INSERT INTO `sys_config` VALUES (21, 'jh_shaojiao', 'com.cisdi.steel.module.job.a2.LianjiaoJob', '炼焦报表设计');
INSERT INTO `sys_config` VALUES (22, 'jh_lianjiaoribao', 'com.cisdi.steel.module.job.a2.LianjiaoribaoJob', 'CK67-炼焦日报表（班日、月）报表');
INSERT INTO `sys_config` VALUES (23, 'jh_lianjiaoluwen', 'com.cisdi.steel.module.job.a2.LianjiaoWDJob', '炼焦炉温报表设计');
INSERT INTO `sys_config` VALUES (24, 'jh_liuan', 'com.cisdi.steel.module.job.a2.LiuanJob', 'CK67-硫铵（日）报表');
INSERT INTO `sys_config` VALUES (25, 'jh_luwenjilu6', 'com.cisdi.steel.module.job.a2.Luwenjilu6Job', 'CK67-6#炉温记录报表');
INSERT INTO `sys_config` VALUES (26, 'jh_luwenjilu7', 'com.cisdi.steel.module.job.a2.Luwenjilu7Job', 'CK67-7#炉温记录报表');
INSERT INTO `sys_config` VALUES (27, 'jh_peimeizuoyequ', 'com.cisdi.steel.module.job.a2.PeimeizuoyequJob', '配煤作业区报表设计');
INSERT INTO `sys_config` VALUES (28, 'jh_shaijiaochuchen', 'com.cisdi.steel.module.job.a2.ShaijiaochuchenJob', 'CK67-筛焦除尘报表');
INSERT INTO `sys_config` VALUES (29, 'jh_tuoliujiexi', 'com.cisdi.steel.module.job.a2.TuoliujiexiJob', 'CK67-脱硫解吸（日）');
INSERT INTO `sys_config` VALUES (30, 'jh_zhengan', 'com.cisdi.steel.module.job.a2.ZhenganJob', 'CK67-蒸氨（日）报表');
INSERT INTO `sys_config` VALUES (31, 'jh_zhilengxunhuanshui', 'com.cisdi.steel.module.job.a2.ZhilengxunhuanshuiJob', 'CK67-制冷循环水（日）报表');
INSERT INTO `sys_config` VALUES (32, 'jh_zhisuancaozuo', 'com.cisdi.steel.module.job.a2.ZhisuancaozuoJob', 'CK67-制酸操作（日）');
INSERT INTO `sys_config` VALUES (33, 'jh_zhonglengxiben', 'com.cisdi.steel.module.job.a2.ZhonglengxibenJob', 'CK67-终冷洗苯报表');
INSERT INTO `sys_config` VALUES (34, 'jh_zhuangmeichuchen', 'com.cisdi.steel.module.job.a2.ZhuangmeichuchenJob', 'CK67-装煤除尘报表');
INSERT INTO `sys_config` VALUES (35, 'jh_zidongpeimei', 'com.cisdi.steel.module.job.a2.ZidongpeimeiJob', 'CK67-自动配煤（班）报表');
INSERT INTO `sys_config` VALUES (36, 'sj_liushaogycanshu5', 'com.cisdi.steel.module.job.a3.GycanshuJob5', '4小时发布-五烧主要工艺参数及实物质量情况日报');
INSERT INTO `sys_config` VALUES (37, 'sj_liushaogycanshu6', 'com.cisdi.steel.module.job.a3.GycanshuJob6', '4小时发布-六烧主要工艺参数及实物质量情况日报');
INSERT INTO `sys_config` VALUES (38, 'sj_shaojieji_day', 'com.cisdi.steel.module.job.a3.JiejiJob5', '5#烧结机生产日报');
INSERT INTO `sys_config` VALUES (39, 'sj_shaojieji6_day', 'com.cisdi.steel.module.job.a3.JiejiJob6', '6#烧结机生产日报');
INSERT INTO `sys_config` VALUES (40, 'sj_tuoliu5', 'com.cisdi.steel.module.job.a3.TuoliuJob5', '5#脱硫系统运行日报');
INSERT INTO `sys_config` VALUES (41, 'sj_tuoliu6', 'com.cisdi.steel.module.job.a3.TuoliuJob6', '6#脱硫系统运行日报');
INSERT INTO `sys_config` VALUES (42, 'sj_tuoliutuoxiaogongyicaiji', 'com.cisdi.steel.module.job.a3.TuoliuTuoxiaoGongyiJob', '脱硫脱硝工艺参数采集');
INSERT INTO `sys_config` VALUES (43, 'sj_tuoxiaoyunxingjilu', 'com.cisdi.steel.module.job.a3.TuoXiaoJob', '脱硝运行记录表');
INSERT INTO `sys_config` VALUES (44, 'sj_gengzongbiao', 'com.cisdi.steel.module.job.a3.ZhuChouWuAndLiuJob', '五烧六烧主抽电耗跟踪表');
INSERT INTO `sys_config` VALUES (45, 'ygl_chengpincang', 'com.cisdi.steel.module.job.a4.ChengPinCangJob', '成品仓出入记录');
INSERT INTO `sys_config` VALUES (46, 'gl_chejianjikongzhongxinjioajieban', 'com.cisdi.steel.module.job.a4.GongliaochejianJob', '供料车间物料外排统计表');
INSERT INTO `sys_config` VALUES (47, 'ygl_gongliaochejian_month', 'com.cisdi.steel.module.job.a4.GongliaochejianMonthJob', '供料车间运输车辆统计_录入');
INSERT INTO `sys_config` VALUES (48, 'ygl_yichanggenzong', 'com.cisdi.steel.module.job.a4.GongliaochejianyichangJob', '供料异常跟踪表');
INSERT INTO `sys_config` VALUES (49, 'ygl_jinchangwuzi', 'com.cisdi.steel.module.job.a4.JinchangwuziJob', '进厂物资（精煤）化验记录表');
INSERT INTO `sys_config` VALUES (50, 'ygl_Liaojiaomei_day', 'com.cisdi.steel.module.job.a4.LiaojiaomeiDayJob', '供料车间运输车辆统计_录入');
INSERT INTO `sys_config` VALUES (51, 'ygl_meitouwaipai_month', 'com.cisdi.steel.module.job.a4.MeitouwaipaiMonthJob', '煤头外排记录');
INSERT INTO `sys_config` VALUES (52, 'ygl_shaixiafentongji_day', 'com.cisdi.steel.module.job.a4.ShaixiafentongjiDayJob', '筛下粉统计');
INSERT INTO `sys_config` VALUES (53, 'ygl_shengchanxiechedegji', 'com.cisdi.steel.module.job.a4.ShengchanxiechedegjiJob', '生产卸车登记表');
INSERT INTO `sys_config` VALUES (54, 'yl_chejianshengchanjiaoban', 'com.cisdi.steel.module.job.a4.YuanliaochejianshenchanjiaojiebanJob', '原料车间生产交班表');
INSERT INTO `sys_config` VALUES (55, 'yl_chejianshengchanyunxing', 'com.cisdi.steel.module.job.a4.YuanliaochejianyunxingjiluJob', '原料车间生产运行记录表');
INSERT INTO `sys_config` VALUES (56, 'ygl_zhongjiaowaipai_month', 'com.cisdi.steel.module.job.a4.ZhongjiaowaipaiMonthJob', '中焦外排记录');
INSERT INTO `sys_config` VALUES (57, 'nj_fourkongcount', 'com.cisdi.steel.module.job.a5.FourkongCountJob', '四空压站启停次数表');
INSERT INTO `sys_config` VALUES (58, 'nj_fourkong', 'com.cisdi.steel.module.job.a5.FourkongJob', '四空压站运行记录表');
INSERT INTO `sys_config` VALUES (59, 'nj_guifengjimeiyaji', 'com.cisdi.steel.module.job.a5.GuifengjimeiyajiJob', '柜区风机煤压机时间统计表');
INSERT INTO `sys_config` VALUES (60, 'nj_meiqihunhemei', 'com.cisdi.steel.module.job.a5.MeiqihunhemeiJob', '煤气柜作业区混合煤气情况表');
INSERT INTO `sys_config` VALUES (61, 'nj_xinyikong', 'com.cisdi.steel.module.job.a5.NewOnekongJob', '新一空压站运行记录表');
INSERT INTO `sys_config` VALUES (62, 'nj_onekongcount', 'com.cisdi.steel.module.job.a5.OnekongCountJob', '一空压站启停次数表');
INSERT INTO `sys_config` VALUES (63, 'nj_sansigui_day', 'com.cisdi.steel.module.job.a5.ThreeFourKongJob', '三四柜区运行记录表');
INSERT INTO `sys_config` VALUES (64, 'nj_threekongcount', 'com.cisdi.steel.module.job.a5.ThreekongCountJob', '三空压站启停次数表');
INSERT INTO `sys_config` VALUES (65, 'nj_threekong', 'com.cisdi.steel.module.job.a5.ThreekongJob', '三空压站运行记录表');
INSERT INTO `sys_config` VALUES (66, 'nj_twokongcount', 'com.cisdi.steel.module.job.a5.TwokongCountJob', '二空压站启停次数表');
INSERT INTO `sys_config` VALUES (67, 'nj_twokong', 'com.cisdi.steel.module.job.a5.TwokongJob', '二空压站运行记录表');
INSERT INTO `sys_config` VALUES (68, 'nj_yasuokongqi', 'com.cisdi.steel.module.job.a5.YasuoKongQiJob', '压缩空气生产情况汇总表');

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '名称',
  `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '编码',
  `type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类型 1父类 没有表示子类',
  `parent_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上级编码',
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统字典' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------
INSERT INTO `sys_dict` VALUES (1, '报表类型', NULL, '1', 'OPTION_REPORT_TYPE', NULL);
INSERT INTO `sys_dict` VALUES (2, '小时', 'OPTION_REPORT_TYPE', NULL, 'report_hour', 1);
INSERT INTO `sys_dict` VALUES (3, '4小时', 'OPTION_REPORT_TYPE', NULL, '4hour_report', 2);
INSERT INTO `sys_dict` VALUES (4, '班报表', 'OPTION_REPORT_TYPE', NULL, 'report_class', 3);
INSERT INTO `sys_dict` VALUES (5, '日报表', 'OPTION_REPORT_TYPE', NULL, 'report_day', 4);
INSERT INTO `sys_dict` VALUES (6, '周表报', 'OPTION_REPORT_TYPE', NULL, 'report_week', 5);
INSERT INTO `sys_dict` VALUES (7, '月表报', 'OPTION_REPORT_TYPE', NULL, 'report_month', 6);
INSERT INTO `sys_dict` VALUES (8, '年表报', 'OPTION_REPORT_TYPE', NULL, 'report_year', 7);

SET FOREIGN_KEY_CHECKS = 1;
