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

 Date: 20/04/2026 20:33:19
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
) ENGINE = InnoDB AUTO_INCREMENT = 65 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考生判题记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of judge_record
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考生考试最终判题结果汇总表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of judge_user_result
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '题目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question
-- ----------------------------
INSERT INTO `question` VALUES (1, '两数之和', '给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出 **和为目标值 target** 的那 **两个** 整数，并返回它们的数组下标。\n你可以假设每种输入只会对应一个答案，并且你不能使用两次相同的元素。\n你可以按任意顺序返回答案。\n\n**示例 1：**\n输入：nums = [2,7,11,15], target = 9  \n输出：[0,1]  \n解释：因为 nums[0] + nums[1] == 9 ，返回 [0, 1] 。\n\n**示例 2：**\n输入：nums = [3,2,4], target = 6  \n输出：[1,2]\n\n**示例 3：**\n输入：nums = [3,3], target = 6  \n输出：[0,1]', 0, 1000, 128, 'class Solution {\n    public int[] twoSum(int[] nums, int target) {\n        \n    }\n}', 'class Solution {\n    public int[] twoSum(int[] nums, int target) {\n        Map<Integer, Integer> idx = new HashMap<>(); // 创建一个空哈希表\n        for (int j = 0; ; j++) { // 枚举 j\n            int x = nums[j];\n            // 在左边找 nums[i]，满足 nums[i]+x=target\n            if (idx.containsKey(target - x)) { // 找到了\n                return new int[]{idx.get(target - x), j}; // 返回两个数的下标\n            }\n            idx.put(x, j); // 保存 nums[j] 和 j\n        }\n    }\n}', 4, 0, 1, 0, 1, '2026-04-20 19:12:44', '2026-04-20 19:13:24');
INSERT INTO `question` VALUES (2, '最长连续序列', '给定一个未排序的整数数组 `nums`，找出数字连续的最长序列（不要求序列元素在原数组中连续）的长度。\n请你设计并实现时间复杂度为 **O(n)** 的算法解决此问题。\n\n**示例 1：**\n输入：nums = [100,4,200,1,3,2]  \n输出：4  \n解释：最长数字连续序列是 [1, 2, 3, 4]。它的长度为 4。\n\n**示例 2：**\n输入：nums = [0,3,7,2,5,8,4,6,0,1]  \n输出：9\n\n**示例 3：**\n输入：nums = [1,0,1,2]  \n输出：3', 1, 1000, 128, 'class Solution {\n    public int longestConsecutive(int[] nums) {\n        \n    }\n}', 'class Solution {\n    public int longestConsecutive(int[] nums) {\n        Set<Integer> st = new HashSet<>();\n        for (int num : nums) {\n            st.add(num); // 把 nums 转成哈希集合\n        }\n        int m = st.size();\n\n        int ans = 0;\n        for (int x : st) { // 遍历哈希集合\n            if (st.contains(x - 1)) { // 如果 x 不是序列的起点，直接跳过\n                continue;\n            }\n            // x 是序列的起点\n            int y = x + 1;\n            while (st.contains(y)) { // 不断查找下一个数是否在哈希集合中\n                y++;\n            }\n            // 循环结束后，y-1 是最后一个在哈希集合中的数\n            ans = Math.max(ans, y - x); // 从 x 到 y-1 一共 y-x 个数\n            if (ans * 2 >= m) {\n                break;\n            }\n        }\n        return ans;\n    }\n}', 4, 0, 1, 0, 1, '2026-04-20 19:15:39', '2026-04-20 19:15:39');
INSERT INTO `question` VALUES (3, '三数之和', '给你一个整数数组 `nums` ，判断是否存在三元组 `[nums[i], nums[j], nums[k]]` 满足 `i != j`、`i != k` 且 `j != k` ，同时还满足 `nums[i] + nums[j] + nums[k] == 0` 。请你返回所有和为 `0` 且不重复的三元组。\n**注意：** 答案中不可以包含重复的三元组。\n\n**示例 1：**\n输入：nums = [-1,0,1,2,-1,-4]  \n输出：[[-1,-1,2],[-1,0,1]]  \n解释：  \nnums[0] + nums[1] + nums[2] = (-1) + 0 + 1 = 0 。  \nnums[1] + nums[2] + nums[4] = 0 + 1 + (-1) = 0 。  \nnums[0] + nums[3] + nums[4] = (-1) + 2 + (-1) = 0 。  \n不同的三元组是 [-1,0,1] 和 [-1,-1,2] 。  \n注意，输出的顺序和三元组的顺序并不重要。\n\n**示例 2：**\n输入：nums = [0,1,1]  \n输出：[]  \n解释：唯一可能的三元组和不为 0 。\n\n**示例 3：**\n输入：nums = [0,0,0]  \n输出：[[0,0,0]]  \n解释：唯一可能的三元组和为 0 。', 1, 1000, 128, 'class Solution {\n    public List<List<Integer>> threeSum(int[] nums) {\n        \n    }\n}', 'class Solution {\n    public List<List<Integer>> threeSum(int[] nums) {\n        Arrays.sort(nums);\n        List<List<Integer>> ans = new ArrayList<>();\n        int n = nums.length;\n        for (int i = 0; i < n - 2; i++) {\n            int x = nums[i];\n            if (i > 0 && x == nums[i - 1]) continue; // 跳过重复数字\n            if (x + nums[i + 1] + nums[i + 2] > 0) break; // 优化一\n            if (x + nums[n - 2] + nums[n - 1] < 0) continue; // 优化二\n            int j = i + 1;\n            int k = n - 1;\n            while (j < k) {\n                int s = x + nums[j] + nums[k];\n                if (s > 0) {\n                    k--;\n                } else if (s < 0) {\n                    j++;\n                } else { // 三数之和为 0\n                    // j = i+1 表示刚开始双指针，此时 j 左边没有数字\n                    // nums[j] != nums[j-1] 说明与上一轮循环的三元组不同\n                    if (j == i + 1 || nums[j] != nums[j - 1]) {\n                        ans.add(List.of(x, nums[j], nums[k]));\n                    }\n                    j++;\n                    k--;\n                }\n            }\n        }\n        return ans;\n    }\n}', 4, 0, 1, 0, 1, '2026-04-20 19:32:16', '2026-04-20 19:32:16');
INSERT INTO `question` VALUES (4, '找到字符串中所有字母异位词', '给定两个字符串 s 和 p，找到 s 中所有 p 的异位词的子串，返回这些子串的起始索引。不考虑答案输出的顺序。\n\n**示例 1：**\n输入：s = \"cbaebabacd\", p = \"abc\"  \n输出：[0,6]  \n解释：  \n起始索引等于 0 的子串是 \"cba\"，它是 \"abc\" 的异位词。  \n起始索引等于 6 的子串是 \"bac\"，它是 \"abc\" 的异位词。\n\n**示例 2：**\n输入：s = \"abab\", p = \"ab\"  \n输出：[0,1,2]  \n解释：  \n起始索引等于 0 的子串是 \"ab\"，它是 \"ab\" 的异位词。  \n起始索引等于 1 的子串是 \"ba\"，它是 \"ab\" 的异位词。  \n起始索引等于 2 的子串是 \"ab\"，它是 \"ab\" 的异位词。', 1, 1000, 128, 'class Solution {\n    public List<Integer> findAnagrams(String s, String p) {\n        \n    }\n}', 'class Solution {\n    public List<Integer> findAnagrams(String s, String p) {\n        // 统计 p 的每种字母的出现次数\n        int[] cnt = new int[26]; \n        for (char c : p.toCharArray()) {\n            cnt[c - \'a\']++;\n        }\n\n        List<Integer> ans = new ArrayList<>();\n        int left = 0;\n        for (int right = 0; right < s.length(); right++) {\n            int c = s.charAt(right) - \'a\';\n            cnt[c]--; // 右端点字母进入窗口\n            while (cnt[c] < 0) { // 字母 c 太多了\n                cnt[s.charAt(left) - \'a\']++; // 左端点字母离开窗口\n                left++;\n            }\n            if (right - left + 1 == p.length()) { // t 和 p 的每种字母的出现次数都相同（证明见上）\n                ans.add(left); // t 左端点下标加入答案\n            }\n        }\n        return ans;\n    }\n}', 4, 0, 1, 0, 1, '2026-04-20 19:38:56', '2026-04-20 19:38:56');
INSERT INTO `question` VALUES (5, '和为 K 的子数组', '给你一个整数数组 `nums` 和一个整数 `k`，请你统计并返回该数组中和为 `k` 的子数组的个数。\n子数组是数组中元素的连续非空序列。\n\n**示例 1：**\n输入：nums = [1,1,1], k = 2  \n输出：2\n\n**示例 2：**\n输入：nums = [1,2,3], k = 3  \n输出：2', 1, 1000, 128, 'class Solution {\n    public int subarraySum(int[] nums, int k) {\n        \n    }\n}', 'class Solution {\n    public int subarraySum(int[] nums, int k) {\n        Map<Integer, Integer> cnt = new HashMap<>(nums.length, 1); // 预分配空间\n        int s = 0;\n        int ans = 0;\n        for (int x : nums) {\n            cnt.merge(s, 1, Integer::sum); // cnt[s]++\n            s += x;\n            ans += cnt.getOrDefault(s - k, 0);\n        }\n        return ans;\n    }\n}', 4, 0, 1, 0, 1, '2026-04-20 19:42:05', '2026-04-20 19:42:05');
INSERT INTO `question` VALUES (6, '滑动窗口最大值', '给你一个整数数组 `nums`，有一个大小为 `k` 的滑动窗口从数组的最左侧移动到数组的最右侧。你只可以看到在滑动窗口内的 `k` 个数字。滑动窗口每次只向右移动一位。\n返回滑动窗口中的最大值。\n\n**示例 1：**\n输入：nums = [1,3,-1,-3,5,3,6,7], k = 3  \n输出：[3,3,5,5,6,7]  \n解释：\n滑动窗口的位置                 最大值\n----------------------------------\n[1  3  -1] -3  5  3  6  7       3\n 1 [3  -1  -3] 5  3  6  7       3\n 1  3 [-1  -3  5] 3  6  7       5\n 1  3  -1 [-3  5  3] 6  7       5\n 1  3  -1  -3 [5  3  6] 7       6\n 1  3  -1  -3  5 [3  6  7]      7\n\n**示例 2：**\n输入：nums = [1], k = 1  \n输出：[1]', 2, 1000, 128, 'class Solution {\n    public int[] maxSlidingWindow(int[] nums, int k) {\n        \n    }\n}', 'class Solution {\n    public int[] maxSlidingWindow(int[] nums, int k) {\n        int n = nums.length;\n        int[] ans = new int[n - k + 1]; // 窗口个数\n        Deque<Integer> q = new ArrayDeque<>(); // 更快的写法见【Java 数组】\n\n        for (int i = 0; i < n; i++) {\n            // 1. 右边入\n            while (!q.isEmpty() && nums[q.getLast()] <= nums[i]) {\n                q.removeLast(); // 维护 q 的单调性\n            }\n            q.addLast(i); // 注意保存的是下标，这样下面可以判断队首是否离开窗口\n\n            // 2. 左边出\n            int left = i - k + 1; // 窗口左端点\n            if (q.getFirst() < left) { // 队首离开窗口\n                q.removeFirst();\n            }\n\n            // 3. 在窗口左端点处记录答案\n            if (left >= 0) {\n                // 由于队首到队尾单调递减，所以窗口最大值就在队首\n                ans[left] = nums[q.getFirst()];\n            }\n        }\n\n        return ans;\n    }\n}', 4, 0, 1, 0, 1, '2026-04-20 19:45:34', '2026-04-20 19:45:34');
INSERT INTO `question` VALUES (7, '最大子数组和', '给你一个整数数组 `nums`，请你找出一个具有最大和的连续子数组（子数组最少包含一个元素），返回其最大和。\n子数组是数组中的一个连续部分。\n\n**示例 1：**\n输入：nums = [-2,1,-3,4,-1,2,1,-5,4]  \n输出：6  \n解释：连续子数组 [4,-1,2,1] 的和最大，为 6。\n\n**示例 2：**\n输入：nums = [1]  \n输出：1\n\n**示例 3：**\n输入：nums = [5,4,-1,7,8]  \n输出：23', 1, 1000, 128, 'class Solution {\n    public int maxSubArray(int[] nums) {\n        \n    }\n}', 'class Solution {\n    public int maxSubArray(int[] nums) {\n        int ans = Integer.MIN_VALUE; // 注意答案可以是负数，不能初始化成 0\n        int f = 0;\n        for (int x : nums) {\n            f = Math.max(f, 0) + x;\n            ans = Math.max(ans, f);\n        }\n        return ans;\n    }\n}', 4, 0, 1, 0, 1, '2026-04-20 19:49:40', '2026-04-20 19:49:40');
INSERT INTO `question` VALUES (8, '缺失的第一个正数', '给你一个未排序的整数数组 `nums`，请你找出其中没有出现的最小的正整数。\n请你实现时间复杂度为 O(n) 并且只使用常数级别额外空间的解决方案。\n\n**示例 1：**\n输入：nums = [1,2,0]  \n输出：3  \n解释：范围 [1,2] 中的数字都在数组中。\n\n**示例 2：**\n输入：nums = [3,4,-1,1]  \n输出：2  \n解释：1 在数组中，但 2 没有。\n\n**示例 3：**\n输入：nums = [7,8,9,11,12]  \n输出：1  \n解释：最小的正数 1 没有出现。', 2, 1000, 128, 'class Solution {\n    public int firstMissingPositive(int[] nums) {\n        \n    }\n}', 'class Solution {\n    public int firstMissingPositive(int[] nums) {\n        int n = nums.length;\n        for (int i = 0; i < n; i++) {\n            // 如果当前学生的学号在 [1,n] 中，但（真身）没有坐在正确的座位上\n            while (1 <= nums[i] && nums[i] <= n && nums[nums[i] - 1] != nums[i]) {\n                // 那么就交换 nums[i] 和 nums[j]，其中 j 是 i 的学号\n                int j = nums[i] - 1; // 减一是因为数组下标从 0 开始\n                int tmp = nums[i];\n                nums[i] = nums[j];\n                nums[j] = tmp;\n            }\n        }\n\n        // 找第一个学号与座位编号不匹配的学生\n        for (int i = 0; i < n; i++) {\n            if (nums[i] != i + 1) {\n                return i + 1;\n            }\n        }\n\n        // 所有学生都坐在正确的座位上\n        return n + 1;\n    }\n}', 4, 0, 1, 0, 1, '2026-04-20 19:51:36', '2026-04-20 19:51:36');
INSERT INTO `question` VALUES (9, '矩阵置零', '给定一个 m x n 的矩阵，如果一个元素为 0，则将其所在行和列的所有元素都设为 0。请使用 **原地** 算法。\n\n**示例 1：**\n输入：matrix = [[1,1,1],[1,0,1],[1,1,1]]  \n输出：[[1,0,1],[0,0,0],[1,0,1]]\n\n**示例 2：**\n输入：matrix = [[0,1,2,0],[3,4,5,2],[1,3,1,5]]  \n输出：[[0,0,0,0],[0,4,5,0],[0,3,1,0]]', 1, 1000, 128, 'class Solution {\n    public void setZeroes(int[][] matrix) {\n        \n    }\n}', 'class Solution {\n    public void setZeroes(int[][] matrix) {\n        int m = matrix.length;\n        int n = matrix[0].length;\n\n        boolean firstRowHasZero = false;\n        for (int j = 0; j < n; j++) {\n            if (matrix[0][j] == 0) {\n                firstRowHasZero = true;\n                break;\n            }\n        }\n\n        for (int i = 1; i < m; i++) {\n            for (int j = 0; j < n; j++) {\n                if (matrix[i][j] == 0) {\n                    matrix[i][0] = matrix[0][j] = 0;\n                }\n            }\n        }\n\n        for (int i = 1; i < m; i++) {\n            // 倒着遍历，避免提前把 matrix[i][0] 改成 0，误认为这一行要全部变成 0\n            for (int j = n - 1; j >= 0; j--) {\n                if (matrix[i][0] == 0 || matrix[0][j] == 0) {\n                    matrix[i][j] = 0;\n                }\n            }\n        }\n\n        if (firstRowHasZero) {\n            Arrays.fill(matrix[0], 0);\n        }\n    }\n}', 4, 0, 1, 0, 1, '2026-04-20 19:55:33', '2026-04-20 19:55:33');
INSERT INTO `question` VALUES (10, '旋转图像', '给定一个 n × n 的二维矩阵 `matrix` 表示一个图像。请你将图像顺时针旋转 90 度。\n你必须在 **原地** 旋转图像，这意味着你需要直接修改输入的二维矩阵。请不要使用另一个矩阵来旋转图像。\n\n**示例 1：**\n输入：matrix = [[1,2,3],[4,5,6],[7,8,9]]  \n输出：[[7,4,1],[8,5,2],[9,6,3]]\n\n**示例 2：**\n输入：matrix = [[5,1,9,11],[2,4,8,10],[13,3,6,7],[15,14,12,16]]  \n输出：[[15,13,2,5],[14,3,4,1],[12,6,8,9],[16,7,10,11]]', 1, 1000, 128, 'class Solution {\n    public void rotate(int[][] matrix) {\n        \n    }\n}', 'class Solution {\n    public void rotate(int[][] matrix) {\n        int n = matrix.length;\n        for (int i = 0; i < n; i++) {\n            int[] row = matrix[i];\n            for (int j = i + 1; j < n; j++) { // 遍历对角线上方元素，做转置\n                int tmp = row[j];\n                row[j] = matrix[j][i];\n                matrix[j][i] = tmp;\n            }\n            for (int j = 0; j < n / 2; j++) { // 遍历左半元素，做行翻转\n                int tmp = row[j];\n                row[j] = row[n - 1 - j];\n                row[n - 1 - j] = tmp;\n            }\n        }\n    }\n}', 4, 0, 1, 0, 1, '2026-04-20 19:57:50', '2026-04-20 19:57:50');
INSERT INTO `question` VALUES (11, '合并 K 个升序链表', '给你一个链表数组，每个链表都已经按升序排列。\n请你将所有链表合并到一个升序链表中，返回合并后的链表。\n\n**示例 1：**\n输入：lists = [[1,4,5],[1,3,4],[2,6]]  \n输出：[1,1,2,3,4,4,5,6]  \n解释：链表数组如下：  \n[\n  1->4->5,\n  1->3->4,\n  2->6\n]  \n将它们合并到一个有序链表中得到：  \n1->1->2->3->4->4->5->6\n\n**示例 2：**\n输入：lists = []  \n输出：[]\n\n**示例 3：**\n输入：lists = [[]]  \n输出：[]', 2, 1000, 128, '/**\n * Definition for singly-linked list.\n * public class ListNode {\n *     int val;\n *     ListNode next;\n *     ListNode() {}\n *     ListNode(int val) { this.val = val; }\n *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }\n * }\n */\nclass Solution {\n    public ListNode mergeKLists(ListNode[] lists) {\n        \n    }\n}', 'class Solution {\n    public ListNode mergeKLists(ListNode[] lists) {\n        int m = lists.length;\n        if (m == 0) {\n            return null;\n        }\n        for (int step = 1; step < m; step *= 2) {\n            for (int i = 0; i < m - step; i += step * 2) {\n                lists[i] = mergeTwoLists(lists[i], lists[i + step]);\n            }\n        }\n        return lists[0];\n    }\n\n    // 21. 合并两个有序链表\n    private ListNode mergeTwoLists(ListNode list1, ListNode list2) {\n        ListNode dummy = new ListNode(); // 用哨兵节点简化代码逻辑\n        ListNode cur = dummy; // cur 指向新链表的末尾\n        while (list1 != null && list2 != null) {\n            if (list1.val < list2.val) {\n                cur.next = list1; // 把 list1 加到新链表中\n                list1 = list1.next;\n            } else { // 注：相等的情况加哪个节点都是可以的\n                cur.next = list2; // 把 list2 加到新链表中\n                list2 = list2.next;\n            }\n            cur = cur.next;\n        }\n        cur.next = list1 != null ? list1 : list2; // 拼接剩余链表\n        return dummy.next;\n    }\n}', 4, 0, 1, 0, 1, '2026-04-20 20:00:29', '2026-04-20 20:00:29');
INSERT INTO `question` VALUES (12, '二叉树中的最大路径和', '二叉树中的 **路径** 被定义为一条节点序列，序列中每对相邻节点之间都存在一条边。同一个节点在一条路径序列中 **至多出现一次**。该路径 **至少包含一个节点**，且不一定经过根节点。\n**路径和** 是路径中各节点值的总和。\n给你一个二叉树的根节点 `root`，返回其 **最大路径和**。\n\n**示例 1：**\n输入：root = [1,2,3]  \n输出：6  \n解释：最优路径是 `2 -> 1 -> 3`，路径和为 `2 + 1 + 3 = 6`。\n\n**示例 2：**\n输入：root = [-10,9,20,null,null,15,7]  \n输出：42  \n解释：最优路径是 `15 -> 20 -> 7`，路径和为 `15 + 20 + 7 = 42`。', 2, 1000, 128, '/**\n * Definition for a binary tree node.\n * public class TreeNode {\n *     int val;\n *     TreeNode left;\n *     TreeNode right;\n *     TreeNode() {}\n *     TreeNode(int val) { this.val = val; }\n *     TreeNode(int val, TreeNode left, TreeNode right) {\n *         this.val = val;\n *         this.left = left;\n *         this.right = right;\n *     }\n * }\n */\nclass Solution {\n    public int maxPathSum(TreeNode root) {\n        \n    }\n}', 'class Solution {\n    private int ans = Integer.MIN_VALUE;\n\n    public int maxPathSum(TreeNode root) {\n        dfs(root);\n        return ans;\n    }\n\n    private int dfs(TreeNode node) {\n        if (node == null) {\n            return 0; // 没有节点，和为 0\n        }\n        int lVal = dfs(node.left); // 左子树最大链和\n        int rVal = dfs(node.right); // 右子树最大链和\n        ans = Math.max(ans, lVal + rVal + node.val); // 两条链拼成路径\n        return Math.max(Math.max(lVal, rVal) + node.val, 0); // 当前子树最大链和（注意这里和 0 取最大值了）\n    }\n}', 4, 0, 1, 0, 1, '2026-04-20 20:04:36', '2026-04-20 20:04:36');
INSERT INTO `question` VALUES (13, '课程表', '你这个学期必须选修 `numCourses` 门课程，记为 `0` 到 `numCourses - 1`。\n在选修某些课程之前需要一些先修课程。先修课程按数组 `prerequisites` 给出，其中 `prerequisites[i] = [ai, bi]`，表示如果要学习课程 `ai` 则 **必须** 先学习课程 `bi`。\n例如，先修课程对 `[0, 1]` 表示：想要学习课程 `0`，你需要先完成课程 `1`。\n请你判断是否可能完成所有课程的学习？如果可以，返回 `true`；否则，返回 `false`。\n\n**示例 1：**\n输入：numCourses = 2, prerequisites = [[1,0]]  \n输出：true  \n解释：总共有 2 门课程。学习课程 1 之前，你需要完成课程 0。这是可能的。\n\n**示例 2：**\n输入：numCourses = 2, prerequisites = [[1,0],[0,1]]  \n输出：false  \n解释：总共有 2 门课程。学习课程 1 之前，你需要先完成课程 0；并且学习课程 0 之前，你还应先完成课程 1。这是不可能的。', 1, 1000, 128, 'class Solution {\n    public boolean canFinish(int numCourses, int[][] prerequisites) {\n        \n    }\n}', 'class Solution {\n    public boolean canFinish(int numCourses, int[][] prerequisites) {\n        List<List<Integer>> adjacency = new ArrayList<>();\n        for(int i = 0; i < numCourses; i++)\n            adjacency.add(new ArrayList<>());\n        int[] flags = new int[numCourses];\n        for(int[] cp : prerequisites)\n            adjacency.get(cp[1]).add(cp[0]);\n        for(int i = 0; i < numCourses; i++)\n            if(!dfs(adjacency, flags, i)) return false;\n        return true;\n    }\n    private boolean dfs(List<List<Integer>> adjacency, int[] flags, int i) {\n        if(flags[i] == 1) return false;\n        if(flags[i] == -1) return true;\n        flags[i] = 1;\n        for(Integer j : adjacency.get(i))\n            if(!dfs(adjacency, flags, j)) return false;\n        flags[i] = -1;\n        return true;\n    }\n}', 4, 0, 1, 0, 1, '2026-04-20 20:09:03', '2026-04-20 20:09:03');

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
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '题目标签中间表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question_tag
-- ----------------------------
INSERT INTO `question_tag` VALUES (1, 1, 1);
INSERT INTO `question_tag` VALUES (2, 2, 1);
INSERT INTO `question_tag` VALUES (3, 3, 2);
INSERT INTO `question_tag` VALUES (4, 4, 3);
INSERT INTO `question_tag` VALUES (5, 5, 4);
INSERT INTO `question_tag` VALUES (7, 6, 3);
INSERT INTO `question_tag` VALUES (6, 6, 4);
INSERT INTO `question_tag` VALUES (8, 7, 5);
INSERT INTO `question_tag` VALUES (9, 8, 5);
INSERT INTO `question_tag` VALUES (10, 9, 6);
INSERT INTO `question_tag` VALUES (11, 10, 6);
INSERT INTO `question_tag` VALUES (12, 11, 7);
INSERT INTO `question_tag` VALUES (13, 12, 8);
INSERT INTO `question_tag` VALUES (14, 13, 9);

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
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '题目测试用例表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of question_test_case
-- ----------------------------
INSERT INTO `question_test_case` VALUES (1, 1, '[2,7,11,15]\n9', '[0,1]');
INSERT INTO `question_test_case` VALUES (2, 1, '[3,2,4]\n6', '[1,2]');
INSERT INTO `question_test_case` VALUES (3, 1, '[3,3]\n6', '[0,1]');
INSERT INTO `question_test_case` VALUES (4, 2, '[100,4,200,1,3,2]', '4');
INSERT INTO `question_test_case` VALUES (5, 2, '[0,3,7,2,5,8,4,6,0,1]', '9');
INSERT INTO `question_test_case` VALUES (6, 2, '[1,0,1,2]', '3');
INSERT INTO `question_test_case` VALUES (7, 3, '[-1,0,1,2,-1,-4]', '[[-1,-1,2],[-1,0,1]]');
INSERT INTO `question_test_case` VALUES (8, 3, '[0,1,1]', '[]');
INSERT INTO `question_test_case` VALUES (9, 3, '[0,0,0]', '[[0,0,0]]');
INSERT INTO `question_test_case` VALUES (10, 4, '\"cbaebabacd\"\n\"abc\"', '[0,6]');
INSERT INTO `question_test_case` VALUES (11, 4, '\"abab\"\n\"ab\"', '[0,1,2]');
INSERT INTO `question_test_case` VALUES (12, 5, '[1,1,1]\n2', '2');
INSERT INTO `question_test_case` VALUES (13, 5, '[1,2,3]\n3', '2');
INSERT INTO `question_test_case` VALUES (14, 6, '[1,3,-1,-3,5,3,6,7]\n3', '[3,3,5,5,6,7]');
INSERT INTO `question_test_case` VALUES (15, 6, '[1]\n1', '[1]');
INSERT INTO `question_test_case` VALUES (16, 7, '[-2,1,-3,4,-1,2,1,-5,4]', '6');
INSERT INTO `question_test_case` VALUES (17, 7, '[1]', '1');
INSERT INTO `question_test_case` VALUES (18, 7, '[5,4,-1,7,8]', '23');
INSERT INTO `question_test_case` VALUES (19, 8, '[1,2,0]', '3');
INSERT INTO `question_test_case` VALUES (20, 8, '[3,4,-1,1]', '2');
INSERT INTO `question_test_case` VALUES (21, 8, '[7,8,9,11,12]', '1');
INSERT INTO `question_test_case` VALUES (22, 9, '[[1,1,1],[1,0,1],[1,1,1]]', '[[1,0,1],[0,0,0],[1,0,1]]');
INSERT INTO `question_test_case` VALUES (23, 9, '[[0,1,2,0],[3,4,5,2],[1,3,1,5]]', '[[0,0,0,0],[0,4,5,0],[0,3,1,0]]');
INSERT INTO `question_test_case` VALUES (24, 10, '[[1,2,3],[4,5,6],[7,8,9]]', '[[7,4,1],[8,5,2],[9,6,3]]');
INSERT INTO `question_test_case` VALUES (25, 10, '[[5,1,9,11],[2,4,8,10],[13,3,6,7],[15,14,12,16]]', '[[15,13,2,5],[14,3,4,1],[12,6,8,9],[16,7,10,11]]');
INSERT INTO `question_test_case` VALUES (26, 11, '[[1,4,5],[1,3,4],[2,6]]', '[1,1,2,3,4,4,5,6]');
INSERT INTO `question_test_case` VALUES (27, 11, '[]', '[]');
INSERT INTO `question_test_case` VALUES (28, 11, '[[]]', '[]');
INSERT INTO `question_test_case` VALUES (29, 12, '[1,2,3]', '6');
INSERT INTO `question_test_case` VALUES (30, 12, '[-10,9,20,null,null,15,7]', '42');
INSERT INTO `question_test_case` VALUES (31, 13, '2\n[[1,0]]', 'true');
INSERT INTO `question_test_case` VALUES (32, 13, '2\n[[1,0],[0,1]]', 'false');

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
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tag
-- ----------------------------
INSERT INTO `tag` VALUES (1, '哈希', '2026-04-20 19:27:35');
INSERT INTO `tag` VALUES (2, '双指针', '2026-04-20 19:37:19');
INSERT INTO `tag` VALUES (3, '滑动窗口', '2026-04-20 19:40:02');
INSERT INTO `tag` VALUES (4, '子串', '2026-04-20 19:40:27');
INSERT INTO `tag` VALUES (5, '普通数组', '2026-04-20 19:52:18');
INSERT INTO `tag` VALUES (6, '矩阵', '2026-04-20 19:53:11');
INSERT INTO `tag` VALUES (7, '链表', '2026-04-20 20:01:33');
INSERT INTO `tag` VALUES (8, '二叉树', '2026-04-20 20:01:59');
INSERT INTO `tag` VALUES (9, '图论', '2026-04-20 20:10:02');

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
INSERT INTO `team` VALUES (1, 'Java测试1组', '用于项目的测试1组', 1, 'JAVA2003');
INSERT INTO `team` VALUES (2, 'Java测试2组', '用于项目的测试2组', 1, 'JAVA1029');

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
INSERT INTO `user` VALUES (1, 'advisor1', 'E10ADC3949BA59ABBE56E057F20F883E', '李教授', 1);
INSERT INTO `user` VALUES (2, 'member1', 'E10ADC3949BA59ABBE56E057F20F883E', '劳劳', 2);
INSERT INTO `user` VALUES (3, 'member2', 'E10ADC3949BA59ABBE56E057F20F883E', '欣欣', 2);
INSERT INTO `user` VALUES (4, 'admin', 'E10ADC3949BA59ABBE56E057F20F883E', '项目管理员', 0);

SET FOREIGN_KEY_CHECKS = 1;
