package com.katouyi.tools.algorithm.二叉搜索树;

public interface BinarySearchTree<E> {

    int size();

    boolean isEmpty();

    void clear();

    void add(E element);

    void remove(E element);

    boolean contains(E element);

}
