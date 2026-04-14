package com.laolao.judge;

public class CommonStructs {
    // 单链表
    public static class ListNode {
        public int val;
        public ListNode next;

        public ListNode(int x) {
            val = x;
            next = null;
        }

        /**
         * 数组转链表
         *
         * @param arr 数组（不可为空）
         * @return 链表
         */
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
    }

    // 二叉树
    public static class TreeNode {
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
    }
}