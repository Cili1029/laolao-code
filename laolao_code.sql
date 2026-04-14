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

 Date: 14/04/2026 16:05:25
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_report
-- ----------------------------
DROP TABLE IF EXISTS `ai_report`;
CREATE TABLE `ai_report`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `target_type` tinyint NOT NULL COMMENT '报告关联的类型: 1-考生单题 2-考生整场试卷, 3-全组考试统测',
  `target_id` int NOT NULL COMMENT '关联的ID (judge_record_id, exam_record_id 或 exam_id)',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'AI生成的报告',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_target`(`target_type` ASC, `target_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI分析报告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_report
-- ----------------------------
INSERT INTO `ai_report` VALUES (17, 1, 44, '同学，我看到你在\"有效的括号\"这道题中遇到了困难。你的代码中写着\"这一题我不会，给点分吧\"，这说明你对栈的应用还不够熟悉。这道题的核心思路是使用栈来匹配括号：遇到左括号时压入对应的右括号，遇到右括号时检查栈顶是否匹配。建议你先理解栈的基本操作，然后尝试用栈来解决括号匹配问题。下次遇到类似题目时，可以先画图分析括号的匹配规则，再动手写代码。');
INSERT INTO `ai_report` VALUES (18, 2, 6, '从这次考试表现来看，你在第一题\"回文串判断\"上完成得相当不错！代码逻辑清晰，考虑了大小写转换，使用了StringBuilder的反转功能，整体实现思路正确。这说明你对字符串处理和回文判断的基本概念掌握得比较扎实。\n\n然而，第二题\"有效的括号\"暴露了你的薄弱环节。你不仅没有完成代码，还在注释中表达了畏难情绪。这道题考察的是栈数据结构的应用，这是算法学习中的一个重要知识点。从你的表现可以看出，你在遇到需要特定数据结构（如栈）来解决问题的题目时，还缺乏足够的解题思路和信心。\n\n建议你重点加强以下几个方面：\n1. **数据结构基础**：系统学习栈、队列等基本数据结构的特性和应用场景\n2. **算法思维训练**：多练习括号匹配这类经典算法题，理解其背后的解题模式\n3. **问题分析能力**：遇到难题时，先分析问题特点，思考适合的数据结构，而不是直接放弃\n\n记住，编程学习是一个循序渐进的过程，遇到困难很正常。保持积极的学习态度，遇到不会的题目先尝试分析，实在不会再看标准答案学习思路。相信通过持续练习，你一定能攻克这些难关！');

-- ----------------------------
-- Table structure for exam
-- ----------------------------
DROP TABLE IF EXISTS `exam`;
CREATE TABLE `exam`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '考试标题',
  `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '考试说明',
  `manager_id` int NOT NULL COMMENT '组管理员ID',
  `team_id` int NOT NULL COMMENT '所属组ID',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态(0-草稿, 5-发布中, 1-已发布, 2-改卷中, 3-已完成, 4-已取消)',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `is_queued` tinyint NOT NULL DEFAULT 0 COMMENT '是否已在队列',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考试表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam
-- ----------------------------
INSERT INTO `exam` VALUES (1, '2024年春季 Java 核心技术月考', '本次考试涵盖多线程、JVM、集合类。请在规定时间内独立完成，AI助教将进行逻辑审查。', 1, 1, 1, '2030-03-20 09:00:00', '2030-03-20 11:00:00', 0);
INSERT INTO `exam` VALUES (2, '【AI生成】动态规划专题练习', '由 Spring AI 根据近期大家的薄弱点自动选取的题目，主要针对背包问题和区间DP。', 1, 2, 1, '2025-03-01 00:00:00', '2027-03-31 23:59:59', 0);
INSERT INTO `exam` VALUES (10, '测试考试', '这是一个测试考试', 1, 1, 3, '2026-03-07 15:01:00', '2026-03-07 15:30:00', 0);
INSERT INTO `exam` VALUES (11, '测试2', '测试2', 1, 2, 0, '2026-03-09 00:00:00', '2026-03-10 00:00:00', 0);

-- ----------------------------
-- Table structure for exam_question_config
-- ----------------------------
DROP TABLE IF EXISTS `exam_question_config`;
CREATE TABLE `exam_question_config`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `exam_id` int NOT NULL COMMENT '所属考试ID',
  `question_id` int NOT NULL COMMENT '题目ID',
  `score` int NOT NULL DEFAULT 0 COMMENT '该题在该场考试中的分值',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_exam_question`(`exam_id` ASC, `question_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 50 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考试题目分值配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam_question_config
-- ----------------------------
INSERT INTO `exam_question_config` VALUES (1, 1, 1, 25);
INSERT INTO `exam_question_config` VALUES (2, 1, 2, 20);
INSERT INTO `exam_question_config` VALUES (3, 1, 3, 20);
INSERT INTO `exam_question_config` VALUES (4, 1, 4, 15);
INSERT INTO `exam_question_config` VALUES (5, 1, 5, 20);
INSERT INTO `exam_question_config` VALUES (6, 2, 2, 35);
INSERT INTO `exam_question_config` VALUES (7, 2, 4, 35);
INSERT INTO `exam_question_config` VALUES (8, 2, 5, 30);
INSERT INTO `exam_question_config` VALUES (21, 10, 16, 50);
INSERT INTO `exam_question_config` VALUES (24, 10, 17, 50);
INSERT INTO `exam_question_config` VALUES (26, 11, 19, 0);
INSERT INTO `exam_question_config` VALUES (41, 11, 24, 0);

-- ----------------------------
-- Table structure for exam_record
-- ----------------------------
DROP TABLE IF EXISTS `exam_record`;
CREATE TABLE `exam_record`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `exam_id` int NOT NULL COMMENT '考试ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `score` int NOT NULL DEFAULT 0 COMMENT '总得分',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态(0-进行中, 1-已提交, 2-已批改, 3-无效)',
  `report` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '整场考试的综合评价',
  `enter_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '考生进入时间',
  `submit_time` datetime NULL DEFAULT NULL COMMENT '考生交卷时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考试记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of exam_record
-- ----------------------------
INSERT INTO `exam_record` VALUES (2, 2, 2, 0, 0, NULL, '2026-02-11 15:20:28', NULL);
INSERT INTO `exam_record` VALUES (5, 2, 1, 0, 0, NULL, '2026-03-01 15:40:00', NULL);
INSERT INTO `exam_record` VALUES (6, 10, 2, 40, 1, NULL, '2026-03-07 15:01:13', '2026-03-07 15:26:14');

-- ----------------------------
-- Table structure for judge_record
-- ----------------------------
DROP TABLE IF EXISTS `judge_record`;
CREATE TABLE `judge_record`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `exam_record_id` int NOT NULL COMMENT '关联的考试记录ID',
  `question_id` int NOT NULL COMMENT '题目ID',
  `user_id` int NOT NULL COMMENT '考生ID',
  `status` tinyint NOT NULL DEFAULT (-(1)) COMMENT '判题状态',
  `score` int NOT NULL DEFAULT 0 COMMENT '得分',
  `answer_code` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '学生提交的代码快照',
  `stdout` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '标准输出',
  `stderr` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误输出（包括报错信息）',
  `question_test_case_id` int NULL DEFAULT NULL COMMENT '未通过的测试用例',
  `time` int NULL DEFAULT NULL COMMENT '执行耗时(ms)',
  `memory` int NULL DEFAULT NULL COMMENT '内存消耗(MB)',
  `submit_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 60 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考生判题记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of judge_record
-- ----------------------------
INSERT INTO `judge_record` VALUES (23, 2, 5, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        // 编写逻辑\n    }\n}', '', NULL, 11, NULL, NULL, '2026-02-25 21:03:26');
INSERT INTO `judge_record` VALUES (24, 2, 5, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        // 编写逻辑\n    }\n}', '', NULL, 11, NULL, NULL, '2026-02-25 21:04:11');
INSERT INTO `judge_record` VALUES (25, 2, 2, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        int target = sc.nextInt();\n        // 编写逻辑\n    }\n}', '', NULL, 4, NULL, NULL, '2026-02-25 21:05:59');
INSERT INTO `judge_record` VALUES (26, 2, 2, 2, 5, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();123\n        int target = sc.nextInt();\n        // 编写逻辑\n    }\n}', NULL, 'Main.java:13: error: not a statement\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();123\n                                                      ^\nMain.java:13: error: \';\' expected\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();123\n                                                         ^\n2 errors\n', NULL, NULL, NULL, '2026-02-25 21:06:06');
INSERT INTO `judge_record` VALUES (27, 2, 2, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    // 定义取模的常量，方便维护\n    private static final int MOD = 1000000007;\n\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        // 调用斐波那契计算函数并输出结果\n        System.out.println(fib(n));\n        sc.close();\n    }\n\n    // 计算斐波那契第n项，取模1e9+7\n    public static int fib(int n) {\n        // 边界条件处理\n        if (n == 0) {\n            return 0;\n        }\n        if (n == 1) {\n            return 1;\n        }\n        // 用两个变量保存前两项，避免数组占用空间\n        long prevPrev = 0; // F(n-2)\n        long prev = 1;     // F(n-1)\n        long current = 0;  // F(n)\n        // 从2开始迭代到n\n        for (int i = 2; i <= n; i++) {\n            current = (prevPrev + prev) % MOD; // 每一步都取模，防止溢出\n            // 迭代更新前两项\n            prevPrev = prev;\n            prev = current;\n        }\n        // 转成int返回（因为取模后结果一定在int范围内）\n        return (int) current;\n    }\n}', '3', NULL, 4, NULL, NULL, '2026-02-25 21:06:13');
INSERT INTO `judge_record` VALUES (28, 2, 5, 2, 0, 30, 'import java.util.Scanner;\n\npublic class Main {\n    // 定义取模的常量，方便维护\n    private static final int MOD = 1000000007;\n\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        // 调用斐波那契计算函数并输出结果\n        System.out.println(fib(n));\n        sc.close();\n    }\n\n    // 计算斐波那契第n项，取模1e9+7\n    public static int fib(int n) {\n        // 边界条件处理\n        if (n == 0) {\n            return 0;\n        }\n        if (n == 1) {\n            return 1;\n        }\n        // 用两个变量保存前两项，避免数组占用空间\n        long prevPrev = 0; // F(n-2)\n        long prev = 1;     // F(n-1)\n        long current = 0;  // F(n)\n        // 从2开始迭代到n\n        for (int i = 2; i <= n; i++) {\n            current = (prevPrev + prev) % MOD; // 每一步都取模，防止溢出\n            // 迭代更新前两项\n            prevPrev = prev;\n            prev = current;\n        }\n        // 转成int返回（因为取模后结果一定在int范围内）\n        return (int) current;\n    }\n}', NULL, NULL, NULL, 120, 34, '2026-02-25 21:06:25');
INSERT INTO `judge_record` VALUES (29, 2, 2, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        int target = sc.nextInt();\n        // 编写逻辑\n    }\n}', '', NULL, 4, NULL, NULL, '2026-03-03 19:51:04');
INSERT INTO `judge_record` VALUES (30, 2, 2, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        int target = sc.nextInt();\n        // 编写逻辑\n    }\n}', '', NULL, 4, NULL, NULL, '2026-03-03 19:51:07');
INSERT INTO `judge_record` VALUES (31, 2, 2, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        int target = sc.nextInt();\n        // 编写逻辑\n    }\n}', '', NULL, 4, NULL, NULL, '2026-03-03 19:51:13');
INSERT INTO `judge_record` VALUES (32, 2, 2, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        int target = sc.nextInt();\n        // 编写逻辑\n    }\n}', '', NULL, 4, NULL, NULL, '2026-03-03 19:54:24');
INSERT INTO `judge_record` VALUES (33, 2, 2, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        int target = sc.nextInt();\n        // 编写逻辑\n    }\n}', '', NULL, 4, NULL, NULL, '2026-03-03 19:51:07');
INSERT INTO `judge_record` VALUES (34, 2, 2, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        int target = sc.nextInt();\n        // 编写逻辑\n    }\n}', '', NULL, 4, NULL, NULL, '2026-03-03 19:51:07');
INSERT INTO `judge_record` VALUES (35, 2, 2, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        int target = sc.nextInt();\n        // 编写逻辑\n    }\n}', '', NULL, 4, NULL, NULL, '2026-03-03 19:51:07');
INSERT INTO `judge_record` VALUES (36, 2, 2, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        int target = sc.nextInt();\n        // 编写逻辑\n    }\n}', '', NULL, 4, NULL, NULL, '2026-03-03 19:51:07');
INSERT INTO `judge_record` VALUES (37, 2, 2, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        int target = sc.nextInt();\n        // 编写逻辑\n    }\n}', '', NULL, 4, NULL, NULL, '2026-03-03 19:51:07');
INSERT INTO `judge_record` VALUES (38, 2, 2, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        int target = sc.nextInt();\n        // 编写逻辑\n    }\n}', '', NULL, 4, NULL, NULL, '2026-03-03 19:51:07');
INSERT INTO `judge_record` VALUES (39, 2, 2, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        int target = sc.nextInt();\n        // 编写逻辑\n    }\n}', '', NULL, 4, NULL, NULL, '2026-03-03 19:51:07');
INSERT INTO `judge_record` VALUES (40, 2, 2, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        int target = sc.nextInt();\n        // 编写逻辑\n    }\n}', '', NULL, 4, NULL, NULL, '2026-03-03 19:51:07');
INSERT INTO `judge_record` VALUES (41, 6, 17, 2, 5, 0, 'import java.util.Scanner;\nimport java.util.Stack;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        String s = sc.nextLine();\n        // 编写逻辑\n        这一题我不会，给点分吧\n    }\n}', NULL, 'Main.java:14: error: illegal character: \'\\uff0c\'\n        这一题我不会，给点分吧\n              ^\nMain.java:14: error: not a statement\n        这一题我不会，给点分吧\n        ^\nMain.java:14: error: not a statement\n        这一题我不会，给点分吧\n               ^\nMain.java:14: error: \';\' expected\n        这一题我不会，给点分吧\n                   ^\n4 errors\n', NULL, NULL, NULL, '2026-03-07 15:03:17');
INSERT INTO `judge_record` VALUES (42, 6, 16, 2, 0, 40, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        String s = sc.nextLine();\n        \n        // 步骤1：统一转为小写（也可以转大写），消除大小写影响\n        String lowerStr = s.toLowerCase();\n        // 步骤2：反转字符串\n        String reversedStr = new StringBuilder(lowerStr).reverse().toString();\n        // 步骤3：对比原小写字符串和反转后的字符串是否相等\n        boolean isPalindrome = lowerStr.equals(reversedStr);\n        \n        // 输出结果\n        System.out.println(isPalindrome);\n    }\n}', NULL, NULL, NULL, 110, 34, '2026-03-07 15:04:35');
INSERT INTO `judge_record` VALUES (44, 6, 17, 2, 5, 0, 'import java.util.Scanner;\nimport java.util.Stack;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        String s = sc.nextLine();\n        // 编写逻辑\n        这一题我不会，给点分吧\n    }\n}', NULL, 'Main.java:14: error: illegal character: \'\\uff0c\'\n        这一题我不会，给点分吧\n              ^\nMain.java:14: error: not a statement\n        这一题我不会，给点分吧\n        ^\nMain.java:14: error: not a statement\n        这一题我不会，给点分吧\n               ^\nMain.java:14: error: \';\' expected\n        这一题我不会，给点分吧\n                   ^\n4 errors\n', NULL, NULL, NULL, '2026-03-07 15:03:17');
INSERT INTO `judge_record` VALUES (45, 2, 2, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        int target = sc.nextInt();\n        // 编写逻辑\n    }\n}', '', NULL, 4, NULL, NULL, '2026-03-16 19:59:24');
INSERT INTO `judge_record` VALUES (46, 2, 2, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        int target = sc.nextInt();\n        // 编写逻辑\n    }\n}', '', NULL, 4, NULL, NULL, '2026-03-16 20:04:14');
INSERT INTO `judge_record` VALUES (47, 2, 2, 2, 5, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();123\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        int target = sc.nextInt();\n        // 编写逻辑\n    }\n}', NULL, 'Main.java:11: error: not a statement\n        int n = sc.nextInt();123\n                             ^\nMain.java:11: error: \';\' expected\n        int n = sc.nextInt();123\n                                ^\n2 errors\n', NULL, NULL, NULL, '2026-03-16 20:15:51');
INSERT INTO `judge_record` VALUES (48, 2, 2, 2, 5, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];1234\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        int target = sc.nextInt();\n        // 编写逻辑\n    }\n}', NULL, 'Main.java:12: error: not a statement\n        int[] nums = new int[n];1234\n                                ^\nMain.java:12: error: \';\' expected\n        int[] nums = new int[n];1234\n                                    ^\n2 errors\n', NULL, NULL, NULL, '2026-03-16 20:39:18');
INSERT INTO `judge_record` VALUES (49, 2, 2, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        int target = sc.nextInt();\n        // 编写逻辑\n    }\n}', '', NULL, 4, NULL, NULL, '2026-03-16 20:41:55');
INSERT INTO `judge_record` VALUES (50, 2, 2, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        int target = sc.nextInt();\n        // 编写逻辑\n    }\n}', '', NULL, 4, NULL, NULL, '2026-03-17 20:59:02');
INSERT INTO `judge_record` VALUES (51, 2, 2, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        int target = sc.nextInt();\n        // 编写逻辑\n    }\n}', '', NULL, 4, NULL, NULL, '2026-03-17 21:01:34');
INSERT INTO `judge_record` VALUES (52, 2, 2, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        int target = sc.nextInt();\n        // 编写逻辑\n    }\n}', '', NULL, 4, NULL, NULL, '2026-03-17 21:06:15');
INSERT INTO `judge_record` VALUES (53, 2, 5, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        // 编写逻辑\n    }\n}', '', NULL, 11, NULL, NULL, '2026-03-17 21:11:01');
INSERT INTO `judge_record` VALUES (54, 2, 2, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        int target = sc.nextInt();\n        // 编写逻辑\n    }\n}', '', NULL, 4, NULL, NULL, '2026-03-17 21:18:03');
INSERT INTO `judge_record` VALUES (55, 2, 2, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        int target = sc.nextInt();\n        // 编写逻辑\n    }\n}', '', NULL, 4, NULL, NULL, '2026-03-17 21:18:23');
INSERT INTO `judge_record` VALUES (56, 2, 5, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        // 编写逻辑\n    }\n}', '', NULL, 11, NULL, NULL, '2026-03-18 17:52:15');
INSERT INTO `judge_record` VALUES (57, 2, 2, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        int target = sc.nextInt();\n        // 编写逻辑\n    }\n}', '', NULL, 4, NULL, NULL, '2026-04-06 14:27:41');
INSERT INTO `judge_record` VALUES (58, 2, 2, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        int target = sc.nextInt();\n        // 编写逻辑\n    }\n}', '', NULL, 4, NULL, NULL, '2026-04-06 14:31:47');
INSERT INTO `judge_record` VALUES (59, 2, 5, 2, 1, 0, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        // 编写逻辑\n    }\n}', '', NULL, 11, NULL, NULL, '2026-04-09 20:45:44');

-- ----------------------------
-- Table structure for judge_user_result
-- ----------------------------
DROP TABLE IF EXISTS `judge_user_result`;
CREATE TABLE `judge_user_result`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `exam_id` int NOT NULL COMMENT '考试ID',
  `exam_record_id` int NOT NULL COMMENT '考试记录ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `question_id` int NOT NULL COMMENT '题目ID',
  `best_judge_record_id` int NOT NULL COMMENT '最优判题记录ID',
  `score` int NOT NULL DEFAULT 0 COMMENT '最高得分',
  `status` tinyint NOT NULL DEFAULT (-(1)) COMMENT '最高分对应的状态',
  `submit_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_exam_user_question`(`exam_record_id` ASC, `question_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考生考试最终判题结果汇总表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of judge_user_result
-- ----------------------------
INSERT INTO `judge_user_result` VALUES (1, 2, 2, 2, 2, 58, 0, 1, '2026-04-06 14:31:47');
INSERT INTO `judge_user_result` VALUES (2, 2, 2, 2, 5, 28, 30, 0, '2026-02-25 21:06:25');
INSERT INTO `judge_user_result` VALUES (3, 10, 6, 2, 16, 42, 40, 0, '2026-03-07 15:04:35');
INSERT INTO `judge_user_result` VALUES (4, 10, 6, 2, 17, 44, 0, 5, '2026-03-07 15:03:17');

-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '题目标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '题目内容 (Markdown格式)',
  `difficulty` tinyint NULL DEFAULT 0 COMMENT '难度(0-简单, 1-中等, 2-困难)',
  `time_limit` int NULL DEFAULT 1000 COMMENT '时间限制 (ms)',
  `memory_limit` int NULL DEFAULT 128 COMMENT '内存限制 (MB)',
  `template_code` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '初始化模板代码 (如 public class Main...)',
  `standard_solution` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '标准答案代码 (供 AI 参考)',
  `creator_id` int NOT NULL COMMENT '创建者ID',
  `parent_id` int NULL DEFAULT 0 COMMENT '父题目ID(0-祖宗模板题，非0-考试属子题快照)',
  `is_public` tinyint(1) NULL DEFAULT 0 COMMENT '是否公开(0-私有, 1-公开)',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否被删除(0-否, 1-是)',
  `is_validated` tinyint NOT NULL DEFAULT 0 COMMENT '是否以通过判题(0-否, 1-是)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '题目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question
-- ----------------------------
INSERT INTO `question` VALUES (1, '回文串判断', '### 题目描述\n请编写一个程序，判断输入的字符串是否为**回文串**。回文串是指正读和反读都一样的字符串（不考虑大小写）。\n\n### 输入描述\n输入一个字符串。\n\n### 输出描述\n如果是回文串输出 `true`，否则输出 `false`。\n\n### 示例\n**输入**：`Level`  \n**输出**：`true`', 0, 1000, 128, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        String s = sc.nextLine();\n        // 请在此处编写逻辑\n    }\n}', 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        String s = sc.nextLine().toLowerCase();\n        String rev = new StringBuilder(s).reverse().toString();\n        System.out.print(s.equals(rev));\n    }\n}', 1, 0, 0, 0, 1, '2026-02-11 15:41:16', '2026-04-07 20:51:37');
INSERT INTO `question` VALUES (2, '两数之和', '### 题目描述\n给定一个整数数组 `nums` 和一个目标值 `target`，请在数组中找出和为目标值的那**两个**整数，并输出它们的下标。\n\n### 输入描述\n第一行输入数组长度 n。  \n第二行输入 n 个整数。  \n第三行输入目标值 target。\n\n### 输出描述\n输出这两个数的下标（空格分隔）。\n\n### 示例\n**输入**：\n3\n3 2 4\n6\n**输出**：\n1 2', 0, 1000, 128, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        int target = sc.nextInt();\n        // 编写逻辑\n    }\n}', 'import java.util.Scanner;\nimport java.util.HashMap;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        int target = sc.nextInt();\n        HashMap<Integer, Integer> map = new HashMap<>();\n        for(int i=0; i<n; i++) {\n            if(map.containsKey(target - nums[i])) {\n                System.out.print(map.get(target - nums[i]) + \" \" + i);\n                return;\n            }\n            map.put(nums[i], i);\n        }\n    }\n}', 1, 0, 0, 0, 1, '2026-02-11 15:41:26', '2026-04-07 20:51:37');
INSERT INTO `question` VALUES (3, '有效的括号', '### 题目描述\n给定一个只包括 `(`，`)`，`{`，`}`，`[`，`]` 的字符串，判断字符串是否有效。\n有效字符串需满足：左括号必须用相同类型的右括号闭合；左括号必须以正确的顺序闭合。\n\n### 输入描述\n输入一个括号字符串。\n\n### 输出描述\n输出 `true` 或 `false`。\n\n### 示例\n**输入**：`()[]{}`  \n**输出**：`true`', 1, 1000, 128, 'import java.util.Scanner;\nimport java.util.Stack;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        String s = sc.nextLine();\n        // 编写逻辑\n    }\n}', 'import java.util.Scanner;\nimport java.util.Stack;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        String s = sc.next();\n        Stack<Character> stack = new Stack<>();\n        for(char c : s.toCharArray()) {\n            if(c==\'(\') stack.push(\')\');\n            else if(c==\'[\') stack.push(\']\');\n            else if(c==\'{\') stack.push(\'}\');\n            else if(stack.isEmpty() || stack.pop() != c) {\n                System.out.print(\"false\"); return;\n            }\n        }\n        System.out.print(stack.isEmpty());\n    }\n}', 1, 0, 1, 0, 1, '2026-02-11 15:41:37', '2026-04-07 20:51:37');
INSERT INTO `question` VALUES (4, '最大子数组和', '### 题目描述\n给定一个整数数组 `nums`，找到一个具有最大和的连续子数组（子数组最少包含一个元素），返回其最大和。\n\n### 示例\n**输入**：\n9\n-2 1 -3 4 -1 2 1 -5 4\n**输出**：\n6  \n(解释：连续子数组 [4,-1,2,1] 的和最大)', 1, 1000, 128, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        // 编写逻辑\n    }\n}', 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int currentMax = 0, globalMax = Integer.MIN_VALUE;\n        for(int i=0; i<n; i++) {\n            int x = sc.nextInt();\n            currentMax = Math.max(x, currentMax + x);\n            globalMax = Math.max(globalMax, currentMax);\n        }\n        System.out.print(globalMax);\n    }\n}', 1, 0, 0, 0, 1, '2026-02-11 15:41:46', '2026-04-07 20:51:37');
INSERT INTO `question` VALUES (5, '斐波那契数列（取模）', '### 题目描述\n写一个函数，输入 n ，求斐波那契（Fibonacci）数列的第 n 项。答案需要取模 1000000007 (1e9+7)。\n\n### 示例\n**输入**：45  \n**输出**：134903163', 1, 1000, 128, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        // 编写逻辑\n    }\n}', 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        if(n < 2) { System.out.print(n); return; }\n        int a = 0, b = 1, sum = 0;\n        for(int i = 2; i <= n; i++) {\n            sum = (a + b) % 1000000007;\n            a = b;\n            b = sum;\n        }\n        System.out.print(b);\n    }\n}', 1, 0, 0, 0, 1, '2026-02-11 15:41:55', '2026-04-07 20:51:37');
INSERT INTO `question` VALUES (16, '回文串判断', '### 题目描述\n请编写一个程序，判断输入的字符串是否为**回文串**。回文串是指正读和反读都一样的字符串（不考虑大小写）。\n\n### 输入描述\n输入一个字符串。\n\n### 输出描述\n如果是回文串输出 `true`，否则输出 `false`。\n\n### 示例\n**输入**：`Level`  \n**输出**：`true`', 0, 1000, 128, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        String s = sc.nextLine();\n        // 请在此处编写逻辑\n    }\n}', 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        String s = sc.nextLine().toLowerCase();\n        String rev = new StringBuilder(s).reverse().toString();\n        System.out.print(s.equals(rev));\n    }\n}', 1, 1, 0, 0, 0, '2026-03-04 19:26:52', '2026-03-04 19:26:52');
INSERT INTO `question` VALUES (17, '有效的括号', '### 题目描述\n给定一个只包括 `(`，`)`，`{`，`}`，`[`，`]` 的字符串，判断字符串是否有效。\n有效字符串需满足：左括号必须用相同类型的右括号闭合；左括号必须以正确的顺序闭合。\n\n### 输入描述\n输入一个括号字符串。\n\n### 输出描述\n输出 `true` 或 `false`。\n\n### 示例\n**输入**：`()[]{}`  \n**输出**：`true`', 1, 1000, 128, 'import java.util.Scanner;\nimport java.util.Stack;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        String s = sc.nextLine();\n        // 编写逻辑\n    }\n}', 'import java.util.Scanner;\nimport java.util.Stack;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        String s = sc.next();\n        Stack<Character> stack = new Stack<>();\n        for(char c : s.toCharArray()) {\n            if(c==\'(\') stack.push(\')\');\n            else if(c==\'[\') stack.push(\']\');\n            else if(c==\'{\') stack.push(\'}\');\n            else if(stack.isEmpty() || stack.pop() != c) {\n                System.out.print(\"false\"); return;\n            }\n        }\n        System.out.print(stack.isEmpty());\n    }\n}', 1, 3, 0, 0, 0, '2026-03-07 14:57:43', '2026-03-07 14:57:43');
INSERT INTO `question` VALUES (24, '最大子数组和', '### 题目描述\n给定一个整数数组 `nums`，找到一个具有最大和的连续子数组（子数组最少包含一个元素），返回其最大和。\n\n### 示例\n**输入**：\n9\n-2 1 -3 4 -1 2 1 -5 4\n**输出**：\n6  \n(解释：连续子数组 [4,-1,2,1] 的和最大)', 1, 1000, 128, 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int[] nums = new int[n];\n        for(int i=0; i<n; i++) nums[i] = sc.nextInt();\n        // 编写逻辑\n    }\n}', 'import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        \n        int currentMax = 0;\n        int globalMax = Integer.MIN_VALUE;\n        \n        // 直接读完全部输入，不用 n\n        while (sc.hasNextInt()) {\n            int x = sc.nextInt();\n            currentMax = Math.max(x, currentMax + x);\n            globalMax = Math.max(globalMax, currentMax);\n        }\n        \n        System.out.println(globalMax);\n    }\n}', 1, 4, 0, 0, 1, '2026-04-08 15:40:51', '2026-04-08 16:06:38');

-- ----------------------------
-- Table structure for question_tag
-- ----------------------------
DROP TABLE IF EXISTS `question_tag`;
CREATE TABLE `question_tag`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `question_id` int NOT NULL COMMENT '题目ID',
  `tag_id` int NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_question_tag`(`question_id` ASC, `tag_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '题目标签中间表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question_tag
-- ----------------------------

-- ----------------------------
-- Table structure for question_test_case
-- ----------------------------
DROP TABLE IF EXISTS `question_test_case`;
CREATE TABLE `question_test_case`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `question_id` int NOT NULL COMMENT '所属题目ID',
  `input` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '测试用例输入数据',
  `output` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '测试用例预期输出数据',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 112 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '题目测试用例表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question_test_case
-- ----------------------------
INSERT INTO `question_test_case` VALUES (1, 1, 'level', 'true');
INSERT INTO `question_test_case` VALUES (2, 1, 'hello', 'false');
INSERT INTO `question_test_case` VALUES (3, 1, 'Aba', 'true');
INSERT INTO `question_test_case` VALUES (4, 2, '4\n2 7 11 15\n9', '0 1');
INSERT INTO `question_test_case` VALUES (5, 2, '3\n3 2 4\n6', '1 2');
INSERT INTO `question_test_case` VALUES (6, 3, '()[]{}', 'true');
INSERT INTO `question_test_case` VALUES (7, 3, '([)]', 'false');
INSERT INTO `question_test_case` VALUES (8, 3, '{[]}', 'true');
INSERT INTO `question_test_case` VALUES (9, 4, '-2 1 -3 4 -1 2 1 -5 4', '6');
INSERT INTO `question_test_case` VALUES (10, 4, '5', '5');
INSERT INTO `question_test_case` VALUES (11, 5, '2', '1');
INSERT INTO `question_test_case` VALUES (12, 5, '5', '5');
INSERT INTO `question_test_case` VALUES (13, 5, '45', '134903163');
INSERT INTO `question_test_case` VALUES (41, 17, '()[]{}', 'true');
INSERT INTO `question_test_case` VALUES (42, 17, '([)]', 'false');
INSERT INTO `question_test_case` VALUES (43, 17, '{[]}', 'true');
INSERT INTO `question_test_case` VALUES (44, 16, 'level', 'true');
INSERT INTO `question_test_case` VALUES (45, 16, 'hello', 'false');
INSERT INTO `question_test_case` VALUES (46, 16, 'Aba', 'true');
INSERT INTO `question_test_case` VALUES (110, 24, '-2 1 -3 4 -1 2 1 -5 4', '6');
INSERT INTO `question_test_case` VALUES (111, 24, '5', '5');

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签名称，如：栈、动态规划',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tag
-- ----------------------------

-- ----------------------------
-- Table structure for team
-- ----------------------------
DROP TABLE IF EXISTS `team`;
CREATE TABLE `team`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '组名',
  `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组描述',
  `manager_id` int NOT NULL COMMENT '组管理员ID',
  `invite_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '组邀请码',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `invite_code`(`invite_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '组表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of team
-- ----------------------------
INSERT INTO `team` VALUES (1, 'Java进阶训练营', '专注Java核心技术与JVM调优', 1, 'JAVA2024');
INSERT INTO `team` VALUES (2, 'AI算法研究组', '基于Spring AI的大模型应用探索', 1, 'AI999');

-- ----------------------------
-- Table structure for team_user
-- ----------------------------
DROP TABLE IF EXISTS `team_user`;
CREATE TABLE `team_user`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `team_id` int NOT NULL COMMENT '组ID',
  `user_id` int NOT NULL COMMENT '成员ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_team_user`(`team_id` ASC, `user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '组成员关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of team_user
-- ----------------------------
INSERT INTO `team_user` VALUES (1, 1, 2);
INSERT INTO `team_user` VALUES (4, 1, 3);
INSERT INTO `team_user` VALUES (2, 2, 2);
INSERT INTO `team_user` VALUES (3, 2, 3);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账号',
  `password` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '名字',
  `role` tinyint NOT NULL COMMENT '用户角色(0-管理员, 1-组管理员, 2-用户)',
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
