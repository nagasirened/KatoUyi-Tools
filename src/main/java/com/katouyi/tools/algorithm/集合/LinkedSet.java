package com.katouyi.tools.algorithm.集合;

import java.util.LinkedList;
import java.util.List;

/**
 * 使用链表实现Set集合
 */
public class LinkedSet<E> implements Set<E> {

    private List<E> list = new LinkedList<>();

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean contains(E element) {
        return list.contains(element);
    }

    /**
     * 不能重复
     */
    @Override
    public void add(E element) {
        int index = list.indexOf(element);
        if (index > -1) {
            list.set(index, element);
        } else {
            list.add(element);
        }
    }

    @Override
    public void remove(E element) {
        int index = list.indexOf(element);
        if (index > -1) {
            list.remove(element);
        }
    }

    @Override
    public void traversal(Visitor<E> visitor) {
        if (visitor == null) return;

        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (visitor.visit(list.get(i))) return;
        }
    }
}
