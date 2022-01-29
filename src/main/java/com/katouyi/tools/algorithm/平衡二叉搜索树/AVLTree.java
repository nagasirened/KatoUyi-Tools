package com.katouyi.tools.algorithm.平衡二叉搜索树;

import com.katouyi.tools.algorithm.二叉搜索树.DefaultBinarySearchTree;

import java.util.Comparator;

/**
 * 平衡二叉搜索树 并不是完全二叉树，只是左右大致平衡即可
 * 即：任意节点的左右子树高度差不能超过1  只能是 -1  0  1三种
 */
public class AVLTree<E> extends DefaultBinarySearchTree<E> {

    public AVLTree(){}

    public AVLTree(Comparator<E> comparator) {
        super(comparator);
    }

    // 添加Node之后，判断平衡，旋转
    public void afterAdd(AVLNode node) {
        while ((node = (AVLNode) node.parent) != null) {

        }

    }

}
