package com.fm.search.client;

import com.fm.common.vo.PageResult;
import com.fm.item.pojo.Spu;
import com.fm.search.pojo.Goods;
import com.fm.search.repository.GoodsRepository;
import com.fm.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsTest {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SearchService searchService;

    @Test
    public void testCreateIndex() {
        template.createIndex(Goods.class);
        template.putMapping(Goods.class);
    }

    /**
     * 把数据库数据加载到索引库
     */
    @Test
    public void loadData() {
        int page = 1;
        int size = 0;
        int rows = 100;
        do {
            PageResult<Spu> result = goodsClient.querySpuByPage(page, rows, true, null);
            List<Spu> spuList = result.getItems();
            size = spuList.size();//存储当前获取的数量

            List<Goods> goodList = spuList.stream()
                    .map(searchService::buildGoods).collect(Collectors.toList());

//            ArrayList<Goods> goodList = new ArrayList<>();
//            for (Spu spu : spuList) {
//                try {
//                    Goods g = searchService.buildGoods(spu);
//                    goodList.add(g);
//
//                } catch (Exception e) {
//                    break;
//                }
//            }

            this.goodsRepository.saveAll(goodList);
            page++;//翻页
        } while (size == 100);//够100证明，还有没查出来的
    }
}
