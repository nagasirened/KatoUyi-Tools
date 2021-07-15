package com.katouyi.tools.metrics.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomFastJsonHttpMessageConverter extends FastJsonHttpMessageConverter {
    Logger logger = LoggerFactory.getLogger(CustomFastJsonHttpMessageConverter.class);
    public static final Charset UTF8 = StandardCharsets.UTF_8;

    private static final ParserConfig config = ParserConfig.getGlobalInstance();

    public CustomFastJsonHttpMessageConverter() {
        this.getFastJsonConfig().setSerializerFeatures(SerializerFeature.QuoteFieldNames, SerializerFeature.BrowserCompatible, SerializerFeature.WriteDateUseDateFormat);
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(new MediaType("application", "json", UTF8));
        this.setSupportedMediaTypes(supportedMediaTypes);
    }

    public Object read(Type type, Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        InputStream in = inputMessage.getBody();
        String data = this.readInputStream(in);
        Object object = JSON.parseObject(data, type, config, JSON.DEFAULT_PARSER_FEATURE);
        return object;
    }

    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        InputStream in = inputMessage.getBody();
        String data = this.readInputStream(in);
        return JSON.parseObject(data, clazz, config, JSON.DEFAULT_PARSER_FEATURE);
    }

    protected String readInputStream(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder builder = new StringBuilder();
        String line;

        while((line = reader.readLine()) != null) {
            builder.append(line + "\n");
        }

        is.close();
        return builder.toString().trim();
    }

    static {
        config.putDeserializer(Date.class, new CustomFastJsonDateFormatDeserializer());
    }
}