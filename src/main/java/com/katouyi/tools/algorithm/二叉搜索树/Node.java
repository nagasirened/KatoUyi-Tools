package com.katouyi.tools.algorithm.二叉搜索树;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Node<E> {
    public E element;
    public Node<E> parent;
    public Node<E> left;
    public Node<E> right;
    public Node(E element, Node<E> parent) {
        this.element = element;
        this.parent  = parent;
    }

    public Node<E> createNode(E element, Node<E> parentt) {
        return new Node<>(element, parentt);
    }
}
