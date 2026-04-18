package com.laolao;

import com.laolao.judge.JudgeUnits;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LaolaoCodeApplicationTests {

    @Test
        // 测试方法
    void contextLoads() {
        JudgeUnits units = new JudgeUnits();
        String userCode = """
                class Solution {
                    private int ans = 0;
                    public int diameterOfBinaryTree(TreeNode root, List<String> answers) {
                        death(root);
                        return ans;
                    }
                
                    private int death(TreeNode node) {
                        if(node == null) {
                            return 0;
                        }
                
                        int leftDeath = death(node.left);
                        int rightDeath = death(node.right);
                        ans = Math.max(leftDeath + rightDeath, ans);
                
                        return Math.max(leftDeath, rightDeath) + 1;
                    }
                }
                """;

        // 1. 解析
        JudgeUnits.MethodInfo info = units.paramParsing(userCode);

        // 2. 生成 Main.java 内容
        String fullCode = units.generateMain(userCode, info);

        // 3. 打印出来看逻辑是否正确
        System.out.println(fullCode);
    }
}