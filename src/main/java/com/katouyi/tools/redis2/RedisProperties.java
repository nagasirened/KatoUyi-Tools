package com.katouyi.tools.redis2;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 包名:com.quick.music.data.config<br>
 *
 * <p>功能:
 *
 * @date:2019/9/19 <br>
 * @version:1.0 <br>
 */
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "redis")
@Component
public class RedisProperties {
  private boolean cluster;
  private List<String> hosts;
  private Integer port;
  private String password;

  public boolean isCluster() {
    return cluster;
  }

  public void setCluster(boolean cluster) {
    this.cluster = cluster;
  }

  public List<String> getHosts() {
    return hosts;
  }

  public void setHosts(List<String> hosts) {
    this.hosts = hosts;
  }

  public Integer getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
