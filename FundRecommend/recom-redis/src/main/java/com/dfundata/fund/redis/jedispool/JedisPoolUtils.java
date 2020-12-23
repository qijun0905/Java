package com.dfundata.fund.redis.jedispool;

import com.dfundata.fund.redis.utils.ConfigUtils;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis 单机 连接池
 */
public class JedisPoolUtils {

    private static volatile JedisPool jedisPool = null;

    public static JedisPool getJedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(ConfigUtils.getConfigIntValue("redis.pool.max-active"));
        config.setMaxIdle(ConfigUtils.getConfigIntValue("redis.pool.max-idle"));
        config.setMaxWaitMillis(ConfigUtils.getConfigIntValue("redis.pool.max-wait"));

        // 向资源池借用连接时是否做连接有效性检查(ping)，无效连接会被移除
//            kafkaConfig.setTestOnBorrow(false);
        // 向资源池归还连接时是否做连接有效性测试(ping)，无效连接会被移除
//            kafkaConfig.setTestOnReturn(false);
        //连接池设置
        if (jedisPool == null) {
            synchronized (JedisPoolUtils.class) {
                if (jedisPool == null) {
                    jedisPool = new JedisPool(config, ConfigUtils.getConfigStrValue("standAlone.redis.host"),
                            ConfigUtils.getConfigIntValue("standAlone.redis.port"),
                            ConfigUtils.getConfigIntValue("redis.timeout"),
                            ConfigUtils.getConfigStrValue("standAlone.redis.password"));
                }
            }
        }
        return jedisPool;
    }

}
