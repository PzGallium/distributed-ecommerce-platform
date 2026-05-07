package com.qiuzhitech.onlineshopping_09.config;

import com.qiuzhitech.onlineshopping_09.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_09.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping_09.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.List;

@Component
public class PreHeatRunner implements ApplicationRunner {

    @Resource
    private OnlineShoppingCommodityDao onlineShoppingCommodityDao;
    @Resource
    private RedisService redisService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<OnlineShoppingCommodity> commodities = onlineShoppingCommodityDao.listItems();
        for (OnlineShoppingCommodity commodity : commodities) {
            String key = "OnlineShoppingCommodity_" + commodity.getCommodityId();
            String availableStock = commodity.getAvailableStock().toString();
            redisService.setJedisPool(key, availableStock);
        }
    }
}
