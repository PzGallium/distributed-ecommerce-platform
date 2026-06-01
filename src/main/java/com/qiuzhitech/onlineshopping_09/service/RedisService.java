package com.qiuzhitech.onlineshopping_09.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Collections;

@Slf4j
@Service
public class RedisService {
    @Resource
    private JedisPool jedisPool;

    public String getJedisPool(String key) {
        Jedis jedis = jedisPool.getResource();
        String val = jedis.get(key);
        jedis.close();
        return val;
    }

    public String setJedisPool(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        String status = jedis.set(key, value);
        jedis.close();
        return status;
    }

    public long deductStock(String key) {
        Jedis jedis = jedisPool.getResource();
            String script =
                    "if redis.call('exists', KEYS[1]) == 1 then\n" +
                    " local stock = tonumber(redis.call('get', KEYS[1]))\n" +
                    " if (stock<=0) then\n" +
                    " return -1\n" +
                    " end\n" +
                    "\n" +
                    " redis.call('decr', KEYS[1]);\n" +
                    " return stock - 1;\n" +
                    "end\n" +
                    "\n" +
                    "return -1;";
            Long stock = (Long) (jedis.eval(script,
                    Collections.singletonList(key), Collections.emptyList()));
            return stock;
    }

    public boolean tryToGetDistributedLock(long commodityID, String requestID, int time) {
        Jedis jedis = jedisPool.getResource();
        String key = "online_shopping_commodity_lock: "  + commodityID;
        String ret = jedis.set(key, requestID, "NX", "PX", time);//EX|PX, expire time units: EX = seconds; PX = milliseconds
        jedis.close();
        return "OK".equals(ret);
    }

    public boolean releaseDistributedLock(long commodityID, String requestID, int time) {
        Jedis jedis = jedisPool.getResource();
        String key = "online_shopping_commodity_lock: "  + commodityID;
        String script = "if redis.call('get', KEYS[1]) == ARGV[1]" +
                " then return redis.call('del', KEYS[1])" +
                " else return 0 end";
        Object result = jedis.eval(script,
                Collections.singletonList(key),
                Collections.singletonList(requestID));
        return (Long) result == 1L;
    }

    public long reverStock(String key) {
        Jedis jedis = jedisPool.getResource();
        Long res = jedis.incr(key);
        jedis.close();
        return res;
    }

    public void addToDenyList(String userID, String CommodityID) {
        Jedis jedis = jedisPool.getResource();
        String key = "online_shopping:DenyListUserID: "  + userID;
        jedis.sadd(key, CommodityID);
        jedis.close();
        log.info("Add userID: {} to DenyList for commodityID: {}", userID, CommodityID);
    }

    public boolean isInDenyList(String userID, String CommodityID) {
        Jedis jedis = jedisPool.getResource();
        String key = "online_shopping_UserID: "  + userID;
        Boolean sismember = jedis.sismember(key, CommodityID);
        jedis.close();
        return sismember;
    }

    public void removeFromDenyList(String userID, String CommodityID) {
        Jedis jedis = jedisPool.getResource();
        String key = "online_shopping_UserID: "  + userID;
        jedis.srem(key, CommodityID);
        jedis.close();
        log.info("Remove userID: {} from DenyList for Commodity: {}", userID, CommodityID);
    }

}
