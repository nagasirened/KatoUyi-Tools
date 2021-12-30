package com.katouyi.tools.mongo.mall;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Collection;
import java.util.List;

public interface MongoDAO<T> {

    /**
     * 保存单个对象
     */
    public T save(T t);

    /**
     * 批量保存
     */
    public Collection<T> save(Collection<T> t);

    /**
     * 根据主键删除
     */
    public boolean removeById(T t);

    /**
     * 条件删除
     */
    public boolean removeByCondition(T t);

    /**
     * 根据主键修改
     */
    public void updateById(String id, T t);

    /**
     * 根据对象的属性查询
     */
    public List<T> findByCondition(T t);

    /**
     * 通过条件查询实体(集合)
     */
    public List<T> find(Query query);

    /**
     * 查询指定返回哪些字段
     */
    public List<T> find(Query query, List<String> fields);

    /**
     * 通过一定的条件查询一个实体
     */
    public T findOne(Query query);

    /**
     * 通过条件查询更新数据
     */
    public void update(Query query, Update update);

    /**
     * 通过ID获取记录
     */
    public T findById(String id);

    /**
     * 通过ID获取记录, 并且指定了集合名(表的意思)
     */
    public T findById(String id, String collectionName);

    /**
     * 通过条件查询,查询分页结果
     */
    public Page<T> findPage(Page<T> page, Query query);

    /**
     * 求数据总和
     */
    public long count(Query query);


    /**
     * 获取MongoDB模板操作
     */
    public MongoTemplate getMongoTemplate();
}
