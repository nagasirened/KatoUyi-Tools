package com.katouyi.tools.mongo.mall;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;

public enum MongoType {

    EQUALS {
        @Override
        public Criteria buildCriteria(MongoField annotationField, Field field, Object value) {
            if (checkField(annotationField, field, value)) {
                return new Criteria();
            }
            String fieldName = getQueryFieldName(annotationField, field);
            return Criteria.where(fieldName).is(Convert.toStr(value));
        }
    },

    LIKE {
        @Override
        public Criteria buildCriteria(MongoField annotationField, Field field, Object value) {
            if (checkField(annotationField, field, value)) {
                return new Criteria();
            }
            String fieldName = getQueryFieldName(annotationField, field);
            return Criteria.where(fieldName).regex(Convert.toStr(value));
        }
    },

    IN {
        @Override
        public Criteria buildCriteria(MongoField annotationField, Field field, Object value) {
            if (checkField(annotationField, field, value)) {
                return new Criteria();
            }
            String fieldName = getQueryFieldName(annotationField, field);
            return Criteria.where(fieldName).in(Convert.toStr(value));
        }
    },
    ;

    /**
     * 枚举需要实现的方法
     */
    public abstract Criteria buildCriteria(MongoField annotationField, Field field, Object value);

    /**
     * 参数校验
     */
    public boolean checkField(MongoField annotationField, Field field, Object value) {
        return ObjectUtil.hasNull(annotationField, field, value);
    }

    /**
     *
     */
    private static String getQueryFieldName(MongoField annotationField, Field field) {
        String param = annotationField.param();
        if (!StringUtils.hasText(param)) {
            param = field.getName();
        }
        return param;
    }
}
