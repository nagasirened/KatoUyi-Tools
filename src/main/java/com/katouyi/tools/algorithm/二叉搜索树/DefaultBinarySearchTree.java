package com.katouyi.tools.algorithm.二叉搜索树;

import com.katouyi.tools.algorithm.User;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

/**
 *             7
 *         4      9
 *       3   5      11
 * 特点1. 节点左子树下面所有的值都比自己的值小
 * 特点2. 节点数值必须能相互比较
 * 特点3. 节点值不允许为null
 */
public class DefaultBinarySearchTree<E> implements BinarySearchTree<E> {

    private int size;
    private Node<E> root;
    private Comparator<E> comparator;

    public DefaultBinarySearchTree() {
        this(null);
    }

    // 如果传递了比较器，就是用这个比较器来比较element的值，否则就按照默认的排序规则
    public DefaultBinarySearchTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        root = null;
    }

    @Override
    public void add(E element) {
        checkElementNotNull(element);
        if (root == null) {
            root = new Node<>(element, null);
            size++;
            return;
        }

        // 添加的不是根节点，首先找到它的父节点
        Node<E> node = root;            // 从跟节点开始
        Node<E> parentNode = root;      // 暂存父节点，后面要简历节点的
        int cmp = 0;
        while (node != null) {
            parentNode = node;
            cmp = compare(element, node.element);
            if (cmp > 0) {
                node = node.right;
            } else if (cmp < 0) {
                node = node.left;
            } else {
                node.element = element;  // 一般使用新值覆盖旧值
                return;
            }
        }
        // 最后一次的比较结果需要保存起来，不然不知道放在左边还是右边
        Node<E> newNode = new Node<>(element, parentNode);
        if (cmp > 0) {
            parentNode.right = newNode;
        } else {
            parentNode.left = newNode;
        }
        size++;
    }

    /**
     * 1.删除叶子节点，直接 node.parent.left = null 或  node.parent.right = null
     * 2.删除度为1的节点，找到它的child，node.parent.left = child 或者 node.parent.right = child
     * 3.删除度为2的节点，需要在它的左子树找到前驱节点或者右子树的后继节点(大小最接近的)N，把哪个节点N的值直接设置到当前节点，然后删除N节点就行
     */
    @Override
    public void remove(E element) {
        remove(node(element));
    }
    // 先根据数值查找Node
    private Node<E> node(E element) {
        Node<E> node = root;
        while (node != null) {
            int cmp = compare(element, node.element);
            if (cmp > 0) {
                node = node.right;
            } else if (cmp < 0) {
                node = node.left;
            } else {
                return node;
            }
        }
        return null;
    }

    public void remove(Node<E> node) {
        if (node == null) return;
        // node != null 说明一定能删除
        size--;
        // 度为2的节点
        if (node.left != null && node.right != null) {
            Node<E> predecessor = predecessor(node);
        }
    }

    /**
     * 获取前驱节点 如果Node.left != null，那么它子树中一定存在前驱节点。且前驱节点存在于它的左子树的最右边
     * 如果node.left == null, 它的左子树为null, 那么它的前驱节点需要从它的父节点中找。即node.parent.right = node 依此循环找到它的前驱节点
     */
    private Node<E> predecessor(Node<E> node) {
        // node.left.right.right.right...
        if (node.left != null) {
            Node<E> predecessor = node.left;
            while (predecessor.right != null) {
                predecessor = predecessor.right;
            }
            return predecessor;
        }

        // node.parent.parent.parent...  截止条件是node在它的父节点的右子树中
        while (node.parent != null && node.parent.left == node) {
            node = node.parent;
        }
        // 推出循环的条件是 node.parent == null(代表没有前驱，返回null, 也就是node.parent)
        // 或者  node.parent.right == node  那么node.parent就是前驱节点
        return node.parent ;
    }

    /**
     * 获取后继节点
     */
    private Node<E> successor(Node<E> node) {
        if (node.right != null) {
            Node<E> successor = node.right;
            while (successor.left != null) {
                successor = successor.left;
            }
            return successor;
        }

        while (node.parent != null && node.parent.right == node) {
            node = node.parent;
        }
        return node.parent;
    }


    @Override
    public boolean contains(E element) {
        return false;
    }

    /**
     * 节点之间的值的比较，返回值>0则e1>e2
     * 如果有比较器，则根据比较器比较值
     * 如果没有比较器，那么认为E一定可比较.如果强转不了，那么数据就有问题才是
     */
    private int compare(E e1, E e2) {
        if (comparator != null) {
            return comparator.compare(e1, e2);
        }
        return ((Comparable<E>)e1).compareTo(e2);
    }

    private void checkElementNotNull(E element) {
        if (Objects.isNull(element)) {
            throw new IllegalArgumentException("element cant be null");
        }
    }


    /**
     * ==================================================================================================
     * 遍历        7
     *       4          9
     *    2     5     8    11
     *  1   3            10   12
     * 前序遍历  根节点，前序遍历左子树，前序遍历右子树    7 4 2 1 3 5 9 8 11 10 12  先出现根节点，然后出现它所有的左子树，最后出现它所有的右子树节点
     * 中序遍历  前序遍历左子树，根节点，前序遍历右子树    1 2 3 4 5 7 8 9 10 11 12  能看出来是递增的规律或者完全递减的规律
     * 后序遍历  前序遍历左子树，前序遍历右子树，根节点    1 3 2 5 4 8 10 12 11 9 7  适用一些先父后子的操作
     * 层级遍历                                     7 4 9 2 5 8 11 1 3 10 12  一层一层地遍历，判断树的高度等
     * ==================================================================================================
     */

    public void preOrderTree(Visitor visitor) {
        preOrderTree(root, visitor);
    }

    private void preOrderTree(Node<E> node, Visitor visitor) {
        if (node == null || visitor == null || visitor.stop) {
            return;
        }
        visitor.stop = visitor.visit(node.element);
        preOrderTree(node.left, visitor);
        preOrderTree(node.right, visitor);
    }

    /**
     * 后序遍历: 两个地方判断visitor.stop 是因为第一个是阻止往下继续遍历  第二个是防止遍历左右的时候有一个ture了，就不需要处理当前node了
     */
    public void sufOrderTree(Visitor visitor) {
        sufOrderTree(root, visitor);
    }

    private void sufOrderTree(Node<E> node, Visitor visitor) {
        if (node == null || visitor == null || visitor.stop) {
            return;
        }
        sufOrderTree(node.left, visitor);
        sufOrderTree(node.right, visitor);
        if (visitor.stop) return;
        visitor.stop = visitor.visit(node.element);
    }

    /**
     * 中序遍历
     */
    public void midOrderTree(Visitor visitor) {
        midOrderTree(root, visitor);
    }

    private void midOrderTree(Node<E> node, Visitor visitor) {
        if (node == null || visitor == null || visitor.stop) {
            return;
        }
        midOrderTree(node.left, visitor);
        if (visitor.stop) return;
        visitor.stop = visitor.visit(node.element);
        midOrderTree(node.right, visitor);
    }

    /**
     * 层序遍历，使用一个Queue辅助
     * 默认根节点入队：  出队一个，将它的子节点入队，继续出队在入队子节点
     */
    public void rankOrderTree(Visitor visitor) {
        if (root == null || visitor == null || visitor.stop) return;
        Queue<Node<E>> queue = new LinkedList<>();
        queue.offer(root);  // 入队
        while (!queue.isEmpty()) {
            Node<E> node = queue.poll();
            // 一旦返回true，就停止遍历
            if (visitor.visit(node.element)) {
                visitor.stop = true;
                return;
            }
            if (node.left != null) {
                queue.offer(node.left);
            }
            if (node.right != null) {
                queue.offer(node.right);
            }
        }
    }

    /**
     * ==================================================================================================
     *      遍历结束
     * ==================================================================================================
     */

    /**
     * 判断是否是完全二叉树 : 使用层级遍历
     * 使用层级遍历的方式判断：1.如果左右都有，就加入到队列
     *                    2.如果右有左没有，判false
     *                    3.如果左右都没有或者左有右没有，那么它后面的节点必须没有子节点
     */
    public boolean isComplete() {
        if (root == null) return false;
        Queue<Node<E>> queue = new LinkedList<>();
        queue.offer(root);

        boolean leaf = false;  // 如果leaf == ture的时候，剩下的节点不能有子节点
        while (!queue.isEmpty()) {
            Node<E> node = queue.poll();
            if (leaf && (node.left != null || node.right != null)) {
                return false;
            }

            if (node.left != null && node.right != null) {
                queue.offer(node.left);
                queue.offer(node.right);
            } else if (node.left == null && node.right != null) {
                return false;
            } else {            // (node.left != null && node.right == null)  或  (node.left == null && node.right == null)
                if (node.left != null) {
                    queue.offer(node.left);
                }
                leaf = true;
            }
        }
        return true;
    }

    public static class Node<E> {
        private E element;
        private Node<E> parent;
        private Node<E> left;
        private Node<E> right;

        // 构造节点的时候是有父节点的，因此传入parent
        public Node(E element, Node<E> parent) {
            this.element = element;
            this.parent  = parent;
        }
    }

    /**
     * 前序遍历可以遍历树    利用前序遍历打印二叉搜索树
     *                    4
     *                    L  2
     *                    L  L  1
     *                    L  R  3
     *                    R  6
     *                    R  L  5
     *                    R  R  7
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toString(root, sb, "");
        return sb.toString();
    }

    // 弄个前缀整成树结构
    private void toString(Node<E> node, StringBuilder sb, String prefix) {
        if (node == null) return;
        sb.append(prefix).append(node.element).append("\n");
        toString(node.left, sb, prefix + "[L]");
        toString(node.right, sb, prefix + "[R]");
    }

    /**
     * 计算二叉树的高度: 递归的方式，就是根节点到最远的叶子节点的距离
     */
    public int height() {
        return height(root);
    }

    // 计算某一个节点的高度: 等于它的左右子节点中高度最高的节点的高度+1
    public int height(Node<E> node) {
        if (node == null) return 0;
        return 1 + Math.max(height(node.left), height(node.right));
    }

    /** 非递归的形式，层序遍历获取 */
    public int height2 (){
        if (root == null) return 0;
        Queue<Node<E>> queue = new LinkedList<>();
        int height = 1;
        queue.offer(root);
        int levelSize = queue.size();      // 存储每一层有多少个元素

        while (!queue.isEmpty()) {
            Node<E> node = queue.poll();
            if (node.left != null) {
                queue.offer(node.left);
            }
            if (node.right != null) {
                queue.offer(node.right);
            }
            // 意味着即将要访问下一层
            if (--levelSize == 0) {
                height++;
                levelSize = queue.size();
            }
        }
        return height - 1;
    }


    /**
     * ==================================================================================================
     * 测试
     * ==================================================================================================
     */
    public static void main(String[] args) {
        Comparator<User> comparator = (user1, user2) -> user1.getAge() - user2.getAge();
        DefaultBinarySearchTree<User> userBinarySearchTree = new DefaultBinarySearchTree<>();

        DefaultBinarySearchTree<Integer> intTree = new DefaultBinarySearchTree<>();
        // Integer[] data = new Integer[]{7,4,9,2,1,3,5,9,8,11,10,12};
        Integer[] data = new Integer[]{7,4,9,2,1};
        for (Integer in : data) {
            intTree.add(in);
        }
        Node<Integer> predecessor = intTree.predecessor(intTree.root);
        System.out.println("跟节点的前驱节点是：" + predecessor.element);

        Node<Integer> successor = intTree.successor(intTree.root);
        System.out.println("跟节点的后继节点是：" + successor.element);

        intTree.rankOrderTree(new Visitor<Integer>() {
            @Override
            public boolean visit(Integer element) {
                System.out.print(element + " ");
                if (element == 1) {
                    return true;
                }
                return false;
            }
        });
        System.out.println();
        System.out.println("height:" + intTree.height());
        System.out.println("isComplete:" + intTree.isComplete());
    }

}


