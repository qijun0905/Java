package com.dfundata.fund.redis.jedispool;

import com.dfundata.fund.redis.JedisCache;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * jedis 单机方法调用
 */
public class JedisClient implements JedisCache {

    private JedisPool jedisPool = null;

    public JedisClient(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    /**
     * 删除 key
     *
     * @param key
     */
    @Override
    public void del(String key) {
        Jedis jedis = jedisPool.getResource();
        jedis.del(key);
        jedis.close();
    }

    /**
     * 检查 key 是否存在
     *
     * @param key
     * @return
     */
    @Override
    public boolean exists(String key) {
        Jedis jedis = jedisPool.getResource();
        Boolean exists = jedis.exists(key);
        jedis.close();
        return exists;
    }

    /**
     * 设置 key 的 有效期的时长
     *
     * @param key
     * @param millisecondsTimestamp 将key的过期时间设置为timestamp所代表的的毫秒数的时间戳
     */
    @Override
    public Long pexpireAt(String key, Long millisecondsTimestamp) {
        Jedis jedis = jedisPool.getResource();
        Long result = jedis.pexpireAt(key, millisecondsTimestamp);
        jedis.close();
        return result;
    }

    /**
     * 当 key 不存在时，返回 -2 。
     * 当 key 存在但没有设置剩余生存时间时，返回 -1 。
     * 否则，以秒为单位，返回 key 的剩余生存时间。
     *
     * @param key
     * @return
     */
    @Override
    public Long ttl(String key) {
        Jedis jedis = jedisPool.getResource();
        Long ttl = jedis.ttl(key);
        jedis.close();
        return ttl;
    }

    /**
     * 得到 key 开头的key
     *
     * @param key
     * @return
     */
    @Override
    public Set<String> keysStart(String key) {
        Jedis jedis = jedisPool.getResource();
        Set<String> keys = jedis.keys(key + "*");
        jedis.close();
        return keys;
    }

    /**
     * 得到 含有 key 的 key
     *
     * @param key
     * @return
     */
    @Override
    public Set<String> hasKeys(String key) {
        Jedis jedis = jedisPool.getResource();
        Set<String> keys = jedis.keys("*" + key + "*");
        jedis.close();
        return keys;
    }

    /**
     * 设置指定 key 的值
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public String set(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        String result = jedis.set(key, value);
        jedis.close();
        return result;
    }

    /**
     * 获取指定 key 的值。
     *
     * @param key
     * @return
     */
    @Override
    public String get(String key) {
        Jedis jedis = jedisPool.getResource();
        String result = jedis.get(key);
        jedis.close();
        return result;
    }

    /**
     * 将一个或多个值插入到列表头部
     * 执行 lpush 命令后，列表的长度
     *
     * @param key
     * @param values
     */
    @Override
    public Long lpush(String key, String... values) {
        Jedis jedis = jedisPool.getResource();
        Long lpush = jedis.lpush(key, values);
        jedis.close();
        return lpush;
    }

    /**
     * 获取列表指定范围内的元素
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    @Override
    public List<String> lrange(String key, Long start, Long end) {
        Jedis jedis = jedisPool.getResource();
        List<String> results = jedis.lrange(key, start, end);
        jedis.close();
        return results;
    }

    /**
     * 获取列表长度
     *
     * @param key
     * @return
     */
    @Override
    public Long llen(String key) {
        Jedis jedis = jedisPool.getResource();
        Long llen = jedis.llen(key);
        jedis.close();
        return llen;
    }

    /**
     * Sadd 命令将一个或多个成员元素加入到集合中，已经存在于集合的成员元素将被忽略。
     *
     * @param key
     * @param member
     * @return 被添加到集合中的新元素的数量，不包括被忽略的元素。
     */
    @Override
    public Long sadd(String key, String member) {
        Jedis jedis = jedisPool.getResource();
        Long sadd = jedis.sadd(key, member);
        jedis.close();
        return sadd;
    }

    /**
     * Smembers命令获取set集合的所有元素
     *
     * @param key
     * @return
     */
    @Override
    public Set<String> smembers(String key) {
        Jedis jedis = jedisPool.getResource();
        Set<String> results = jedis.smembers(key);
        jedis.close();
        return results;
    }

    /**
     * set集合中是否包含 member
     *
     * @param key
     * @param member
     * @return true or false
     */
    @Override
    public Boolean sismember(String key, String member) {
        Jedis jedis = jedisPool.getResource();
        Boolean sismember = jedis.sismember(key, member);
        jedis.close();
        return sismember;
    }

    /**
     * Zincrby 命令对有序集合中指定成员的分数加上增量 increment
     * 可以通过传递一个负数值 increment ，让分数减去相应的值，
     * 比如 ZINCRBY key -5 member ，就是让 member 的 score 值减去 5 。
     * 当 key 不存在，或分数不是 key 的成员时，
     * ZINCRBY key increment member 等同于 ZADD key increment member 。
     * 当 key 不是有序集类型时，返回一个错误。
     *
     * @param key
     * @param increment
     * @param member
     * @return
     */
    @Override
    public Double zincrby(String key, double increment, String member) {
        Jedis jedis = jedisPool.getResource();
        Double zincrby = jedis.zincrby(key, increment, member);
        jedis.close();
        return zincrby;
    }

    /**
     * 向有序集合添加一个成员，或者更新已存在成员的分数
     *
     * @param key
     * @param score
     * @param value
     * @return 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。
     */
    @Override
    public Long zadd(String key, double score, String value) {
        Jedis jedis = jedisPool.getResource();
        Long result = jedis.zadd(key, score, value);
        jedis.close();
        return result;
    }

    /**
     * 删除有序集合中一个或者多个元素
     *
     * @param key
     * @param members
     * @return
     */
    @Override
    public Long zrem(String key, String... members) {
        Jedis jedis = jedisPool.getResource();
        Long result = jedis.zrem(key, members);
        jedis.close();
        return result;
    }

    /**
     * 返回有序集中 指定区间内 的成员，通过索引，分数从高到低
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    @Override
    public Set<String> zrevrange(String key, Long start, Long end) {
        Jedis jedis = jedisPool.getResource();
        Set<String> results = jedis.zrevrange(key, start, end);
        jedis.close();
        return results;
    }

    /**
     * 返回有序集中 指定区间内 的成员(带分数)，通过索引，分数从高到低
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
        Jedis jedis = jedisPool.getResource();
        Set<Tuple> results = jedis.zrevrangeByScoreWithScores(key, max, min);
        jedis.close();
        return results;
    }

    /**
     * 返回有序集中 指定分数区间内 的成员，分数从高到低排序。
     * 带偏移量
     *
     * @param key
     * @param min
     * @param max
     * @param offset 从哪个偏移量开始取数据
     * @param count  取多少数据
     * @return
     */
    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
        Jedis jedis = jedisPool.getResource();
        Set<String> results = jedis.zrevrangeByScore(key, max, min, offset, count);
        jedis.close();
        return results;
    }

    /**
     * 返回有序集中 指定分数区间内 的成员，分数从高到低排序。
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min) {
        Jedis jedis = jedisPool.getResource();
        Set<String> results = jedis.zrevrangeByScore(key, max, min);
        jedis.close();
        return results;
    }

    /**
     * 返回有序集中 指定分数区间内 的成员(带分数)，分数从高到低排序。
     *
     * @param key
     * @param min
     * @param max
     * @param offset 从哪个偏移量开始取数据
     * @param count  取多少数据
     * @return
     */
    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
        Jedis jedis = jedisPool.getResource();
        Set<Tuple> results = jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
        jedis.close();
        return results;
    }

    /**
     * 返回 有序集中 分数值最高的成员(带分数)。
     * zrevrangeByScoreWithScores(key, Double.MAX_VALUE, Double.MIN_VALUE, 0, 1)
     *
     * @param key
     * @return
     */
    @Override
    public Set<Tuple> getZsetMaxScore(String key) {
        Jedis jedis = jedisPool.getResource();
        Set<Tuple> results =
                jedis.zrevrangeByScoreWithScores(key, Double.MAX_VALUE, Double.MIN_VALUE, 0, 1);
        jedis.close();
        return results;
    }

    /**
     * 获取 Zset 元素数量
     * zcount(key, Double.MIN_VALUE, Double.MAX_VALUE)
     *
     * @param key
     * @return
     */
    @Override
    public Long getZSetSize(String key) {
        Jedis jedis = jedisPool.getResource();
        Long zcount = jedis.zcount(key, Double.MIN_VALUE, Double.MAX_VALUE);
        jedis.close();
        return zcount;
    }

    /**
     * 获取指定区间 Zset 元素的数量
     * zcount(key, Double.MIN_VALUE, Double.MAX_VALUE)
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    @Override
    public Long getZSetSize(String key, Double min, Double max) {
        Jedis jedis = jedisPool.getResource();
        Long zcount = jedis.zcount(key, min, max);
        jedis.close();
        return zcount;
    }

    /**
     * 返回有序集中，成员的分数值。
     *
     * @param key
     * @param member
     * @return 如果成员元素不是有序集 key 的成员，或 key 不存在，返回 null
     */
    @Override
    public Double zscore(String key, String member) {
        Jedis jedis = jedisPool.getResource();
        Double zscore = jedis.zscore(key, member);
        jedis.close();
        return zscore;
    }

    /**
     * 返回 有序集合 中元素的排名信息
     *
     * @param key
     * @param member
     * @return
     */
    @Override
    public Long zrank(String key, String member) {
        Jedis jedis = jedisPool.getResource();
        Long zrank = jedis.zrank(key, member);
        jedis.close();
        return zrank;
    }

    /**
     * zremrangeByRank 命令用于移除有序集中，指定排名(rank)区间内的所有成员。
     *
     * @param key
     * @param start
     * @return 被移除成员的数量
     */
    @Override
    public Long zremrangeByRank(String key, Long start) {
        Jedis jedis = jedisPool.getResource();
        Long result = jedis.zremrangeByRank(key, start, -1L);
        jedis.close();
        return result;
    }

    /**
     * 通过字典区间返回有序集合的成员
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    @Override
    public Set<String> zrangeByLex(String key, String min, String max) {
        Jedis jedis = jedisPool.getResource();
        Set<String> result = jedis.zrangeByLex(key, min, max);
        jedis.close();
        return result;
    }

    /**
     * 通过字典区间返回有序集合的成员
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    @Override
    public Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
        Jedis jedis = jedisPool.getResource();
        Set<String> result = jedis.zrangeByLex(key, min, max, offset, count);
        jedis.close();
        return result;
    }

    /**
     * hset 命令用于为哈希表中的字段赋值 。
     * 如果字段已经存在于哈希表中，旧值将被覆盖。
     *
     * @param key
     * @param field
     * @param value
     */
    @Override
    public void hset(String key, String field, String value) {
        Jedis jedis = jedisPool.getResource();
        jedis.hset(key, field, value);
        jedis.close();
    }

    /**
     * hget 命令用于获取存储在哈希表中指定字段的值。
     *
     * @param key
     * @param field
     * @return
     */
    @Override
    public String hget(String key, String field) {
        Jedis jedis = jedisPool.getResource();
        String result = jedis.hget(key, field);
        jedis.close();
        return result;
    }

    /**
     * Hmset 命令用于同时将多个 field-value (字段-值)对设置到哈希表中。
     * 此命令会覆盖哈希表中已存在的字段。
     *
     * @param key
     * @param hash
     */
    @Override
    public void hmset(String key, Map<String, String> hash) {
        Jedis jedis = jedisPool.getResource();
        jedis.hmset(key, hash);
        jedis.close();
    }

    /**
     * 删除一个或多个哈希表字段
     *
     * @param key
     * @param fields
     * @return 被成功删除字段的数量，不包括被忽略的字段。
     */
    @Override
    public Long hdel(String key, String... fields) {
        Jedis jedis = jedisPool.getResource();
        Long result = jedis.hdel(key, fields);
        jedis.close();
        return result;
    }

    /**
     * Hmget 命令用于返回哈希表中，一个或多个给定字段的值。
     *
     * @param key
     * @param fields
     * @return 如果指定的字段不存在于哈希表，那么返回一个 nil 值。
     */
    @Override
    public List<String> hmget(String key, String... fields) {
        Jedis jedis = jedisPool.getResource();
        List<String> results = jedis.hmget(key, fields);
        jedis.close();
        return results;
    }

    /**
     * 获取在哈希表中指定 key 的所有字段和值
     *
     * @param key
     * @return
     */
    @Override
    public Map<String, String> hgetAll(String key) {
        Jedis jedis = jedisPool.getResource();
        Map<String, String> results = jedis.hgetAll(key);
        jedis.close();
        return results;
    }

    /**
     * Hexists 命令用于查看哈希表的指定字段是否存在。
     *
     * @param key
     * @param field
     * @return
     */
    @Override
    public Boolean hexists(String key, String field) {
        Jedis jedis = jedisPool.getResource();
        Boolean hexists = jedis.hexists(key, field);
        jedis.close();
        return hexists;
    }

    /**
     * 将信息发送到指定的频道
     *
     * @param channel
     * @param message
     */
    @Override
    public void publish(String channel, String message) {
        Jedis jedis = jedisPool.getResource();
        jedis.publish(channel, message);
        jedis.close();

    }

    /**
     * 订阅给定的频道的信息。
     *
     * @param pubSub
     * @param channel
     */
    @Override
    public void subscribe(JedisPubSub pubSub, String channel) {
        Jedis jedis = jedisPool.getResource();
        jedis.subscribe(pubSub, channel);
        jedis.close();

    }

    /**
     * @param key,min,max
     * @return 被移除成员的数量
     * @author Zpjeck
     * @date 2019/11/8
     * @Description: 删除 从min 到max 之间的数据 （zset）
     */
    @Override
    public Long zremrangeByRank(String key, Long min, Long max) {
        Jedis jedis = jedisPool.getResource();
        Long result = jedis.zremrangeByRank(key, min, max);
        jedis.close();
        return result;
    }

}
