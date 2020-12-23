package com.dfundata.fund.redis.jediscluster;

import com.dfundata.fund.redis.utils.ConfigUtils;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * redis 集群连接池
 */
public class JedisClusterPool {

    private static volatile JedisCluster jedisCluster = null;

    public static JedisCluster getJedisCluster() {
        Set<HostAndPort> nodes = new HashSet<HostAndPort>();
        String redisUrl = ConfigUtils.getConfigStrValue("cluster.redis.url");
        String redisPassword = ConfigUtils.getConfigStrValue("cluster.redis.password");
        Integer connectionTimeout = Integer.parseInt(ConfigUtils.getConfigStrValue("redis.timeout"));
        Integer soTimeout = Integer.parseInt(ConfigUtils.getConfigStrValue("redis.timeout"));
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(ConfigUtils.getConfigIntValue("redis.pool.max-idle"));
        config.setMaxWaitMillis(ConfigUtils.getConfigIntValue("redis.pool.max-wait"));
        config.setMaxTotal(ConfigUtils.getConfigIntValue("redis.pool.max-active"));
        if (StringUtils.isNotBlank(redisUrl)) {
            String[] urls = redisUrl.split(",");
            for (int i = 0; urls != null && i < urls.length; i++) {
                String url = urls[i];
                String[] hostAndPort = url.split(":");
                nodes.add(new HostAndPort(hostAndPort[0], Integer.parseInt(hostAndPort[1])));
            }

        } else {
            // 在nodes中指定每个节点的地址
            nodes.add(new HostAndPort("127.0.0.1", 8001));
            nodes.add(new HostAndPort("127.0.0.1", 8002));
            nodes.add(new HostAndPort("127.0.0.1", 8003));
            nodes.add(new HostAndPort("127.0.0.1", 8004));
            nodes.add(new HostAndPort("127.0.0.1", 8005));
            nodes.add(new HostAndPort("127.0.0.1", 8006));
        }
        if (jedisCluster == null) {
            // 创建一个jedisCluster对象，该对象在系统中应该是单例的
            jedisCluster = new JedisCluster(nodes, connectionTimeout, soTimeout, 5, redisPassword, config);

            return jedisCluster;
        } else {
            return jedisCluster;
        }
    }
}
