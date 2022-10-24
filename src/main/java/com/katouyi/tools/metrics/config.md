
文档：http://micrometer.io/docs


###1、Micrometer 介绍
Micrometer 为 Java 平台上的性能数据收集提供了一个通用的 API，它提供了多种度量指标类型（Timers、Guauges、Counters等），同时支持接入不同的监控系统，例如 Influxdb、Graphite、Prometheus 等。我们可以通过 Micrometer 收集 Java 性能数据，配合 Prometheus 监控系统实时获取数据，并最终在 Grafana 上展示出来，从而很容易实现应用的监控。

Micrometer 中有两个最核心的概念，分别是计量器（Meter）和计量器注册表（MeterRegistry）。计量器用来收集不同类型的性能指标信息，Micrometer 提供了如下几种不同类型的计量器：

1. 计数器（Counter）: 表示收集的数据是按照某个趋势（增加／减少）一直变化的，也是最常用的一种计量器，例如接口请求总数、请求错误总数、队列数量变化等。
2. 计量仪（Gauge）: 表示搜集的瞬时的数据，可以任意变化的，例如常用的 CPU Load、Mem 使用量、Network 使用量、实时在线人数统计等，
3. 计时器（Timer）: 用来记录事件的持续时间，这个用的比较少。
4. 分布概要（Distribution summary）: 用来记录事件的分布情况，表示一段时间范围内对数据进行采样，可以用于统计网络请求平均延迟、请求延迟占比等。


###2、Spring Boot 工程集成 Micrometer
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```
这里引入了 io.micrometer 的 micrometer-registry-prometheus 依赖以及 spring-boot-starter-actuator 依赖，因为该包对 Prometheus 进行了封装，可以很方便的集成到 Spring Boot 工程中。

其次，在application.yml 中配置如下：
```yaml

      
spring:
  application:
    name: 项目名字（标识哪个项目的指标）
```

增加httpMessage转换(需要支持  MediaType.TEXT_PLAIN) )
```java
@Override
protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(new CustomFastJsonHttpMessageConverter());
    StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
    stringHttpMessageConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.TEXT_PLAIN));
    converters.add(stringHttpMessageConverter);
}
```


如果要使用@Timed注解统计方法耗时需pom引入
```xml
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
    <version>1.9.6</version>
</dependency>
```
并配置
```java
@Configuration
@EnableAspectJAutoProxy
public class AutoTimingConfiguration {
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}
```

最后，启动服务，浏览器访问 http://127.0.0.1:8080/metrics/prometheus

就可以看到应用的 一系列不同类型 metrics 信息，例如 http_server_requests_seconds summary、jvm_memory_used_bytes gauge、jvm_gc_memory_promoted_bytes_total counter 等等（如果本地能看到各种指标说明集成成功了）。

然后要上线需要在K8s配置文件加入如下配置（找运维添加 说加一下采集普罗米修斯指标）：
```
podAnnotations:
  prometheus.io/port: "8080"
  prometheus.io/scrape: "true"
  prometheus.io/path: /metrics/prometheus
```