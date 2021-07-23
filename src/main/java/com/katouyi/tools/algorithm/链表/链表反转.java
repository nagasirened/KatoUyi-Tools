package com.katouyi.tools.algorithm.链表;

public class 链表反转 {

    public static void main(String[] args) {
        ListNode node5 = new ListNode(5, null);
        ListNode node4 = new ListNode(4, node5);
        ListNode node3 = new ListNode(3, node4);
        ListNode node2 = new ListNode(2,  node3);
        ListNode node1 = new ListNode(1, node2);
        ListNode reverse = recursion(node1);
        System.out.println(reverse);
    }

    /**
     * 迭代方式  prev 是上一个节点的值，head之前默认是null
     *          next 是下一个节点的指针所在
     *          curr 代表当前节点
     *
     *         1.先把next节点的指针找到，方便下次遍历
     *         2.当前节点的next就可以往前指了
     *         ----
     *         3.告一段落后，重置指针：将prev指向curr，curr指向next
     */
    public static ListNode reverse(ListNode head) {
        ListNode curr = head;
        ListNode next;
        ListNode prev = null;
        while (curr != null) {
            next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        return prev;
    }

    /**
     * 递归方式   原始  1->2->3->4->5
     * 一直往后找，找到最后两个元素，
     *
     * head = 4   5.next = 4  4.next = null   返回的newHead是 5
     * 再回去到3
     * head = 3  3.next还是4，  因此4.next = 3   3.next = null   返回的还是5
     */
    public static ListNode recursion(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        ListNode newHead = recursion(head.next);
        head.next.next = head;
        head.next = null;
        return newHead;
    }

    static class ListNode {
        int val;
        ListNode next;

        public ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }
}



