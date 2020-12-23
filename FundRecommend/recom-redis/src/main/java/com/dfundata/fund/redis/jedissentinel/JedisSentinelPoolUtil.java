package com.dfundata.fund.redis.jedissentinel;

import com.dfundata.fund.redis.utils.ConfigUtils;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashSet;
import java.util.Set;

/**
 * @author ly
 * @date 2019-10-29 13:41
 * @description 哨兵集群搭建连接池
 */
public class JedisSentinelPoolUtil {

    private static volatile JedisSentinelPool jedisSentinelPool = null;

    public static JedisSentinelPool getJedisSentinelPool() {

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(ConfigUtils.getConfigIntValue("redis.pool.max-idle"));
        config.setMaxWaitMillis(ConfigUtils.getConfigIntValue("redis.pool.max-wait"));
        config.setMaxTotal(ConfigUtils.getConfigIntValue("redis.pool.max-active"));
        Set<String> set = new HashSet<>();

        String redisUrl = ConfigUtils.getConfigStrValue("sentinel.redis.url");
        if (StringUtils.isNotBlank(redisUrl)) {
            String[] urls = redisUrl.split(",");
            for (String url : urls) {
                set.add(url);
            }
        } else {
            set.add("192.168.0.104:28000");
            set.add("192.168.0.104:28001");
            set.add("192.168.0.104:28002");
        }
        if (jedisSentinelPool == null) {
            synchronized (JedisSentinelPoolUtil.class) {
                if (jedisSentinelPool == null) {
                    JedisSentinelPool jedisSentinelPool =
                            new JedisSentinelPool(
                                    ConfigUtils.getConfigStrValue("sentinel.redis.mastername"),
                                    set, config,
                                    ConfigUtils.getConfigStrValue("sentinel.redis.password"));
                }
            }
            return jedisSentinelPool;
        } else {
            return jedisSentinelPool;
        }
    }
}
