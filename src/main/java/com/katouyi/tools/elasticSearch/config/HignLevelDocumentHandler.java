
package com.katouyi.tools.elasticSearch.config;


import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;

/**
 * author: ZGF
 * context : 操作文档的接口
 */

public interface HignLevelDocumentHandler<T, ID> {

    public void save(T t) throws Exception;

    public String detail(ID id)throws IOException;

    public void update(T t) throws Exception;

    public void delete(ID id) throws IOException;

    public void bulk(BulkRequest request)  throws IOException;

    public SearchResponse search(SearchSourceBuilder searchSourceBuilder)  throws IOException;
}
