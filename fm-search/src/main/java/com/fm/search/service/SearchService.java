package com.fm.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fm.common.enums.ExceptionEnum;
import com.fm.common.exception.FmException;
import com.fm.common.utils.JsonUtils;
import com.fm.item.pojo.*;
import com.fm.search.client.BrandClient;
import com.fm.search.client.CategoryClient;
import com.fm.search.client.GoodsClient;
import com.fm.search.client.SpecClient;
import com.fm.search.pojo.Goods;
import com.fm.search.pojo.SearchRequest;
import com.fm.search.pojo.SearchResult;
import com.fm.search.repository.GoodsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SearchService {
    @Autowired
    private BrandClient brandClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecClient specClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    private GoodsRepository repository;

    public Goods buildGoods(Spu spu) {
        //抽取经常使用的参数
        Long spuId = spu.getId();
        //查询商品分类名
        List<String> names = categoryClient.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
                .stream()
                .map(Category::getName)
                .collect(Collectors.toList());

        //查询商品品牌
        Brand brand = brandClient.queryById(spu.getBrandId());
        if (brand == null) {
            throw new FmException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        //所有的搜索字段拼接到all中，all存入索引库，并进行分词处理，搜索时与all中的字段进行匹配查询
        String all = spu.getTitle() + StringUtils.join(names, " ") + brand.getName();

        //查询sku
        List<Sku> skuList = goodsClient.querySkuBySpuId(spuId);

        if (CollectionUtils.isEmpty(skuList)) {
            throw new FmException(ExceptionEnum.GOODS_NOT_FOUND);
        }
          //节省一次循环遍历，在下面获取sku值的时候顺带获取价格
//        Set<Double> priceSet = skuList.stream().map(Sku::getPrice).collect(Collectors.toSet());

//        //存储price的集合
        TreeSet<Double> priceSet = new TreeSet<>();

        //现有的sku字段，存在多余的属性，可以进行排除
        // 设置存储skus的json结构的集合，用map结果转化sku对象，转化为json之后与对象结构相似（或者重新定义一个对象，存储前台要展示的数据，并把sku对象转化成自己定义的对象）
        List<Map<String, Object>> skus = new ArrayList<>();
        //从sku中取出要进行展示的字段，并将sku转换成json格式
        for (Sku sku : skuList) {
            priceSet.add(sku.getPrice());//处理价格
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", sku.getId());
            map.put("title", sku.getTitle());
            map.put("price", sku.getPrice());
            //sku中有多个图片，只展示第一张
            map.put("image", StringUtils.substringBefore(sku.getImages(), ","));
            skus.add(map);
        }


        //根据cid查询规格参数，规格参数中分为通用规格参数和特有规格参数
        List<SpecParam> params = specClient.querySpecParams(null, spu.getCid3(), true, null);
        if (CollectionUtils.isEmpty(params)) {
            throw new FmException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        //查询商品详情
        SpuDetail spuDetail = goodsClient.querySpuDetailById(spuId);

        //获取通用规格参数
        Map<Long, String> genericSpec = JsonUtils.toMap(spuDetail.getGenericSpec(), Long.class, String.class);
        //获取特有规格参数
        Map<Long, List<String>> specialSpec = JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {
        });

        //定义spec对应的map，key是规格参数的名字，值是规格参数的值
        HashMap<String, Object> specs = new HashMap<>();
        //对规格进行遍历，并封装spec，其中spec的key是规格参数的名称，值是商品详情中的值
        for (SpecParam param : params) {
            //key是规格参数的名称
            String key = param.getName();
            Object value = "";

            if (param.getGeneric()) {
                //参数是通用属性，通过规格参数的ID从商品详情存储的规格参数中查出值
                value = genericSpec.get(param.getId());
                if (param.getNumeric()) {
                    //参数是数值类型，处理成段，方便后期对数值类型进行范围过滤
                    value = chooseSegment(value.toString(), param);
                }
            } else {
                //参数不是通用类型
                value = specialSpec.get(param.getId());
            }
            value = (value == null ? "其他" : value);
            //存入map
            specs.put(key, value);
        }


        Goods goods = new Goods();
        goods.setId(spuId);
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setAll(all); //搜索字段，包括品牌，分类，标题
        goods.setPrice(priceSet);//sku价格集合
        goods.setSkus(JsonUtils.toString(skus));//所有sku的集合的json格式
        goods.setSpecs(specs);//所有可搜索的规格参数
        goods.setSubTitle(spu.getSubTitle());
        return goods;
    }

    /**
     * 将规格参数为数值型的参数划分为段
     *
     * @param value
     * @param p
     * @return
     */
    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    public SearchResult<Goods> search(SearchRequest searchRequest) {

        String key = searchRequest.getKey();
        /**
         * 判断是否有搜索条件，如果没有，直接返回null。不允许搜索全部商品
         */
        if (StringUtils.isBlank(key)) {//默认搜索关键词“健身”
//            throw new FmException(ExceptionEnum.INVALID_PARAM);
            searchRequest.setKey("健身");
        }

        //NativeSearchQueryBuilder：Spring提供的一个查询条件构建器，帮助构建json格式的请求体
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //通过sourceFilter字段过滤只要我们需要的数据
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "subTitle", "skus"}, null));

        //分页和排序
        searchWithPageAndSort(queryBuilder, searchRequest);

        //基本搜索条件
        QueryBuilder basicQuery = buildBasicQuery(searchRequest);
        queryBuilder.withQuery(basicQuery);

        //对分类和品牌聚合
        String categoryAggName = "categoryAgg";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));

        String brandAggName = "brandAgg";
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        //查询，获取结果
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);

        //解析聚合结果
        Aggregations aggs = result.getAggregations();
        //解析分类聚合
        List<Category> categories = handleCategoryAgg(aggs.get(categoryAggName));
        //解析品牌聚合
        List<Brand> brands = handleBrandAgg(aggs.get(brandAggName));

        //对规格参数聚合
        List<Map<String, Object>> specs = null;

        //商品分类存在并数量为1，可以聚合规格参数
        if (categories != null && categories.size() == 1) {
            specs = handleSpecs(categories.get(0).getId(), basicQuery);
        }

        //解析分页结果
        long total = result.getTotalElements();
        int totalPage = result.getTotalPages();
        List<Goods> items = result.getContent();

        return new SearchResult(total, totalPage, items, categories, brands, specs);
    }

    /**
     * 对规格参数进行聚合并解析结果
     *
     * @param cid
     * @param basicQuery
     * @return
     */
    private List<Map<String, Object>> handleSpecs(Long cid, QueryBuilder basicQuery) {
        List<Map<String, Object>> specs = new ArrayList<>();

        //查询需要聚合的规格参数
        List<SpecParam> params = specClient.querySpecParams(null, cid, true, null);

        //基本查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(basicQuery);
        queryBuilder.withPageable(PageRequest.of(0, 1));

        for (SpecParam param : params) {
            //聚合
            String name = param.getName();
            queryBuilder.addAggregation(AggregationBuilders.terms(name).field("specs." + name + ".keyword"));
        }
        //查询
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);

        //对聚合结果进行解析
        Aggregations aggs = result.getAggregations();
        for (SpecParam param : params) {
            String name = param.getName();
            Terms terms = aggs.get(name);
            //创建聚合结果
            HashMap<String, Object> map = new HashMap<>();
            map.put("k", name);
            map.put("options", terms.getBuckets()
                    .stream()
                    .map(b -> b.getKey())
                    .collect(Collectors.toList()));
            specs.add(map);
        }
        return specs;
    }

    /**
     * 分页和排序
     *
     * @param queryBuilder
     * @param searchRequest
     */
    private void searchWithPageAndSort(NativeSearchQueryBuilder queryBuilder, SearchRequest searchRequest) {
        Integer page = searchRequest.getPage() - 1;//es页码从0开始
        Integer size = searchRequest.getSize();

        String sortBy = searchRequest.getSortBy();
        Boolean desc = searchRequest.getDescending();

        //分页
        queryBuilder.withPageable(PageRequest.of(page, size));

        //排序
        if (StringUtils.isNotBlank(sortBy)) {
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(desc ? SortOrder.DESC : SortOrder.ASC));
        }
    }

    /**
     * 解析品牌聚合结果
     *
     * @param terms
     * @return
     */
    private List<Brand> handleBrandAgg(LongTerms terms) {
        //获取品牌ID
        try {
            List<Long> ids = terms.getBuckets()
                    .stream()
                    .map(b -> b.getKeyAsNumber().longValue())
                    .collect(Collectors.toList());
            //根据品牌ids查询品牌
            return brandClient.queryBrandsByIds(ids);

        } catch (Exception e) {
            log.error("查询品牌信息失败", e);
            return null;
        }
    }

    /**
     * 对分类聚合结果进行解析
     *
     * @param terms
     * @return
     */
    public List<Category> handleCategoryAgg(LongTerms terms) {
        try {
            //获取id
            List<Long> ids = terms.getBuckets()
                    .stream()
                    .map(b -> b.getKeyAsNumber().longValue())
                    .collect(Collectors.toList());
            //根据ID查询分类
            List<Category> categories = categoryClient.queryByIds(ids);
            for (Category category : categories) {
                category.setParentId(null);
                category.setIsParent(null);
                category.setSort(null);
            }
            return categories;
        } catch (Exception e) {
            log.error("查询分类信息失败", e);
            return null;
        }

    }

    /**
     * 构建基本查询
     *
     * @param request
     * @return
     */
    private QueryBuilder buildBasicQuery(SearchRequest request) {
        //构建布尔查询
        BoolQueryBuilder basicQuery = QueryBuilders.boolQuery();
        //搜索条件
        basicQuery.must(QueryBuilders.matchQuery("all", request.getKey()));

        //过滤条件
        Map<String, String> filterMap = request.getFilter();

        //智能定制化查询
//        UserInfo loginUser = LoginInterceptor.getLoginUser();
//        filterMap.put("适用人群",loginUser.getTag().split(",")[0]);

        if (!CollectionUtils.isEmpty(filterMap)) {
            for (Map.Entry<String, String> entry : filterMap.entrySet()) {
                String key = entry.getKey();
                //判断key是否是分类或者品牌过滤条件
//                if (!"cid2".equals(key) && !"brandId".equals(key)) {
                if (!"cid3".equals(key) && !"brandId".equals(key)) {
                    key = "specs." + key + ".keyword";
                }
                //过滤条件
                String value = entry.getValue();
                //因为是keyword类型，使用terms查询
                basicQuery.filter(QueryBuilders.termQuery(key, value));
            }
        }
        return basicQuery;
    }

    /**
     * 插入或更新索引
     *
     * @param spuId
     */
    @Transactional
    public void insertOrUpdate(Long spuId) {

        //查询spu
        Spu spu = goodsClient.querySpuBySpuId(spuId);
        if (spu == null) {
            log.error("索引对应的spu不存在，spuId:{}", spuId);
            throw new RuntimeException();
        }

        //构建商品
        Goods goods = this.buildGoods(spu);
        //不处理异常，失败就回滚重试
        //保存到索引库
        repository.save(goods);

//        try {
//            Goods goods = buildGoods(spu);
//            //保存到索引库
//            repository.save(goods);
//        } catch (IOException e) {
//            log.error("构建商品失败", e);
//            throw new RuntimeException();
//        }

    }

    /**
     * 删除索引
     *
     * @param id
     */
    public void delete(Long id) {
        repository.deleteById(id);
    }

}
