package com.offcn;

import com.offcn.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-solr.xml")
public class TestTemplate {

    @Autowired
    private SolrTemplate solrTemplate;

    /*
    *
    * 插入到索引库
    * */

    @Test
    public void testAdd(){

        TbItem item = new TbItem();

        // 插值
        item.setId(3L);
        item.setBrand("小米");
        item.setCategory("手机pluse");
        item.setGoodsId(1L);
        item.setTitle("小米10Pro");
        item.setPrice(new BigDecimal(4599));


        solrTemplate.saveBean(item);
        solrTemplate.commit();
    }

    /*
    *
    * 按主键查询
    * */
    @Test
    public void getById(){

        TbItem item = solrTemplate.getById(3, TbItem.class);
        System.out.println(item.getBrand());
    }

    /*
    * 按主键删除
    * */
    @Test
    public void deleteById(){

        solrTemplate.deleteById("3");
        solrTemplate.commit();
    }

    /*
    * 循环插入
    * */
    @Test
    public void testAddList(){
        List<TbItem> list = new ArrayList();
        for(int i=1;i<101;i++){
            TbItem item = new TbItem();
            item.setId(Long.valueOf(i));
            item.setBrand("华为");
            item.setCategory("手机");
            item.setGoodsId(1L);
            item.setTitle("华为Mate"+i);
            item.setPrice(new BigDecimal(2000+i));
            list.add(item);
        }
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    /*
    * 分页查询
    * */
    @Test
    public void testPageQuery() {
        // 创建查询条件
        Query query = new SimpleQuery("*:*");
        query.setOffset(10);// 开始索引（默认0）
        query.setRows(20);	// 每页记录数(默认10)
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        System.out.println("总记录数：" + page.getTotalElements());
        List<TbItem> list = page.getContent();

        for (TbItem item : list) {
            System.out.println(item.getTitle() + item.getPrice());
        }

    }
    /*
    *条件查询
    * */

    @Test
    public void testPageQueryMutil(){
        // 创建查询条件
        Query query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_title").contains("1");
        criteria = criteria.and("item_price").greaterThanEqual(2012);
        query.addCriteria(criteria);

        // 排序
        Sort sort = new Sort(Sort.Direction.DESC,"item_price");
        query.addSort(sort);
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        List<TbItem> itemList = page.getContent();
        for (TbItem item : itemList) {
            System.out.println(item.getTitle()+":" + item.getPrice());
        }
    }

    /*
    * 删除所有
    * */
    @Test
    public void testDeleteAll(){
        Query query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }
}
