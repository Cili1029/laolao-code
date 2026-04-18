package com.laolao.judge;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

class JudgeResult {
    public String status;
    public String errorMessage;
    public int caseIndex;
    public long time;
    public long memory;
    public String output;
}

/**
 * 二叉树
 */
class TreeNode {
    public int val;
    public TreeNode left;
    public TreeNode right;

    public TreeNode(int x) {
        val = x;
        left = null;
        right = null;
    }

    /**
     * 数组转二叉树
     *
     * @param arr 数组（可为空）
     * @return 二叉树
     */
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
            // 左节点
            if (arr[i] != null) {
                curr.left = new TreeNode(arr[i]);
                q.add(curr.left);
            }
            i++;
            // 右节点
            if (i < arr.length && arr[i] != null) {
                curr.right = new TreeNode(arr[i]);
                q.add(curr.right);
            }
            i++;
        }
        return root;
    }

    /**
     * 叉树转回数组
     *
     * @param rootObj 二叉树
     * @return 数组
     */
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
        // 移除末尾连续的 null，符合 LeetCode 标准输出格式
        while (!result.isEmpty() && result.get(result.size() - 1) == null) {
            result.remove(result.size() - 1);
        }
        return result;
    }
}
public class Main {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Scanner sc = new Scanner(System.in);
        Solution sol = new Solution();
        int testCount = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < testCount; i++) {
            try {
                String line0 = sc.nextLine();
                TreeNode arg0 = TreeNode.fromArray(mapper.readValue(line0, Integer[].class));
                String line1 = sc.nextLine();
                List<String> arg1 = mapper.readValue(line1, new TypeReference<List<String>>(){});
                Object result = sol.diameterOfBinaryTree(arg0, arg1);
                System.out.println(mapper.writeValueAsString(result));
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
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