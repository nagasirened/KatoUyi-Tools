package com.katouyi.tools.redis.geo;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.*;
import redis.clients.jedis.params.GeoRadiusParam;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class GeoTest {

    public static final String GEO_KEY = "geo_test";

    private Jedis getJedis() {
        return new Jedis("localhost", 6379);
    }

    @Test
    public void testGeoAdd() throws Exception {
        List<GeoInfo> geoInfos = infoGeo();
        try (
            Jedis jedis = getJedis();
            Pipeline pipelined = jedis.pipelined()
        ) {
            for (GeoInfo geoInfo : geoInfos) {
                pipelined.geoadd(GEO_KEY, geoInfo.getLongitude(), geoInfo.getLatitude(), geoInfo.getName());
            }
            pipelined.sync();
        }
    }

    private List<GeoInfo> infoGeo() throws IOException {
        URL resource = this.getClass().getClassLoader().getResource("geo.json");
        String content = FileUtils.readFileToString(new File(resource.getFile()), "utf-8");

        /*PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource("geo.json");
        String content = FileUtils.readFileToString(resource.getFile(), "utf-8");*/
        return JSONArray.parseArray(content, GeoInfo.class);
    }

    /**
     * 计算两个地区的距离
     */
    @Test
    public void testGeoDist() {
        String start = "成都";
        String end = "重庆";
        Double length = getJedis().geodist(GEO_KEY, start, end, GeoUnit.KM);
        System.out.println(start + "和" + end + "相距 " + length + " 千米");
    }


    /**
     * 获取到指定位置距离的集合
     */
    @Test
    public void testGeoRadiusMember() {
        GeoRadiusParam param = GeoRadiusParam.geoRadiusParam();
        param.count(5); //最多返回5个
        // param.sortDescending();//降序
        param.sortAscending();//升序
        param.withCoord();//返回经纬度
        param.withDist();//返回距离

        //返回距离成都100千米之内 最近的5座城市
        final List<GeoRadiusResponse> responses = getJedis().georadiusByMember(GEO_KEY, "成都", 200, GeoUnit.KM, param);
        for(GeoRadiusResponse response : responses){
            System.out.println(response.getCoordinate());       // (104.0679207444191,30.679941735690853)
            System.out.println(response.getMemberByString());   // memberName
            System.out.println(response.getDistance());         // 距离
        }

        // 如果定位没有member，可以直接使用坐标
        // List<GeoRadiusResponse> resList = getJedis().georadius(GEO_KEY, 104.0679207444191, 30.679941735690853, 200, GeoUnit.KM, param);
    }

    /**
     * 删除某个member
     */
    @Test
    public void geoDel() {
        getJedis().zrem(GEO_KEY, "memberName");
    }

    /**
     * 获取某个城市的坐标
     */
    @Test
    public void testGeoPos() {
        // geopos(String key, String... members)
        List<GeoCoordinate> posList = getJedis().geopos(GEO_KEY, "北京");
        for (GeoCoordinate geoCoordinate : posList) {
            System.out.println(geoCoordinate);
        }
    }

    /**
     * 获取某个城市的hash值，也就是zSet的score
     */
    @Test
    public void testGeoHash() {
        List<String> hashList = getJedis().geohash(GEO_KEY, "北京");
        for (String hash : hashList) {
            System.out.println(hash);
        }
    }
}
