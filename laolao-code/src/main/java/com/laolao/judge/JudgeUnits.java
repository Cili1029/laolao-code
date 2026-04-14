package com.laolao.judge;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;

public class JudgeUnits {
    /**
     * 检测入参类型
     * 调用方需自己处理异常（考生修改模板）
     *
     * @param code 代码
     * @return 参数类型
     */
    public List<String> paramParsing(String code) {
        CompilationUnit cu = StaticJavaParser.parse(code);
        ArrayList<String> params = new ArrayList<>();
        cu.getClassByName("Solution")
                .flatMap(solution -> solution.getMethods().stream()
                        .filter(MethodDeclaration::isPublic)  // 只留 public 方法
                        .findFirst())                         // 拿第一个
                .ifPresent(method -> method.getParameters().forEach(p -> params.add(p.getNameAsString())));
        return params;
    }

    /**
     * 类型转换
     *
     * @param types 参数类型（int[] / ListNode / TreeNode / ...）
     * @return 一段可执行的 Java 代码字符串
     */
    private String generateDataConverter(List<String> types) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < types.size(); i++) {
            if (types.get(i).equals("ListNode")) {
                // 生成：先读成 int[]，再调用工具类转成 ListNode
                sb.append("ListNode arg").append(i).append(" = ListNode.fromArray(mapper.readValue(sc.nextLine(), int[].class));");
            } else if (types.get(i).equals("TreeNode")) {
                // 生成：先读成 Integer[]，再构建二叉树
                sb.append("TreeNode arg").append(i).append(" = TreeNode.fromArray(mapper.readValue(sc.nextLine(), Integer[].class));");
            } else {
                // 生成：普通的 Jackson 读取
                sb.append(types.get(i)).append(" arg").append(" = mapper.readValue(sc.nextLine(), ").append(types.get(i)).append(".class);");
            }
        }
        return sb.toString();
    }
}
