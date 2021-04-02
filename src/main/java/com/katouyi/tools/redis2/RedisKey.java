package com.katouyi.tools.redis2;


import org.apache.commons.lang3.StringUtils;

/**
 * 包名：com.taihe.service.cache<br>
 * <p>
 * 功能:redis KEY前缀及过期时间
 * </p>
 *
 * @date:2018-11-23 17:41<br/>
 * @version:1.0 <br/>
 */
public enum RedisKey {
    /**
     * 用户推荐队列(6小时 避免推荐业务出现问题 业务不可用)
     */
    MUSIC_RECOMMEND("user_reco_", 6 * 60 * 60),
    /**
     * 默认用户推荐池(默认用户推荐池,用于新用户以及老用户个性化推荐池使用完的业务场景)
     */
    MUSIC_DEFAULT_RECOMMEND("music_default_recommend_v1_", 3 * 24 * 60 * 60),
    /**
     * 音乐去重服务太多过后做补偿
     */
    MUSIC_COMPENSATION_RECOMMEND("music_compensation_recommend_", 3 * 24 * 60 * 60),
    /**
     * 新默认用户推荐池(默认用户推荐池,用于新用户以及老用户个性化推荐池使用完的业务场景)
     */
    MUSIC_NEW_DEFAULT_RECOMMEND("music_default_recommend_", 3 * 24 * 60 * 60),
    /**
     * 默认用户推荐池(默认用户推荐池,用于新用户以及老用户个性化推荐池使用完的业务场景)
     */
    MUSIC_DEFAULT_V2_RECOMMEND("music_default_recommend_v2_", 3 * 24 * 60 * 60),
    /**
     * 默认用户推荐池(默认用户推荐池,用于新用户以及老用户个性化推荐池使用完的业务场景) max computer
     */
    MUSIC_DEFAULT_V3_RECOMMEND("music_default_recommend_v3_", 3 * 24 * 60 * 60),
    /**
     * 默认用户推荐池(默认用户推荐池,用于新用户以及老用户个性化推荐池使用完的业务场景) max computer
     */
    MUSIC_DEFAULT_V4_RECOMMEND("music_default_recommend_v4_", 3 * 24 * 60 * 60),
    /**
     * 默认用户推荐池(默认用户推荐池,用于新用户以及老用户个性化推荐池使用完的业务场景) max computer
     */
    MUSIC_DEFAULT_V5_RECOMMEND("music_default_recommend_v5_", 3 * 24 * 60 * 60),
    /**
     * 默认用户推荐池(默认用户推荐池,用于新用户以及老用户个性化推荐池使用完的业务场景) max computer
     */
    MUSIC_DEFAULT_V6_RECOMMEND("music_default_recommend_v6_", 3 * 24 * 60 * 60),
    /**
     * 默认用户推荐池 多路召回之前的推荐池 max computer
     */
    MUSIC_DEFAULT_RECOMMEND_FT("music_default_recommend_ft_", 3 * 24 * 60 * 60),
    /**
     * 默认召回池-互动
     */
    MUSIC_DEFAULT_V4_RECOMMEND_HUDONG("music_default_recommend_v4_hudong_", 3 * 24 * 60 * 60),
    /**
     * 默认召回池-点击
     */
    MUSIC_DEFAULT_V4_RECOMMEND_CLICK("music_default_recommend_v4_click_", 3 * 24 * 60 * 60),
    /**
     * 默认召回池-完播
     */
    MUSIC_DEFAULT_V4_RECOMMEND_FINISH("music_default_recommend_v4_finish_", 3 * 24 * 60 * 60),
    /**
     * 视频个性化推荐
     */
    VIDEO_RECOMMEND("video_recommend", 3 * 24 * 60 * 60),

    /**
     * 默认用户推荐池分布式锁
     */
    MUSIC_NEW_DEFAULT_RECOMMEND_LOCK("music_default_recommend_lock", 1 * 60),
    /**
     * 音乐 运营曝光推荐(主要用于类实时下线不合规的歌曲)
     */
    MUSIC_OPERATING_SHOW_RECOMMEND("music_operating_show_recommend", 365 * 24 * 60 * 60),

    /**
     * 天级别实时不分发音乐
     */
    MUSIC_OPERATING_RECOMMEND("music_operating_recommend_", 3 * 24 * 60 * 60),

    /**
     * 音乐默认推荐用户标识
     */
    MUSIC_DEFAULT_USER_RECOMMEND_LABEL("music_default_user_recommend_label", 3 * 24 * 60 * 60),

    /**
     * 默认用户推荐池分布式锁
     */
    MUSIC_DEFAULT_USER_RECOMMEND_LOCK("music_new_user_show_lock", 1 * 60),

    /**
     * 用户推荐已获取对象
     */
    MUSIC_RECOMMEND_HASH_RECORD("user_reco_last_", 2 * 24 * 60 * 60),
    /**
     * 用户推荐已获取对象
     */
    USER_MUSIC_RECOMMEND_RECORD("user_music_recommend_record", 7 * 24 * 60 * 60),

    /**
     * 用户已经曝光数据
     */
    USER_MUSIC_SHOW_RECORD("user_music_show_record", 1 * 24 * 60 * 60 + 60),

    /**
     * 音乐强曝光
     */
    MUSIC_STRONG_LIGHT("music_strong_light", 7 * 24 * 60 * 60),

    /**
     * 用户音乐曝光
     */
    MUSIC_USER_SHOW("music_show_", 7 * 24 * 60 * 60),
    /**
     * 默认用户推荐池(默认用户推荐池,用于新用户以及老用户个性化推荐池使用完的业务场景)
     */
    VIDEO_DEFAULT_RECOMMEND("video_default_recommend", 3 * 24 * 60 * 60),

    /**
     * 视频第三方推荐池
     */
    VIDEO_THREE_SOURCE_RECOMMEND("video_three_source_recommend", 3 * 24 * 60 * 60),
    /**
     * 视频强曝光
     */
    VIDEO_STRONG_LIGHT("video_strong_light", 7 * 24 * 60 * 60),

    /**
     * 用户视频曝光
     */
    VIDEO_USER_SHOW("video_user_show_", 7 * 24 * 60 * 60),

    /**
     * 新用户音乐推荐
     */
    NEW_USER_MUSIC_RECOMMEND("new_user_music_recommend", 24 * 60 * 60),

    /**
     * 歌曲相似度歌曲
     */
    MUSIC_SIMILARITY("music_similarity", 24 * 60 * 60),

    /**
     * 频道音乐缓存
     */
    CHANNEL_MUSIC_CACHE_POOL("channel_cache_pool_", 2 * 24 * 60 * 60),

    /**
     * 频道音乐池锁
     */
    CHANNEL_MUSIC_CACHE_POOL_LOCK("channel_music_cache_pool_lock_", 1 * 60),

    USER_CHANNEL_YYYYMMDD("user_channel", 3 * 24 * 60 * 60),

    AD_CHANNEL("ad_channel:", 180 * 24 * 60 * 60),

    /**
     * 冷启动音乐
     */
    MUSIC_COLD_START("music_cold_start_v1", 3 * 24 * 60 * 60),
    /**
     * 冷启动用户
     */
    MUSIC_COLD_START_USERS("music_cold_start_user_", 3 * 24 * 60 * 60),
    /**
     * 全网最热标签音乐
     */
    MUSIC_INTEL_HOTTEST("music_intel_hottest_v1", 3 * 24 * 60 * 60),
    MUSIC_INTEL_HOTTEST_USERS("music_intel_hottest_user_", 3 * 24 * 60 * 60),

    /**
     * ab 规则缓存
     */
    AB_VERSION_USEFUL("ab_version_useful", 60);
    ;

    private String redisKeyPrefix;
    private int expire;

    /**
     * @param redisKeyPrefix redis KEY 前缀
     * @param expire         过期时间
     */
    RedisKey(String redisKeyPrefix, int expire) {
        this.redisKeyPrefix = redisKeyPrefix;
        this.expire = expire;
    }

    public String getRedisKeyPrefix() {
        return redisKeyPrefix;
    }

    public int getExpire() {
        return expire;
    }

    public String makeRedisKey(Object... details) {
        if (details == null || details.length == 0) {
            return getRedisKeyPrefix();
        }
        StringBuilder builder = new StringBuilder();
        builder.append(getRedisKeyPrefix());
        for (int i = 0; i < details.length; i++) {
            if (details[i] == null) {
                continue;
            }
            if (StringUtils.isBlank(details[i].toString())) {
                continue;
            }
            builder.append(details[i]);
            if (i + 1 != details.length) {
                builder.append("#");
            }
        }
        return builder.toString();
    }
}
