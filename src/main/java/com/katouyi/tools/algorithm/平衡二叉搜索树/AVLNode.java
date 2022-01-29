package com.katouyi.tools.algorithm.平衡二叉搜索树;

import com.katouyi.tools.algorithm.二叉搜索树.Node;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AVLNode<E> extends Node<E> {

    public AVLNode(E element, Node<E> parent) {
        super(element, parent);
    }

    // AVL树平衡二叉树计算高度简单一点，增加一个节点高度的字段
    public int height;
    // 平衡因子
    public int balanceFactor;

    @Override
    public Node<E> createNode(E element, Node<E> parentt) {
        return new AVLNode<>(element, parentt);
    }
}

