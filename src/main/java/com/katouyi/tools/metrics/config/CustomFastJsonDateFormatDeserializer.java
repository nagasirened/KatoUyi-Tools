package com.katouyi.tools.metrics.config;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.AbstractDateDeserializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CustomFastJsonDateFormatDeserializer extends AbstractDateDeserializer {
    public CustomFastJsonDateFormatDeserializer() {
    }

    protected <T> T cast(DefaultJSONParser parser, Type clazz, Object fieldName, Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Calendar) {
            return (T) ((Calendar)value).getTime();
        } else if (value instanceof Date) {
            return (T) value;
        } else if (value instanceof Number) {
            long longValue = ((Number)value).longValue();
            return (T) new Date(longValue);
        } else if (!(value instanceof String)) {
            throw new JSONException("can not cast to Date, value : " + value);
        } else {
            String strVal = (String)value;
            String format;
            if (strVal.length() == 10) {
                format = "yyyy-MM-dd";
            } else if (strVal.length() == "yyyy-MM-dd HH:mm".length()) {
                format = "yyyy-MM-dd HH:mm";
            } else if (strVal.length() == "yyyy-MM-dd HH:mm:ss".length()) {
                format = "yyyy-MM-dd HH:mm:ss";
            } else {
                format = "yyyy-MM-dd HH:mm:ss.SSS";
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat(format);

            try {
                return (T) dateFormat.parse(strVal);
            } catch (ParseException var9) {
                throw new JSONException("can not cast to Date, value : " + strVal);
            }
        }
    }

    public int getFastMatchToken() {
        return 2;
    }
}