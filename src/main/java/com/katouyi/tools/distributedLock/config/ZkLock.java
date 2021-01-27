package com.katouyi.tools.distributedLock.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * author: ZGF
 * 12-2020/12/8 : 10:50
 * context :
 */

public class ZkLock implements AutoCloseable, Watcher {

    private ZooKeeper zooKeeper;

    private String znode;

    public ZkLock() throws IOException {
        this.zooKeeper = new ZooKeeper("101.132.123.185:2181", 10000, this);
    }

    /**
     * 获取锁
     * @param businessCode  不同的业务使用不同的businessCode
     * @return
     */
    public boolean getLock (String businessCode) {
        String nodePath = "/" + businessCode;
        try {
            Stat stat = zooKeeper.exists(nodePath, false);
            if (Objects.isNull(stat)) {
                // 1路径  2值，这里随便这是一个  3权限，OPEN代表不需要认证  4.创建模式：应该是瞬时顺序节点(PRE是持久节点)
                /** 创建业务根节点 */
                zooKeeper.create("/" + businessCode, businessCode.getBytes(),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }

            /** 创建瞬时-有序节点 */
            znode = zooKeeper.create(nodePath + nodePath + "_", businessCode.getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

            List<String> childrenNodes = zooKeeper.getChildren(nodePath, false);
            Collections.sort(childrenNodes);
            String firstNode = childrenNodes.get(0);
            /** 如果是最小的第一个元素，就可以获取到锁 */
            if (StringUtils.endsWith(znode, firstNode)) {
                System.out.println("成功获取到锁1");
                return true;
            }
            /** 不是第一个子节点，则监听前一个节点  lastNode表示上一个节点---第二个节点进来会监听这个 */
            String lastNode = firstNode;
            for (String node : childrenNodes) {
                /** 如果到最后一个了，那么就监听前一个：lastNode保持是前一个节点 */
                if (StringUtils.endsWith(znode, node)) {
                    zooKeeper.exists(nodePath + "/" + lastNode, true);
                    break;
                } else {
                    lastNode = node;
                }
            }
            synchronized (this) {
                wait();
            }

            System.out.println("成功获取到锁2");
            return true;
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 自动关闭接口
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        System.out.println("释放锁");
        zooKeeper.delete(znode, -1);
        zooKeeper.close();
    }

    /**
     * 监听器
     * @param watchedEvent
     */
    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getType() == Event.EventType.NodeDeleted) {
            synchronized (this) {
                notify();
            }
        }
    }
}
