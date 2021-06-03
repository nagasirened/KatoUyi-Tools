package com.katouyi.tools.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.SimplifiedObjectMeta;
import com.csvreader.CsvReader;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.micrometer.core.annotation.Timed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Component
public class OssCacheRepository {
    final Logger logger = LogManager.getLogger(OssCacheRepository.class);

    @Value("${oss.endPoint}")
    private String endPoint;

    @Value("${oss.accessKeyId}")
    private String accessKeyId;

    @Value("${oss.accessKeySecret}")
    private String accessKeySecret;

    @Value("${oss.bucketName}")
    private String bucket;

    // music cache 更新时间
    private volatile long lastMusicDailyModified = 0;
    private volatile long lastMusicFullModified = 0;


    /**
     * 缓存过期时间
     */
    private final int CACHE_EXPIRE_HOURS = 36;

    /**
     * 缓存音乐条数
     */
    private final long CACHE_MUSIC_LIMIT = 4000000;

    /**
     * 新版音乐feature缓存
     */
    Cache<String, String> musicCacheV1 = CacheBuilder
            .newBuilder()
            .initialCapacity(600000)
            .maximumSize(CACHE_MUSIC_LIMIT)
            .expireAfterWrite(CACHE_EXPIRE_HOURS, TimeUnit.HOURS)
            .build();

    /**
     * 全量音乐特征
     */
    Cache<String, String> musicCacheFull = CacheBuilder
            .newBuilder()
            .initialCapacity(2000000)
            .maximumSize(CACHE_MUSIC_LIMIT)
            .expireAfterWrite(CACHE_EXPIRE_HOURS, TimeUnit.HOURS)
            .build();


    /**
     * 批量获取音乐code
     * @param musicCodes 音乐code
     * @return Map
     */
    @Timed(histogram = true, percentiles = {0.5, 0.90, 0.99})
    public Map<String, String> getMusicFeatureV1(List<String> musicCodes) {
        return musicCacheV1.getAllPresent(musicCodes);
    }

    /**
     * 批量获取音乐code
     * @param musicCodes 音乐code
     * @return Map
     */
    @Timed(histogram = true, percentiles = {0.5, 0.90, 0.99})
    public Map<String, String> getMusicFeatureFull(List<String> musicCodes) {
        return musicCacheFull.getAllPresent(musicCodes);
    }

    /**
     * 获取音乐code
     */
    public String getMusicFeatureFull(String musicCode) {
        return musicCacheFull.getIfPresent(musicCode);
    }


    /**
     * 加载文件
     *
     * @param fileName String
     * @return boolean
     */
    public boolean loadFeatureFile(String fileName, int type) {
        // 判断是否为有效的feature
        if (type != 1 && type != 2) {
            return false;
        }
        OSS ossClient = null;
        CsvReader csvReader = null;
        OSSObject ossObject = null;
        InputStreamReader inputStreamReader = null;
        try {
            ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);
            if (!ossClient.doesObjectExist(bucket, fileName)) {
                logger.info("load feature file, file not exist: bucket: {}, fileName: {}", bucket, fileName);
                return false;
            }

            // 检查文件是否修改
            SimplifiedObjectMeta metadata = ossClient.getSimplifiedObjectMeta(bucket, fileName);
            long ts = metadata.getLastModified().getTime();
            if (type == 1 && ts <= lastMusicDailyModified) {
                logger.info("load feature file, file:{} not modified,lastMusicDailyModified:{},ts:{}",
                        fileName, lastMusicDailyModified, ts);
                return true;
            } else if (type == 2 && ts <= lastMusicFullModified) {
                logger.info("load feature file, file:{} not modified,lastMusicFullModified:{},ts:{}",
                        fileName, lastMusicFullModified, ts);
                return true;
            }

            ossObject = ossClient.getObject(bucket, fileName);
            inputStreamReader = new InputStreamReader(ossObject.getObjectContent());
            csvReader = new CsvReader(inputStreamReader);
            String[] featureData;
            setSwitchConfig(type,false);
            while (csvReader.readRecord()) {
                featureData = csvReader.getValues();
                if (featureData.length == 2) {
                    switch (type) {
                        case 1:
                            musicCacheV1.put(featureData[0], featureData[1]);
                            break;
                        case 2:
                            musicCacheFull.put(featureData[0], featureData[1]);
                            break;
                        default:
                    }
                }
            }

            // 更新更新时间
            if (type == 1) {
                lastMusicDailyModified = ts;
                logger.info("load feature file, daily music feature cache cnt:{}, file:{},lastMusicDailyModified:{}",
                        musicCacheV1.size(), fileName, lastMusicDailyModified);
            } else if (type == 2) {
                lastMusicFullModified = ts;
                logger.info("load feature file, full music feature cache cnt:{}, file:{} ,lastMusicFullModified:{}",
                        musicCacheFull.size(), fileName, lastMusicFullModified);
            }
            return true;
        } catch (Exception ex) {
            logger.error("load feature file, file:{} load error,type:{},lastMusicDailyModified:{},lastMusicFullModified:{}",
                    fileName, type, lastMusicDailyModified, lastMusicFullModified, ex);
        } finally {
            setSwitchConfig(type,true);
            logger.info("load feature file, start close reader, file:{}", fileName);
            // 初始化成功，则关闭
            try {
                if (csvReader != null) {
                    csvReader.close();
                }
            } catch (Exception ex) {
                logger.error("load feature file, csv reader close err, file:{}, msg:{}", fileName, ex.getMessage(), ex);
            }
            try {
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
            } catch (Exception ex) {
                logger.error("load feature file, input stream reader close err, file:{}, msg:{}", fileName, ex.getMessage(), ex);
            }

            try {
                if (ossObject != null) {
                    ossObject.close();
                }
            } catch (Exception ex) {
                logger.error("load feature file, oss reader close err, file:{}, msg:{}", fileName, ex.getMessage(), ex);
            }

            try {
                if (ossClient != null) {
                    ossClient.shutdown();
                }
            } catch (Exception ex) {
                logger.error("load feature file, oss client close err, file:{}, msg:{}", fileName, ex.getMessage(), ex);
            }
            logger.info("load feature file, finish close reader, file:{}", fileName);
        }
        logger.info("load feature file, file loaded success, fileName: {}, musicCacheFullCnt: {}, musicCacheV1Cnt: {}",
                fileName, musicCacheFull.size(), musicCacheV1.size());
        return false;
    }

    private void setSwitchConfig(int type, boolean isOK) {
        if (type == 1) {
            //...doSthing
        } else if (type == 2) {
            //...doSthing
        }
        logger.info("load feature file, setSwitchConfig type:{}, isOK:{}", type, isOK);
    }

}
