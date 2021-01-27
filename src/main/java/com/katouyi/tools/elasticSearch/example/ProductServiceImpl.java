//package com.katouyi.tools.elasticSearch.example;

//import com.alibaba.fastjson.JSON;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.imageman.mapper.ProductImgMapper;
//import com.imageman.mapper.ProductMapper;
//import com.imageman.others.es.DefaultHignLevelDocumentHandler;
//import com.imageman.others.redis.RedisAuxiliary;
//import com.imageman.others.redis.prefix.BasicPrefix;
//import com.imageman.pojo.Product;
//import com.imageman.pojo.ProductImg;
//import com.imageman.pojo.bo.ProductElasticSearchParam;
//import com.imageman.pojo.bo.ProductParam;
//import com.imageman.pojo.vo.ProductElasticVO;
//import com.imageman.service.ProductService;
//import org.apache.commons.lang3.StringUtils;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.index.query.*;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.elasticsearch.search.sort.SortOrder;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.CollectionUtils;
//
//import javax.annotation.PostConstruct;
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.*;

/**
 * <p>
 * 商品表 商品表 服务实现类
 * </p>
 *
 * @author KatoUyi
 * @since 2021-01-13
 */
//@Service
//@Transactional
//public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
//
//    @Autowired
//    @SuppressWarnings("all")
//    private ProductMapper productMapper;
//
//    @Autowired
//    @SuppressWarnings("all")
//    private ProductImgMapper productImgMapper;
//
//    @Autowired
//    private DefaultHignLevelDocumentHandler productClient;
//
//    @Autowired
//    private RedisAuxiliary redisAuxiliary;
//
//    @PostConstruct
//    public void loadRecommended() throws IOException {
//        this.dailyRecommended();
//    }
//
//    /**
//     * 新增一个商品信息
//     * @param productParam
//     */
//    @Override
//    public void insertProduct(ProductParam productParam) throws IOException {
//        Product product = productParam.getProduct();
//        product.setSellCounts(0);
//        product.setStatus(0);
//        productMapper.insert(product);
//
//        ProductImg productImg = productParam.getProductImg();
//        productImg.setProductId(product.getId());
//        productImgMapper.insert(productImg);
//
//        ProductElasticVO elasticVO = new ProductElasticVO();
//        BeanUtils.copyProperties(product, elasticVO);
//        elasticVO.setPreviewLittle(productImg.getPreviewLittle());
//        productClient.save(elasticVO, elasticVO.getId());
//    }
//
//    /**
//     * 修改商品信息
//     * @param productParam
//     */
//    @Override
//    public void updateProduct(ProductParam productParam) throws Exception {
//        ProductElasticVO elasticVO = new ProductElasticVO();
//
//        Product product = productParam.getProduct();
//        productMapper.updateById(product);
//        BeanUtils.copyProperties(product, elasticVO);
//
//        ProductImg productImg = productParam.getProductImg();
//        if (Objects.nonNull(productImg) && StringUtils.isNotEmpty(productImg.getId())) {
//            productImgMapper.updateById(productImg);
//            if (StringUtils.isNotEmpty(productImg.getPreviewLittle())) {
//                elasticVO.setPreviewLittle(productImg.getPreviewLittle());
//            }
//        }
//
//        productClient.update(elasticVO, product.getId());
//    }
//
//    /**
//     * 根据主键逻辑删除
//     * @param id
//     */
//    @Override
//    public void deleteProductById(String id) throws IOException {
//        productMapper.deleteById(id);
//        productClient.delete(id);
//    }
//
//    /**
//     * 查询商品信息
//     * @param productElasticSearchParam
//     * @return
//     */
//    @Override
//    public Page<ProductElasticVO> searchByParam(ProductElasticSearchParam productElasticSearchParam) throws IOException {
//        Integer pageSize  = productElasticSearchParam.getPageSize();
//        Integer pageNo    = productElasticSearchParam.getPageNo();
//        String rootCatId  = productElasticSearchParam.getRootCatId();
//        String catId      = productElasticSearchParam.getCatId();
//        String searchInfo = productElasticSearchParam.getSearchInfo();
//        Integer sortType  = productElasticSearchParam.getSortType();
//
//        Page<ProductElasticVO> result = new Page<>(pageNo, pageSize);
//
//        SearchSourceBuilder builder = new SearchSourceBuilder();
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        if (StringUtils.isNotEmpty(rootCatId)) {
//            boolQueryBuilder.filter(new MatchQueryBuilder("rootCatId", rootCatId));
//        }
//        if (StringUtils.isNotEmpty(catId)) {
//            boolQueryBuilder.filter(new MatchQueryBuilder("catId", catId));
//        }
//        if (StringUtils.isNotEmpty(searchInfo)) {
//            boolQueryBuilder.should(new FuzzyQueryBuilder("itemName", searchInfo).boost(3));
//            boolQueryBuilder.should(new FuzzyQueryBuilder("content",  searchInfo).boost(1));
//        }
//        builder.query(boolQueryBuilder);
//
//        builder.from((productElasticSearchParam.getPageNo() - 1) * productElasticSearchParam.getPageSize());
//        builder.size(productElasticSearchParam.getPageSize());
//
//        SearchResponse searchResponse = productClient.search(builder);
//        if (Objects.isNull(searchResponse) || searchResponse.getHits().getHits().length < 1) {
//            return result;
//        }
//
//        result.setRecords(analysis(searchResponse));
//        result.setTotal(searchResponse.getTotalShards());
//        return result;
//    }
//
//    /**
//     * 每日推荐 -- 定时任务
//     * @return
//     */
//    @Override
//    public Set<ProductElasticVO> dailyRecommended() throws IOException {
//        SearchSourceBuilder builder = new SearchSourceBuilder();
//        // 查询三天内销售最高的数据
//        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder().filter(new RangeQueryBuilder("createdTime").gte(LocalDateTime.now().plusDays(-3)));
//        builder.query(boolQueryBuilder);
//        builder.from(0);
//        builder.size(3);
//        builder.sort("sellCounts", SortOrder.DESC);
//        SearchResponse searchResponse = productClient.search(builder);
//        List<ProductElasticVO> analysis = analysis(searchResponse);
//        Set<ProductElasticVO> productElasticVOS = new HashSet<>();
//        while (productElasticVOS.size() < 2) {
//            int i = new Random().nextInt(3);
//            productElasticVOS.add(analysis.get(i));
//        }
//        redisAuxiliary.setWithExpire(BasicPrefix.DAILY_RECOMMENDED_PRODUCT, "", productElasticVOS);
//        return productElasticVOS;
//    }
//
//    /**
//     * 获取每日推荐
//     * @return
//     */
//    @Override
//    public Set<ProductElasticVO> getRecommend() throws IOException {
//        Set<ProductElasticVO> collectionRecommended = (Set<ProductElasticVO>)redisAuxiliary.get(BasicPrefix.DAILY_RECOMMENDED_PRODUCT, "");
//        if (CollectionUtils.isEmpty(collectionRecommended)) {
//            return dailyRecommended();
//        } else {
//            return collectionRecommended;
//        }
//    }
//
//    /**
//     * 解析es搜出来的内容
//     * @param searchResponse
//     * @return
//     */
//    private List<ProductElasticVO> analysis(SearchResponse searchResponse) {
//        SearchHit[] hits = searchResponse.getHits().getHits();
//        ArrayList<ProductElasticVO> productElasticVOS = new ArrayList<>();
//        for (SearchHit hit : hits) {
//            productElasticVOS.add(JSON.parseObject(hit.getSourceAsString(), ProductElasticVO.class));
//        }
//        return productElasticVOS;
//    }
//
//}
