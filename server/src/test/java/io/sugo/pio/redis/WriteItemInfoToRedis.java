package io.sugo.pio.redis;

import io.sugo.pio.server.broker.sort.ClickSorter;
import io.sugo.pio.server.broker.sort.PublishTimeSorter;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

public class WriteItemInfoToRedis {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("192.168.0.220", 6379, 3000);
        jedis.flushDB();
        Map<String, String> data = new HashMap<>();
        for (int i = 1; i < 1001; i++) {
            String value = "" + i;
            data.put(ClickSorter.TYPE, value);
            data.put(PublishTimeSorter.TYPE, value);
            jedis.hmset(value, data);
        }
        jedis.close();
    }
}
