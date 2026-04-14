package com.laolao;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest  // Spring 环境，不用管
class LaolaoCodeApplicationTests {

    @Test
        // 测试方法
    void contextLoads() {

        // ======================
        // 1. 学生写的 Java 代码
        // ======================
        String code = "public class Solution {\n" +
                "    public int find(TreeNode root, int target) {\n" +
                "        return 0;\n" +
                "    }\n" +
                "}";
        try {
            CompilationUnit cu = StaticJavaParser.parse(code);
            cu.getClassByName("Solution")
                    .flatMap(solution -> solution.getMethods().stream()
                            .filter(MethodDeclaration::isPublic)  // 只留 public 方法
                            .findFirst())                         // 拿第一个
                    .ifPresent(method -> {  // 如果找到了方法，才执行下面
                        method.getParameters().forEach(p -> {
                            String type = p.getType().asString();    // 取参数类型：TreeNode、int
                        });
                    });

        } catch (Exception e) {
            // ======================
            // 7. 代码错了 → 提示错误，不崩溃
            // ======================
            System.out.println("代码格式错误：" + e.getMessage());
        }
    }
}