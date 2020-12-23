package com.dfundata.fund.redis.utils;

import com.dfundata.fund.redis.JedisCache;
import com.dfundata.fund.redis.jediscluster.JedisClusterClient;
import com.dfundata.fund.redis.jediscluster.JedisClusterPool;
import com.dfundata.fund.redis.jedispool.JedisClient;
import com.dfundata.fund.redis.jedispool.JedisPoolUtils;
import com.dfundata.fund.redis.jedissentinel.JedisSentinelClient;
import com.dfundata.fund.redis.jedissentinel.JedisSentinelPoolUtil;

public class JedisClientUtils {

    private volatile static JedisCache jedisCache = null;

    public static JedisCache getJedisCache() {
        //redis 以哪种方式启动
        //1 单机版 2 哨兵集群 3 cluster集群
        String redisStart = ConfigUtils.getConfigStrValue("redis.start");
        if (jedisCache == null) {
            synchronized (JedisClientUtils.class) {
                if (jedisCache == null) {
                    if ("1".equals(redisStart)) {
                        jedisCache = new JedisClient(JedisPoolUtils.getJedisPool());
                    }
                    if ("2".equals(redisStart)) {
                        jedisCache = new JedisSentinelClient(JedisSentinelPoolUtil.getJedisSentinelPool());
                    }
                    if ("3".equals(redisStart)) {
                        jedisCache = new JedisClusterClient(JedisClusterPool.getJedisCluster());
                    }
                }
            }
        }
        return jedisCache;
    }

}
