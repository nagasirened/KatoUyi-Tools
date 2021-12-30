package com.katouyi.tools.mongo.mall;

import cn.hutool.core.convert.Convert;
import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Objects;

@Slf4j
public abstract class MongoDAOSupport<T> implements MongoDAO<T> {

    public static final String ID = "_id";

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
        query.addCriteria(Criteria.where(ID).is(id));
        Update update = buildBaseUpdate(t);
        update(query, update);
    }

    @Override
    public List<T> findByCondition(T t) {
        Query query = buildBaseQuery(t);
        return find(query);
    }

    @Override
    public List<T> find(Query query) {
        return mongoTemplate.find(query, getActualClass());
    }

    @Override
    public T findOne(Query query) {
        return mongoTemplate.findOne(query, getActualClass());
    }

    @Override
    public void update(Query query, Update update) {
        mongoTemplate.updateMulti(query, update, getActualClass());
    }

    @Override
    public T findById(String id) {
        return mongoTemplate.findById(id, getActualClass());
    }

    @Override
    public T findById(String id, String collectionName) {
        return mongoTemplate.findById(id, getActualClass(), collectionName);
    }

    @Override
    public Page<T> findPage(Page<T> page, Query query) {
        int count = Convert.toInt(count(query));
        page.setTotalCount(count);
        int pageNo = page.getCurrentPage();
        int pageSize = page.getPageSize();
        query.skip(Convert.toLong((pageNo - 1) * pageSize)).limit(pageSize);
        page.setRows(find(query));

        int divisor = count / pageSize;
        int remainder = count % pageSize;
        page.setTotalPage(remainder == 0 ? divisor == 0 ? 1 : divisor : divisor + 1);
        return page;
    }

    @Override
    public long count(Query query) {
        return mongoTemplate.count(query, getActualClass());
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
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

    /**
     * 构建基础的Update
     */
    private Update buildBaseUpdate(T t) {
        Update update = new Update();
        Field[] fields = t.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                Object value = field.get(t);
                if (Objects.isNull(value)) {
                    continue;
                }
                field.setAccessible(true);
                update.set(field.getName(), value);
            }
        } catch (Exception e) {
            log.error("MongoDAOSupport#buildBaseQuery, parse MongoField error, msg: {}", e.getMessage(), e);
        }
        return update;
    }

    /**
     * 获取当前类的泛型
     */
    private Class<T> getActualClass() {
        return ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }
}
