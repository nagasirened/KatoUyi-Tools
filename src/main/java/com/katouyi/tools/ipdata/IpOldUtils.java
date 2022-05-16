package com.katouyi.tools.ipdata;

import cn.hutool.core.io.IoUtil;
import org.apache.commons.lang3.StringUtils;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class IpOldUtils {
    private final Logger logger = LoggerFactory.getLogger(IpOldUtils.class);
    public static final String COUNTRY = "country";
    public static final String PROVINCE = "province";
    public static final String CITY = "city";
    DbSearcher searcher = null;
    public IpOldUtils() {
        try {
            ClassPathResource classPathResource = new ClassPathResource("ipdata/ip2region_old.db");
            DbConfig config = new DbConfig();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            IoUtil.copy(classPathResource.getInputStream(), byteArrayOutputStream);
            searcher = new DbSearcher(config, byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            logger.error("IpOldUtils#init, init ip utils err, errMsg: {}", e.getMessage(), e);
        }
    }

    /**
     * 国家、省、市
     * @param ip 网络ip
     * @return Map
     */
    public Map<String, String> getCPC(String ip) {
        try {
            if (searcher == null) {
                return new HashMap<>();
            }
            DataBlock result = searcher.memorySearch(ip);
            String region = result.getRegion();
            return getDetail(region, ip);
        } catch (Exception ex) {
            logger.warn("IpOldUtils#getCPC, get province and city err, ip: {}, errMsg: {}", ip, ex.getMessage(), ex);
            return new HashMap<>();
        }
    }

    /**
     * 获取省份和城市名称
     * @param region  结果信息
     * @return
     */
    private Map<String, String> getDetail(String region, String ip) {
        HashMap<String, String> resultMap = new HashMap<>();
        if ( StringUtils.isBlank(region)) {
            return resultMap;
        }
        String[] regionArr = region.split("\\|");
        // 中国|0|四川|成都|电信
        if (regionArr.length != 5) {
            logger.info("IpOldUtils#getDetail, ip length is not 5, ip: {}, region: {}", ip, region);
            return resultMap;
        }
        String country = regionArr[0];
        String province = regionArr[2];
        String city = regionArr[3];
        if (org.apache.commons.lang3.StringUtils.isAnyEmpty(country, province, city)) {
            logger.info("IpOldUtils#getDetail, sth address info is null, country: {}, province: {}, city: {}", country, province, city);
        }
        resultMap.put(COUNTRY, country);
        resultMap.put(PROVINCE, province);
        resultMap.put(CITY, city);
        return resultMap;
    }

}
