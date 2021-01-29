package com.katouyi.tools.mybatisPlus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;



/**
 * author: ZGF
 * context : 自动填充组件
 *
 * 使用
 *
     @TableField(fill = FieldFill.INSERT)
     private String createdTime;
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.setInsertFieldValByName("createdTime", DateTime.now().toDate(), metaObject);
        this.setInsertFieldValByName("updatedTime", DateTime.now().toDate(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setInsertFieldValByName("updatedTime", DateTime.now().toDate(), metaObject);
    }
}
