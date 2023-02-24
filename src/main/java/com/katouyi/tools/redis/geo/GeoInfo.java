package com.katouyi.tools.redis.geo;

import com.alibaba.fastjson.annotation.JSONType;
import lombok.Data;

@Data
@JSONType(orders = {"name", "longitude", "latitude", "level"})
public class GeoInfo {

    private String name;

    private Double longitude;

    private Double latitude;

    /** 0地区  1直辖市  2省  3市 */
    private Integer level;
}
