package com.qiuzhitech.onlineshopping_09.controller;

import com.qiuzhitech.onlineshopping_09.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_09.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping_09.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
public class CommodityController {
    @Resource
    private OnlineShoppingCommodityDao  onlineShoppingCommodityDao;

    @RequestMapping("/addItem")
    public String AddCommodity() {
        return "add_commodity";
    }

    @Resource
    SearchService searchService;

    @PostMapping("/commodities")
    public String HandleAddCommodity(@RequestParam("commodityId") long commodityId,
                               @RequestParam("commodityName") String commodityName,
                               @RequestParam("commodityDesc") String commodityDesc,
                               @RequestParam("price") int price,
                               @RequestParam("creatorUserId") long creatorUserId,
                               @RequestParam("availableStock") int availableStock,
                               Map<String, Object> resultMap
                               ) {
        OnlineShoppingCommodity commodity = OnlineShoppingCommodity.builder()
                .commodityId(commodityId)
                .commodityName(commodityName)
                .commodityDesc(commodityDesc)
                .price(price)
                .creatorUserId(creatorUserId)
                .availableStock(availableStock)
                .totalStock(availableStock)
                .lockStock(0)
                .build();
        int ret = onlineShoppingCommodityDao.insertCommodity(commodity);
        resultMap.put("abc", commodity);
        return "add_commodity_success";
    }

    @GetMapping("/item/{itemID}")
    String itemDetail(@PathVariable("itemID") long itemID, Map<String, Object> resultMap) {
        OnlineShoppingCommodity onlineShoppingCommodity = onlineShoppingCommodityDao.selectByCommodityId(itemID);
        resultMap.put("commodity", onlineShoppingCommodity);
        return "item_detail";
    }

    @GetMapping("/")
    String itemList(Map<String, Object> resultMap) {
        List<OnlineShoppingCommodity> commodities = onlineShoppingCommodityDao.listItems();
        resultMap.put("itemList", commodities);
        return "list_items";
    }


    @GetMapping("searchAction")
    public String searchCommodity(@RequestParam("keyWord") String keyword, Map<String, Object> resultMap) {
        List<OnlineShoppingCommodity> commodities = searchService.searchCommodityByES(keyword);
        resultMap.put("itemList", commodities);
        return "list_items";
    }
}
