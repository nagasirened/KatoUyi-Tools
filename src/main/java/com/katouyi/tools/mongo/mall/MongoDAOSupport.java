package com.katouyi.tools.mongo.mall;

import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

@Slf4j
public abstract class MongoDAOSupport<T> implements MongoDAO<T> {

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public T save(T t) {
        mongoTemplate.save(t);
        return t;
    }

    @Override
    public boolean removeById(T t) {
        DeleteResult deleteResult = mongoTemplate.remove(t);
        return deleteResult.getDeletedCount() > 0;
    }

    @Override
    public boolean removeByCondition(T t) {
        Query query = buildBaseQuery(t);
        DeleteResult deleteResult = mongoTemplate.remove(query);
        return deleteResult.getDeletedCount() > 0;
    }

    @Override
    public void updateById(String id, T t) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        /**
         * todo
         */
    }

    @Override
    public List<T> findByCondition(T t) {
        return null;
    }

    @Override
    public List<T> find(Query query) {
        return null;
    }

    @Override
    public T findOne(Query query) {
        return null;
    }

    @Override
    public void update(Query query, Update update) {

    }

    @Override
    public T findById(String id) {
        return null;
    }

    @Override
    public T findById(String id, String collectionName) {
        return null;
    }

    @Override
    public Page<T> findPage(Page<T> page, Query query) {
        return null;
    }

    @Override
    public long count(Query query) {
        return 0;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return null;
    }


    /**
     * 组装基础的Query查询条件
     */
    private Query buildBaseQuery(T t) {
        Query query = new Query();
        Field[] fields = t.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                Object value = field.get(t);
                if (Objects.isNull(value)) {
                    continue;
                }
                field.setAccessible(true);
                MongoField mongoField = field.getAnnotation(MongoField.class);
                if (Objects.nonNull(mongoField)) {
                    Criteria criteria = mongoField.type().buildCriteria(mongoField, field, value);
                    query.addCriteria(criteria);
                }
            }
        } catch (Exception e) {
            log.error("MongoDAOSupport#buildBaseQuery, parse MongoField error, msg: {}", e.getMessage(), e);
        }
        return query;
    }
}
