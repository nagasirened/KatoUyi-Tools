package com.katouyi.tools.utils;

import cn.hutool.core.text.StrPool;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Optional;

public class CsvUtils {

    public byte[] generatorCsvBytes(LinkedList<String[]> data, int lineLength) {
        StringBuilder sb = new StringBuilder();
        data.forEach(arr -> {
            for (int i = 0; i < lineLength; i++) {
                if ( i != 0 ) {
                    sb.append(StrPool.COMMA);
                }
                String val = Optional.ofNullable(arr[i]).orElse("").trim();
                if (StringUtils.indexOf(val, StrPool.COMMA) > -1) {
                    sb.append("\"\"").append(val).append("\"\"");
                } else {
                    sb.append(val);
                }
            }
            sb.append(StrPool.CRLF);
        });
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

}
