/*
 Navicat Premium Dump SQL

 Source Server         : 数据库
 Source Server Type    : MySQL
 Source Server Version : 80039 (8.0.39)
 Source Host           : localhost:3306
 Source Schema         : laolao_code

 Target Server Type    : MySQL
 Target Server Version : 80039 (8.0.39)
 File Encoding         : 65001

 Date: 09/02/2026 17:50:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for exam
-- ----------------------------
DROP TABLE IF EXISTS `exam`;
CREATE TABLE `exam`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '考试标题',
  `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '考试说明',
  `group_id` int NOT NULL COMMENT '所属组ID',
  `questions` json NOT NULL COMMENT '题目ID列表，例如 [1, 12, 33]',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `advisor_id` int NOT NULL COMMENT '导师ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考试表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam
-- ----------------------------
INSERT INTO `exam` VALUES (1, '2024年春季 Java 核心技术月考', '本次考试涵盖多线程、JVM、集合类。请在规定时间内独立完成，AI助教将进行逻辑审查。', 1, '[1, 5, 12, 18, 20]', '2030-03-20 09:00:00', '2024-03-20 11:00:00', 1);
INSERT INTO `exam` VALUES (2, '【AI生成】动态规划专题练习', '由 Spring AI 根据近期大家的薄弱点自动选取的题目，主要针对背包问题和区间DP。', 2, '[101, 105, 110]', '2030-03-01 00:00:00', '2024-03-31 23:59:59', 1);

-- ----------------------------
-- Table structure for exam_record
-- ----------------------------
DROP TABLE IF EXISTS `exam_record`;
CREATE TABLE `exam_record`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `exam_id` int NOT NULL COMMENT '考试ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `score` int NULL DEFAULT 0 COMMENT '总得分',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态：0-进行中，1-已提交，2-AI已批改',
  `report` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '整场考试的综合评价',
  `start_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '学生开始答题时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考试记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam_record
-- ----------------------------
INSERT INTO `exam_record` VALUES (1, 1, 2, 20, 2, '本次考试整体表现一般。第一题算法实现准确，展示了良好的编码习惯；但第二题出现了基础语法拼写错误导致编译失败，反映出基础还不够扎实。建议加强对 Java 关键字和基础语法的拼写练习，减少低级错误。', '2024-03-20 09:05:00');

-- ----------------------------
-- Table structure for group
-- ----------------------------
DROP TABLE IF EXISTS `group`;
CREATE TABLE `group`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '组名',
  `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组描述',
  `advisor_id` int NOT NULL COMMENT '老师的ID',
  `invite_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '组邀请码',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `invite_code`(`invite_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '组表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of group
-- ----------------------------
INSERT INTO `group` VALUES (1, 'Java进阶训练营', '专注Java核心技术与JVM调优', 1, 'JAVA2024');
INSERT INTO `group` VALUES (2, 'AI算法研究组', '基于Spring AI的大模型应用探索', 1, 'AI999');

-- ----------------------------
-- Table structure for group_member
-- ----------------------------
DROP TABLE IF EXISTS `group_member`;
CREATE TABLE `group_member`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group_id` int NOT NULL COMMENT '组ID',
  `member_id` int NOT NULL COMMENT '成员ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_group_member`(`group_id` ASC, `member_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '班级成员关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of group_member
-- ----------------------------
INSERT INTO `group_member` VALUES (1, 1, 2);
INSERT INTO `group_member` VALUES (2, 2, 2);
INSERT INTO `group_member` VALUES (3, 2, 3);

-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '题目标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '题目内容 (Markdown格式)',
  `tags` json NULL COMMENT '标签列表 [ \"栈\", \"简单\", \"Java\" ]',
  `difficulty` tinyint NULL DEFAULT 0 COMMENT '难度：0-简单, 1-中等, 2-困难',
  `time_limit` int NULL DEFAULT 1000 COMMENT '时间限制 (ms)',
  `memory_limit` int NULL DEFAULT 128 COMMENT '内存限制 (MB)',
  `test_cases` json NOT NULL COMMENT '测试用例 [ {\"input\": \"1 2\", \"output\": \"3\"}, {\"input\": \"2 2\", \"output\": \"4\"} ]',
  `template_code` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '初始化模板代码 (如 public class Main...)',
  `standard_solution` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '标准答案代码 (供 AI 参考)',
  `explanation` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '题目解析 (由老师编写或 AI 生成)',
  `advisor_id` int NOT NULL COMMENT '创建者ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '题目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question
-- ----------------------------

-- ----------------------------
-- Table structure for record_report
-- ----------------------------
DROP TABLE IF EXISTS `record_report`;
CREATE TABLE `record_report`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `exam_record_id` int NOT NULL COMMENT '关联的考试记录ID',
  `question_id` int NOT NULL COMMENT '题目ID',
  `user_id` int NOT NULL COMMENT '用户ID（冗余字段，方便查询）',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '题目状态：0-未答, 1-通过(AC), 2-解答错误(WA), 3-运行超时, 4-编译错误',
  `score` int NULL DEFAULT 0 COMMENT '该题得分',
  `answer_code` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '学生提交的代码快照',
  `ai_feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'AI对这道题的代码审查和改进建议',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '沙盒返回的原始错误信息',
  `time_consumed` int NULL DEFAULT 0 COMMENT '执行耗时(ms)',
  `memory_consumed` int NULL DEFAULT 0 COMMENT '内存消耗(KB)',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考试题目详情报告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of record_report
-- ----------------------------
INSERT INTO `record_report` VALUES (1, 1, 101, 2, 1, 20, 'public class Main { ... }', '代码逻辑清晰，空间复杂度 O(1) 表现优异。建议：可以将变量名 s 改为更具语义的 inputString。', NULL, 0, 0, '2026-02-08 17:14:04');
INSERT INTO `record_report` VALUES (2, 1, 102, 2, 4, 0, 'pubic static void main...', '你把 public 写成了 pubic。这是一个低级拼写错误。', 'Main.java:1: error: expected...', 0, 0, '2026-02-08 17:14:04');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账号',
  `password` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '名字',
  `role` tinyint NOT NULL COMMENT '用户角色：0-管理员，1-导师，2-成员',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'advisor1', 'E10ADC3949BA59ABBE56E057F20F883E', '张教授', 1);
INSERT INTO `user` VALUES (2, 'member1', 'E10ADC3949BA59ABBE56E057F20F883E', '小明', 2);
INSERT INTO `user` VALUES (3, 'member2', 'E10ADC3949BA59ABBE56E057F20F883E', '小红', 2);
INSERT INTO `user` VALUES (4, 'admin', 'E10ADC3949BA59ABBE56E057F20F883E', '管理', 0);
INSERT INTO `user` VALUES (5, 'laolao', 'e10adc3949ba59abbe56e057f20f883e', '劳劳', 2);

SET FOREIGN_KEY_CHECKS = 1;
