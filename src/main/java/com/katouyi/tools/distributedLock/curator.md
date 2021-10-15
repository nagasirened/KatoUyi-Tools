Curator4.0+版本对ZooKeeper 3.5.x支持比较好。开始之前，请先将下面的依赖添加进你的项目。

```xml
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-framework</artifactId>
    <version>4.2.0</version>
</dependency>
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-recipes</artifactId>
    <version>4.2.0</version>
</dependency>
```

### 1 连接 ZooKeeper 客户端
通过 `CuratorFrameworkFactory` 创建 `CuratorFramework` 对象，然后再调用 `CuratorFramework` 对象的 `start()` 方法即可！

```java
private static final int BASE_SLEEP_TIME = 1000;
private static final int MAX_RETRIES = 3;

// Retry strategy. Retry 3 times, and will increase the sleep time between retries.
RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
CuratorFramework zkClient = CuratorFrameworkFactory.builder()
    // the server to connect to (can be a server list)
    .connectString("127.0.0.1:2181")
    .retryPolicy(retryPolicy)
    .build();
zkClient.start();
```

对于一些基本参数的说明：
* baseSleepTimeMs：重试之间等待的初始时间
* maxRetries ：最大重试次数
* connectString ：要连接的服务器列表
* retryPolicy ：重试策略

### 2. 数据节点的增删改查

##### a.创建持久化节点
你可以通过下面两种方式创建持久化的节点。

```java
// 注意:下面的代码会报错，下文说了具体原因
zkClient.create().forPath("/node1/00001");
zkClient.create().withMode(CreateMode.PERSISTENT).forPath("/node1/00002");

/**
        PERSISTENT 持久节点
        PERSISTENT_SEQUENTIAL 持久顺序节点
        EPHEMERAL 临时节点
        EPHEMERAL_SEQUENTIAL 临时顺序节点    
*/
```
但是，你运行上面的代码会报错，这是因为的父节点node1还未创建。

你可以先创建父节点 node1 ，然后再执行上面的代码就不会报错了。
```java
zkClient.create().forPath("/node1");
```

更推荐的方式是通过下面这行代码， creatingParentsIfNeeded() 可以保证父节点不存在的时候自动创建父节点，这是非常有用的。
```java
zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/node1/00001");
```
##### b.创建临时节点
```java
zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/node1/00001");
```
##### c.创建节点并指定数据内容
```java
zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/node1/00001","java".getBytes());
zkClient.getData().forPath("/node1/00001");//获取节点的数据内容，获取到的是 byte数组
```
##### d.检测节点是否创建成功
```java
zkClient.checkExists().forPath("/node1/00001");//不为null的话，说明节点创建成功
```
##### e.删除一个子节点
```java
zkClient.delete().forPath("/node1/00001");
```
##### f.删除一个节点以及其下的所有子节点
```java
zkClient.delete().deletingChildrenIfNeeded().forPath("/node1");
```

##### g.获取/更新节点数据内容
```java
zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/node1/00001","java".getBytes());
zkClient.getData().forPath("/node1/00001");//获取节点的数据内容
zkClient.setData().forPath("/node1/00001","c++".getBytes());//更新节点数据内容
```

##### h.获取某个节点的所有子节点路径
```java
List<String> childrenPaths = zkClient.getChildren().forPath("/node1");
```