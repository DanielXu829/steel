/*
 Navicat Premium Data Transfer

 Source Server         : 赛迪
 Source Server Type    : MySQL
 Source Server Version : 50560
 Source Host           : 10.66.3.221:3306
 Source Schema         : steel

 Target Server Type    : MySQL
 Target Server Version : 50560
 File Encoding         : 65001

 Date: 06/12/2018 15:44:22
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
-- Records of QRTZ_FIRED_TRIGGERS
-- ----------------------------
INSERT INTO `QRTZ_FIRED_TRIGGERS` VALUES ('schedulerFactoryBean', 'NON_CLUSTERED1543808175118', 'triggertest', '高炉', 'NON_CLUSTERED', 1543808198544, 1543808220000, 5, 'ACQUIRED', NULL, NULL, '0', '0');

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
INSERT INTO `QRTZ_TRIGGERS` VALUES ('schedulerFactoryBean', 'triggertest', '高炉', 'test', '高炉', NULL, 1543808220000, 1543808198057, 5, 'ACQUIRED', 'CRON', 1542609734000, 0, NULL, 0, '');

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
) ENGINE = InnoDB AUTO_INCREMENT = 66 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '报表分类' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of report_category
-- ----------------------------
INSERT INTO `report_category` VALUES (1, 0, '高炉', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (2, 0, '焦化', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (3, 0, '烧结', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (4, 0, '原供料', NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category` VALUES (5, 0, '能介', NULL, '1', NULL, NULL, NULL, NULL, NULL, NULL);
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
  `build_delay` int(11) NULL DEFAULT NULL COMMENT '文件生成延迟',
  `build_delay_unit` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件生成延迟单位',
  `forbid` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '0' COMMENT '是否禁止 1是 0否',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `attr1` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `attr2` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `attr3` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `attr4` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `attr5` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '分类模板配置' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of report_category_template
-- ----------------------------
INSERT INTO `report_category_template` VALUES (1, 'gl_chutiezuoye_day', '6高炉', '出铁作业 日报', 'D:\\template\\6高炉\\出铁作业日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (2, 'gl_chutiezuoye_month', '6高炉', '出铁作业 月报', 'D:\\template\\cn_zh\\bf5\\出鐵作業月報表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (3, 'gl_bentilushenjingya_day', '6高炉', '高炉本体炉身静压 日报', 'D:\\template\\cn_zh\\bf5\\高爐本體爐身靜壓日報表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (4, 'gl_bentilushenjingya_month', '6高炉', '高炉本体炉身静压 月报', 'D:\\template\\cn_zh\\bf5\\高爐本體爐身靜壓月報表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (5, 'gl_bentiwendu_day', '6高炉', '高炉本体温度日报表', 'D:\\template\\6高炉\\高炉本体温度日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (6, 'gl_bentiwendu_month', '6高炉', '高炉本体温度月报表', 'D:\\template\\6高炉\\高炉本体温度月报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (7, 'gl_lengquebiwendu_day', '6高炉', '高炉冷却壁温度 日报', 'D:\\template\\6高炉\\高炉冷却壁温度日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (8, 'gl_lengquebiwendu_month', '6高炉', '高炉冷却壁温度 月报', 'D:\\template\\6高炉\\高炉冷却壁温度月报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (9, 'gl_ludingzhuangliaozuoye_day1', '6高炉', '高炉炉顶装料作业 日报1', 'D:\\template\\6高炉\\炉顶装料作业日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (10, 'gl_ludingzhuangliaozuoye_day2', '6高炉', '高炉炉顶装料作业 日报2', 'D:\\template\\cn_zh\\bf5\\爐頂佈料作業日報表2.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (11, 'gl_refenglu_day', '6高炉', '热风炉 日报', 'D:\\template\\cn_zh\\bf5\\热风炉日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (12, 'gl_refenglu_month', '6高炉', '热风炉 月报', 'D:\\template\\cn_zh\\bf5\\热风炉月报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (13, 'gl_jswgaolu_day', '6高炉', '高炉 日报', 'D:\\template\\6高炉\\高炉日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (14, 'gl_taisu1_month', '6高炉', '高炉 月报', 'D:\\template\\6高炉\\高炉月报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (15, 'gl_jswzhinengpinghengjisuan', '6高炉', 'JSW质能平衡计算报表', 'D:\\template\\cn_zh\\bf5\\质能平衡报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (17, 'ygl_yuanliaogongliao_day', 'ygl1', '原料供料日报', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (18, 'ygl_yichanggenzong', '原供料', '供料异常跟踪表', 'D:\\template\\原供料\\7.供料车间异常用仓信息记录_录入.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (19, 'gl_chejianjikongzhongxinjioajieban', '原供料', '供料车间集控中心交接班记录', 'D:\\template\\原供料\\1.供料车间集控中心交接班记录.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (20, 'gl_chejianwuliaowaipai', '原供料', '供料车间物料外排统计表', '', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (21, 'yl_duihunyunkuangfenpeibi', 'yl1', '原料堆混匀矿粉配比通知单', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (22, 'yl_shujujilu', 'yl1', '原料数据记录表', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (23, 'yl_hunyunkuangfenA4ganzhuanshi', 'yl1', '原料混匀矿粉A4干转湿配比换算计算表', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (24, 'yl_lianjiaomeikucundongtai_day', 'yl1', '原料炼焦煤每日库存动态表', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (25, 'yl_jinchangwuzijingmeihuayan', 'yl1', '原料进厂物资精煤化验记录表', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (26, 'yl_chejianshengchanjiaoban', '原供料', '原料车间生产交班表', 'D:\\template\\原供料\\9.原料车间生产交班表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (27, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表', 'D:\\template\\原供料\\12.原料车间生产运行记录表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (28, 'ygl_shengchanxiechedegji', '原供料', '原料生产卸车登记表', 'D:\\template\\原供料\\10.各作业班生产卸车登记表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (29, 'yl_chejianzhongkongshiyuanshijilu', 'yl1', '原料车间中控室原始记录表', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (30, 'jh_peimeizuoyequ', '焦化', '配煤作业区报表设计', 'D:\\template\\焦化\\CK67-配煤报表设计1107.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (31, 'jh_huachan', '焦化', '化产报表设计', 'D:\\template\\焦化\\CK67-化产报表设计1109.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (32, 'jh_ganxijiao', '焦化', '干熄焦报表设计', 'D:\\template\\焦化\\CK67-干熄焦报表设计1107.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (33, 'jh_shaojiao', '焦化', '炼焦报表设计', 'D:\\template\\焦化\\CK67-炼焦报表设计1107.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (35, 'sj_gengzongbiao', '烧结', '五六烧主抽电耗跟踪表', 'D:\\template\\烧结\\5烧6烧主抽电耗跟踪表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (36, 'sj_rongjiranliao', 'sj1', '熔剂燃料质量管控', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (37, 'sj_tuoliutuoxiaogongyicaiji', '烧结', '6#脱硫脱硝工艺参数采集', 'D:\\template\\烧结\\6#脱硫脱硝工艺参数采集.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (38, 'sj_tuoliu', '烧结', '5#脱硫系统运行日报', 'D:\\template\\烧结\\5#脱硫系统运行日报.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (39, 'nj_twokong', '能介', '二空压站运行记录表', 'D:\\template\\能介\\空压站\\二空压站运行记录表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (40, 'nj_threekong', '能介', '三空压站运行记录表', 'D:\\template\\能介\\空压站\\三空压站运行记录表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (41, 'nj_fourkong', '能介', '四空压站运行记录表', 'D:\\template\\能介\\空压站\\四空压站运行记录表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (42, 'nj_xinyikong', '能介', '新一空压站运行记录表', 'D:\\template\\能介\\空压站\\新一空压站运行记录表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (44, 'gl_ludingbuliao_day', '6高炉', '高炉炉顶布料作业日报', 'D:\\template\\6高炉\\炉顶布料作业日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (45, 'ygl_shaixiafentongji_day', '原供料', '筛下粉统计', 'D:\\template\\原供料\\2.筛下粉统计.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (46, 'ygl_zhongjiaowaipai_month', '原供料', '中焦外排记录', 'D:\\template\\原供料\\3.中焦外排记录.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (47, 'ygl_meitouwaipai_month', '原供料', '煤头外排记录', 'D:\\template\\原供料\\4.煤头外排记录.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (48, 'ygl_gongliaochejian_month', '原供料', '供料车间运输车辆统计_录入', 'D:\\template\\原供料\\5.供料车间运输车辆统计_录入.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (49, 'ygl_Liaojiaomei_day', '原供料', '炼焦煤每日库存动态表', 'D:\\template\\原供料\\8.炼焦煤每日库存动态表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (50, 'sj_tuoliu5', '烧结', '5#脱硫系统运行日报', 'D:\\template\\烧结\\5#脱硫系统运行日报.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (51, 'sj_tuoliu6', '烧结', '6#脱硫系统运行日报', 'D:\\template\\烧结\\6#脱硫系统运行日报.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (52, 'nj_sansigui_day', '能介', '三四柜区运行记录表', 'D:\\template\\能介\\煤气柜\\三四柜区运行记录表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (54, 'ygl_chengpincang', '原供料', '成品仓出入记录', 'D:\\template\\原供料\\6.成品仓出入记录.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (55, 'ygl_jinchangwuzi', '原供料', '进厂物资（精煤）化验记录表', 'D:\\template\\原供料\\11.进厂物资（精煤）化验记录表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (56, 'sj_tuoxiaoyunxingjilu', '烧结', '6#脱硝运行记录表月报', 'D:\\template\\烧结\\6#脱硝运行记录表月报.xlsx', 'report_month', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (57, 'sj_shaojieji6_day', '烧结', '6#烧结机生产日报', 'D:\\template\\烧结\\6#烧结机生产日报.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (58, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报', 'D:\\template\\烧结\\5#烧结机生产日报.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (59, 'nj_onekongcount', '能介', '一空压站启停次数表', 'D:\\template\\能介\\空压站\\一空压站启停次数表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (60, 'nj_twokongcount', '能介', '二空压站启停次数表', 'D:\\template\\能介\\空压站\\二空压站启停次数表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (61, 'nj_threekongcount', '能介', '三空压站启停次数表', 'D:\\template\\能介\\空压站\\三空压站启停次数表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (62, 'nj_fourkongcount', '能介', '四空压站启停次数表', 'D:\\template\\能介\\空压站\\四空压站启停次数表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (63, 'nj_yasuokongqi', '能介', '压缩空气生产情况汇总表', 'D:\\template\\能介\\空压站\\压缩空气生产情况汇总表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (64, 'nj_meiqihunhemei', '能介', '煤气柜作业区混合煤气情况表', 'D:\\template\\能介\\煤气柜\\煤气柜作业区混合煤气情况表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (65, 'nj_guifengjimeiyaji', '能介', '柜区风机煤压机时间统计表', 'D:\\template\\能介\\煤气柜\\柜区风机煤压机时间统计表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (66, 'jh_lianjiaoluwen', '焦化', '炼焦炉温记录表', 'D:\\template\\焦化\\CK67-炼焦炉温报表设计1107.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (67, 'jh_zidongpeimei', '焦化', 'CK67-自动配煤（班）报表', 'D:\\template\\焦化\\CK67-自动配煤（班）报表设计.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (68, 'jh_fensuixidu', '焦化', 'CK67-粉碎细度（月）报表', 'D:\\template\\焦化\\CK67-粉碎细度（月）报表设计.xlsx', 'report_month', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (69, 'jh_cdqcaozuo', '焦化', 'CK67-CDQ操作运行报表（日）报表', 'D:\\template\\焦化\\CK67-CDQ操作运行报表（日）报表设计.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (70, 'jh_chujiaochuchen', '焦化', 'CK67-出焦除尘报表', 'D:\\template\\焦化\\CK67-出焦除尘报表设计.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (71, 'jh_zhuangmeichuchen', '焦化', 'CK67-装煤除尘报表', 'D:\\template\\焦化\\CK67-装煤除尘报表设计.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (72, 'jh_cdqchuchen', '焦化', 'CK67-CDQ除尘报表', 'D:\\template\\焦化\\CK67-CDQ除尘报表设计.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (73, 'jh_shaijiaochuchen', '焦化', 'CK67-筛焦除尘报表', 'D:\\template\\焦化\\CK67-筛焦除尘报表设计.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (74, 'jh_gufenglengning', '焦化', 'CK67-鼓风冷凝(日)报表', 'D:\\template\\焦化\\CK67-鼓风冷凝(日)报表设计.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (75, 'jh_zhilengxunhuanshui', '焦化', 'CK67-制冷循环水（日）报表', 'D:\\template\\焦化\\CK67-制冷循环水（日）报表设计.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (76, 'jh_zhengan', '焦化', 'CK67-蒸氨（日）报表', 'D:\\template\\焦化\\CK67-蒸氨（日）报表设计.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (77, 'jh_liuan', '焦化', 'CK67-硫铵（日）报表', 'D:\\template\\焦化\\CK67-硫铵（日）报表设计.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (78, 'jh_chubenzhengliu', '焦化', 'CK67-粗苯蒸馏（日）报表', 'D:\\template\\焦化\\CK67-粗苯蒸馏（日）报表设计.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (79, 'jh_zhonglengxiben', '焦化', 'CK67-终冷洗苯报表', 'D:\\template\\焦化\\CK67-终冷洗苯报表设计.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (80, 'jh_tuoliujiexi', '焦化', 'CK67-脱硫解吸（日）报表', 'D:\\template\\焦化\\CK67-脱硫解吸（日）报表设计.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (81, 'jh_zhisuancaozuo', '焦化', 'CK67-制酸操作（日）报表', 'D:\\template\\焦化\\CK67-制酸操作（日）报表设计.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (82, 'jh_lianjiaoribao', '焦化', 'CK67-炼焦日报表（班日、月）报表', 'D:\\template\\焦化\\CK67-炼焦日报表（班日、月）报表设计.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (83, 'jh_jiaolujiare6', '焦化', 'CK67-6#焦炉加热制度表（日）报表', 'D:\\template\\焦化\\CK67-6#焦炉加热制度表（日）报表设计.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (84, 'jh_jiaolujiare7', '焦化', 'CK67-7#焦炉加热制度表（日）报表', 'D:\\template\\焦化\\CK67-7#焦炉加热制度表（日）报表设计.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (85, 'jh_luwenjilu6', '焦化', 'CK67-6#炉温记录报表', 'D:\\template\\焦化\\CK67-6#炉温记录报表设计.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (86, 'jh_luwenjilu7', '焦化', 'CK67-7#炉温记录报表', 'D:\\template\\焦化\\CK67-7#炉温记录报表设计.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (87, 'sj_liushaogycanshu6', '烧结', '4小时发布-六烧主要工艺参数及实物质量情况日报', 'D:\\template\\烧结\\4小时发布-六烧主要工艺参数及实物质量情况日报.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (88, 'gl_chutiezuoye_day', '8高炉', '出铁作业 日报', 'D:\\template\\8高炉\\出铁作业日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (89, 'gl_bentiwendu_day', '8高炉', '高炉本体温度日报表', 'D:\\template\\8高炉\\高炉本体温度日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (90, 'gl_bentiwendu_month', '8高炉', '高炉本体温度月报表', 'D:\\template\\8高炉\\高炉本体温度月报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (91, 'gl_lengquebiwendu_day', '8高炉', '高炉冷却壁温度 日报', 'D:\\template\\8高炉\\高炉冷却壁温度日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (92, 'gl_lengquebiwendu_month', '8高炉', '高炉冷却壁温度 月报', 'D:\\template\\8高炉\\高炉冷却壁温度月报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (93, 'gl_ludingzhuangliaozuoye_day1', '8高炉', '高炉炉顶装料作业 日报1', 'D:\\template\\8高炉\\炉顶装料作业日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (94, 'gl_jswgaolu_day', '8高炉', '高炉 日报', 'D:\\template\\8高炉\\高炉日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (95, 'gl_taisu1_month', '8高炉', '高炉 月报', 'D:\\template\\8高炉\\高炉月报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (96, 'gl_ludingbuliao_day', '8高炉', '高炉炉顶布料作业日报', 'D:\\template\\8高炉\\炉顶布料作业日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (97, 'gl_ludingzhuangliaozuoye_day1', '8高炉', '高炉炉顶装料作业 日报1', 'D:\\template\\8高炉\\炉顶装料作业日报表.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `report_category_template` VALUES (98, 'sj_liushaogycanshu5', '烧结', '4小时发布-五烧主要工艺参数及实物质量情况日报', 'D:\\template\\烧结\\4小时发布-五烧主要工艺参数及实物质量情况日报.xlsx', 'report_day', 'cn_zh', NULL, NULL, NULL, '0', NULL, NULL, NULL, NULL, NULL, NULL);

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
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `index_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型',
  `index_lang` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '语言',
  `attr1` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `attr2` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `attr3` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `attr4` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6865 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '报表文件-索引' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of report_index
-- ----------------------------
INSERT INTO `report_index` VALUES (6189, 'sj_tuoliu', '烧结', '5#脱硫系统运行日报_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#脱硫系统运行日报_2018-11-14_16.xlsx', '2018-11-14 16:40:02', '2018-11-14 16:40:02', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6190, 'sj_tuoliu', '烧结', '6#脱硫系统运行日报_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\6#脱硫系统运行日报_2018-11-14_16.xlsx', '2018-11-14 16:40:02', '2018-11-14 16:40:02', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6191, 'ygl_chengpincang', '原供料', '成品仓出入记录_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\成品仓出入记录_2018-11-14_16.xlsx', '2018-11-14 16:40:05', '2018-11-14 16:40:05', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6192, 'jh_peimeizuoyequ', '焦化', '配煤作业区报表设计_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\配煤作业区报表设计_2018-11-14_16.xlsx', '2018-11-14 16:40:09', '2018-11-14 16:40:09', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6193, 'gl_taisu1_month', 'bf6', '高炉 月报_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉 月报_2018-11-14_16.xlsx', '2018-11-14 16:40:17', '2018-11-14 16:40:17', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6194, 'ygl_yichanggenzong', '原供料', '供料异常跟踪表_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\供料异常跟踪表_2018-11-14_16.xlsx', '2018-11-14 16:40:18', '2018-11-14 16:40:18', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6195, 'gl_jswgaolu_day', 'bf6', '高炉 日报_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉 日报_2018-11-14_16.xlsx', '2018-11-14 16:40:24', '2018-11-14 16:40:24', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6196, 'gl_chutiezuoye_day', 'bf6', '出铁作业 日报_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\出铁作业 日报_2018-11-14_16.xlsx', '2018-11-14 16:40:26', '2018-11-14 16:40:26', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6197, 'ygl_Liaojiaomei_day', '原供料', '炼焦煤每日库存动态表_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\炼焦煤每日库存动态表_2018-11-14_16.xlsx', '2018-11-14 16:40:27', '2018-11-14 16:40:27', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6198, 'gl_ludingbuliao_day', 'bf6', '高炉炉顶布料作业日报_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉炉顶布料作业日报_2018-11-14_16.xlsx', '2018-11-14 16:40:34', '2018-11-14 16:40:34', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6199, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-14_16.xlsx', '2018-11-14 16:40:43', '2018-11-14 16:40:43', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6200, 'ygl_shengchanxiechedegji', '原供料', '原料生产卸车登记表_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料生产卸车登记表_2018-11-14_16.xlsx', '2018-11-14 16:40:44', '2018-11-14 16:40:44', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6201, 'ygl_gongliaochejian_month', '原供料', '供料车间运输车辆统计_录入_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\供料车间运输车辆统计_录入_2018-11-14_16.xlsx', '2018-11-14 16:40:45', '2018-11-14 16:40:45', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6202, 'ygl_zhongjiaowaipai_month', '原供料', '中焦外排记录_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\中焦外排记录_2018-11-14_16.xlsx', '2018-11-14 16:40:46', '2018-11-14 16:40:46', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6203, 'jh_ganxijiao', '焦化', '干熄焦报表设计_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\干熄焦报表设计_2018-11-14_16.xlsx', '2018-11-14 16:41:02', '2018-11-14 16:41:02', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6204, 'gl_lengquebiwendu_day', 'bf6', '高炉冷却壁温度 日报_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉冷却壁温度 日报_2018-11-14_16.xlsx', '2018-11-14 16:41:22', '2018-11-14 16:41:22', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6205, 'jh_huachan', '焦化', '化产报表设计_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\化产报表设计_2018-11-14_16.xlsx', '2018-11-14 16:41:27', '2018-11-14 16:41:27', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6206, 'gl_lengquebiwendu_month', 'bf6', '高炉冷却壁温度 月报_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉冷却壁温度 月报_2018-11-14_16.xlsx', '2018-11-14 16:41:28', '2018-11-14 16:41:28', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6207, 'nj_twokong', '能介', '二空压站运行记录表_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\二空压站运行记录表_2018-11-14_16.xlsx', '2018-11-14 16:41:42', '2018-11-14 16:41:42', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6208, 'gl_ludingzhuangliaozuoye_day1', 'bf6', '高炉炉顶装料作业 日报1_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉炉顶装料作业 日报1_2018-11-14_16.xlsx', '2018-11-14 16:41:45', '2018-11-14 16:41:45', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6209, 'gl_bentiwendu_day', 'bf6', '高炉本体温度日报表_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度日报表_2018-11-14_16.xlsx', '2018-11-14 16:42:23', '2018-11-14 16:42:23', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6210, 'gl_bentiwendu_month', 'bf6', '高炉本体温度月报表_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度月报表_2018-11-14_16.xlsx', '2018-11-14 16:42:32', '2018-11-14 16:42:32', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6211, 'nj_sansigui_day', '能介', '三四柜区运行记录表_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\三四柜区运行记录表_2018-11-14_16.xlsx', '2018-11-14 16:46:47', '2018-11-14 16:46:47', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6212, 'sj_tuoliu', '烧结', '5#脱硫系统运行日报_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#脱硫系统运行日报_2018-11-14_16.xlsx', '2018-11-14 16:50:00', '2018-11-14 16:50:00', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6213, 'sj_tuoliu', '烧结', '6#脱硫系统运行日报_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\6#脱硫系统运行日报_2018-11-14_16.xlsx', '2018-11-14 16:50:02', '2018-11-14 16:50:02', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6214, 'ygl_chengpincang', '原供料', '成品仓出入记录_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\成品仓出入记录_2018-11-14_16.xlsx', '2018-11-14 16:50:07', '2018-11-14 16:50:07', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6215, 'jh_peimeizuoyequ', '焦化', '配煤作业区报表设计_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\配煤作业区报表设计_2018-11-14_16.xlsx', '2018-11-14 16:50:16', '2018-11-14 16:50:16', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6216, 'gl_taisu1_month', 'bf6', '高炉 月报_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉 月报_2018-11-14_16.xlsx', '2018-11-14 16:50:23', '2018-11-14 16:50:23', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6217, 'ygl_yichanggenzong', '原供料', '供料异常跟踪表_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\供料异常跟踪表_2018-11-14_16.xlsx', '2018-11-14 16:50:24', '2018-11-14 16:50:24', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6218, 'gl_jswgaolu_day', 'bf6', '高炉 日报_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉 日报_2018-11-14_16.xlsx', '2018-11-14 16:50:25', '2018-11-14 16:50:25', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6219, 'gl_chutiezuoye_day', 'bf6', '出铁作业 日报_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\出铁作业 日报_2018-11-14_16.xlsx', '2018-11-14 16:50:29', '2018-11-14 16:50:29', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6220, 'ygl_Liaojiaomei_day', '原供料', '炼焦煤每日库存动态表_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\炼焦煤每日库存动态表_2018-11-14_16.xlsx', '2018-11-14 16:50:29', '2018-11-14 16:50:29', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6221, 'gl_ludingbuliao_day', 'bf6', '高炉炉顶布料作业日报_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉炉顶布料作业日报_2018-11-14_16.xlsx', '2018-11-14 16:50:34', '2018-11-14 16:50:34', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6222, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-14_16.xlsx', '2018-11-14 16:50:47', '2018-11-14 16:50:47', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6223, 'ygl_shengchanxiechedegji', '原供料', '原料生产卸车登记表_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料生产卸车登记表_2018-11-14_16.xlsx', '2018-11-14 16:50:48', '2018-11-14 16:50:48', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6224, 'ygl_gongliaochejian_month', '原供料', '供料车间运输车辆统计_录入_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\供料车间运输车辆统计_录入_2018-11-14_16.xlsx', '2018-11-14 16:50:49', '2018-11-14 16:50:49', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6225, 'ygl_zhongjiaowaipai_month', '原供料', '中焦外排记录_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\中焦外排记录_2018-11-14_16.xlsx', '2018-11-14 16:50:50', '2018-11-14 16:50:50', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6226, 'jh_ganxijiao', '焦化', '干熄焦报表设计_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\干熄焦报表设计_2018-11-14_16.xlsx', '2018-11-14 16:51:06', '2018-11-14 16:51:06', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6227, 'gl_lengquebiwendu_day', 'bf6', '高炉冷却壁温度 日报_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉冷却壁温度 日报_2018-11-14_16.xlsx', '2018-11-14 16:51:25', '2018-11-14 16:51:25', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6228, 'jh_huachan', '焦化', '化产报表设计_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\化产报表设计_2018-11-14_16.xlsx', '2018-11-14 16:51:28', '2018-11-14 16:51:28', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6229, 'gl_lengquebiwendu_month', 'bf6', '高炉冷却壁温度 月报_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉冷却壁温度 月报_2018-11-14_16.xlsx', '2018-11-14 16:51:33', '2018-11-14 16:51:33', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6230, 'gl_ludingzhuangliaozuoye_day1', 'bf6', '高炉炉顶装料作业 日报1_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉炉顶装料作业 日报1_2018-11-14_16.xlsx', '2018-11-14 16:51:48', '2018-11-14 16:51:48', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6231, 'gl_bentiwendu_day', 'bf6', '高炉本体温度日报表_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度日报表_2018-11-14_16.xlsx', '2018-11-14 16:52:03', '2018-11-14 16:52:03', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6232, 'nj_twokong', '能介', '二空压站运行记录表_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\二空压站运行记录表_2018-11-14_16.xlsx', '2018-11-14 16:52:09', '2018-11-14 16:52:09', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6233, 'gl_bentiwendu_month', 'bf6', '高炉本体温度月报表_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度月报表_2018-11-14_16.xlsx', '2018-11-14 16:52:26', '2018-11-14 16:52:26', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6234, 'nj_sansigui_day', '能介', '三四柜区运行记录表_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\三四柜区运行记录表_2018-11-14_16.xlsx', '2018-11-14 16:56:26', '2018-11-14 16:56:26', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6235, 'sj_tuoliu', '烧结', '5#脱硫系统运行日报_2018-11-14_17.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#脱硫系统运行日报_2018-11-14_17.xlsx', '2018-11-14 17:00:00', '2018-11-14 17:00:00', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6236, 'sj_tuoliu', '烧结', '6#脱硫系统运行日报_2018-11-14_17.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\6#脱硫系统运行日报_2018-11-14_17.xlsx', '2018-11-14 17:00:01', '2018-11-14 17:00:01', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6237, 'ygl_chengpincang', '原供料', '成品仓出入记录_2018-11-14_17.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\成品仓出入记录_2018-11-14_17.xlsx', '2018-11-14 17:00:05', '2018-11-14 17:00:05', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6238, 'jh_peimeizuoyequ', '焦化', '配煤作业区报表设计_2018-11-14_17.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\配煤作业区报表设计_2018-11-14_17.xlsx', '2018-11-14 17:00:08', '2018-11-14 17:00:08', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6239, 'gl_taisu1_month', 'bf6', '高炉 月报_2018-11-14_17.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉 月报_2018-11-14_17.xlsx', '2018-11-14 17:00:14', '2018-11-14 17:00:14', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6240, 'ygl_yichanggenzong', '原供料', '供料异常跟踪表_2018-11-14_17.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\供料异常跟踪表_2018-11-14_17.xlsx', '2018-11-14 17:00:15', '2018-11-14 17:00:15', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6241, 'gl_jswgaolu_day', 'bf6', '高炉 日报_2018-11-14_17.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉 日报_2018-11-14_17.xlsx', '2018-11-14 17:00:18', '2018-11-14 17:00:18', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6242, 'gl_chutiezuoye_day', 'bf6', '出铁作业 日报_2018-11-14_17.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\出铁作业 日报_2018-11-14_17.xlsx', '2018-11-14 17:00:21', '2018-11-14 17:00:21', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6243, 'ygl_Liaojiaomei_day', '原供料', '炼焦煤每日库存动态表_2018-11-14_17.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\炼焦煤每日库存动态表_2018-11-14_17.xlsx', '2018-11-14 17:00:22', '2018-11-14 17:00:22', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6244, 'gl_ludingbuliao_day', 'bf6', '高炉炉顶布料作业日报_2018-11-14_17.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉炉顶布料作业日报_2018-11-14_17.xlsx', '2018-11-14 17:00:28', '2018-11-14 17:00:28', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6245, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-14_17.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-14_17.xlsx', '2018-11-14 17:00:40', '2018-11-14 17:00:40', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6246, 'ygl_shengchanxiechedegji', '原供料', '原料生产卸车登记表_2018-11-14_17.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料生产卸车登记表_2018-11-14_17.xlsx', '2018-11-14 17:00:42', '2018-11-14 17:00:42', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6247, 'ygl_gongliaochejian_month', '原供料', '供料车间运输车辆统计_录入_2018-11-14_17.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\供料车间运输车辆统计_录入_2018-11-14_17.xlsx', '2018-11-14 17:00:43', '2018-11-14 17:00:43', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6248, 'ygl_zhongjiaowaipai_month', '原供料', '中焦外排记录_2018-11-14_17.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\中焦外排记录_2018-11-14_17.xlsx', '2018-11-14 17:00:43', '2018-11-14 17:00:43', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6249, 'jh_ganxijiao', '焦化', '干熄焦报表设计_2018-11-14_17.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\干熄焦报表设计_2018-11-14_17.xlsx', '2018-11-14 17:00:58', '2018-11-14 17:00:58', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6250, 'gl_lengquebiwendu_day', 'bf6', '高炉冷却壁温度 日报_2018-11-14_17.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉冷却壁温度 日报_2018-11-14_17.xlsx', '2018-11-14 17:01:20', '2018-11-14 17:01:20', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6251, 'gl_lengquebiwendu_month', 'bf6', '高炉冷却壁温度 月报_2018-11-14_17.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉冷却壁温度 月报_2018-11-14_17.xlsx', '2018-11-14 17:01:23', '2018-11-14 17:01:23', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6252, 'jh_huachan', '焦化', '化产报表设计_2018-11-14_17.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\化产报表设计_2018-11-14_17.xlsx', '2018-11-14 17:01:25', '2018-11-14 17:01:25', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6253, 'gl_ludingzhuangliaozuoye_day1', 'bf6', '高炉炉顶装料作业 日报1_2018-11-14_17.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉炉顶装料作业 日报1_2018-11-14_17.xlsx', '2018-11-14 17:01:47', '2018-11-14 17:01:47', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6254, 'nj_twokong', '能介', '二空压站运行记录表_2018-11-14_17.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\二空压站运行记录表_2018-11-14_17.xlsx', '2018-11-14 17:01:52', '2018-11-14 17:01:52', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6255, 'gl_bentiwendu_month', 'bf6', '高炉本体温度月报表_2018-11-14_17.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度月报表_2018-11-14_17.xlsx', '2018-11-14 17:02:13', '2018-11-14 17:02:13', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6256, 'gl_bentiwendu_day', 'bf6', '高炉本体温度日报表_2018-11-14_17.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度日报表_2018-11-14_17.xlsx', '2018-11-14 17:02:15', '2018-11-14 17:02:15', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6257, 'nj_sansigui_day', '能介', '三四柜区运行记录表_2018-11-14_16.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\三四柜区运行记录表_2018-11-14_16.xlsx', '2018-11-14 17:06:20', '2018-11-14 17:06:20', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6258, 'jh_ganxijiao', '焦化', '干熄焦报表设计_2018-11-14_17.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\干熄焦报表设计_2018-11-14_17.xlsx', '2018-11-14 17:18:13', '2018-11-14 17:18:13', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6259, 'ygl_yichanggenzong', '原供料', '供料异常跟踪表_2018-11-14_17.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\供料异常跟踪表_2018-11-14_17.xlsx', '2018-11-14 17:37:09', '2018-11-14 17:37:09', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6260, 'jh_ganxijiao', '焦化', '干熄焦报表设计_2018-11-14_17.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\干熄焦报表设计_2018-11-14_17.xlsx', '2018-11-14 17:48:18', '2018-11-14 17:48:18', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6261, 'ygl_jinchangwuzi', '原供料', '进厂物资（精煤）化验记录表.xlsx_2018-11-14_17.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\进厂物资（精煤）化验记录表.xlsx_2018-11-14_17.xlsx', '2018-11-14 17:52:26', '2018-11-14 17:52:26', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6262, 'jh_ganxijiao', '焦化', '干熄焦报表设计_2018-11-14_18.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\干熄焦报表设计_2018-11-14_18.xlsx', '2018-11-14 18:00:36', '2018-11-14 18:00:36', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6263, 'jh_ganxijiao', '焦化', '干熄焦报表设计_2018-11-15_10.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\干熄焦报表设计_2018-11-15_10.xlsx', '2018-11-15 10:30:21', '2018-11-15 10:30:21', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6264, 'jh_ganxijiao', '焦化', '干熄焦报表设计_2018-11-15_10.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\干熄焦报表设计_2018-11-15_10.xlsx', '2018-11-15 10:32:32', '2018-11-15 10:32:32', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6265, 'sj_tuoliutuoxiaogongyicaiji', '烧结', '6#脱硫脱硝工艺参数采集_2018-11-15_10.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\6#脱硫脱硝工艺参数采集_2018-11-15_10.xlsx', '2018-11-15 10:43:59', '2018-11-15 10:43:59', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6266, 'sj_tuoxiaoyunxingjilu', '烧结', '6#脱硝运行记录表月报_2018-11-15.xlsx', 'D:\\excel\\cn_zh\\烧结\\月表报\\6#脱硝运行记录表月报_2018-11-15.xlsx', '2018-11-15 10:55:40', '2018-11-15 10:55:40', NULL, 'report_month', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6267, 'sj_tuoxiaoyunxingjilu', '烧结', '6#脱硝运行记录表月报_2018-11-15.xlsx', 'D:\\excel\\cn_zh\\烧结\\月表报\\6#脱硝运行记录表月报_2018-11-15.xlsx', '2018-11-15 10:57:25', '2018-11-15 10:57:25', NULL, 'report_month', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6268, 'sj_tuoxiaoyunxingjilu', '烧结', '6#脱硝运行记录表月报_2018-11-15.xlsx', 'D:\\excel\\cn_zh\\烧结\\月表报\\6#脱硝运行记录表月报_2018-11-15.xlsx', '2018-11-15 11:24:32', '2018-11-15 11:24:32', NULL, 'report_month', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6269, 'sj_tuoxiaoyunxingjilu', '烧结', '6#脱硝运行记录表月报_2018-11-15.xlsx', 'D:\\excel\\cn_zh\\烧结\\月表报\\6#脱硝运行记录表月报_2018-11-15.xlsx', '2018-11-15 11:27:48', '2018-11-15 11:27:48', NULL, 'report_month', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6270, 'sj_tuoxiaoyunxingjilu', '烧结', '6#脱硝运行记录表月报_2018-11-15.xlsx', 'D:\\excel\\cn_zh\\烧结\\月表报\\6#脱硝运行记录表月报_2018-11-15.xlsx', '2018-11-15 11:41:25', '2018-11-15 11:41:25', NULL, 'report_month', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6271, 'sj_tuoxiaoyunxingjilu', '烧结', '6#脱硝运行记录表月报_2018-11-15.xlsx', 'D:\\excel\\cn_zh\\烧结\\月表报\\6#脱硝运行记录表月报_2018-11-15.xlsx', '2018-11-15 13:00:52', '2018-11-15 13:00:52', NULL, 'report_month', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6272, 'ygl_chengpincang', '原供料', '成品仓出入记录_2018-11-15_14.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\成品仓出入记录_2018-11-15_14.xlsx', '2018-11-15 14:17:03', '2018-11-15 14:17:03', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6273, 'ygl_chengpincang', '原供料', '成品仓出入记录_2018-11-15_14.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\成品仓出入记录_2018-11-15_14.xlsx', '2018-11-15 14:58:45', '2018-11-15 14:58:45', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6274, 'ygl_chengpincang', '原供料', '成品仓出入记录_2018-11-15_15.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\成品仓出入记录_2018-11-15_15.xlsx', '2018-11-15 15:02:39', '2018-11-15 15:02:39', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6275, 'ygl_yichanggenzong', '原供料', '供料异常跟踪表_2018-11-15_15.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\供料异常跟踪表_2018-11-15_15.xlsx', '2018-11-15 15:31:22', '2018-11-15 15:31:22', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6276, 'ygl_shengchanxiechedegji', '原供料', '原料生产卸车登记表_2018-11-15_15.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料生产卸车登记表_2018-11-15_15.xlsx', '2018-11-15 15:32:36', '2018-11-15 15:32:36', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6277, 'ygl_jinchangwuzi', '原供料', '进厂物资（精煤）化验记录表_2018-11-15_15.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\进厂物资（精煤）化验记录表_2018-11-15_15.xlsx', '2018-11-15 15:34:35', '2018-11-15 15:34:35', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6278, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表_2018-11-15_16.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产运行记录表_2018-11-15_16.xlsx', '2018-11-15 16:06:59', '2018-11-15 16:06:59', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6279, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表_2018-11-15_16.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产运行记录表_2018-11-15_16.xlsx', '2018-11-15 16:09:09', '2018-11-15 16:09:09', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6280, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表_2018-11-15_16.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产运行记录表_2018-11-15_16.xlsx', '2018-11-15 16:15:31', '2018-11-15 16:15:31', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6281, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表_2018-11-15_16.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产运行记录表_2018-11-15_16.xlsx', '2018-11-15 16:16:12', '2018-11-15 16:16:12', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6282, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表_2018-11-15_16.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产运行记录表_2018-11-15_16.xlsx', '2018-11-15 16:17:39', '2018-11-15 16:17:39', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6283, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表_2018-11-15_16.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产运行记录表_2018-11-15_16.xlsx', '2018-11-15 16:22:52', '2018-11-15 16:22:52', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6284, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表_2018-11-15_17.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产运行记录表_2018-11-15_17.xlsx', '2018-11-15 17:03:13', '2018-11-15 17:03:13', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6285, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表_2018-11-15_17.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产运行记录表_2018-11-15_17.xlsx', '2018-11-15 17:15:16', '2018-11-15 17:15:16', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6286, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表_2018-11-15_17.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产运行记录表_2018-11-15_17.xlsx', '2018-11-15 17:22:05', '2018-11-15 17:22:05', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6287, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表_2018-11-15_17.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产运行记录表_2018-11-15_17.xlsx', '2018-11-15 17:27:10', '2018-11-15 17:27:10', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6288, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表_2018-11-15_17.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产运行记录表_2018-11-15_17.xlsx', '2018-11-15 17:28:55', '2018-11-15 17:28:55', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6289, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表_2018-11-15_17.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产运行记录表_2018-11-15_17.xlsx', '2018-11-15 17:55:34', '2018-11-15 17:55:34', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6290, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表_2018-11-15_18.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产运行记录表_2018-11-15_18.xlsx', '2018-11-15 18:06:36', '2018-11-15 18:06:36', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6291, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表_2018-11-15_18.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产运行记录表_2018-11-15_18.xlsx', '2018-11-15 18:14:54', '2018-11-15 18:14:54', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6292, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表_2018-11-15_18.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产运行记录表_2018-11-15_18.xlsx', '2018-11-15 18:19:03', '2018-11-15 18:19:03', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6293, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表_2018-11-15_18.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产运行记录表_2018-11-15_18.xlsx', '2018-11-15 18:20:52', '2018-11-15 18:20:52', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6294, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表_2018-11-15_18.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产运行记录表_2018-11-15_18.xlsx', '2018-11-15 18:26:44', '2018-11-15 18:26:44', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6295, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表_2018-11-15_18.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产运行记录表_2018-11-15_18.xlsx', '2018-11-15 18:28:48', '2018-11-15 18:28:48', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6296, 'ygl_chengpincang', '原供料', '成品仓出入记录_2018-11-15_18.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\成品仓出入记录_2018-11-15_18.xlsx', '2018-11-15 18:33:26', '2018-11-15 18:33:26', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6297, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表_2018-11-15_18.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产运行记录表_2018-11-15_18.xlsx', '2018-11-15 18:43:35', '2018-11-15 18:43:35', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6298, 'yl_chejianshengchanjiaoban', '原供料', '原料车间生产交班表_2018-11-16_10.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产交班表_2018-11-16_10.xlsx', '2018-11-16 10:20:40', '2018-11-16 10:20:40', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6299, 'yl_chejianshengchanjiaoban', '原供料', '原料车间生产交班表_2018-11-16_10.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产交班表_2018-11-16_10.xlsx', '2018-11-16 10:23:32', '2018-11-16 10:23:32', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6300, 'yl_chejianshengchanjiaoban', '原供料', '原料车间生产交班表_2018-11-16_10.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产交班表_2018-11-16_10.xlsx', '2018-11-16 10:26:35', '2018-11-16 10:26:35', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6301, 'yl_chejianshengchanjiaoban', '原供料', '原料车间生产交班表_2018-11-16_10.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产交班表_2018-11-16_10.xlsx', '2018-11-16 10:33:45', '2018-11-16 10:33:45', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6302, 'yl_chejianshengchanjiaoban', '原供料', '原料车间生产交班表_2018-11-16_10.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产交班表_2018-11-16_10.xlsx', '2018-11-16 10:38:23', '2018-11-16 10:38:23', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6303, 'yl_chejianshengchanjiaoban', '原供料', '原料车间生产交班表_2018-11-16_10.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产交班表_2018-11-16_10.xlsx', '2018-11-16 10:42:37', '2018-11-16 10:42:37', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6304, 'yl_chejianshengchanjiaoban', '原供料', '原料车间生产交班表_2018-11-16_10.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产交班表_2018-11-16_10.xlsx', '2018-11-16 10:46:09', '2018-11-16 10:46:09', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6305, 'yl_chejianshengchanjiaoban', '原供料', '原料车间生产交班表_2018-11-16_11.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产交班表_2018-11-16_11.xlsx', '2018-11-16 11:03:53', '2018-11-16 11:03:53', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6306, 'yl_chejianshengchanjiaoban', '原供料', '原料车间生产交班表_2018-11-16_11.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产交班表_2018-11-16_11.xlsx', '2018-11-16 11:10:54', '2018-11-16 11:10:54', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6307, 'yl_chejianshengchanjiaoban', '原供料', '原料车间生产交班表_2018-11-16_11.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产交班表_2018-11-16_11.xlsx', '2018-11-16 11:20:57', '2018-11-16 11:20:57', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6308, 'ygl_chengpincang', '原供料', '成品仓出入记录_2018-11-16_11.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\成品仓出入记录_2018-11-16_11.xlsx', '2018-11-16 11:31:47', '2018-11-16 11:31:47', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6309, 'gl_chejianjikongzhongxinjioajieban', '原供料', '供料车间集控中心交接班记录_2018-11-16_11.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\供料车间集控中心交接班记录_2018-11-16_11.xlsx', '2018-11-16 11:34:01', '2018-11-16 11:34:01', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6310, 'ygl_chengpincang', '原供料', '成品仓出入记录_2018-11-16_15.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\成品仓出入记录_2018-11-16_15.xlsx', '2018-11-16 15:49:50', '2018-11-16 15:49:50', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6311, 'gl_taisu1_month', 'bf6', '高炉 月报_2018-11-16_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉 月报_2018-11-16_16.xlsx', '2018-11-16 16:46:33', '2018-11-16 16:46:33', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6312, 'gl_jswgaolu_day', 'bf6', '高炉 日报_2018-11-16_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉 日报_2018-11-16_16.xlsx', '2018-11-16 16:46:33', '2018-11-16 16:46:33', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6313, 'ygl_chengpincang', '原供料', '成品仓出入记录_2018-11-16_16.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\成品仓出入记录_2018-11-16_16.xlsx', '2018-11-16 16:46:36', '2018-11-16 16:46:36', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6314, 'gl_bentiwendu_day', 'bf6', '高炉本体温度日报表_2018-11-16_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度日报表_2018-11-16_16.xlsx', '2018-11-16 16:46:40', '2018-11-16 16:46:40', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6315, 'ygl_yichanggenzong', '原供料', '供料异常跟踪表_2018-11-16_16.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\供料异常跟踪表_2018-11-16_16.xlsx', '2018-11-16 16:46:41', '2018-11-16 16:46:41', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6316, 'jh_peimeizuoyequ', '焦化', '配煤作业区报表设计_2018-11-16_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\配煤作业区报表设计_2018-11-16_16.xlsx', '2018-11-16 16:46:41', '2018-11-16 16:46:41', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6317, 'gl_bentiwendu_month', 'bf6', '高炉本体温度月报表_2018-11-16_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度月报表_2018-11-16_16.xlsx', '2018-11-16 16:46:50', '2018-11-16 16:46:50', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6318, 'gl_ludingbuliao_day', 'bf6', '高炉炉顶布料作业日报_2018-11-16_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉炉顶布料作业日报_2018-11-16_16.xlsx', '2018-11-16 16:46:50', '2018-11-16 16:46:50', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6319, 'gl_chutiezuoye_day', 'bf6', '出铁作业 日报_2018-11-16_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\出铁作业 日报_2018-11-16_16.xlsx', '2018-11-16 16:46:51', '2018-11-16 16:46:51', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6320, 'ygl_shengchanxiechedegji', '原供料', '原料生产卸车登记表_2018-11-16_16.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料生产卸车登记表_2018-11-16_16.xlsx', '2018-11-16 16:46:52', '2018-11-16 16:46:52', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6321, 'gl_lengquebiwendu_day', 'bf6', '高炉冷却壁温度 日报_2018-11-16_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉冷却壁温度 日报_2018-11-16_16.xlsx', '2018-11-16 16:46:53', '2018-11-16 16:46:53', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6322, 'gl_lengquebiwendu_month', 'bf6', '高炉冷却壁温度 月报_2018-11-16_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉冷却壁温度 月报_2018-11-16_16.xlsx', '2018-11-16 16:46:54', '2018-11-16 16:46:54', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6323, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-16_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-16_16.xlsx', '2018-11-16 16:47:22', '2018-11-16 16:47:22', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6324, 'nj_xinyikong', '能介', '新一空压站运行记录表_2018-11-16_16.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\新一空压站运行记录表_2018-11-16_16.xlsx', '2018-11-16 16:47:33', '2018-11-16 16:47:33', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6325, 'nj_threekong', '能介', '三空压站运行记录表_2018-11-16_16.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\三空压站运行记录表_2018-11-16_16.xlsx', '2018-11-16 16:47:43', '2018-11-16 16:47:43', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6326, 'jh_ganxijiao', '焦化', '干熄焦报表设计_2018-11-16_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\干熄焦报表设计_2018-11-16_16.xlsx', '2018-11-16 16:47:53', '2018-11-16 16:47:53', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6327, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-16_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-16_16.xlsx', '2018-11-16 16:47:53', '2018-11-16 16:47:53', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6328, 'nj_fourkong', '能介', '四空压站运行记录表_2018-11-16_16.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\四空压站运行记录表_2018-11-16_16.xlsx', '2018-11-16 16:48:11', '2018-11-16 16:48:11', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6329, 'jh_huachan', '焦化', '化产报表设计_2018-11-16_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\化产报表设计_2018-11-16_16.xlsx', '2018-11-16 16:48:22', '2018-11-16 16:48:22', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6330, 'jh_ganxijiao', '焦化', '干熄焦报表设计_2018-11-16_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\干熄焦报表设计_2018-11-16_16.xlsx', '2018-11-16 16:48:31', '2018-11-16 16:48:31', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6331, 'nj_fourkong', '能介', '四空压站运行记录表_2018-11-16_16.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\四空压站运行记录表_2018-11-16_16.xlsx', '2018-11-16 16:48:39', '2018-11-16 16:48:39', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6332, 'jh_huachan', '焦化', '化产报表设计_2018-11-16_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\化产报表设计_2018-11-16_16.xlsx', '2018-11-16 16:48:40', '2018-11-16 16:48:40', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6333, 'ygl_Liaojiaomei_day', '原供料', '炼焦煤每日库存动态表_2018-11-16_17.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\炼焦煤每日库存动态表_2018-11-16_17.xlsx', '2018-11-16 17:16:36', '2018-11-16 17:16:36', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6334, 'jh_huachan', '焦化', '化产报表设计_2018-11-19_10.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\化产报表设计_2018-11-19_10.xlsx', '2018-11-19 10:50:24', '2018-11-19 10:50:24', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6335, 'jh_huachan', '焦化', '化产报表设计_2018-11-19_10.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\化产报表设计_2018-11-19_10.xlsx', '2018-11-19 10:54:00', '2018-11-19 10:54:00', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6336, 'jh_huachan', '焦化', '化产报表设计_2018-11-19_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\化产报表设计_2018-11-19_11.xlsx', '2018-11-19 11:21:59', '2018-11-19 11:21:59', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6337, 'jh_huachan', '焦化', '化产报表设计_2018-11-19_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\化产报表设计_2018-11-19_11.xlsx', '2018-11-19 11:31:44', '2018-11-19 11:31:44', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6338, 'jh_huachan', '焦化', '化产报表设计_2018-11-19_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\化产报表设计_2018-11-19_11.xlsx', '2018-11-19 11:40:05', '2018-11-19 11:40:05', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6339, 'jh_ganxijiao', '焦化', '干熄焦报表设计_2018-11-19_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\干熄焦报表设计_2018-11-19_11.xlsx', '2018-11-19 11:47:31', '2018-11-19 11:47:31', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6340, 'jh_huachan', '焦化', '化产报表设计_2018-11-19_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\化产报表设计_2018-11-19_11.xlsx', '2018-11-19 11:48:18', '2018-11-19 11:48:18', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6341, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-19_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-19_11.xlsx', '2018-11-19 11:48:44', '2018-11-19 11:48:44', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6342, 'jh_peimeizuoyequ', '焦化', '配煤作业区报表设计_2018-11-19_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\配煤作业区报表设计_2018-11-19_11.xlsx', '2018-11-19 11:49:03', '2018-11-19 11:49:03', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6343, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表_2018-11-19_16.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产运行记录表_2018-11-19_16.xlsx', '2018-11-19 16:47:13', '2018-11-19 16:47:13', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6344, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表_2018-11-19_17.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产运行记录表_2018-11-19_17.xlsx', '2018-11-19 17:27:52', '2018-11-19 17:27:52', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6345, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表_2018-11-19_17.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产运行记录表_2018-11-19_17.xlsx', '2018-11-19 17:31:46', '2018-11-19 17:31:46', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6346, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表_2018-11-19_18.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产运行记录表_2018-11-19_18.xlsx', '2018-11-19 18:11:34', '2018-11-19 18:11:34', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6347, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表_2018-11-19_18.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产运行记录表_2018-11-19_18.xlsx', '2018-11-19 18:18:02', '2018-11-19 18:18:02', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6348, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表_2018-11-19_18.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产运行记录表_2018-11-19_18.xlsx', '2018-11-19 18:30:29', '2018-11-19 18:30:29', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6349, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表_2018-11-19_18.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产运行记录表_2018-11-19_18.xlsx', '2018-11-19 18:32:08', '2018-11-19 18:32:08', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6350, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表_2018-11-19_18.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产运行记录表_2018-11-19_18.xlsx', '2018-11-19 18:36:57', '2018-11-19 18:36:57', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6351, 'gl_chejianjikongzhongxinjioajieban', '原供料', '供料车间集控中心交接班记录_2018-11-19_18.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\供料车间集控中心交接班记录_2018-11-19_18.xlsx', '2018-11-19 18:39:27', '2018-11-19 18:39:27', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6352, 'sj_shaojieji_day', '烧结', '6#烧结机生产日报_2018-11-20_09.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\6#烧结机生产日报_2018-11-20_09.xlsx', '2018-11-20 09:38:18', '2018-11-20 09:38:18', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6353, 'sj_shaojieji_day', '烧结', '6#烧结机生产日报_2018-11-20_09.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\6#烧结机生产日报_2018-11-20_09.xlsx', '2018-11-20 09:55:34', '2018-11-20 09:55:34', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6354, 'sj_shaojieji_day', '烧结', '6#烧结机生产日报_2018-11-20_11.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\6#烧结机生产日报_2018-11-20_11.xlsx', '2018-11-20 11:01:23', '2018-11-20 11:01:23', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6355, 'sj_shaojieji_day', '烧结', '6#烧结机生产日报_2018-11-20_11.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\6#烧结机生产日报_2018-11-20_11.xlsx', '2018-11-20 11:14:07', '2018-11-20 11:14:07', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6356, 'sj_shaojieji_day', '烧结', '6#烧结机生产日报_2018-11-20_11.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\6#烧结机生产日报_2018-11-20_11.xlsx', '2018-11-20 11:18:39', '2018-11-20 11:18:39', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6357, 'sj_shaojieji_day', '烧结', '6#烧结机生产日报_2018-11-20_11.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\6#烧结机生产日报_2018-11-20_11.xlsx', '2018-11-20 11:19:47', '2018-11-20 11:19:47', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6358, 'sj_shaojieji_day', '烧结', '6#烧结机生产日报_2018-11-20_11.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\6#烧结机生产日报_2018-11-20_11.xlsx', '2018-11-20 11:25:50', '2018-11-20 11:25:50', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6359, 'sj_shaojieji_day', '烧结', '6#烧结机生产日报_2018-11-20_11.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\6#烧结机生产日报_2018-11-20_11.xlsx', '2018-11-20 11:42:14', '2018-11-20 11:42:14', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6360, 'gl_ludingzhuangliaozuoye_day1', 'bf6', '高炉炉顶装料作业 日报1_2018-11-20_11.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉炉顶装料作业 日报1_2018-11-20_11.xlsx', '2018-11-20 11:46:09', '2018-11-20 11:46:09', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6361, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-20_11.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-20_11.xlsx', '2018-11-20 11:55:10', '2018-11-20 11:55:10', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6362, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-20_11.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-20_11.xlsx', '2018-11-20 11:59:04', '2018-11-20 11:59:04', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6363, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-20_13.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-20_13.xlsx', '2018-11-20 13:13:42', '2018-11-20 13:13:42', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6364, 'sj_shaojieji6_day', '烧结', '6#烧结机生产日报_2018-11-20_13.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\6#烧结机生产日报_2018-11-20_13.xlsx', '2018-11-20 13:14:00', '2018-11-20 13:14:00', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6365, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-20_13.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-20_13.xlsx', '2018-11-20 13:17:43', '2018-11-20 13:17:43', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6366, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-20_13.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-20_13.xlsx', '2018-11-20 13:19:54', '2018-11-20 13:19:54', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6367, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-20_14.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-20_14.xlsx', '2018-11-20 14:49:11', '2018-11-20 14:49:11', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6368, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-20_14.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-20_14.xlsx', '2018-11-20 14:51:01', '2018-11-20 14:51:01', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6369, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-20_14.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-20_14.xlsx', '2018-11-20 14:55:03', '2018-11-20 14:55:03', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6370, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-20_14.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-20_14.xlsx', '2018-11-20 14:58:06', '2018-11-20 14:58:06', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6371, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-20_14.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-20_14.xlsx', '2018-11-20 15:02:24', '2018-11-20 15:02:24', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6372, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-20_15.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-20_15.xlsx', '2018-11-20 15:06:42', '2018-11-20 15:06:42', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6373, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-20_15.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-20_15.xlsx', '2018-11-20 15:50:14', '2018-11-20 15:50:14', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6374, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-20_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-20_16.xlsx', '2018-11-20 16:41:56', '2018-11-20 16:41:56', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6375, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-20_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-20_16.xlsx', '2018-11-20 16:49:31', '2018-11-20 16:49:31', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6376, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-20_17.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-20_17.xlsx', '2018-11-20 17:37:20', '2018-11-20 17:37:20', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6377, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-20_17.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-20_17.xlsx', '2018-11-20 17:38:31', '2018-11-20 17:38:31', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6378, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-21_09.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-21_09.xlsx', '2018-11-21 09:48:52', '2018-11-21 09:48:52', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6379, 'gl_ludingzhuangliaozuoye_day1', 'bf6', '高炉炉顶装料作业 日报1_2018-11-21_09.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉炉顶装料作业 日报1_2018-11-21_09.xlsx', '2018-11-21 09:52:07', '2018-11-21 09:52:07', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6380, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-21_09.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-21_09.xlsx', '2018-11-21 09:52:24', '2018-11-21 09:52:24', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6381, 'gl_chutiezuoye_day', 'bf6', '出铁作业 日报_2018-11-21_09.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\出铁作业 日报_2018-11-21_09.xlsx', '2018-11-21 09:52:42', '2018-11-21 09:52:42', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6382, 'gl_bentiwendu_day', 'bf6', '高炉本体温度日报表_2018-11-21_09.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度日报表_2018-11-21_09.xlsx', '2018-11-21 09:54:03', '2018-11-21 09:54:03', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6383, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-21_09.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-21_09.xlsx', '2018-11-21 09:54:15', '2018-11-21 09:54:15', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6384, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-21_09.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-21_09.xlsx', '2018-11-21 09:54:56', '2018-11-21 09:54:56', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6385, 'gl_bentiwendu_month', 'bf6', '高炉本体温度月报表_2018-11-21_09.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度月报表_2018-11-21_09.xlsx', '2018-11-21 09:57:12', '2018-11-21 09:57:12', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6386, 'gl_lengquebiwendu_day', 'bf6', '高炉冷却壁温度 日报_2018-11-21_09.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉冷却壁温度 日报_2018-11-21_09.xlsx', '2018-11-21 09:58:02', '2018-11-21 09:58:02', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6387, 'gl_lengquebiwendu_month', 'bf6', '高炉冷却壁温度 月报_2018-11-21_09.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉冷却壁温度 月报_2018-11-21_09.xlsx', '2018-11-21 09:59:29', '2018-11-21 09:59:29', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6388, 'gl_jswgaolu_day', 'bf6', '高炉 日报_2018-11-21_09.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉 日报_2018-11-21_09.xlsx', '2018-11-21 10:00:07', '2018-11-21 10:00:07', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6389, 'gl_taisu1_month', 'bf6', '高炉 月报_2018-11-21_10.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉 月报_2018-11-21_10.xlsx', '2018-11-21 10:01:40', '2018-11-21 10:01:40', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6390, 'gl_ludingbuliao_day', 'bf6', '高炉炉顶布料作业日报_2018-11-21_10.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉炉顶布料作业日报_2018-11-21_10.xlsx', '2018-11-21 10:03:07', '2018-11-21 10:03:07', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6391, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-21_10.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-21_10.xlsx', '2018-11-21 10:09:33', '2018-11-21 10:09:33', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6392, 'nj_onekongcount', '能介', '一空压站启停次数表_2018-11-21_11.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\一空压站启停次数表_2018-11-21_11.xlsx', '2018-11-21 11:02:13', '2018-11-21 11:02:13', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6393, 'nj_onekongcount', '能介', '一空压站启停次数表_2018-11-21_11.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\一空压站启停次数表_2018-11-21_11.xlsx', '2018-11-21 11:04:25', '2018-11-21 11:04:25', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6394, 'nj_onekongcount', '能介', '一空压站启停次数表_2018-11-21_11.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\一空压站启停次数表_2018-11-21_11.xlsx', '2018-11-21 11:06:03', '2018-11-21 11:06:03', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6395, 'nj_onekongcount', '能介', '一空压站启停次数表_2018-11-21_11.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\一空压站启停次数表_2018-11-21_11.xlsx', '2018-11-21 11:17:45', '2018-11-21 11:17:45', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6396, 'nj_onekongcount', '能介', '一空压站启停次数表_2018-11-21_11.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\一空压站启停次数表_2018-11-21_11.xlsx', '2018-11-21 11:30:21', '2018-11-21 11:30:21', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6397, 'nj_onekongcount', '能介', '一空压站启停次数表_2018-11-21_11.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\一空压站启停次数表_2018-11-21_11.xlsx', '2018-11-21 11:33:08', '2018-11-21 11:33:08', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6398, 'nj_twokongcount', '能介', '二空压站启停次数表_2018-11-21_11.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\二空压站启停次数表_2018-11-21_11.xlsx', '2018-11-21 11:34:02', '2018-11-21 11:34:02', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6399, 'nj_threekongcount', '能介', '三空压站启停次数表_2018-11-21_12.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\三空压站启停次数表_2018-11-21_12.xlsx', '2018-11-21 12:43:40', '2018-11-21 12:43:40', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6400, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-21_14.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-21_14.xlsx', '2018-11-21 14:51:59', '2018-11-21 14:51:59', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6401, 'nj_twokong', '能介', '二空压站运行记录表_2018-11-21_15.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\二空压站运行记录表_2018-11-21_15.xlsx', '2018-11-21 15:36:26', '2018-11-21 15:36:26', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6402, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-21_15.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-21_15.xlsx', '2018-11-21 15:56:32', '2018-11-21 15:56:32', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6403, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-21_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-21_16.xlsx', '2018-11-21 16:07:43', '2018-11-21 16:07:43', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6404, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-21_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-21_16.xlsx', '2018-11-21 16:18:47', '2018-11-21 16:18:47', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6405, 'gl_jswgaolu_day', 'bf6', '高炉 日报_2018-11-21_17.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉 日报_2018-11-21_17.xlsx', '2018-11-21 17:33:40', '2018-11-21 17:33:40', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6406, 'gl_jswgaolu_day', 'bf6', '高炉 日报_2018-11-21_17.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉 日报_2018-11-21_17.xlsx', '2018-11-21 17:36:04', '2018-11-21 17:36:04', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6407, 'gl_jswgaolu_day', 'bf6', '高炉 日报_2018-11-21_17.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉 日报_2018-11-21_17.xlsx', '2018-11-21 17:39:11', '2018-11-21 17:39:11', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6408, 'gl_taisu1_month', 'bf6', '高炉 月报_2018-11-21_17.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉 月报_2018-11-21_17.xlsx', '2018-11-21 17:41:58', '2018-11-21 17:41:58', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6409, 'gl_jswgaolu_day', 'bf6', '高炉 日报_2018-11-21_17.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉 日报_2018-11-21_17.xlsx', '2018-11-21 17:44:43', '2018-11-21 17:44:43', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6410, 'gl_jswgaolu_day', 'bf6', '高炉 日报_2018-11-21_17.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉 日报_2018-11-21_17.xlsx', '2018-11-21 17:46:31', '2018-11-21 17:46:31', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6411, 'nj_twokongcount', '能介', '二空压站启停次数表_2018-11-22_09.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\二空压站启停次数表_2018-11-22_09.xlsx', '2018-11-22 09:10:24', '2018-11-22 09:10:24', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6412, 'nj_twokong', '能介', '二空压站运行记录表_2018-11-22_09.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\二空压站运行记录表_2018-11-22_09.xlsx', '2018-11-22 09:18:27', '2018-11-22 09:18:27', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6413, 'nj_twokong', '能介', '二空压站运行记录表_2018-11-22_10.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\二空压站运行记录表_2018-11-22_10.xlsx', '2018-11-22 10:07:47', '2018-11-22 10:07:47', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6414, 'nj_twokong', '能介', '二空压站运行记录表_2018-11-22_10.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\二空压站运行记录表_2018-11-22_10.xlsx', '2018-11-22 10:10:53', '2018-11-22 10:10:53', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6415, 'nj_threekong', '能介', '三空压站运行记录表_2018-11-22_10.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\三空压站运行记录表_2018-11-22_10.xlsx', '2018-11-22 10:50:54', '2018-11-22 10:50:54', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6416, 'nj_fourkong', '能介', '四空压站运行记录表_2018-11-22_10.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\四空压站运行记录表_2018-11-22_10.xlsx', '2018-11-22 10:51:43', '2018-11-22 10:51:43', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6417, 'nj_twokong', '能介', '二空压站运行记录表_2018-11-22_14.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\二空压站运行记录表_2018-11-22_14.xlsx', '2018-11-22 14:36:11', '2018-11-22 14:36:11', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6418, 'nj_twokong', '能介', '二空压站运行记录表_2018-11-22_14.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\二空压站运行记录表_2018-11-22_14.xlsx', '2018-11-22 14:44:16', '2018-11-22 14:44:16', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6419, 'nj_twokong', '能介', '二空压站运行记录表_2018-11-22_14.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\二空压站运行记录表_2018-11-22_14.xlsx', '2018-11-22 14:45:16', '2018-11-22 14:45:16', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6420, 'nj_twokong', '能介', '二空压站运行记录表_2018-11-22_14.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\二空压站运行记录表_2018-11-22_14.xlsx', '2018-11-22 14:50:20', '2018-11-22 14:50:20', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6421, 'nj_threekong', '能介', '三空压站运行记录表_2018-11-22_14.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\三空压站运行记录表_2018-11-22_14.xlsx', '2018-11-22 14:50:39', '2018-11-22 14:50:39', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6422, 'nj_fourkong', '能介', '四空压站运行记录表_2018-11-22_14.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\四空压站运行记录表_2018-11-22_14.xlsx', '2018-11-22 14:51:04', '2018-11-22 14:51:04', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6423, 'nj_xinyikong', '能介', '新一空压站运行记录表_2018-11-22_14.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\新一空压站运行记录表_2018-11-22_14.xlsx', '2018-11-22 14:51:24', '2018-11-22 14:51:24', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6424, 'nj_twokong', '能介', '二空压站运行记录表_2018-11-22_14.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\二空压站运行记录表_2018-11-22_14.xlsx', '2018-11-22 14:54:24', '2018-11-22 14:54:24', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6425, 'nj_onekongcount', '能介', '一空压站启停次数表_2018-11-22_15.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\一空压站启停次数表_2018-11-22_15.xlsx', '2018-11-22 15:00:23', '2018-11-22 15:00:23', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6426, 'nj_twokongcount', '能介', '二空压站启停次数表_2018-11-22_15.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\二空压站启停次数表_2018-11-22_15.xlsx', '2018-11-22 15:01:02', '2018-11-22 15:01:02', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6427, 'nj_threekongcount', '能介', '三空压站启停次数表_2018-11-22_15.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\三空压站启停次数表_2018-11-22_15.xlsx', '2018-11-22 15:01:48', '2018-11-22 15:01:48', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6428, 'nj_fourkongcount', '能介', '四空压站启停次数表_2018-11-22_15.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\四空压站启停次数表_2018-11-22_15.xlsx', '2018-11-22 15:02:31', '2018-11-22 15:02:31', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6429, 'nj_yasuokongqi', '能介', '压缩空气生产情况汇总表_2018-11-22_17.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\压缩空气生产情况汇总表_2018-11-22_17.xlsx', '2018-11-22 17:38:17', '2018-11-22 17:38:17', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6430, 'nj_yasuokongqi', '能介', '压缩空气生产情况汇总表_2018-11-22_18.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\压缩空气生产情况汇总表_2018-11-22_18.xlsx', '2018-11-22 18:28:21', '2018-11-22 18:28:21', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6431, 'nj_yasuokongqi', '能介', '压缩空气生产情况汇总表_2018-11-22_18.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\压缩空气生产情况汇总表_2018-11-22_18.xlsx', '2018-11-22 18:30:25', '2018-11-22 18:30:25', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6432, 'nj_yasuokongqi', '能介', '压缩空气生产情况汇总表_2018-11-22_18.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\压缩空气生产情况汇总表_2018-11-22_18.xlsx', '2018-11-22 18:33:10', '2018-11-22 18:33:10', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6433, 'nj_yasuokongqi', '能介', '压缩空气生产情况汇总表_2018-11-22_18.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\压缩空气生产情况汇总表_2018-11-22_18.xlsx', '2018-11-22 18:40:02', '2018-11-22 18:40:02', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6434, 'nj_yasuokongqi', '能介', '压缩空气生产情况汇总表_2018-11-22_18.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\压缩空气生产情况汇总表_2018-11-22_18.xlsx', '2018-11-22 18:43:22', '2018-11-22 18:43:22', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6435, 'nj_yasuokongqi', '能介', '压缩空气生产情况汇总表_2018-11-22_18.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\压缩空气生产情况汇总表_2018-11-22_18.xlsx', '2018-11-22 18:46:09', '2018-11-22 18:46:09', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6436, 'nj_yasuokongqi', '能介', '压缩空气生产情况汇总表_2018-11-22_18.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\压缩空气生产情况汇总表_2018-11-22_18.xlsx', '2018-11-22 18:53:01', '2018-11-22 18:53:01', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6437, 'nj_yasuokongqi', '能介', '压缩空气生产情况汇总表_2018-11-23_08.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\压缩空气生产情况汇总表_2018-11-23_08.xlsx', '2018-11-23 08:46:58', '2018-11-23 08:46:58', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6438, 'nj_twokong', '能介', '二空压站运行记录表_2018-11-23_09.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\二空压站运行记录表_2018-11-23_09.xlsx', '2018-11-23 09:01:44', '2018-11-23 09:01:44', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6439, 'nj_threekong', '能介', '三空压站运行记录表_2018-11-23_09.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\三空压站运行记录表_2018-11-23_09.xlsx', '2018-11-23 09:02:54', '2018-11-23 09:02:54', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6440, 'nj_fourkong', '能介', '四空压站运行记录表_2018-11-23_09.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\四空压站运行记录表_2018-11-23_09.xlsx', '2018-11-23 09:03:29', '2018-11-23 09:03:29', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6441, 'nj_xinyikong', '能介', '新一空压站运行记录表_2018-11-23_09.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\新一空压站运行记录表_2018-11-23_09.xlsx', '2018-11-23 09:04:10', '2018-11-23 09:04:10', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6442, 'nj_onekongcount', '能介', '一空压站启停次数表_2018-11-23_09.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\一空压站启停次数表_2018-11-23_09.xlsx', '2018-11-23 09:05:00', '2018-11-23 09:05:00', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6443, 'nj_twokongcount', '能介', '二空压站启停次数表_2018-11-23_09.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\二空压站启停次数表_2018-11-23_09.xlsx', '2018-11-23 09:05:20', '2018-11-23 09:05:20', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6444, 'sj_tuoliutuoxiaogongyicaiji', '烧结', '6#脱硫脱硝工艺参数采集_2018-11-23_09.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\6#脱硫脱硝工艺参数采集_2018-11-23_09.xlsx', '2018-11-23 09:12:34', '2018-11-23 09:12:34', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6445, 'sj_gengzongbiao', '烧结', '五六烧主抽电耗跟踪表_2018-11-23_09.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\五六烧主抽电耗跟踪表_2018-11-23_09.xlsx', '2018-11-23 09:15:01', '2018-11-23 09:15:01', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6446, 'sj_tuoxiaoyunxingjilu', '烧结', '6#脱硝运行记录表月报_2018-11-23.xlsx', 'D:\\excel\\cn_zh\\烧结\\月表报\\6#脱硝运行记录表月报_2018-11-23.xlsx', '2018-11-23 09:17:27', '2018-11-23 09:17:27', NULL, 'report_month', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6447, 'sj_shaojieji6_day', '烧结', '6#烧结机生产日报_2018-11-23_09.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\6#烧结机生产日报_2018-11-23_09.xlsx', '2018-11-23 09:18:09', '2018-11-23 09:18:09', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6448, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-23_09.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-23_09.xlsx', '2018-11-23 09:18:29', '2018-11-23 09:18:29', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6449, 'sj_tuoliu', '烧结', '5#脱硫系统运行日报_2018-11-23_09.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#脱硫系统运行日报_2018-11-23_09.xlsx', '2018-11-23 09:31:50', '2018-11-23 09:31:50', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6450, 'sj_tuoliu', '烧结', '5#脱硫系统运行日报_2018-11-23_09.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#脱硫系统运行日报_2018-11-23_09.xlsx', '2018-11-23 09:34:15', '2018-11-23 09:34:15', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6451, 'sj_tuoliu', '烧结', '6#脱硫系统运行日报_2018-11-23_09.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\6#脱硫系统运行日报_2018-11-23_09.xlsx', '2018-11-23 09:34:16', '2018-11-23 09:34:16', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6452, 'nj_yasuokongqi', '能介', '压缩空气生产情况汇总表_2018-11-23_10.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\压缩空气生产情况汇总表_2018-11-23_10.xlsx', '2018-11-23 10:10:52', '2018-11-23 10:10:52', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6453, 'nj_sansigui_day', '能介', '三四柜区运行记录表_2018-11-23_11.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\三四柜区运行记录表_2018-11-23_11.xlsx', '2018-11-23 11:21:40', '2018-11-23 11:21:40', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6454, 'nj_sansigui_day', '能介', '三四柜区运行记录表_2018-11-23_11.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\三四柜区运行记录表_2018-11-23_11.xlsx', '2018-11-23 11:32:02', '2018-11-23 11:32:02', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6455, 'nj_sansigui_day', '能介', '三四柜区运行记录表_2018-11-23_11.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\三四柜区运行记录表_2018-11-23_11.xlsx', '2018-11-23 11:40:39', '2018-11-23 11:40:39', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6456, 'nj_sansigui_day', '能介', '三四柜区运行记录表_2018-11-23_11.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\三四柜区运行记录表_2018-11-23_11.xlsx', '2018-11-23 11:41:52', '2018-11-23 11:41:52', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6457, 'nj_sansigui_day', '能介', '三四柜区运行记录表_2018-11-23_11.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\三四柜区运行记录表_2018-11-23_11.xlsx', '2018-11-23 11:49:51', '2018-11-23 11:49:51', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6458, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-23_14.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-23_14.xlsx', '2018-11-23 14:06:10', '2018-11-23 14:06:10', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6459, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-23_14.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-23_14.xlsx', '2018-11-23 14:17:09', '2018-11-23 14:17:09', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6460, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-23_14.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-23_14.xlsx', '2018-11-23 14:25:18', '2018-11-23 14:25:18', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6461, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-23_14.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-23_14.xlsx', '2018-11-23 14:32:02', '2018-11-23 14:32:02', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6462, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-23_14.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-23_14.xlsx', '2018-11-23 14:33:54', '2018-11-23 14:33:54', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6463, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-23_15.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-23_15.xlsx', '2018-11-23 15:03:02', '2018-11-23 15:03:02', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6464, 'nj_meiqihunhemei', '能介', '煤气柜作业区混合煤气情况表_2018-11-23_15.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\煤气柜作业区混合煤气情况表_2018-11-23_15.xlsx', '2018-11-23 15:11:56', '2018-11-23 15:11:56', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6465, 'nj_sansigui_day', '能介', '三四柜区运行记录表_2018-11-23_15.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\三四柜区运行记录表_2018-11-23_15.xlsx', '2018-11-23 15:15:04', '2018-11-23 15:15:04', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6466, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-23_15.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-23_15.xlsx', '2018-11-23 15:24:52', '2018-11-23 15:24:52', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6467, 'nj_meiqihunhemei', '能介', '煤气柜作业区混合煤气情况表_2018-11-23_15.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\煤气柜作业区混合煤气情况表_2018-11-23_15.xlsx', '2018-11-23 15:26:08', '2018-11-23 15:26:08', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6468, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-23_15.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-23_15.xlsx', '2018-11-23 15:26:15', '2018-11-23 15:26:15', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6469, 'gl_bentiwendu_day', 'bf6', '高炉本体温度日报表_2018-11-23_15.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度日报表_2018-11-23_15.xlsx', '2018-11-23 15:38:51', '2018-11-23 15:38:51', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6470, 'gl_bentiwendu_day', 'bf6', '高炉本体温度日报表_2018-11-23_15.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度日报表_2018-11-23_15.xlsx', '2018-11-23 15:52:18', '2018-11-23 15:52:18', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6471, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-23_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-23_16.xlsx', '2018-11-23 16:01:58', '2018-11-23 16:01:58', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6472, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-23_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-23_16.xlsx', '2018-11-23 16:03:55', '2018-11-23 16:03:55', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6473, 'gl_bentiwendu_day', 'bf6', '高炉本体温度日报表_2018-11-23_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度日报表_2018-11-23_16.xlsx', '2018-11-23 16:06:19', '2018-11-23 16:06:19', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6474, 'yl_chejianshengchanyunxing', '原供料', '原料车间生产运行记录表_2018-11-23_16.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产运行记录表_2018-11-23_16.xlsx', '2018-11-23 16:08:20', '2018-11-23 16:08:20', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6475, 'yl_chejianshengchanjiaoban', '原供料', '原料车间生产交班表_2018-11-23_16.xlsx', 'D:\\excel\\cn_zh\\原供料\\日报表\\原料车间生产交班表_2018-11-23_16.xlsx', '2018-11-23 16:09:28', '2018-11-23 16:09:28', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6476, 'gl_bentiwendu_day', 'bf6', '高炉本体温度日报表_2018-11-23_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度日报表_2018-11-23_16.xlsx', '2018-11-23 16:09:40', '2018-11-23 16:09:40', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6477, 'gl_bentiwendu_day', 'bf6', '高炉本体温度日报表_2018-11-23_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度日报表_2018-11-23_16.xlsx', '2018-11-23 16:10:45', '2018-11-23 16:10:45', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6478, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-23_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-23_16.xlsx', '2018-11-23 16:12:04', '2018-11-23 16:12:04', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6479, 'gl_bentiwendu_day', 'bf6', '高炉本体温度日报表_2018-11-23_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度日报表_2018-11-23_16.xlsx', '2018-11-23 16:12:00', '2018-11-23 16:12:00', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6480, 'gl_ludingzhuangliaozuoye_day1', 'bf6', '高炉炉顶装料作业 日报1_2018-11-23_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉炉顶装料作业 日报1_2018-11-23_16.xlsx', '2018-11-23 16:15:11', '2018-11-23 16:15:11', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6481, 'gl_chutiezuoye_day', 'bf6', '出铁作业 日报_2018-11-23_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\出铁作业 日报_2018-11-23_16.xlsx', '2018-11-23 16:15:39', '2018-11-23 16:15:39', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6482, 'gl_bentiwendu_month', 'bf6', '高炉本体温度月报表_2018-11-23_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度月报表_2018-11-23_16.xlsx', '2018-11-23 16:19:10', '2018-11-23 16:19:10', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6483, 'gl_lengquebiwendu_day', 'bf6', '高炉冷却壁温度 日报_2018-11-23_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉冷却壁温度 日报_2018-11-23_16.xlsx', '2018-11-23 16:19:38', '2018-11-23 16:19:38', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6484, 'gl_jswgaolu_day', 'bf6', '高炉 日报_2018-11-23_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉 日报_2018-11-23_16.xlsx', '2018-11-23 16:21:30', '2018-11-23 16:21:30', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6485, 'gl_ludingbuliao_day', 'bf6', '高炉炉顶布料作业日报_2018-11-23_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉炉顶布料作业日报_2018-11-23_16.xlsx', '2018-11-23 16:22:13', '2018-11-23 16:22:13', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6486, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-23_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-23_16.xlsx', '2018-11-23 16:22:57', '2018-11-23 16:22:57', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6487, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-23_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-23_16.xlsx', '2018-11-23 16:27:49', '2018-11-23 16:27:49', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6488, 'nj_guifengjimeiyaji', '能介', '柜区风机煤压机时间统计表_2018-11-23_16.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\柜区风机煤压机时间统计表_2018-11-23_16.xlsx', '2018-11-23 16:38:06', '2018-11-23 16:38:06', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6489, 'jh_shaojiao', '焦化', '炼焦报表设计_2018-11-23_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦报表设计_2018-11-23_16.xlsx', '2018-11-23 16:48:14', '2018-11-23 16:48:14', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6490, 'gl_bentiwendu_month', 'bf6', '高炉本体温度月报表_2018-11-23_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度月报表_2018-11-23_16.xlsx', '2018-11-23 16:48:28', '2018-11-23 16:48:28', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6491, 'gl_bentiwendu_month', 'bf6', '高炉本体温度月报表_2018-11-23_16.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度月报表_2018-11-23_16.xlsx', '2018-11-23 16:49:31', '2018-11-23 16:49:31', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6492, 'nj_guifengjimeiyaji', '能介', '柜区风机煤压机时间统计表_2018-11-23_16.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\柜区风机煤压机时间统计表_2018-11-23_16.xlsx', '2018-11-23 16:52:58', '2018-11-23 16:52:58', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6493, 'nj_guifengjimeiyaji', '能介', '柜区风机煤压机时间统计表_2018-11-23_17.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\柜区风机煤压机时间统计表_2018-11-23_17.xlsx', '2018-11-23 17:06:58', '2018-11-23 17:06:58', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6494, 'nj_guifengjimeiyaji', '能介', '柜区风机煤压机时间统计表_2018-11-23_17.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\柜区风机煤压机时间统计表_2018-11-23_17.xlsx', '2018-11-23 17:11:59', '2018-11-23 17:11:59', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6495, 'nj_guifengjimeiyaji', '能介', '柜区风机煤压机时间统计表_2018-11-23_17.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\柜区风机煤压机时间统计表_2018-11-23_17.xlsx', '2018-11-23 17:27:36', '2018-11-23 17:27:36', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6496, 'jh_lianjiaoluwen', '焦化', '炼焦炉温记录表_2018-11-23_17.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦炉温记录表_2018-11-23_17.xlsx', '2018-11-23 17:52:56', '2018-11-23 17:52:56', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6497, 'nj_twokong', '能介', '二空压站运行记录表_2018-11-26_08.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\二空压站运行记录表_2018-11-26_08.xlsx', '2018-11-26 08:43:22', '2018-11-26 08:43:22', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6498, 'nj_threekong', '能介', '三空压站运行记录表_2018-11-26_08.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\三空压站运行记录表_2018-11-26_08.xlsx', '2018-11-26 08:43:46', '2018-11-26 08:43:46', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6499, 'nj_fourkong', '能介', '四空压站运行记录表_2018-11-26_08.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\四空压站运行记录表_2018-11-26_08.xlsx', '2018-11-26 08:44:05', '2018-11-26 08:44:05', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6500, 'nj_xinyikong', '能介', '新一空压站运行记录表_2018-11-26_08.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\新一空压站运行记录表_2018-11-26_08.xlsx', '2018-11-26 08:44:23', '2018-11-26 08:44:23', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6501, 'nj_sansigui_day', '能介', '三四柜区运行记录表_2018-11-26_08.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\三四柜区运行记录表_2018-11-26_08.xlsx', '2018-11-26 08:44:44', '2018-11-26 08:44:44', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6502, 'nj_onekongcount', '能介', '一空压站启停次数表_2018-11-26_08.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\一空压站启停次数表_2018-11-26_08.xlsx', '2018-11-26 08:45:00', '2018-11-26 08:45:00', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6503, 'nj_twokongcount', '能介', '二空压站启停次数表_2018-11-26_08.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\二空压站启停次数表_2018-11-26_08.xlsx', '2018-11-26 08:45:16', '2018-11-26 08:45:16', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6504, 'nj_threekongcount', '能介', '三空压站启停次数表_2018-11-26_08.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\三空压站启停次数表_2018-11-26_08.xlsx', '2018-11-26 08:45:32', '2018-11-26 08:45:32', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6505, 'nj_fourkongcount', '能介', '四空压站启停次数表_2018-11-26_08.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\四空压站启停次数表_2018-11-26_08.xlsx', '2018-11-26 08:45:49', '2018-11-26 08:45:49', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6506, 'nj_yasuokongqi', '能介', '压缩空气生产情况汇总表_2018-11-26_08.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\压缩空气生产情况汇总表_2018-11-26_08.xlsx', '2018-11-26 08:46:06', '2018-11-26 08:46:06', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6507, 'nj_meiqihunhemei', '能介', '煤气柜作业区混合煤气情况表_2018-11-26_08.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\煤气柜作业区混合煤气情况表_2018-11-26_08.xlsx', '2018-11-26 08:46:22', '2018-11-26 08:46:22', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6508, 'nj_guifengjimeiyaji', '能介', '柜区风机煤压机时间统计表_2018-11-26_08.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\柜区风机煤压机时间统计表_2018-11-26_08.xlsx', '2018-11-26 08:46:37', '2018-11-26 08:46:37', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6509, 'nj_xinyikong', '能介', '新一空压站运行记录表_2018-11-26_09.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\新一空压站运行记录表_2018-11-26_09.xlsx', '2018-11-26 09:21:08', '2018-11-26 09:21:08', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6510, 'nj_twokong', '能介', '二空压站运行记录表_2018-11-26_09.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\二空压站运行记录表_2018-11-26_09.xlsx', '2018-11-26 09:21:26', '2018-11-26 09:21:26', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6511, 'nj_threekong', '能介', '三空压站运行记录表_2018-11-26_09.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\三空压站运行记录表_2018-11-26_09.xlsx', '2018-11-26 09:21:43', '2018-11-26 09:21:43', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6512, 'nj_fourkong', '能介', '四空压站运行记录表_2018-11-26_09.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\四空压站运行记录表_2018-11-26_09.xlsx', '2018-11-26 09:22:00', '2018-11-26 09:22:00', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6513, 'sj_shaojieji6_day', '烧结', '6#烧结机生产日报_2018-11-26_09.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\6#烧结机生产日报_2018-11-26_09.xlsx', '2018-11-26 09:25:46', '2018-11-26 09:25:46', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6514, 'jh_lianjiaoluwen', '焦化', '炼焦炉温记录表_2018-11-27_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦炉温记录表_2018-11-27_16.pptx', '2018-11-26 09:45:03', '2018-11-26 09:45:03', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6515, 'jh_lianjiaoluwen', '焦化', '炼焦炉温记录表_2018-11-26_09.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\炼焦炉温记录表_2018-11-26_09.xlsx', '2018-11-26 09:46:34', '2018-11-26 09:46:34', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6516, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-26_10.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-26_10.xlsx', '2018-11-26 10:16:56', '2018-11-26 10:16:56', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6517, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-26_10.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-26_10.xlsx', '2018-11-26 10:30:21', '2018-11-26 10:30:21', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6518, 'jh_zidongpeimei', '焦化', '自动配煤（班）报表_2018-11-26_10.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\自动配煤（班）报表_2018-11-26_10.xlsx', '2018-11-26 10:49:14', '2018-11-26 10:49:14', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6519, 'jh_zidongpeimei', '焦化', '自动配煤（班）报表_2018-11-26_10.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\自动配煤（班）报表_2018-11-26_10.xlsx', '2018-11-26 10:57:30', '2018-11-26 10:57:30', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6520, 'jh_zidongpeimei', '焦化', '自动配煤（班）报表_2018-11-26_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\自动配煤（班）报表_2018-11-26_11.xlsx', '2018-11-26 11:06:36', '2018-11-26 11:06:36', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6521, 'jh_zidongpeimei', '焦化', '自动配煤（班）报表_2018-11-26_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\自动配煤（班）报表_2018-11-26_11.xlsx', '2018-11-26 11:15:58', '2018-11-26 11:15:58', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6522, 'jh_fensuixidu', '焦化', '粉碎细度（月）报表设计_2018-11-26.xlsx', 'D:\\excel\\cn_zh\\焦化\\月表报\\粉碎细度（月）报表设计_2018-11-26.xlsx', '2018-11-26 11:36:18', '2018-11-26 11:36:18', NULL, 'report_month', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6523, 'jh_cdqcaozuo', '焦化', 'CDQ操作运行报表（日）报表设计_2018-11-26_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CDQ操作运行报表（日）报表设计_2018-11-26_11.xlsx', '2018-11-26 11:52:11', '2018-11-26 11:52:11', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6524, 'nj_twokongcount', '能介', '二空压站启停次数表_2018-11-26_13.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\二空压站启停次数表_2018-11-26_13.xlsx', '2018-11-26 13:11:04', '2018-11-26 13:11:04', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6525, 'nj_twokongcount', '能介', '二空压站启停次数表_2018-11-26_13.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\二空压站启停次数表_2018-11-26_13.xlsx', '2018-11-26 13:15:26', '2018-11-26 13:15:26', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6526, 'nj_onekongcount', '能介', '一空压站启停次数表_2018-11-26_13.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\一空压站启停次数表_2018-11-26_13.xlsx', '2018-11-26 13:16:06', '2018-11-26 13:16:06', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6527, 'nj_twokongcount', '能介', '二空压站启停次数表_2018-11-26_13.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\二空压站启停次数表_2018-11-26_13.xlsx', '2018-11-26 13:21:59', '2018-11-26 13:21:59', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6528, 'nj_threekongcount', '能介', '三空压站启停次数表_2018-11-26_13.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\三空压站启停次数表_2018-11-26_13.xlsx', '2018-11-26 13:22:23', '2018-11-26 13:22:23', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6529, 'nj_threekongcount', '能介', '三空压站启停次数表_2018-11-26_13.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\三空压站启停次数表_2018-11-26_13.xlsx', '2018-11-26 13:23:21', '2018-11-26 13:23:21', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6530, 'nj_yasuokongqi', '能介', '压缩空气生产情况汇总表_2018-11-26_13.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\压缩空气生产情况汇总表_2018-11-26_13.xlsx', '2018-11-26 13:24:39', '2018-11-26 13:24:39', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6531, 'nj_yasuokongqi', '能介', '压缩空气生产情况汇总表_2018-11-26_13.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\压缩空气生产情况汇总表_2018-11-26_13.xlsx', '2018-11-26 13:36:27', '2018-11-26 13:36:27', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6532, 'nj_threekong', '能介', '三空压站运行记录表_2018-11-26_13.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\三空压站运行记录表_2018-11-26_13.xlsx', '2018-11-26 13:37:48', '2018-11-26 13:37:48', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6533, 'nj_fourkong', '能介', '四空压站运行记录表_2018-11-26_13.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\四空压站运行记录表_2018-11-26_13.xlsx', '2018-11-26 13:38:07', '2018-11-26 13:38:07', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6534, 'nj_twokongcount', '能介', '二空压站启停次数表_2018-11-26_13.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\二空压站启停次数表_2018-11-26_13.xlsx', '2018-11-26 13:39:00', '2018-11-26 13:39:00', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6535, 'nj_threekongcount', '能介', '三空压站启停次数表_2018-11-26_13.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\三空压站启停次数表_2018-11-26_13.xlsx', '2018-11-26 13:39:18', '2018-11-26 13:39:18', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6536, 'nj_fourkong', '能介', '四空压站运行记录表_2018-11-26_13.xlsx', 'D:\\excel\\cn_zh\\能介\\日报表\\四空压站运行记录表_2018-11-26_13.xlsx', '2018-11-26 13:42:18', '2018-11-26 13:42:18', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6537, 'jh_chujiaochuchen', '焦化', '出焦除尘报表_2018-11-26_13.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\出焦除尘报表_2018-11-26_13.xlsx', '2018-11-26 13:56:14', '2018-11-26 13:56:14', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6538, 'jh_zhuangmeichuchen', '焦化', 'CK67-装煤除尘报表_2018-11-26_14.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-装煤除尘报表_2018-11-26_14.xlsx', '2018-11-26 14:06:23', '2018-11-26 14:06:23', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6539, 'jh_zhuangmeichuchen', '焦化', 'CK67-装煤除尘报表_2018-11-26_14.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-装煤除尘报表_2018-11-26_14.xlsx', '2018-11-26 14:07:35', '2018-11-26 14:07:35', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6540, 'jh_cdqchuchen', '焦化', 'CK67-CDQ除尘报表_2018-11-26_14.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-CDQ除尘报表_2018-11-26_14.xlsx', '2018-11-26 14:13:37', '2018-11-26 14:13:37', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6541, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-26_14.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-26_14.xlsx', '2018-11-26 14:16:55', '2018-11-26 14:16:55', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6542, 'jh_shaijiaochuchen', '焦化', 'CK67-筛焦除尘报表_2018-11-26_14.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-筛焦除尘报表_2018-11-26_14.xlsx', '2018-11-26 14:18:00', '2018-11-26 14:18:00', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6543, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-26_14.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-26_14.xlsx', '2018-11-26 14:22:40', '2018-11-26 14:22:40', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6544, 'jh_gufenglengning', '焦化', 'CK67-鼓风冷凝(日)报表_2018-11-26_14.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-鼓风冷凝(日)报表_2018-11-26_14.xlsx', '2018-11-26 14:26:20', '2018-11-26 14:26:20', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6545, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-26_14.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-26_14.xlsx', '2018-11-26 14:27:05', '2018-11-26 14:27:05', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6546, 'jh_zhilengxunhuanshui', '焦化', 'CK67-制冷循环水（日）报表_2018-11-26_14.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-制冷循环水（日）报表_2018-11-26_14.xlsx', '2018-11-26 14:31:03', '2018-11-26 14:31:03', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6547, 'jh_zhengan', '焦化', 'CK67-蒸氨（日）报表_2018-11-26_14.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-蒸氨（日）报表_2018-11-26_14.xlsx', '2018-11-26 14:35:27', '2018-11-26 14:35:27', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6548, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-26_14.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-26_14.xlsx', '2018-11-26 14:38:14', '2018-11-26 14:38:14', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6549, 'jh_liuan', '焦化', 'CK67-硫铵（日）报表_2018-11-26_14.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-硫铵（日）报表_2018-11-26_14.xlsx', '2018-11-26 14:40:02', '2018-11-26 14:40:02', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6550, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-26_14.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-26_14.xlsx', '2018-11-26 14:41:06', '2018-11-26 14:41:06', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6551, 'jh_chubenzhengliu', '焦化', 'CK67-粗苯蒸馏（日）报表_2018-11-26_14.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-粗苯蒸馏（日）报表_2018-11-26_14.xlsx', '2018-11-26 14:45:26', '2018-11-26 14:45:26', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6552, 'jh_tuoliujiexi', '焦化', 'CK67-脱硫解吸（日）4.3m无此表报表_2018-11-26_14.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-脱硫解吸（日）4.3m无此表报表_2018-11-26_14.xlsx', '2018-11-26 14:54:15', '2018-11-26 14:54:15', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6553, 'jh_lianjiaoribao', '焦化', 'CK67-炼焦日报表（班日、月）报表_2018-11-26_15.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-炼焦日报表（班日、月）报表_2018-11-26_15.xlsx', '2018-11-26 15:04:44', '2018-11-26 15:04:44', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6554, 'jh_jiaolujiare7', '焦化', 'CK67-7#焦炉加热制度表（日）报表_2018-11-26_15.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-7#焦炉加热制度表（日）报表_2018-11-26_15.xlsx', '2018-11-26 15:13:30', '2018-11-26 15:13:30', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6555, 'jh_jiaolujiare6', '焦化', 'CK67-6#焦炉加热制度表（日）报表_2018-11-26_15.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-6#焦炉加热制度表（日）报表_2018-11-26_15.xlsx', '2018-11-26 15:14:22', '2018-11-26 15:14:22', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6556, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-26_15.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-26_15.xlsx', '2018-11-26 15:20:23', '2018-11-26 15:20:23', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6557, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-26_15.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-26_15.xlsx', '2018-11-26 15:25:00', '2018-11-26 15:25:00', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6558, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-26_15.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-26_15.xlsx', '2018-11-26 15:32:23', '2018-11-26 15:32:23', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6559, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-26_15.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-26_15.xlsx', '2018-11-26 15:36:10', '2018-11-26 15:36:10', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6560, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-26_15.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-26_15.xlsx', '2018-11-26 15:46:04', '2018-11-26 15:46:04', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6561, 'jh_luwenjilu6', '焦化', 'CK67-6#炉温记录从动态管控系统读取报表_2018-11-26_15.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-6#炉温记录从动态管控系统读取报表_2018-11-26_15.xlsx', '2018-11-26 15:51:55', '2018-11-26 15:51:55', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6562, 'jh_luwenjilu7', '焦化', 'CK67-7#炉温记录从动态管控系统读取报表_2018-11-26_15.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-7#炉温记录从动态管控系统读取报表_2018-11-26_15.xlsx', '2018-11-26 15:52:55', '2018-11-26 15:52:55', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6563, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-26_15.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-26_15.xlsx', '2018-11-26 15:57:33', '2018-11-26 15:57:33', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6564, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-26_16.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-26_16.xlsx', '2018-11-26 16:03:34', '2018-11-26 16:03:34', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6565, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-26_16.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-26_16.xlsx', '2018-11-26 16:06:29', '2018-11-26 16:06:29', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6566, 'jh_luwenjilu7', '焦化', 'CK67-7#炉温记录从动态管控系统读取报表_2018-11-26_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-7#炉温记录从动态管控系统读取报表_2018-11-26_16.xlsx', '2018-11-26 16:06:40', '2018-11-26 16:06:40', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6567, 'jh_luwenjilu7', '焦化', 'CK67-7#炉温记录从动态管控系统读取报表_2018-11-26_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-7#炉温记录从动态管控系统读取报表_2018-11-26_16.xlsx', '2018-11-26 16:08:32', '2018-11-26 16:08:32', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6568, 'jh_luwenjilu7', '焦化', 'CK67-7#炉温记录从动态管控系统读取报表_2018-11-26_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-7#炉温记录从动态管控系统读取报表_2018-11-26_16.xlsx', '2018-11-26 16:12:26', '2018-11-26 16:12:26', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6569, 'jh_luwenjilu6', '焦化', 'CK67-6#炉温记录从动态管控系统读取报表_2018-11-26_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-6#炉温记录从动态管控系统读取报表_2018-11-26_16.xlsx', '2018-11-26 16:17:26', '2018-11-26 16:17:26', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6570, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-26_16.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-26_16.xlsx', '2018-11-26 16:20:34', '2018-11-26 16:20:34', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6571, 'jh_luwenjilu6', '焦化', 'CK67-6#炉温记录从动态管控系统读取报表_2018-11-26_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-6#炉温记录从动态管控系统读取报表_2018-11-26_16.xlsx', '2018-11-26 16:21:08', '2018-11-26 16:21:08', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6572, 'jh_luwenjilu7', '焦化', 'CK67-7#炉温记录从动态管控系统读取报表_2018-11-26_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-7#炉温记录从动态管控系统读取报表_2018-11-26_16.xlsx', '2018-11-26 16:21:23', '2018-11-26 16:21:23', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6573, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-26_16.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-26_16.xlsx', '2018-11-26 16:32:20', '2018-11-26 16:32:20', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6574, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-26_16.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-26_16.xlsx', '2018-11-26 16:42:14', '2018-11-26 16:42:14', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6575, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-26_16.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-26_16.xlsx', '2018-11-26 16:47:08', '2018-11-26 16:47:08', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6576, 'jh_luwenjilu6', '焦化', 'CK67-6#炉温记录从动态管控系统读取报表_2018-11-26_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-6#炉温记录从动态管控系统读取报表_2018-11-26_16.xlsx', '2018-11-26 16:48:34', '2018-11-26 16:48:34', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6577, 'jh_luwenjilu7', '焦化', 'CK67-7#炉温记录从动态管控系统读取报表_2018-11-26_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-7#炉温记录从动态管控系统读取报表_2018-11-26_16.xlsx', '2018-11-26 16:48:51', '2018-11-26 16:48:51', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6578, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-26_16.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-26_16.xlsx', '2018-11-26 16:50:17', '2018-11-26 16:50:17', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6579, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-26_16.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-26_16.xlsx', '2018-11-26 16:54:15', '2018-11-26 16:54:15', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6580, 'jh_luwenjilu6', '焦化', 'CK67-6#炉温记录从动态管控系统读取报表_2018-11-26_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-6#炉温记录从动态管控系统读取报表_2018-11-26_16.xlsx', '2018-11-26 16:56:31', '2018-11-26 16:56:31', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6581, 'jh_luwenjilu7', '焦化', 'CK67-7#炉温记录从动态管控系统读取报表_2018-11-26_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-7#炉温记录从动态管控系统读取报表_2018-11-26_16.xlsx', '2018-11-26 16:57:29', '2018-11-26 16:57:29', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6582, 'gl_chutiezuoye_day', 'bf6', '出铁作业 日报_2018-11-26_17.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\出铁作业 日报_2018-11-26_17.xlsx', '2018-11-26 17:01:26', '2018-11-26 17:01:26', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6583, 'gl_ludingzhuangliaozuoye_day1', 'bf6', '高炉炉顶装料作业 日报1_2018-11-26_17.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉炉顶装料作业 日报1_2018-11-26_17.xlsx', '2018-11-26 17:03:40', '2018-11-26 17:03:40', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6584, 'gl_bentiwendu_day', 'bf6', '高炉本体温度日报表_2018-11-26_17.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度日报表_2018-11-26_17.xlsx', '2018-11-26 17:04:44', '2018-11-26 17:04:44', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6585, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-26_17.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-26_17.xlsx', '2018-11-26 17:06:42', '2018-11-26 17:06:42', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6586, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-26_17.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-26_17.xlsx', '2018-11-26 17:08:34', '2018-11-26 17:08:34', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6587, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-26_17.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-26_17.xlsx', '2018-11-26 17:16:12', '2018-11-26 17:16:12', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6588, 'gl_bentiwendu_month', 'bf6', '高炉本体温度月报表_2018-11-26_17.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度月报表_2018-11-26_17.xlsx', '2018-11-26 17:24:14', '2018-11-26 17:24:14', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6589, 'gl_ludingzhuangliaozuoye_day1', 'bf6', '高炉炉顶装料作业 日报1_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉炉顶装料作业 日报1_2018-11-27_09.xlsx', '2018-11-27 09:16:14', '2018-11-27 09:16:14', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6590, 'gl_chutiezuoye_day', 'bf6', '出铁作业 日报_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\出铁作业 日报_2018-11-27_09.xlsx', '2018-11-27 09:16:42', '2018-11-27 09:16:42', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6591, 'gl_bentiwendu_day', 'bf6', '高炉本体温度日报表_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度日报表_2018-11-27_09.xlsx', '2018-11-27 09:17:05', '2018-11-27 09:17:05', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6592, 'gl_bentiwendu_month', 'bf6', '高炉本体温度月报表_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度月报表_2018-11-27_09.xlsx', '2018-11-27 09:17:51', '2018-11-27 09:17:51', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6593, 'gl_lengquebiwendu_day', 'bf6', '高炉冷却壁温度 日报_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉冷却壁温度 日报_2018-11-27_09.xlsx', '2018-11-27 09:18:24', '2018-11-27 09:18:24', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6594, 'gl_lengquebiwendu_month', 'bf6', '高炉冷却壁温度 月报_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉冷却壁温度 月报_2018-11-27_09.xlsx', '2018-11-27 09:18:56', '2018-11-27 09:18:56', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6595, 'gl_jswgaolu_day', 'bf6', '高炉 日报_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉 日报_2018-11-27_09.xlsx', '2018-11-27 09:19:32', '2018-11-27 09:19:32', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6596, 'gl_taisu1_month', 'bf6', '高炉 月报_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉 月报_2018-11-27_09.xlsx', '2018-11-27 09:20:04', '2018-11-27 09:20:04', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6597, 'gl_ludingbuliao_day', 'bf6', '高炉炉顶布料作业日报_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉炉顶布料作业日报_2018-11-27_09.xlsx', '2018-11-27 09:20:30', '2018-11-27 09:20:30', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6598, 'gl_ludingzhuangliaozuoye_day1', 'bf6', '高炉炉顶装料作业 日报1_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉炉顶装料作业 日报1_2018-11-27_09.xlsx', '2018-11-27 09:21:24', '2018-11-27 09:21:24', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6599, 'gl_chutiezuoye_day', 'bf6', '出铁作业 日报_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\出铁作业 日报_2018-11-27_09.xlsx', '2018-11-27 09:21:52', '2018-11-27 09:21:52', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6600, 'gl_bentiwendu_day', 'bf6', '高炉本体温度日报表_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度日报表_2018-11-27_09.xlsx', '2018-11-27 09:22:35', '2018-11-27 09:22:35', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6601, 'gl_bentiwendu_month', 'bf6', '高炉本体温度月报表_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度月报表_2018-11-27_09.xlsx', '2018-11-27 09:27:16', '2018-11-27 09:27:16', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6602, 'gl_lengquebiwendu_day', 'bf6', '高炉冷却壁温度 日报_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉冷却壁温度 日报_2018-11-27_09.xlsx', '2018-11-27 09:28:13', '2018-11-27 09:28:13', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6603, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-27_09.xlsx', '2018-11-27 09:29:18', '2018-11-27 09:29:18', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6604, 'jh_zidongpeimei', '焦化', 'CK67-自动配煤（班）报表_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-自动配煤（班）报表_2018-11-27_09.xlsx', '2018-11-27 09:33:49', '2018-11-27 09:33:49', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6605, 'jh_fensuixidu', '焦化', 'CK67-粉碎细度（月）报表_2018-11-27.xlsx', 'D:\\excel\\cn_zh\\焦化\\月表报\\CK67-粉碎细度（月）报表_2018-11-27.xlsx', '2018-11-27 09:34:07', '2018-11-27 09:34:07', NULL, 'report_month', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6606, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-27_09.xlsx', '2018-11-27 09:34:19', '2018-11-27 09:34:19', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6607, 'jh_chujiaochuchen', '焦化', 'CK67-出焦除尘报表_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-出焦除尘报表_2018-11-27_09.xlsx', '2018-11-27 09:35:00', '2018-11-27 09:35:00', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6608, 'jh_zhuangmeichuchen', '焦化', 'CK67-装煤除尘报表_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-装煤除尘报表_2018-11-27_09.xlsx', '2018-11-27 09:35:22', '2018-11-27 09:35:22', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6609, 'jh_cdqchuchen', '焦化', 'CK67-CDQ除尘报表_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-CDQ除尘报表_2018-11-27_09.xlsx', '2018-11-27 09:35:40', '2018-11-27 09:35:40', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6610, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-27_09.xlsx', '2018-11-27 09:35:45', '2018-11-27 09:35:45', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6611, 'jh_shaijiaochuchen', '焦化', 'CK67-筛焦除尘报表_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-筛焦除尘报表_2018-11-27_09.xlsx', '2018-11-27 09:35:59', '2018-11-27 09:35:59', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6612, 'jh_gufenglengning', '焦化', 'CK67-鼓风冷凝(日)报表_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-鼓风冷凝(日)报表_2018-11-27_09.xlsx', '2018-11-27 09:36:36', '2018-11-27 09:36:36', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6613, 'jh_zhilengxunhuanshui', '焦化', 'CK67-制冷循环水（日）报表_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-制冷循环水（日）报表_2018-11-27_09.xlsx', '2018-11-27 09:37:00', '2018-11-27 09:37:00', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6614, 'jh_zhengan', '焦化', 'CK67-蒸氨（日）报表_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-蒸氨（日）报表_2018-11-27_09.xlsx', '2018-11-27 09:37:36', '2018-11-27 09:37:36', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6615, 'jh_liuan', '焦化', 'CK67-硫铵（日）报表_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-硫铵（日）报表_2018-11-27_09.xlsx', '2018-11-27 09:38:12', '2018-11-27 09:38:12', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6616, 'jh_chubenzhengliu', '焦化', 'CK67-粗苯蒸馏（日）报表_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-粗苯蒸馏（日）报表_2018-11-27_09.xlsx', '2018-11-27 09:38:41', '2018-11-27 09:38:41', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6617, 'jh_zhonglengxiben', '焦化', 'CK67-终冷洗苯报表_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-终冷洗苯报表_2018-11-27_09.xlsx', '2018-11-27 09:39:06', '2018-11-27 09:39:06', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6618, 'jh_tuoliujiexi', '焦化', 'CK67-脱硫解吸（日）4.3m无此表报表_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-脱硫解吸（日）4.3m无此表报表_2018-11-27_09.xlsx', '2018-11-27 09:39:28', '2018-11-27 09:39:28', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6619, 'jh_zhisuancaozuo', '焦化', 'CK67-制酸操作（日）4.3m无此表报表_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-制酸操作（日）4.3m无此表报表_2018-11-27_09.xlsx', '2018-11-27 09:39:54', '2018-11-27 09:39:54', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6620, 'jh_lianjiaoribao', '焦化', 'CK67-炼焦日报表（班日、月）报表_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-炼焦日报表（班日、月）报表_2018-11-27_09.xlsx', '2018-11-27 09:40:10', '2018-11-27 09:40:10', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6621, 'jh_jiaolujiare6', '焦化', 'CK67-6#焦炉加热制度表（日）报表_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-6#焦炉加热制度表（日）报表_2018-11-27_09.xlsx', '2018-11-27 09:40:34', '2018-11-27 09:40:34', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6622, 'jh_jiaolujiare7', '焦化', 'CK67-7#焦炉加热制度表（日）报表_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-7#焦炉加热制度表（日）报表_2018-11-27_09.xlsx', '2018-11-27 09:41:03', '2018-11-27 09:41:03', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6623, 'jh_luwenjilu6', '焦化', 'CK67-6#炉温记录从动态管控系统读取报表_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-6#炉温记录从动态管控系统读取报表_2018-11-27_09.xlsx', '2018-11-27 09:41:22', '2018-11-27 09:41:22', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6624, 'jh_luwenjilu7', '焦化', 'CK67-7#炉温记录从动态管控系统读取报表_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-7#炉温记录从动态管控系统读取报表_2018-11-27_09.xlsx', '2018-11-27 09:41:38', '2018-11-27 09:41:38', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6625, 'jh_luwenjilu6', '焦化', 'CK67-6#炉温记录从动态管控系统读取报表_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-6#炉温记录从动态管控系统读取报表_2018-11-27_09.xlsx', '2018-11-27 09:52:51', '2018-11-27 09:52:51', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6626, 'jh_luwenjilu7', '焦化', 'CK67-7#炉温记录从动态管控系统读取报表_2018-11-27_09.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-7#炉温记录从动态管控系统读取报表_2018-11-27_09.xlsx', '2018-11-27 09:53:12', '2018-11-27 09:53:12', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6627, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-27_10.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-27_10.xlsx', '2018-11-27 10:17:42', '2018-11-27 10:17:42', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6628, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-27_10.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-27_10.xlsx', '2018-11-27 10:20:03', '2018-11-27 10:20:03', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6629, 'jh_jiaolujiare6', '焦化', 'CK67-6#焦炉加热制度表（日）报表_2018-11-27_10.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-6#焦炉加热制度表（日）报表_2018-11-27_10.xlsx', '2018-11-27 10:32:27', '2018-11-27 10:32:27', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6630, 'gl_bentiwendu_day', 'bf6', '高炉本体温度日报表_2018-11-27_10.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度日报表_2018-11-27_10.xlsx', '2018-11-27 10:45:36', '2018-11-27 10:45:36', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6631, 'gl_bentiwendu_month', 'bf6', '高炉本体温度月报表_2018-11-27_10.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度月报表_2018-11-27_10.xlsx', '2018-11-27 10:47:01', '2018-11-27 10:47:01', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6632, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-27_10.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-27_10.xlsx', '2018-11-27 10:53:06', '2018-11-27 10:53:06', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6633, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-27_11.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-27_11.xlsx', '2018-11-27 11:02:14', '2018-11-27 11:02:14', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6634, 'jh_luwenjilu6', '焦化', 'CK67-6#炉温记录从动态管控系统读取报表_2018-11-27_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-6#炉温记录从动态管控系统读取报表_2018-11-27_11.xlsx', '2018-11-27 11:08:27', '2018-11-27 11:08:27', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6635, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-27_11.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-27_11.xlsx', '2018-11-27 11:08:57', '2018-11-27 11:08:57', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6636, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-27_11.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-27_11.xlsx', '2018-11-27 11:17:04', '2018-11-27 11:17:04', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6637, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-27_11.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-27_11.xlsx', '2018-11-27 11:24:47', '2018-11-27 11:24:47', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6638, 'sj_shaojieji5_day', '烧结', '5#烧结机生产日报_2018-11-27_11.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#烧结机生产日报_2018-11-27_11.xlsx', '2018-11-27 11:29:36', '2018-11-27 11:29:36', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6639, 'sj_tuoliu', '烧结', '5#脱硫系统运行日报_2018-11-27_14.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\5#脱硫系统运行日报_2018-11-27_14.xlsx', '2018-11-27 14:19:15', '2018-11-27 14:19:15', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6640, 'sj_tuoliu', '烧结', '6#脱硫系统运行日报_2018-11-27_14.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\6#脱硫系统运行日报_2018-11-27_14.xlsx', '2018-11-27 14:19:15', '2018-11-27 14:19:15', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6641, 'gl_bentiwendu_month', 'bf6', '高炉本体温度月报表_2018-11-27_14.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度月报表_2018-11-27_14.xlsx', '2018-11-27 14:28:52', '2018-11-27 14:28:52', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6642, 'gl_lengquebiwendu_day', 'bf6', '高炉冷却壁温度 日报_2018-11-27_15.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉冷却壁温度 日报_2018-11-27_15.xlsx', '2018-11-27 15:28:55', '2018-11-27 15:28:55', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6643, 'jh_fensuixidu', '焦化', 'CK67-粉碎细度（月）报表_2018-11-27.xlsx', 'D:\\excel\\cn_zh\\焦化\\月表报\\CK67-粉碎细度（月）报表_2018-11-27.xlsx', '2018-11-27 15:29:40', '2018-11-27 15:29:40', NULL, 'report_month', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6644, 'gl_bentiwendu_month', 'bf6', '高炉本体温度月报表_2018-11-27_15.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度月报表_2018-11-27_15.xlsx', '2018-11-27 15:30:27', '2018-11-27 15:30:27', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6645, 'sj_shaojieji6_day', '烧结', '6#烧结机生产日报_2018-11-27_16.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\6#烧结机生产日报_2018-11-27_16.xlsx', '2018-11-27 16:26:26', '2018-11-27 16:26:26', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6646, 'sj_liushaogycanshu6', '烧结', '4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-27_17.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-27_17.xlsx', '2018-11-27 17:02:56', '2018-11-27 17:02:56', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6647, 'sj_liushaogycanshu6', '烧结', '4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-27_17.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-27_17.xlsx', '2018-11-27 17:03:38', '2018-11-27 17:03:38', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6648, 'sj_liushaogycanshu6', '烧结', '4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-27_17.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-27_17.xlsx', '2018-11-27 17:04:48', '2018-11-27 17:04:48', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6649, 'sj_liushaogycanshu6', '烧结', '4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-27_17.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-27_17.xlsx', '2018-11-27 17:06:08', '2018-11-27 17:06:08', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6650, 'sj_liushaogycanshu6', '烧结', '4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-27_17.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-27_17.xlsx', '2018-11-27 17:09:47', '2018-11-27 17:09:47', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6651, 'sj_liushaogycanshu6', '烧结', '4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-27_17.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-27_17.xlsx', '2018-11-27 17:18:17', '2018-11-27 17:18:17', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6652, 'jh_luwenjilu6', '焦化', 'CK67-6#炉温记录从动态管控系统读取报表_2018-11-28_09.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-6#炉温记录从动态管控系统读取报表_2018-11-28_09.xlsx', '2018-11-28 09:20:08', '2018-11-28 09:20:08', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6653, 'jh_luwenjilu7', '焦化', 'CK67-7#炉温记录从动态管控系统读取报表_2018-11-28_09.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-7#炉温记录从动态管控系统读取报表_2018-11-28_09.xlsx', '2018-11-28 09:25:23', '2018-11-28 09:25:23', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6654, 'jh_luwenjilu7', '焦化', 'CK67-7#炉温记录从动态管控系统读取报表_2018-11-28_09.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-7#炉温记录从动态管控系统读取报表_2018-11-28_09.xlsx', '2018-11-28 09:41:15', '2018-11-28 09:41:15', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6655, 'gl_bentiwendu_month', 'bf6', '高炉本体温度月报表_2018-11-28_10.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉本体温度月报表_2018-11-28_10.xlsx', '2018-11-28 10:02:15', '2018-11-28 10:02:15', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6656, 'gl_lengquebiwendu_month', 'bf6', '高炉冷却壁温度 月报_2018-11-28_10.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉冷却壁温度 月报_2018-11-28_10.xlsx', '2018-11-28 10:04:13', '2018-11-28 10:04:13', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6657, 'gl_taisu1_month', 'bf6', '高炉 月报_2018-11-28_10.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉 月报_2018-11-28_10.xlsx', '2018-11-28 10:04:42', '2018-11-28 10:04:42', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6658, 'gl_taisu1_month', 'bf6', '高炉 月报_2018-11-28_10.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉 月报_2018-11-28_10.xlsx', '2018-11-28 10:05:49', '2018-11-28 10:05:49', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6659, 'gl_taisu1_month', 'bf6', '高炉 月报_2018-11-28_10.xlsx', 'D:\\excel\\cn_zh\\bf6\\日报表\\高炉 月报_2018-11-28_10.xlsx', '2018-11-28 10:07:02', '2018-11-28 10:07:02', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6660, 'jh_luwenjilu6', '焦化', 'CK67-6#炉温记录从动态管控系统读取报表_2018-11-28_10.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-6#炉温记录从动态管控系统读取报表_2018-11-28_10.xlsx', '2018-11-28 10:34:43', '2018-11-28 10:34:43', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6661, 'jh_luwenjilu6', '焦化', 'CK67-6#炉温记录从动态管控系统读取报表_2018-11-28_10.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-6#炉温记录从动态管控系统读取报表_2018-11-28_10.xlsx', '2018-11-28 10:42:18', '2018-11-28 10:42:18', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6662, 'jh_luwenjilu6', '焦化', 'CK67-6#炉温记录从动态管控系统读取报表_2018-11-28_10.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-6#炉温记录从动态管控系统读取报表_2018-11-28_10.xlsx', '2018-11-28 10:46:20', '2018-11-28 10:46:20', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6663, 'jh_luwenjilu6', '焦化', 'CK67-6#炉温记录从动态管控系统读取报表_2018-11-28_10.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-6#炉温记录从动态管控系统读取报表_2018-11-28_10.xlsx', '2018-11-28 10:52:19', '2018-11-28 10:52:19', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6664, 'sj_liushaogycanshu6', '烧结', '4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-28_11.xlsx', '2018-11-28 11:02:24', '2018-11-28 11:02:24', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6665, 'jh_luwenjilu6', '焦化', 'CK67-6#炉温记录从动态管控系统读取报表_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-6#炉温记录从动态管控系统读取报表_2018-11-28_11.xlsx', '2018-11-28 11:02:36', '2018-11-28 11:02:36', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6666, 'jh_luwenjilu6', '焦化', 'CK67-6#炉温记录从动态管控系统读取报表_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-6#炉温记录从动态管控系统读取报表_2018-11-28_11.xlsx', '2018-11-28 11:04:11', '2018-11-28 11:04:11', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6667, 'sj_liushaogycanshu6', '烧结', '4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-28_11.xlsx', '2018-11-28 11:05:13', '2018-11-28 11:05:13', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6668, 'jh_luwenjilu7', '焦化', 'CK67-7#炉温记录从动态管控系统读取报表_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-7#炉温记录从动态管控系统读取报表_2018-11-28_11.xlsx', '2018-11-28 11:07:40', '2018-11-28 11:07:40', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6669, 'sj_liushaogycanshu6', '烧结', '4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-28_11.xlsx', '2018-11-28 11:09:13', '2018-11-28 11:09:13', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6670, 'jh_luwenjilu7', '焦化', 'CK67-7#炉温记录从动态管控系统读取报表_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-7#炉温记录从动态管控系统读取报表_2018-11-28_11.xlsx', '2018-11-28 11:10:53', '2018-11-28 11:10:53', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6671, 'jh_luwenjilu7', '焦化', 'CK67-7#炉温记录从动态管控系统读取报表_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-7#炉温记录从动态管控系统读取报表_2018-11-28_11.xlsx', '2018-11-28 11:17:29', '2018-11-28 11:17:29', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6672, 'jh_luwenjilu6', '焦化', 'CK67-6#炉温记录从动态管控系统读取报表_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-6#炉温记录从动态管控系统读取报表_2018-11-28_11.xlsx', '2018-11-28 11:24:19', '2018-11-28 11:24:19', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6673, 'jh_luwenjilu7', '焦化', 'CK67-7#炉温记录从动态管控系统读取报表_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-7#炉温记录从动态管控系统读取报表_2018-11-28_11.xlsx', '2018-11-28 11:25:27', '2018-11-28 11:25:27', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6674, 'jh_jiaolujiare7', '焦化', 'CK67-7#焦炉加热制度表（日）报表_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-7#焦炉加热制度表（日）报表_2018-11-28_11.xlsx', '2018-11-28 11:28:03', '2018-11-28 11:28:03', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6675, 'jh_jiaolujiare6', '焦化', 'CK67-6#焦炉加热制度表（日）报表_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-6#焦炉加热制度表（日）报表_2018-11-28_11.xlsx', '2018-11-28 11:28:28', '2018-11-28 11:28:28', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6676, 'sj_liushaogycanshu6', '烧结', '4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-28_11.xlsx', '2018-11-28 11:28:39', '2018-11-28 11:28:39', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6677, 'jh_lianjiaoribao', '焦化', 'CK67-炼焦日报表（班日、月）报表_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-炼焦日报表（班日、月）报表_2018-11-28_11.xlsx', '2018-11-28 11:28:46', '2018-11-28 11:28:46', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6678, 'jh_zhisuancaozuo', '焦化', 'CK67-制酸操作（日）4.3m无此表报表_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-制酸操作（日）4.3m无此表报表_2018-11-28_11.xlsx', '2018-11-28 11:29:15', '2018-11-28 11:29:15', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6679, 'jh_tuoliujiexi', '焦化', 'CK67-脱硫解吸（日）4.3m无此表报表_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-脱硫解吸（日）4.3m无此表报表_2018-11-28_11.xlsx', '2018-11-28 11:29:41', '2018-11-28 11:29:41', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6680, 'jh_zhonglengxiben', '焦化', 'CK67-终冷洗苯报表_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-终冷洗苯报表_2018-11-28_11.xlsx', '2018-11-28 11:30:11', '2018-11-28 11:30:11', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6681, 'jh_chubenzhengliu', '焦化', 'CK67-粗苯蒸馏（日）报表_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-粗苯蒸馏（日）报表_2018-11-28_11.xlsx', '2018-11-28 11:30:43', '2018-11-28 11:30:43', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6682, 'jh_liuan', '焦化', 'CK67-硫铵（日）报表_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-硫铵（日）报表_2018-11-28_11.xlsx', '2018-11-28 11:31:05', '2018-11-28 11:31:05', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6683, 'jh_zhengan', '焦化', 'CK67-蒸氨（日）报表_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-蒸氨（日）报表_2018-11-28_11.xlsx', '2018-11-28 11:31:33', '2018-11-28 11:31:33', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6684, 'jh_zhilengxunhuanshui', '焦化', 'CK67-制冷循环水（日）报表_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-制冷循环水（日）报表_2018-11-28_11.xlsx', '2018-11-28 11:31:57', '2018-11-28 11:31:57', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6685, 'jh_gufenglengning', '焦化', 'CK67-鼓风冷凝(日)报表_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-鼓风冷凝(日)报表_2018-11-28_11.xlsx', '2018-11-28 11:32:29', '2018-11-28 11:32:29', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6686, 'jh_gufenglengning', '焦化', 'CK67-鼓风冷凝(日)报表_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-鼓风冷凝(日)报表_2018-11-28_11.xlsx', '2018-11-28 11:33:01', '2018-11-28 11:33:01', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6687, 'jh_shaijiaochuchen', '焦化', 'CK67-筛焦除尘报表_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-筛焦除尘报表_2018-11-28_11.xlsx', '2018-11-28 11:33:19', '2018-11-28 11:33:19', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6688, 'jh_cdqchuchen', '焦化', 'CK67-CDQ除尘报表_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-CDQ除尘报表_2018-11-28_11.xlsx', '2018-11-28 11:33:53', '2018-11-28 11:33:53', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6689, 'jh_zhuangmeichuchen', '焦化', 'CK67-装煤除尘报表_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-装煤除尘报表_2018-11-28_11.xlsx', '2018-11-28 11:34:12', '2018-11-28 11:34:12', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6690, 'jh_chujiaochuchen', '焦化', 'CK67-出焦除尘报表_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-出焦除尘报表_2018-11-28_11.xlsx', '2018-11-28 11:34:31', '2018-11-28 11:34:31', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6691, 'jh_fensuixidu', '焦化', 'CK67-粉碎细度（月）报表_2018-11-28.xlsx', 'D:\\excel\\cn_zh\\焦化\\月表报\\CK67-粉碎细度（月）报表_2018-11-28.xlsx', '2018-11-28 11:36:02', '2018-11-28 11:36:02', NULL, 'report_month', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6692, 'jh_zidongpeimei', '焦化', 'CK67-自动配煤（班）报表_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-自动配煤（班）报表_2018-11-28_11.xlsx', '2018-11-28 11:36:31', '2018-11-28 11:36:31', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6693, 'jh_cdqcaozuo', '焦化', 'CK67-CDQ操作运行报表（日）报表_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-CDQ操作运行报表（日）报表_2018-11-28_11.xlsx', '2018-11-28 11:38:22', '2018-11-28 11:38:22', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6694, 'sj_liushaogycanshu6', '烧结', '4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-28_11.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-28_11.xlsx', '2018-11-28 11:42:43', '2018-11-28 11:42:43', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6695, 'sj_liushaogycanshu6', '烧结', '4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-28_13.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-28_13.xlsx', '2018-11-28 13:14:52', '2018-11-28 13:14:52', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6696, 'sj_liushaogycanshu6', '烧结', '4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-28_13.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-28_13.xlsx', '2018-11-28 13:19:46', '2018-11-28 13:19:46', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6697, 'sj_liushaogycanshu6', '烧结', '4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-28_13.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-28_13.xlsx', '2018-11-28 13:28:50', '2018-11-28 13:28:50', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6698, 'sj_liushaogycanshu6', '烧结', '4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-28_14.xlsx', '2018-11-28 14:12:04', '2018-11-28 14:12:04', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6699, 'gl_chutiezuoye_day', '6高炉', '出铁作业 日报_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\出铁作业 日报_2018-11-28_14.xlsx', '2018-11-28 14:29:46', '2018-11-28 14:29:46', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6700, 'gl_chutiezuoye_day', '8高炉', '出铁作业 日报_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\出铁作业 日报_2018-11-28_14.xlsx', '2018-11-28 14:29:46', '2018-11-28 14:29:46', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6701, 'gl_bentiwendu_day', '6高炉', '高炉本体温度日报表_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉本体温度日报表_2018-11-28_14.xlsx', '2018-11-28 14:35:43', '2018-11-28 14:35:43', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6702, 'gl_bentiwendu_day', '6高炉', '高炉本体温度日报表_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉本体温度日报表_2018-11-28_14.xlsx', '2018-11-28 14:35:44', '2018-11-28 14:35:44', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6703, 'gl_bentiwendu_day', '6高炉', '高炉本体温度日报表_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉本体温度日报表_2018-11-28_14.xlsx', '2018-11-28 14:36:15', '2018-11-28 14:36:15', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6704, 'gl_bentiwendu_day', '8高炉', '高炉本体温度日报表_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉本体温度日报表_2018-11-28_14.xlsx', '2018-11-28 14:36:16', '2018-11-28 14:36:16', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6705, 'sj_liushaogycanshu6', '烧结', '4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-28_14.xlsx', '2018-11-28 14:39:06', '2018-11-28 14:39:06', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6706, 'gl_bentiwendu_day', '6高炉', '高炉本体温度日报表_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉本体温度日报表_2018-11-28_14.xlsx', '2018-11-28 14:39:52', '2018-11-28 14:39:52', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6707, 'gl_bentiwendu_day', '8高炉', '高炉本体温度日报表_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉本体温度日报表_2018-11-28_14.xlsx', '2018-11-28 14:39:52', '2018-11-28 14:39:52', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6708, 'gl_bentiwendu_day', '6高炉', '高炉本体温度日报表_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉本体温度日报表_2018-11-28_14.xlsx', '2018-11-28 14:40:48', '2018-11-28 14:40:48', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6709, 'gl_bentiwendu_day', '8高炉', '高炉本体温度日报表_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉本体温度日报表_2018-11-28_14.xlsx', '2018-11-28 14:40:48', '2018-11-28 14:40:48', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6710, 'gl_bentiwendu_day', '6高炉', '高炉本体温度日报表_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉本体温度日报表_2018-11-28_14.xlsx', '2018-11-28 14:42:07', '2018-11-28 14:42:07', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6711, 'gl_bentiwendu_day', '8高炉', '高炉本体温度日报表_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉本体温度日报表_2018-11-28_14.xlsx', '2018-11-28 14:42:07', '2018-11-28 14:42:07', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6712, 'gl_bentiwendu_day', '6高炉', '高炉本体温度日报表_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉本体温度日报表_2018-11-28_14.xlsx', '2018-11-28 14:44:24', '2018-11-28 14:44:24', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6713, 'gl_bentiwendu_day', '8高炉', '高炉本体温度日报表_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉本体温度日报表_2018-11-28_14.xlsx', '2018-11-28 14:44:25', '2018-11-28 14:44:25', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6714, 'gl_bentiwendu_month', '6高炉', '高炉本体温度月报表_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉本体温度月报表_2018-11-28_14.xlsx', '2018-11-28 14:46:15', '2018-11-28 14:46:15', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6715, 'gl_bentiwendu_month', '8高炉', '高炉本体温度月报表_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉本体温度月报表_2018-11-28_14.xlsx', '2018-11-28 14:46:16', '2018-11-28 14:46:16', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6716, 'gl_lengquebiwendu_day', '6高炉', '高炉冷却壁温度 日报_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉冷却壁温度 日报_2018-11-28_14.xlsx', '2018-11-28 14:49:30', '2018-11-28 14:49:30', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6717, 'gl_lengquebiwendu_day', '6高炉', '高炉冷却壁温度 日报_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉冷却壁温度 日报_2018-11-28_14.xlsx', '2018-11-28 14:50:08', '2018-11-28 14:50:08', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6718, 'gl_lengquebiwendu_day', '8高炉', '高炉冷却壁温度 日报_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉冷却壁温度 日报_2018-11-28_14.xlsx', '2018-11-28 14:50:08', '2018-11-28 14:50:08', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6719, 'sj_liushaogycanshu6', '烧结', '4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-28_14.xlsx', '2018-11-28 14:50:47', '2018-11-28 14:50:47', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6720, 'gl_lengquebiwendu_month', '6高炉', '高炉冷却壁温度 月报_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉冷却壁温度 月报_2018-11-28_14.xlsx', '2018-11-28 14:52:21', '2018-11-28 14:52:21', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6721, 'gl_lengquebiwendu_month', '8高炉', '高炉冷却壁温度 月报_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉冷却壁温度 月报_2018-11-28_14.xlsx', '2018-11-28 14:52:22', '2018-11-28 14:52:22', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6722, 'gl_jswgaolu_day', '6高炉', '高炉 日报_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉 日报_2018-11-28_14.xlsx', '2018-11-28 14:56:41', '2018-11-28 14:56:41', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6723, 'gl_jswgaolu_day', '8高炉', '高炉 日报_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉 日报_2018-11-28_14.xlsx', '2018-11-28 14:56:41', '2018-11-28 14:56:41', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6724, 'sj_liushaogycanshu6', '烧结', '4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-28_14.xlsx', '2018-11-28 14:56:56', '2018-11-28 14:56:56', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6725, 'gl_jswgaolu_day', '6高炉', '高炉 日报_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉 日报_2018-11-28_14.xlsx', '2018-11-28 14:58:31', '2018-11-28 14:58:31', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6726, 'gl_jswgaolu_day', '8高炉', '高炉 日报_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉 日报_2018-11-28_14.xlsx', '2018-11-28 14:58:31', '2018-11-28 14:58:31', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6727, 'gl_jswgaolu_day', '6高炉', '高炉 日报_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉 日报_2018-11-28_14.xlsx', '2018-11-28 14:59:01', '2018-11-28 14:59:01', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6728, 'gl_jswgaolu_day', '8高炉', '高炉 日报_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉 日报_2018-11-28_14.xlsx', '2018-11-28 14:59:04', '2018-11-28 14:59:04', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6729, 'gl_jswgaolu_day', '6高炉', '高炉 日报_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉 日报_2018-11-28_14.xlsx', '2018-11-28 14:59:34', '2018-11-28 14:59:34', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6730, 'gl_jswgaolu_day', '8高炉', '高炉 日报_2018-11-28_14.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉 日报_2018-11-28_14.xlsx', '2018-11-28 14:59:34', '2018-11-28 14:59:34', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6731, 'gl_chutiezuoye_day', '6高炉', '出铁作业 日报_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\出铁作业 日报_2018-11-28_15.xlsx', '2018-11-28 15:00:15', '2018-11-28 15:00:15', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6732, 'gl_chutiezuoye_day', '8高炉', '出铁作业 日报_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\出铁作业 日报_2018-11-28_15.xlsx', '2018-11-28 15:00:15', '2018-11-28 15:00:15', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6733, 'gl_chutiezuoye_day', '6高炉', '出铁作业 日报_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\出铁作业 日报_2018-11-28_15.xlsx', '2018-11-28 15:00:53', '2018-11-28 15:00:53', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6734, 'gl_chutiezuoye_day', '8高炉', '出铁作业 日报_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\出铁作业 日报_2018-11-28_15.xlsx', '2018-11-28 15:00:54', '2018-11-28 15:00:54', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6735, 'gl_chutiezuoye_day', '6高炉', '出铁作业 日报_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\出铁作业 日报_2018-11-28_15.xlsx', '2018-11-28 15:02:14', '2018-11-28 15:02:14', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6736, 'gl_chutiezuoye_day', '8高炉', '出铁作业 日报_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\出铁作业 日报_2018-11-28_15.xlsx', '2018-11-28 15:02:14', '2018-11-28 15:02:14', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6737, 'gl_bentiwendu_day', '6高炉', '高炉本体温度日报表_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉本体温度日报表_2018-11-28_15.xlsx', '2018-11-28 15:03:53', '2018-11-28 15:03:53', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6738, 'gl_bentiwendu_day', '8高炉', '高炉本体温度日报表_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉本体温度日报表_2018-11-28_15.xlsx', '2018-11-28 15:03:54', '2018-11-28 15:03:54', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6739, 'gl_bentiwendu_day', '6高炉', '高炉本体温度日报表_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉本体温度日报表_2018-11-28_15.xlsx', '2018-11-28 15:06:42', '2018-11-28 15:06:42', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6740, 'gl_bentiwendu_day', '6高炉', '高炉本体温度日报表_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉本体温度日报表_2018-11-28_15.xlsx', '2018-11-28 15:07:06', '2018-11-28 15:07:06', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6741, 'gl_bentiwendu_day', '8高炉', '高炉本体温度日报表_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉本体温度日报表_2018-11-28_15.xlsx', '2018-11-28 15:07:06', '2018-11-28 15:07:06', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6742, 'gl_bentiwendu_month', '6高炉', '高炉本体温度月报表_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉本体温度月报表_2018-11-28_15.xlsx', '2018-11-28 15:09:38', '2018-11-28 15:09:38', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6743, 'gl_bentiwendu_month', '8高炉', '高炉本体温度月报表_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉本体温度月报表_2018-11-28_15.xlsx', '2018-11-28 15:09:40', '2018-11-28 15:09:40', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6744, 'gl_lengquebiwendu_day', '6高炉', '高炉冷却壁温度 日报_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉冷却壁温度 日报_2018-11-28_15.xlsx', '2018-11-28 15:11:01', '2018-11-28 15:11:01', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6745, 'gl_lengquebiwendu_day', '8高炉', '高炉冷却壁温度 日报_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉冷却壁温度 日报_2018-11-28_15.xlsx', '2018-11-28 15:11:02', '2018-11-28 15:11:02', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6746, 'sj_liushaogycanshu6', '烧结', '4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-28_15.xlsx', '2018-11-28 15:12:02', '2018-11-28 15:12:02', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6747, 'gl_lengquebiwendu_month', '6高炉', '高炉冷却壁温度 月报_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉冷却壁温度 月报_2018-11-28_15.xlsx', '2018-11-28 15:12:38', '2018-11-28 15:12:38', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6748, 'gl_lengquebiwendu_month', '8高炉', '高炉冷却壁温度 月报_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉冷却壁温度 月报_2018-11-28_15.xlsx', '2018-11-28 15:12:39', '2018-11-28 15:12:39', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6749, 'gl_jswgaolu_day', '6高炉', '高炉 日报_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉 日报_2018-11-28_15.xlsx', '2018-11-28 15:13:38', '2018-11-28 15:13:38', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6750, 'gl_jswgaolu_day', '8高炉', '高炉 日报_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉 日报_2018-11-28_15.xlsx', '2018-11-28 15:13:39', '2018-11-28 15:13:39', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6751, 'gl_jswgaolu_day', '6高炉', '高炉 日报_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉 日报_2018-11-28_15.xlsx', '2018-11-28 15:15:39', '2018-11-28 15:15:39', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6752, 'gl_jswgaolu_day', '8高炉', '高炉 日报_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉 日报_2018-11-28_15.xlsx', '2018-11-28 15:15:40', '2018-11-28 15:15:40', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6753, 'gl_jswgaolu_day', '6高炉', '高炉 日报_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉 日报_2018-11-28_15.xlsx', '2018-11-28 15:17:12', '2018-11-28 15:17:12', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6754, 'gl_jswgaolu_day', '8高炉', '高炉 日报_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉 日报_2018-11-28_15.xlsx', '2018-11-28 15:17:13', '2018-11-28 15:17:13', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6755, 'gl_taisu1_month', '6高炉', '高炉 月报_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉 月报_2018-11-28_15.xlsx', '2018-11-28 15:18:49', '2018-11-28 15:18:49', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6756, 'gl_taisu1_month', '8高炉', '高炉 月报_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉 月报_2018-11-28_15.xlsx', '2018-11-28 15:18:51', '2018-11-28 15:18:51', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6757, 'gl_ludingbuliao_day', '6高炉', '高炉炉顶布料作业日报_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉炉顶布料作业日报_2018-11-28_15.xlsx', '2018-11-28 15:21:53', '2018-11-28 15:21:53', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6758, 'gl_ludingbuliao_day', '6高炉', '高炉炉顶布料作业日报_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉炉顶布料作业日报_2018-11-28_15.xlsx', '2018-11-28 15:22:33', '2018-11-28 15:22:33', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6759, 'gl_ludingbuliao_day', '8高炉', '高炉炉顶布料作业日报_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉炉顶布料作业日报_2018-11-28_15.xlsx', '2018-11-28 15:22:37', '2018-11-28 15:22:37', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6760, 'gl_ludingzhuangliaozuoye_day1', '6高炉', '高炉炉顶装料作业 日报1_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉炉顶装料作业 日报1_2018-11-28_15.xlsx', '2018-11-28 15:26:01', '2018-11-28 15:26:01', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6761, 'gl_ludingzhuangliaozuoye_day1', '8高炉', '高炉炉顶装料作业 日报1_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉炉顶装料作业 日报1_2018-11-28_15.xlsx', '2018-11-28 15:26:22', '2018-11-28 15:26:22', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6762, 'gl_ludingzhuangliaozuoye_day1', '8高炉', '高炉炉顶装料作业 日报1_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉炉顶装料作业 日报1_2018-11-28_15.xlsx', '2018-11-28 15:26:42', '2018-11-28 15:26:42', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6763, 'gl_ludingzhuangliaozuoye_day1', '6高炉', '高炉炉顶装料作业 日报1_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉炉顶装料作业 日报1_2018-11-28_15.xlsx', '2018-11-28 15:31:06', '2018-11-28 15:31:06', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6764, 'gl_ludingzhuangliaozuoye_day1', '8高炉', '高炉炉顶装料作业 日报1_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉炉顶装料作业 日报1_2018-11-28_15.xlsx', '2018-11-28 15:31:17', '2018-11-28 15:31:17', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6765, 'gl_ludingzhuangliaozuoye_day1', '8高炉', '高炉炉顶装料作业 日报1_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉炉顶装料作业 日报1_2018-11-28_15.xlsx', '2018-11-28 15:31:29', '2018-11-28 15:31:29', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6766, 'gl_ludingzhuangliaozuoye_day1', '6高炉', '高炉炉顶装料作业 日报1_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉炉顶装料作业 日报1_2018-11-28_15.xlsx', '2018-11-28 15:32:31', '2018-11-28 15:32:31', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6767, 'gl_ludingzhuangliaozuoye_day1', '8高炉', '高炉炉顶装料作业 日报1_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉炉顶装料作业 日报1_2018-11-28_15.xlsx', '2018-11-28 15:32:45', '2018-11-28 15:32:45', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6768, 'gl_ludingzhuangliaozuoye_day1', '8高炉', '高炉炉顶装料作业 日报1_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉炉顶装料作业 日报1_2018-11-28_15.xlsx', '2018-11-28 15:32:59', '2018-11-28 15:32:59', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6769, 'sj_liushaogycanshu6', '烧结', '4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-28_15.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-28_15.xlsx', '2018-11-28 15:35:12', '2018-11-28 15:35:12', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6770, 'sj_liushaogycanshu5', '烧结', '4小时发布-五烧主要工艺参数及实物质量情况日报_2018-11-28_16.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\4小时发布-五烧主要工艺参数及实物质量情况日报_2018-11-28_16.xlsx', '2018-11-28 16:26:06', '2018-11-28 16:26:06', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6771, 'jh_lianjiaoribao', '焦化', 'CK67-炼焦日报表（班日、月）报表_2018-11-28_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-炼焦日报表（班日、月）报表_2018-11-28_16.xlsx', '2018-11-28 16:46:23', '2018-11-28 16:46:23', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6772, 'sj_liushaogycanshu5', '烧结', '4小时发布-五烧主要工艺参数及实物质量情况日报_2018-11-28_16.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\4小时发布-五烧主要工艺参数及实物质量情况日报_2018-11-28_16.xlsx', '2018-11-28 16:50:20', '2018-11-28 16:50:20', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6773, 'sj_liushaogycanshu6', '烧结', '4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-29_09.xlsx', 'D:\\excel\\cn_zh\\烧结\\日报表\\4小时发布-六烧主要工艺参数及实物质量情况日报_2018-11-29_09.xlsx', '2018-11-29 09:11:52', '2018-11-29 09:11:52', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6774, 'jh_lianjiaoribao', '焦化', 'CK67-炼焦日报表（班日、月）报表_2018-11-29_10.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-炼焦日报表（班日、月）报表_2018-11-29_10.xlsx', '2018-11-29 10:29:27', '2018-11-29 10:29:27', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6775, 'jh_lianjiaoribao', '焦化', 'CK67-炼焦日报表（班日、月）报表_2018-11-29_10.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-炼焦日报表（班日、月）报表_2018-11-29_10.xlsx', '2018-11-29 10:45:04', '2018-11-29 10:45:04', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6776, 'jh_lianjiaoribao', '焦化', 'CK67-炼焦日报表（班日、月）报表_2018-11-29_10.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-炼焦日报表（班日、月）报表_2018-11-29_10.xlsx', '2018-11-29 10:56:48', '2018-11-29 10:56:48', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6777, 'jh_lianjiaoribao', '焦化', 'CK67-炼焦日报表（班日、月）报表_2018-11-29_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-炼焦日报表（班日、月）报表_2018-11-29_11.xlsx', '2018-11-29 11:22:44', '2018-11-29 11:22:44', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6778, 'jh_lianjiaoribao', '焦化', 'CK67-炼焦日报表（班日、月）报表_2018-11-29_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-炼焦日报表（班日、月）报表_2018-11-29_11.xlsx', '2018-11-29 11:36:13', '2018-11-29 11:36:13', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6779, 'jh_lianjiaoribao', '焦化', 'CK67-炼焦日报表（班日、月）报表_2018-11-29_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-炼焦日报表（班日、月）报表_2018-11-29_11.xlsx', '2018-11-29 11:37:33', '2018-11-29 11:37:33', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6780, 'jh_lianjiaoribao', '焦化', 'CK67-炼焦日报表（班日、月）报表_2018-11-29_11.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-炼焦日报表（班日、月）报表_2018-11-29_11.xlsx', '2018-11-29 11:40:11', '2018-11-29 11:40:11', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6781, 'jh_lianjiaoribao', '焦化', 'CK67-炼焦日报表（班日、月）报表_2018-11-30_16.xlsx', 'D:\\excel\\cn_zh\\焦化\\日报表\\CK67-炼焦日报表（班日、月）报表_2018-11-30_16.xlsx', '2018-11-30 16:05:52', '2018-11-30 16:05:52', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6782, 'gl_bentiwendu_day', '6高炉', '高炉本体温度日报表_2018-12-03_09.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉本体温度日报表_2018-12-03_09.xlsx', '2018-12-03 09:57:55', '2018-12-03 09:57:55', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6783, 'gl_bentiwendu_day', '8高炉', '高炉本体温度日报表_2018-12-03_09.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉本体温度日报表_2018-12-03_09.xlsx', '2018-12-03 09:57:57', '2018-12-03 09:57:57', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6784, 'gl_chutiezuoye_day', '6高炉', '出铁作业 日报_2018-12-03_09.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\出铁作业 日报_2018-12-03_09.xlsx', '2018-12-03 09:59:36', '2018-12-03 09:59:36', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6785, 'gl_chutiezuoye_day', '8高炉', '出铁作业 日报_2018-12-03_09.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\出铁作业 日报_2018-12-03_09.xlsx', '2018-12-03 09:59:37', '2018-12-03 09:59:37', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6786, 'gl_bentiwendu_day', '6高炉', '高炉本体温度日报表_2018-12-03_10.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉本体温度日报表_2018-12-03_10.xlsx', '2018-12-03 10:00:44', '2018-12-03 10:00:44', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6787, 'gl_bentiwendu_day', '8高炉', '高炉本体温度日报表_2018-12-03_10.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉本体温度日报表_2018-12-03_10.xlsx', '2018-12-03 10:00:45', '2018-12-03 10:00:45', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6788, 'gl_bentiwendu_month', '6高炉', '高炉本体温度月报表_2018-12-03_10.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉本体温度月报表_2018-12-03_10.xlsx', '2018-12-03 10:01:56', '2018-12-03 10:01:56', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6789, 'gl_bentiwendu_month', '8高炉', '高炉本体温度月报表_2018-12-03_10.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉本体温度月报表_2018-12-03_10.xlsx', '2018-12-03 10:01:57', '2018-12-03 10:01:57', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6790, 'gl_lengquebiwendu_day', '6高炉', '高炉冷却壁温度 日报_2018-12-03_10.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉冷却壁温度 日报_2018-12-03_10.xlsx', '2018-12-03 10:03:06', '2018-12-03 10:03:06', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6791, 'gl_lengquebiwendu_day', '8高炉', '高炉冷却壁温度 日报_2018-12-03_10.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉冷却壁温度 日报_2018-12-03_10.xlsx', '2018-12-03 10:03:07', '2018-12-03 10:03:07', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6792, 'gl_lengquebiwendu_month', '6高炉', '高炉冷却壁温度 月报_2018-12-03_10.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉冷却壁温度 月报_2018-12-03_10.xlsx', '2018-12-03 10:03:57', '2018-12-03 10:03:57', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6793, 'gl_lengquebiwendu_month', '8高炉', '高炉冷却壁温度 月报_2018-12-03_10.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉冷却壁温度 月报_2018-12-03_10.xlsx', '2018-12-03 10:03:58', '2018-12-03 10:03:58', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6794, 'gl_jswgaolu_day', '6高炉', '高炉 日报_2018-12-03_10.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉 日报_2018-12-03_10.xlsx', '2018-12-03 10:05:07', '2018-12-03 10:05:07', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6795, 'gl_jswgaolu_day', '8高炉', '高炉 日报_2018-12-03_10.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉 日报_2018-12-03_10.xlsx', '2018-12-03 10:05:08', '2018-12-03 10:05:08', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6796, 'gl_taisu1_month', '6高炉', '高炉 月报_2018-12-03_10.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉 月报_2018-12-03_10.xlsx', '2018-12-03 10:12:48', '2018-12-03 10:12:48', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6797, 'gl_taisu1_month', '8高炉', '高炉 月报_2018-12-03_10.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉 月报_2018-12-03_10.xlsx', '2018-12-03 10:12:49', '2018-12-03 10:12:49', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6798, 'gl_ludingbuliao_day', '6高炉', '高炉炉顶布料作业日报_2018-12-03_10.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉炉顶布料作业日报_2018-12-03_10.xlsx', '2018-12-03 10:14:13', '2018-12-03 10:14:13', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6799, 'gl_ludingbuliao_day', '8高炉', '高炉炉顶布料作业日报_2018-12-03_10.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉炉顶布料作业日报_2018-12-03_10.xlsx', '2018-12-03 10:14:16', '2018-12-03 10:14:16', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6800, 'gl_ludingbuliao_day', '6高炉', '高炉炉顶布料作业日报_2018-12-03_10.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉炉顶布料作业日报_2018-12-03_10.xlsx', '2018-12-03 10:18:02', '2018-12-03 10:18:02', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6801, 'gl_ludingbuliao_day', '8高炉', '高炉炉顶布料作业日报_2018-12-03_10.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉炉顶布料作业日报_2018-12-03_10.xlsx', '2018-12-03 10:18:04', '2018-12-03 10:18:04', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6802, 'gl_ludingzhuangliaozuoye_day1', '6高炉', '高炉炉顶装料作业 日报1_2018-12-03_10.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉炉顶装料作业 日报1_2018-12-03_10.xlsx', '2018-12-03 10:18:42', '2018-12-03 10:18:42', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6803, 'gl_ludingzhuangliaozuoye_day1', '8高炉', '高炉炉顶装料作业 日报1_2018-12-03_10.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉炉顶装料作业 日报1_2018-12-03_10.xlsx', '2018-12-03 10:18:49', '2018-12-03 10:18:49', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6804, 'gl_ludingzhuangliaozuoye_day1', '8高炉', '高炉炉顶装料作业 日报1_2018-12-03_10.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉炉顶装料作业 日报1_2018-12-03_10.xlsx', '2018-12-03 10:18:57', '2018-12-03 10:18:57', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6805, 'gl_taisu1_month', '6高炉', '高炉 月报_2018-12-03_10.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉 月报_2018-12-03_10.xlsx', '2018-12-03 10:48:36', '2018-12-03 10:48:36', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6806, 'gl_taisu1_month', '8高炉', '高炉 月报_2018-12-03_10.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉 月报_2018-12-03_10.xlsx', '2018-12-03 10:48:37', '2018-12-03 10:48:37', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6807, 'gl_taisu1_month', '6高炉', '高炉 月报_2018-12-03_10.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉 月报_2018-12-03_10.xlsx', '2018-12-03 10:55:33', '2018-12-03 10:55:33', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6808, 'gl_taisu1_month', '8高炉', '高炉 月报_2018-12-03_10.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉 月报_2018-12-03_10.xlsx', '2018-12-03 10:55:34', '2018-12-03 10:55:34', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6809, 'gl_taisu1_month', '6高炉', '高炉 月报_2018-12-03_10.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉 月报_2018-12-03_10.xlsx', '2018-12-03 10:57:03', '2018-12-03 10:57:03', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6810, 'gl_taisu1_month', '8高炉', '高炉 月报_2018-12-03_10.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉 月报_2018-12-03_10.xlsx', '2018-12-03 10:57:03', '2018-12-03 10:57:03', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6811, 'gl_jswgaolu_day', '6高炉', '高炉 日报_2018-12-03_10.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉 日报_2018-12-03_10.xlsx', '2018-12-03 10:58:28', '2018-12-03 10:58:28', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6812, 'gl_jswgaolu_day', '8高炉', '高炉 日报_2018-12-03_10.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉 日报_2018-12-03_10.xlsx', '2018-12-03 10:58:28', '2018-12-03 10:58:28', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6813, 'gl_jswgaolu_day', '6高炉', '高炉 日报_2018-12-03_10.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉 日报_2018-12-03_10.xlsx', '2018-12-03 10:59:58', '2018-12-03 10:59:58', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6814, 'gl_jswgaolu_day', '6高炉', '高炉 日报_2018-12-03_11.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉 日报_2018-12-03_11.xlsx', '2018-12-03 11:00:21', '2018-12-03 11:00:21', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6815, 'gl_jswgaolu_day', '8高炉', '高炉 日报_2018-12-03_11.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉 日报_2018-12-03_11.xlsx', '2018-12-03 11:00:22', '2018-12-03 11:00:22', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6816, 'gl_ludingzhuangliaozuoye_day1', '6高炉', '高炉炉顶装料作业 日报1_2018-12-03_11.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉炉顶装料作业 日报1_2018-12-03_11.xlsx', '2018-12-03 11:36:35', '2018-12-03 11:36:35', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6817, 'gl_ludingzhuangliaozuoye_day1', '8高炉', '高炉炉顶装料作业 日报1_2018-12-03_11.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉炉顶装料作业 日报1_2018-12-03_11.xlsx', '2018-12-03 11:36:44', '2018-12-03 11:36:44', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6818, 'gl_ludingzhuangliaozuoye_day1', '8高炉', '高炉炉顶装料作业 日报1_2018-12-03_11.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉炉顶装料作业 日报1_2018-12-03_11.xlsx', '2018-12-03 11:36:54', '2018-12-03 11:36:54', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6819, 'gl_chutiezuoye_day', '6高炉', '出铁作业 日报_2018-12-03_11.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\出铁作业 日报_2018-12-03_11.xlsx', '2018-12-03 11:37:14', '2018-12-03 11:37:14', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6820, 'gl_chutiezuoye_day', '8高炉', '出铁作业 日报_2018-12-03_11.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\出铁作业 日报_2018-12-03_11.xlsx', '2018-12-03 11:37:15', '2018-12-03 11:37:15', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6821, 'gl_bentiwendu_day', '6高炉', '高炉本体温度日报表_2018-12-03_11.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉本体温度日报表_2018-12-03_11.xlsx', '2018-12-03 11:37:50', '2018-12-03 11:37:50', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6822, 'gl_bentiwendu_day', '8高炉', '高炉本体温度日报表_2018-12-03_11.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉本体温度日报表_2018-12-03_11.xlsx', '2018-12-03 11:37:51', '2018-12-03 11:37:51', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6823, 'gl_bentiwendu_month', '6高炉', '高炉本体温度月报表_2018-12-03_11.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉本体温度月报表_2018-12-03_11.xlsx', '2018-12-03 11:38:27', '2018-12-03 11:38:27', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6824, 'gl_bentiwendu_month', '8高炉', '高炉本体温度月报表_2018-12-03_11.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉本体温度月报表_2018-12-03_11.xlsx', '2018-12-03 11:38:27', '2018-12-03 11:38:27', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6825, 'gl_lengquebiwendu_day', '6高炉', '高炉冷却壁温度 日报_2018-12-03_11.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉冷却壁温度 日报_2018-12-03_11.xlsx', '2018-12-03 11:39:07', '2018-12-03 11:39:07', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6826, 'gl_lengquebiwendu_day', '8高炉', '高炉冷却壁温度 日报_2018-12-03_11.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉冷却壁温度 日报_2018-12-03_11.xlsx', '2018-12-03 11:39:07', '2018-12-03 11:39:07', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6827, 'gl_lengquebiwendu_month', '6高炉', '高炉冷却壁温度 月报_2018-12-03_11.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉冷却壁温度 月报_2018-12-03_11.xlsx', '2018-12-03 11:39:53', '2018-12-03 11:39:53', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6828, 'gl_lengquebiwendu_month', '8高炉', '高炉冷却壁温度 月报_2018-12-03_11.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉冷却壁温度 月报_2018-12-03_11.xlsx', '2018-12-03 11:39:54', '2018-12-03 11:39:54', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6829, 'gl_jswgaolu_day', '6高炉', '高炉 日报_2018-12-03_11.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉 日报_2018-12-03_11.xlsx', '2018-12-03 11:40:31', '2018-12-03 11:40:31', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6830, 'gl_jswgaolu_day', '8高炉', '高炉 日报_2018-12-03_11.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉 日报_2018-12-03_11.xlsx', '2018-12-03 11:40:32', '2018-12-03 11:40:32', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6831, 'gl_taisu1_month', '6高炉', '高炉 月报_2018-12-03_11.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉 月报_2018-12-03_11.xlsx', '2018-12-03 11:41:11', '2018-12-03 11:41:11', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6832, 'gl_taisu1_month', '8高炉', '高炉 月报_2018-12-03_11.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉 月报_2018-12-03_11.xlsx', '2018-12-03 11:41:11', '2018-12-03 11:41:11', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6833, 'gl_ludingbuliao_day', '6高炉', '高炉炉顶布料作业日报_2018-12-03_11.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉炉顶布料作业日报_2018-12-03_11.xlsx', '2018-12-03 11:41:42', '2018-12-03 11:41:42', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6834, 'gl_ludingbuliao_day', '8高炉', '高炉炉顶布料作业日报_2018-12-03_11.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉炉顶布料作业日报_2018-12-03_11.xlsx', '2018-12-03 11:41:44', '2018-12-03 11:41:44', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6835, 'gl_jswgaolu_day', '6高炉', '高炉 日报_2018-12-03_13.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉 日报_2018-12-03_13.xlsx', '2018-12-03 13:52:46', '2018-12-03 13:52:46', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6836, 'gl_jswgaolu_day', '8高炉', '高炉 日报_2018-12-03_13.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉 日报_2018-12-03_13.xlsx', '2018-12-03 13:52:46', '2018-12-03 13:52:46', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6837, 'gl_jswgaolu_day', '6高炉', '高炉 日报_2018-12-04_09.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉 日报_2018-12-04_09.xlsx', '2018-12-04 09:12:43', '2018-12-04 09:12:43', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6838, 'gl_jswgaolu_day', '8高炉', '高炉 日报_2018-12-04_09.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉 日报_2018-12-04_09.xlsx', '2018-12-04 09:12:43', '2018-12-04 09:12:43', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6839, 'gl_taisu1_month', '6高炉', '高炉 月报_2018-12-04_09.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉 月报_2018-12-04_09.xlsx', '2018-12-04 09:13:02', '2018-12-04 09:13:02', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6840, 'gl_taisu1_month', '8高炉', '高炉 月报_2018-12-04_09.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉 月报_2018-12-04_09.xlsx', '2018-12-04 09:13:02', '2018-12-04 09:13:02', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6841, 'gl_bentiwendu_day', '6高炉', '高炉本体温度日报表_2018-12-04_14.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉本体温度日报表_2018-12-04_14.xlsx', '2018-12-04 14:02:10', '2018-12-04 14:02:10', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6842, 'gl_bentiwendu_day', '8高炉', '高炉本体温度日报表_2018-12-04_14.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉本体温度日报表_2018-12-04_14.xlsx', '2018-12-04 14:02:11', '2018-12-04 14:02:11', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6843, 'gl_lengquebiwendu_day', '6高炉', '高炉冷却壁温度 日报_2018-12-04_14.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉冷却壁温度 日报_2018-12-04_14.xlsx', '2018-12-04 14:02:57', '2018-12-04 14:02:57', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6844, 'gl_lengquebiwendu_day', '8高炉', '高炉冷却壁温度 日报_2018-12-04_14.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉冷却壁温度 日报_2018-12-04_14.xlsx', '2018-12-04 14:02:57', '2018-12-04 14:02:57', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6845, 'gl_jswgaolu_day', '6高炉', '高炉 日报_2018-12-04_14.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉 日报_2018-12-04_14.xlsx', '2018-12-04 14:03:51', '2018-12-04 14:03:51', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6846, 'gl_jswgaolu_day', '8高炉', '高炉 日报_2018-12-04_14.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉 日报_2018-12-04_14.xlsx', '2018-12-04 14:03:51', '2018-12-04 14:03:51', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6847, 'gl_jswgaolu_day', '6高炉', '高炉 日报_2018-12-04_14.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉 日报_2018-12-04_14.xlsx', '2018-12-04 14:07:11', '2018-12-04 14:07:11', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6848, 'gl_jswgaolu_day', '8高炉', '高炉 日报_2018-12-04_14.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉 日报_2018-12-04_14.xlsx', '2018-12-04 14:07:11', '2018-12-04 14:07:11', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6849, 'gl_jswgaolu_day', '6高炉', '高炉 日报_2018-12-04_15.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉 日报_2018-12-04_15.xlsx', '2018-12-04 15:21:28', '2018-12-04 15:21:28', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6850, 'gl_jswgaolu_day', '8高炉', '高炉 日报_2018-12-04_15.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉 日报_2018-12-04_15.xlsx', '2018-12-04 15:21:29', '2018-12-04 15:21:29', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6851, 'gl_taisu1_month', '6高炉', '高炉 月报_2018-12-04_15.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉 月报_2018-12-04_15.xlsx', '2018-12-04 15:23:43', '2018-12-04 15:23:43', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6852, 'gl_taisu1_month', '8高炉', '高炉 月报_2018-12-04_15.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉 月报_2018-12-04_15.xlsx', '2018-12-04 15:23:44', '2018-12-04 15:23:44', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6853, 'gl_bentiwendu_day', '6高炉', '高炉本体温度日报表_2018-12-05_12.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉本体温度日报表_2018-12-05_12.xlsx', '2018-12-05 12:46:57', '2018-12-05 12:46:57', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6854, 'gl_bentiwendu_day', '8高炉', '高炉本体温度日报表_2018-12-05_12.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉本体温度日报表_2018-12-05_12.xlsx', '2018-12-05 12:46:58', '2018-12-05 12:46:58', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6855, 'gl_lengquebiwendu_day', '6高炉', '高炉冷却壁温度 日报_2018-12-05_12.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉冷却壁温度 日报_2018-12-05_12.xlsx', '2018-12-05 12:47:18', '2018-12-05 12:47:18', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6856, 'gl_lengquebiwendu_day', '8高炉', '高炉冷却壁温度 日报_2018-12-05_12.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉冷却壁温度 日报_2018-12-05_12.xlsx', '2018-12-05 12:47:19', '2018-12-05 12:47:19', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6857, 'gl_lengquebiwendu_month', '6高炉', '高炉冷却壁温度 月报_2018-12-05_12.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉冷却壁温度 月报_2018-12-05_12.xlsx', '2018-12-05 12:52:28', '2018-12-05 12:52:28', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6858, 'gl_lengquebiwendu_month', '8高炉', '高炉冷却壁温度 月报_2018-12-05_12.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉冷却壁温度 月报_2018-12-05_12.xlsx', '2018-12-05 12:52:29', '2018-12-05 12:52:29', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6859, 'gl_lengquebiwendu_month', '6高炉', '高炉冷却壁温度 月报_2018-12-05_12.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉冷却壁温度 月报_2018-12-05_12.xlsx', '2018-12-05 12:55:37', '2018-12-05 12:55:37', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6860, 'gl_lengquebiwendu_month', '8高炉', '高炉冷却壁温度 月报_2018-12-05_12.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉冷却壁温度 月报_2018-12-05_12.xlsx', '2018-12-05 12:55:37', '2018-12-05 12:55:37', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6861, 'gl_lengquebiwendu_day', '6高炉', '高炉冷却壁温度 日报_2018-12-06_09.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉冷却壁温度 日报_2018-12-06_09.xlsx', '2018-12-06 09:02:01', '2018-12-06 09:02:01', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6862, 'gl_lengquebiwendu_day', '8高炉', '高炉冷却壁温度 日报_2018-12-06_09.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉冷却壁温度 日报_2018-12-06_09.xlsx', '2018-12-06 09:02:02', '2018-12-06 09:02:02', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6863, 'gl_lengquebiwendu_month', '6高炉', '高炉冷却壁温度 月报_2018-12-06_09.xlsx', 'D:\\excel\\cn_zh\\6高炉\\日报表\\高炉冷却壁温度 月报_2018-12-06_09.xlsx', '2018-12-06 09:03:33', '2018-12-06 09:03:33', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);
INSERT INTO `report_index` VALUES (6864, 'gl_lengquebiwendu_month', '8高炉', '高炉冷却壁温度 月报_2018-12-06_09.xlsx', 'D:\\excel\\cn_zh\\8高炉\\日报表\\高炉冷却壁温度 月报_2018-12-06_09.xlsx', '2018-12-06 09:03:34', '2018-12-06 09:03:34', NULL, 'report_day', 'cn_zh', NULL, NULL, NULL, NULL);

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
INSERT INTO `sys_config` VALUES (38, 'sj_shaojieji5_day', 'com.cisdi.steel.module.job.a3.JiejiJob5', '5#烧结机生产日报');
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
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统字典' ROW_FORMAT = Compact;

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
