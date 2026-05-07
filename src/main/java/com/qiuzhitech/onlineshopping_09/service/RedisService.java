package com.qiuzhitech.onlineshopping_09.service;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Collections;

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

    public Double deductStock(String key) {
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
            Double stock = (Double)(jedis.eval(script,
                    Collections.singletonList(key), Collections.emptyList()));
            return stock;
    }
}
