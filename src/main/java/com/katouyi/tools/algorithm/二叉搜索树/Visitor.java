package com.katouyi.tools.algorithm.二叉搜索树;

/**
 * Visitor 定义了在遍历的时候要做什么操作，而不是打印
 */
public abstract class Visitor<E> {

    /**
     * 属性记录给自己，递归的时候就可以用来打断遍历了
     */
    boolean stop = false;

    /**
     * 如果返回true,  就代表遍历停止
     * 如果返回false, 就继续遍历
     */
    abstract boolean visit(E element);
}