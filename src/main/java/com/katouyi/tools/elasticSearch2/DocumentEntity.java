package com.katouyi.tools.elasticSearch2;

import java.util.HashMap;
import org.apache.commons.lang3.StringUtils;

public class DocumentEntity extends HashMap<String, Object> {
    private String id;

    public DocumentEntity() {
    }

    public String getId() {
        if (StringUtils.isNotBlank(this.id)) {
            return this.id;
        } else {
            Object object = this.get("id");
            return object == null ? null : object.toString();
        }
    }

    public void setId(String id) {
        this.id = id;
        this.put("id", id);
    }
}