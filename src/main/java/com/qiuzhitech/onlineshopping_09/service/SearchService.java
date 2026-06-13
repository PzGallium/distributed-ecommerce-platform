package com.qiuzhitech.onlineshopping_09.service;

import com.qiuzhitech.onlineshopping_09.config.ESConfig;
import com.qiuzhitech.onlineshopping_09.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_09.db.po.OnlineShoppingCommodity;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {
    @Resource
    OnlineShoppingCommodityDao onlineShoppingCommodityDao;
    @Resource
    ESService esService;


    public List<OnlineShoppingCommodity> searchCommodityByDB(String keyword) {
        return onlineShoppingCommodityDao.searchCommodityByKeyword(keyword);
    }

    public List<OnlineShoppingCommodity> searchCommodityByES(String keyword) {
        return esService.searchCommodityByES(keyword);
    }
}

