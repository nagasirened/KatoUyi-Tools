# KatoUyi-Tools
工具代码整理，方便直接使用；

### Redis

* 拓展key的前缀，仅需继承`BasePrefix`抽象类，用法等同于`StaticPrefix`
* 工具类为`RedisAuxiliary`，目前仅含常用的 string 方式的方法，被存储对象记得实现序列化接口
* 包含案例将工具类注册为了Bean，结合Spring框架使用

### Mybatis-Plus

* 提供了代码自动生成、分页、自动插入功能的示例

### FastDFS

* 附docker 安装命令，可快速使用
* 提供基本的上传、下载、删除的接口

### ElasticSearch

ES的客户端依赖使用的是Hign-Level-Client

* `HignLevelDocumentHandler` 是自定义的一个基础功能接口
* `DefaultHignLevelDocumentHandler` 是一个默认的实现类，可以根据此类拓展不同的"索引"文件库的Bean对象。自定义Bean的例子见`ElasticsearchConfig`, 可定义多个`Client`
* `ElasticSearchClientContainer` 是一个将所有的`Client`包装起来的容器，可以用这个容器，传入“indexName” 索引名称，获取到想要的`Client`
* 索引文档操作的例子见`DocumentController`
