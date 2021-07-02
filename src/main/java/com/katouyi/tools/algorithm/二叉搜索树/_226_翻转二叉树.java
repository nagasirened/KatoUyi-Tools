package com.katouyi.tools.algorithm.二叉搜索树;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 翻转二叉树      翻转前       4                   翻转后       4
 *                      2           7                   7         2
 *                    1   3      6     9            9     6     3    1
 */

public class _226_翻转二叉树 {

    // 前序遍历，先交换左右，再往下遍历     子节点交换，继续往上左右交换
    public TreeNode invertNodePreOrder(TreeNode root) {
        if (root == null) return root;

        TreeNode temp = root.left;
        root.left = root.right;
        root.right = temp;

        invertNodePreOrder(root.left);
        invertNodePreOrder(root.right);
        return root;
    }

    // 后序遍历，先访问左右，再访问自己    子节点交换，继续往上左右交换
    public TreeNode invertNodeSufOrder(TreeNode root) {
        if (root == null) return root;
        invertNodeSufOrder(root.left);
        invertNodeSufOrder(root.right);

        TreeNode temp = root.left;
        root.left = root.right;
        root.right = temp;
        return root;
    }

    // 中序遍历   前后两个都是left才行，因为后面那个left已经被交换过了
    public TreeNode invertNodeMidOrder(TreeNode root) {
        if (root == null) return root;
        invertNodeSufOrder(root.left);

        TreeNode temp = root.left;
        root.left = root.right;
        root.right = temp;

        invertNodeSufOrder(root.left);
        return root;
    }

    // 层序遍历，poll一个值就交换它的左右就行了
    public TreeNode invertNodeRankOrder(TreeNode root) {
        if (root == null) return root;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            TreeNode temp = node.left;
            node.left = node.right;
            node.right = temp;

            if (node.left != null) {
                queue.offer(node.left);
            }
            if (node.right != null) {
                queue.offer(node.right);
            }
        }
        return root;
    }
}


class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}