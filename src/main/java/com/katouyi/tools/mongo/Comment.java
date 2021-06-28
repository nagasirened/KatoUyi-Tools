package com.katouyi.tools.mongo;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Document(collation = "content")
// 复合索引 1 升序  -1是降序
// @CompoundIndex(def = "{'user_id': 1, 'nick_name': -1}")
// @CompoundIndexes(value = {@CompoundIndex(def = "{'user_id': 1, 'nick_name': -1}")})
public class Comment {

    /** 主键标示，会自动对应mongodb的 "_id"字段，如果属性名字本来就叫id，可以不加这个注解 */
    @Id
    private String id;

    private String content;
    /** 注解名字对应mongodb的字段名称，如果是一致的就无需注解 */
    @Field("user_id")
    @Indexed        // FIXME 代表给这个字段加一个单字段的索引
    private String userId;
    @Field("nick_name")
    private String nickName;
    @Field("create_data_time")
    private LocalDateTime createDataTime;
    @Field("like_num")
    private Integer likeNum;                // 点赞数
    @Field("reply_num")
    private Integer replyNum;               // 回复数

    private Integer status;                 // 状态
    @Field("parent_id")
    private String parentId;
    @Field("article_id")
    private String articleId;
}
