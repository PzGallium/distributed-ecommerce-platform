package com.qiuzhitech.onlineshopping_09.db.dao.impl;

import com.qiuzhitech.onlineshopping_09.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_09.db.po.OnlineShoppingCommodity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Slf4j
class OnlineShoppingCommodityDaoImplTest {
    @Resource
    private OnlineShoppingCommodityDao onlineShoppingCommodityDao;

    @BeforeEach
    void setUp() {
    }

    @Test
    void insertCommodity() {
        OnlineShoppingCommodity  onlineShoppingCommodity =
                OnlineShoppingCommodity.builder()
                        .commodityName("IphoneX")
                        .creatorUserId(124L)
                        .commodityDesc("desc")
                        .totalStock(100)
                        .availableStock(100)
                        .lockStock(0)
                        .price(999)
                        .build();
        onlineShoppingCommodityDao.insertCommodity(onlineShoppingCommodity);
    }

    @Test
    void selectByCommodityId() {
        OnlineShoppingCommodity onlineShoppingCommodity = onlineShoppingCommodityDao.selectByCommodityId(123L);
        //log.info(onlineShoppingCommodity.toString());
    }


    @Test
    void selectByUserId() {
        List<OnlineShoppingCommodity> onlineShoppingCommodities = onlineShoppingCommodityDao.selectByUserId(124L);
        log.info(onlineShoppingCommodities.toString());
    }
}


