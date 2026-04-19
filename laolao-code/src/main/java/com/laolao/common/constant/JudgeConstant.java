package com.laolao.common.constant;

public class JudgeConstant {
    // 定义标签常量，用于标识哪些容器是本项目创建的
    public static final String LABEL_KEY = "owner";
    public static final String LABEL_VALUE = "laolao-judge";

    // 判题过程
    public static final int STATUS_JUDGING = -1;  // 判题中
    // 判题最终状态
    public static final int STATUS_AC = 0;   // 全部通过
    public static final int STATUS_WA = 1;   // 答案错误
    public static final int STATUS_MLE = 2;  // 内存超限
    public static final int STATUS_TLE = 3;  // 超时
    public static final int STATUS_RE = 4;   // 运行时错误
    public static final int STATUS_CE = 5;   // 编译错误
    public static final int STATUS_SE = 6;   // 系统错误
    public static final int STATUS_UNKNOWN = 7;// 未知错误（未覆盖的异常）

    // 导包
    public static final String COMMON_IMPORTS =
            """
            import java.io.PrintWriter;
            import java.io.StringWriter;
            import java.util.*;
            import com.fasterxml.jackson.databind.ObjectMapper;
            import com.fasterxml.jackson.core.type.TypeReference;
            """;

    // 通用结构
    public static final String COMMON_STRUCTS =
            """
            class JudgeResult {
                public Integer status;
                public String errorMessage;
                public Integer caseIndex;
                public List<String> output;
            
                public JudgeResult(Integer status, String errorMessage, Integer caseIndex, List<String> output) {
                    this.status = status;
                    this.errorMessage = errorMessage;
                    this.caseIndex = caseIndex;
                    this.output = output;
                }
            }
            """;

    // 链表
    public static final String LIST_NODE_STRUCT =
            """
            class ListNode {
                public int val;
                public ListNode next;
        
                public ListNode(int x) {
                    val = x;
                    next = null;
                }
        
                public static ListNode fromArray(int[] arr) {
                    if (arr == null || arr.length == 0) {
                        return null;
                    }
                    ListNode dummy = new ListNode(0);
                    ListNode cur = dummy;
                    for (int val : arr) {
                        cur.next = new ListNode(val);
                        cur = cur.next;
                    }
                    return dummy.next;
                }
        
                public static List<Integer> toList(Object nodeObj) {
                    List<Integer> res = new ArrayList<>();
                    ListNode node = (ListNode) nodeObj;
                    while (node != null) {
                        res.add(node.val);
                        node = node.next;
                    }
                    return res;
                }
            }
            """;

    // 二叉树
    public static final String TREE_NODE_STRUCT =
            """
            class TreeNode {
                public int val;
                public TreeNode left;
                public TreeNode right;
        
                public TreeNode(int x) {
                    val = x;
                    left = null;
                    right = null;
                }
        
                public static TreeNode fromArray(Integer[] arr) {
                    if (arr == null || arr.length == 0) {
                        return null;
                    }
                    TreeNode root = new TreeNode(arr[0]);
                    java.util.Queue<TreeNode> q = new java.util.LinkedList<>();
                    q.add(root);
                    int i = 1;
                    while (!q.isEmpty() && i < arr.length) {
                        TreeNode curr = q.poll();
                        if (arr[i] != null) {
                            curr.left = new TreeNode(arr[i]);
                            q.add(curr.left);
                        }
                        i++;
                        if (i < arr.length && arr[i] != null) {
                            curr.right = new TreeNode(arr[i]);
                            q.add(curr.right);
                        }
                        i++;
                    }
                    return root;
                }
        
                public static List<Integer> toList(Object rootObj) {
                    List<Integer> result = new java.util.ArrayList<>();
                    if (rootObj == null) {
                        return result;
                    }
                    TreeNode root = (TreeNode) rootObj;
                    java.util.Queue<TreeNode> queue = new java.util.LinkedList<>();
                    queue.add(root);
                    while (!queue.isEmpty()) {
                        TreeNode curr = queue.poll();
                        if (curr != null) {
                            result.add(curr.val);
                            queue.add(curr.left);
                            queue.add(curr.right);
                        } else {
                            result.add(null);
                        }
                    }
                    while (!result.isEmpty() && result.get(result.size() - 1) == null) {
                        result.remove(result.size() - 1);
                    }
                    return result;
                }
            }
            """;
}
