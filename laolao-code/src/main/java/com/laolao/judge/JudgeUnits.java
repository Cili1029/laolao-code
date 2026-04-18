package com.laolao.judge;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithType;
import com.laolao.common.constant.JudgeConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class JudgeUnits {


    /**
     * 生成方法信息
     * 调用方需自己处理异常（考生修改模板）
     *
     * @param code 代码
     * @return 方法信息
     */
    public MethodInfo paramParsing(String code) {
        CompilationUnit cu = StaticJavaParser.parse(code);
        return cu.getClassByName("Solution")
                .flatMap(solution -> solution.getMethods().stream()
                        .filter(MethodDeclaration::isPublic)  // 只留 public 方法
                        .findFirst())                         // 拿第一个
                .map(method -> {
                    // 提取方法名
                    String methodName = method.getNameAsString();
                    // 提取返回值类型
                    String returnType = method.getTypeAsString();
                    // 提取参数类型列表
                    List<String> paramTypes = method.getParameters().stream()
                            .map(NodeWithType::getTypeAsString)
                            .toList();
                    // 封装成对象返回
                    return new MethodInfo(methodName, returnType, paramTypes);
                }).orElse(null);
    }

    /**
     * 生成完整代码
     *
     * @param userCode 用户代码
     * @param info 方法信息
     * @return 完整代码
     */
    public String generateMain(String userCode, MethodInfo info) {
        StringBuilder sb = new StringBuilder();

        // 导包
        sb.append(JudgeConstant.COMMON_IMPORTS);

        // 注入通用类
        sb.append((JudgeConstant.COMMON_STRUCTS));

        // 按需注入辅助类
        if (needsListNode(info)) {
            sb.append(JudgeConstant.LIST_NODE_STRUCT);
        }
        if (needsTreeNode(info)) {
            sb.append(JudgeConstant.TREE_NODE_STRUCT);
        }

        // Main逻辑前半部分
        sb.append("""
                public class Main {
                    public static void main(String[] args) throws Exception {
                        ObjectMapper mapper = new ObjectMapper();
                        Scanner sc = new Scanner(System.in);
                        Solution sol = new Solution();
                        int testCount = Integer.parseInt(sc.nextLine());
                        for (int i = 0; i < testCount; i++) {
                            try {
                """);

        // 循环生成参数解析代码
        for (int i = 0; i < info.paramTypes.size(); i++) {
            String type = info.paramTypes.get(i);
            sb.append("""
                                    String line%d = sc.nextLine();
                    """.formatted(i));
            sb.append(generateSingleParam(type, i));
        }

        // 调用用户方法并输出结果
        if ("void".equals(info.returnType)) {
            // 原地修改模式：调用后序列化第一个参数 arg0
            sb.append("""
                                    sol.%s(%s);
                                    System.out.println(mapper.writeValueAsString(arg0));
                    """.formatted(info.methodName, appendArgs(info.paramTypes.size())));
        } else {
            // 返回值模式：直接序列化结果
            sb.append("""
                                    Object result = sol.%s(%s);
                    """.formatted(info.methodName, appendArgs(info.paramTypes.size())));

            // 特殊处理：如果返回的是 ListNode 或 TreeNode，需要转成数组/List再打印
            if (info.returnType.equals("ListNode")) {
                sb.append("""
                                        System.out.println(mapper.writeValueAsString(ListNode.toList(result)));
                        """);
            } else if (info.returnType.equals("TreeNode")) {
                sb.append("""
                                        System.out.println(mapper.writeValueAsString(TreeNode.toList(result)));
                        """);
            } else {
                // 普通类型直接打印
                sb.append("""
                                        System.out.println(mapper.writeValueAsString(result));
                        """);
            }
        }

        // Main逻辑后半部分
        sb.append("""
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.exit(1);
                            }
                        }
                    }
                }
                """);

        // 注入用户代码
        sb.append(userCode);
        return sb.toString();
    }

    /**
     * 类型转换
     *
     * @param type 参数类型（int[] / ListNode / TreeNode / ...）
     * @return 一段可执行的 Java 代码字符串
     */
    private String generateSingleParam(String type, int index) {
        String varName = "arg" + index;
        String line = "line" + index;
        if (type.equals("ListNode")) {
            return """
                                    ListNode %s = ListNode.fromArray(mapper.readValue(%s, int[].class));
                    """.formatted(varName, line);
        } else if (type.equals("TreeNode")) {
            return """
                                    TreeNode %s = TreeNode.fromArray(mapper.readValue(%s, Integer[].class));
                    """.formatted(varName, line);
        } else if (type.contains("<")) {
            return """
                                    %s %s = mapper.readValue(%s, new TypeReference<%s>(){});
                    """.formatted(type, varName, line, type);
        } else {
            return """
                                    %s %s = mapper.readValue(%s, %s.class);
                    """.formatted(type, varName, line, type);
        }
    }

    /**
     * 方法入参拼接: arg0, arg1...
     *
     * @param count 个数
     * @return 拼接后的字符串
     */
    private String appendArgs(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append("arg%d%s".formatted(i, i == count - 1 ? "" : ", "));
        }
        return sb.toString();
    }

    /**
     * 判断是否需要链表
     *
     * @param info 方法信息
     * @return 结果
     */
    private boolean needsListNode(MethodInfo info) {
        // 检查返回值
        if (info.returnType.contains("ListNode")) return true;
        // 检查参数列表
        return info.paramTypes.stream().anyMatch(t -> t.contains("ListNode"));
    }

    /**
     * 判断是否需要二叉树
     *
     * @param info 方法信息
     * @return 结果
     */
    private boolean needsTreeNode(MethodInfo info) {
        if (info.returnType.contains("TreeNode")) return true;
        return info.paramTypes.stream().anyMatch(t -> t.contains("TreeNode"));
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class MethodInfo {
        public String methodName;
        public String returnType;
        public List<String> paramTypes;
    }
}
