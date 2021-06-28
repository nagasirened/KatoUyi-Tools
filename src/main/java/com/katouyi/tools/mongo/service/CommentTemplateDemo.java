package com.katouyi.tools.mongo.service;


import com.katouyi.tools.mongo.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class CommentTemplateDemo {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 点赞数加1
     */
    public void incrLikeNumber(String id) {
        // 第一个是查询条件
        Query query = Query.query(Criteria.where("id").is(id));  // .addCriteria().addCriteria();
        // 第二个是更新条件
        Update update = new Update();
        update.inc("like_num", 1);
        // update.set("", "");

        // 第三个可以是实体类，也可以是collection集合的名称"comment"
        mongoTemplate.updateFirst(query, update, Comment.class);
    }

}
