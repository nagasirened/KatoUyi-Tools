### 1 导入依赖
```aidl
<!-- fastdfs客户端，application.yml有配置信息 -->
<dependency>
    <groupId>com.github.tobato</groupId>
    <artifactId>fastdfs-client</artifactId>
    <version>${fastdfs.client.version}</version>
</dependency>
```
### 2、配置
```yaml
# fastDFS
fdfs:
  connect-timeout: 300  # 连接超时时间
  so-timeout: 300       # 读取超时时间
  tracker-list: 101.132.123.185:22122
```

### 3、导入工具类 FastDFSUtils

### 4、docker 安装 FastDFS 

```shell
# 下载fastdfs镜像
docker search fastdfs
# 下载镜像
docker pull delron/fastdfs

# 查看镜像
docker images|grep fastdfs

# 启动tracker
docker run -it -d --name trakcer -v ~/tracker_data:/fastdfs/tracker/data --net=host season/fastdfs tracker

# Run as a storage  改一下IP
docker run -it --name storage -v ~/storage_data:/fastdfs/storage/data -v storage-fastdfs:/fastdfs/store_path --net=host -e TRACKER_SERVER:【自己服务器的IP】:22122 season/fastdfs storage

# 进入 tracker 修改文件
docker exec -it tracker bash
vi /etc/fdfs/client.conf
将配置 tracker_server=你自己的ip:22122
```

