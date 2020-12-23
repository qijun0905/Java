package com.dfundata.fund.redis;

import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * jedis cache 操作  接口规范
 */
public interface JedisCache {

    /**
     * 删除 key
     *
     * @param key
     */
    void del(String key);

    /**
     * 检查 key 是否存在Sismember
     *
     * @param key
     * @return
     */
    boolean exists(String key);

    /**
     * 设置 key 的 有效期的时长
     *
     * @param key
     * @param millisecondsTimestamp 将key的过期时间设置为timestamp所代表的的毫秒数的时间戳
     */
    Long pexpireAt(String key, Long millisecondsTimestamp);

    /**
     * 当 key 不存在时，返回 -2 。
     * 当 key 存在但没有设置剩余生存时间时，返回 -1 。
     * 否则，以秒为单位，返回 key 的剩余生存时间。
     *
     * @param key
     * @return
     */
    Long ttl(String key);

    /**
     * 得到 key 开头的key
     *
     * @param key
     * @return
     */
    Set<String> keysStart(String key);

    /**
     * 得到 含有 key 的 key
     *
     * @param key
     * @return
     */
    Set<String> hasKeys(String key);

    //=================Redis String 操作开始=======================

    /**
     * 设置指定 key 的值
     *
     * @param key
     * @param value
     * @return
     */
    String set(String key, String value);

    /**
     * 获取指定 key 的值。
     *
     * @param key
     * @return
     */
    String get(String key);


    //=================Redis String 操作结束=======================

    /**
     * List
     * Redis 列表是简单的字符串列表，按照插入顺序排序。
     * 你可以添加一个元素导列表的头部（左边）或者尾部（右边）。
     * 它的底层实际是个链表.
     * 它是一个字符串链表，left、right都可以插入添加；
     * 如果键不存在，创建新的链表；
     * 如果键已存在，新增内容；
     * 如果值全移除，对应的键也就消失了。
     * 链表的操作无论是头和尾效率都极高，但假如是对中间元素进行操作，效率就很惨淡了。
     */
    //=================Redis List 操作开始 ========================

    /**
     * 将一个或多个值插入到列表头部
     * 执行 lpush 命令后，列表的长度
     *
     * @param key
     * @param values
     */
    Long lpush(String key, String... values);

    /**
     * 获取列表指定范围内的元素
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    List<String> lrange(String key, Long start, Long end);

    /**
     * 获取列表长度
     *
     * @param key
     * @return
     */
    Long llen(String key);

    //=================Redis List 操作结束========================
    //=================Redis Set 操作开始 ========================

    /**
     * Sadd 命令将一个或多个成员元素加入到集合中，已经存在于集合的成员元素将被忽略。
     *
     * @param key
     * @param member
     * @return 被添加到集合中的新元素的数量，不包括被忽略的元素。
     */
    Long sadd(String key, String member);

    /**
     * smembers命令获取set集合中所有元素
     *
     * @param key
     * @return 返回Set集合的所有元素
     */
    Set<String> smembers(String key);

    /**
     * set集合中是否包含 member
     *
     * @param key
     * @param member
     * @return true or false
     */
    Boolean sismember(String key, String member);


    //=================Redis Set 操作结束    ========================
    /**
     * Redis SortSet 和 set 一样也是string类型元素的集合,且不允许重复的成员。
     * 不同的是每个元素都会关联一个double类型的分数。
     * redis正是通过分数来为集合中的成员进行从小到大的排序。
     * SortSet的成员是唯一的,但分数(score)却可以重复。
     */
    //=================Redis SortSet 操作开始========================

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
    Double zincrby(String key, double increment, String member);

    /**
     * 向有序集合添加一个成员，或者更新已存在成员的分数
     *
     * @param key
     * @param score
     * @param value
     * @return 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。
     */
    Long zadd(String key, double score, String value);

    /**
     * 删除有序集合中一个或者多个元素
     *
     * @param key
     * @param members
     * @return
     */
    Long zrem(String key, String... members);

    /**
     * 返回有序集中 指定区间内 的成员，通过索引，分数从高到低
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    Set<String> zrevrange(String key, Long start, Long end);

    /**
     * 返回有序集中 指定区间内 的成员(带分数)，通过索引，分数从高到低
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min);


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
    Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count);

    /**
     * 返回有序集中 指定分数区间内 的成员，分数从高到低排序。
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    Set<String> zrevrangeByScore(String key, double max, double min);

    /**
     * 返回有序集中 指定分数区间内 的成员(带分数)，分数从高到低排序。
     *
     * @param key
     * @param max
     * @param min
     * @param offset 从哪个偏移量开始取数据
     * @param count  取多少数据
     * @return
     */
    Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count);


    /**
     * 返回 有序集中 分数值最高的成员(带分数)。
     * zrevrangeByScoreWithScores(key, Double.MAX_VALUE, Double.MIN_VALUE, 0, 1)
     *
     * @param key
     * @return
     */
    Set<Tuple> getZsetMaxScore(String key);

    /**
     * 获取 Zset 元素数量
     * zcount(key, Double.MIN_VALUE, Double.MAX_VALUE)
     *
     * @param key
     * @return
     */
    Long getZSetSize(String key);

    /**
     * 获取指定区间 Zset 元素的数量
     * zcount(key, Double.MIN_VALUE, Double.MAX_VALUE)
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    Long getZSetSize(String key, Double min, Double max);


    /**
     * 返回有序集中，成员的分数值。
     *
     * @param key
     * @param member
     * @return 如果成员元素不是有序集 key 的成员，或 key 不存在，返回 null
     */
    Double zscore(String key, String member);

    /**
     * 返回 有序集合 中元素的排名信息
     *
     * @param key
     * @param member
     * @return
     */
    Long zrank(String key, String member);

    /**
     * zremrangeByRank 命令用于移除有序集中，指定排名(rank)区间内的所有成员。
     *
     * @param key
     * @param start
     * @return 被移除成员的数量
     */
    Long zremrangeByRank(String key, Long start);

    /**
     * 通过字典区间返回有序集合的成员
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    Set<String> zrangeByLex(String key, String min, String max);

    /**
     * 通过字典区间返回有序集合的成员
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    Set<String> zrangeByLex(String key, String min, String max, int offset, int count);

    //=================Redis SortSet 操作结束========================
    /**
     * Redis hash 是一个键值对集合。
     * Redis hash是一个string类型的field和value的映射表，hash特别适合用于存储对象。
     *
     * kv模式不变，但v是一个键值对
     *
     * 类似Java里面的Map<String,Object>
     */
    //=================Redis hash 操作开始   ========================

    /**
     * hset 命令用于为哈希表中的字段赋值 。
     * 如果字段已经存在于哈希表中，旧值将被覆盖。
     *
     * @param key
     * @param field
     * @param value
     */
    void hset(String key, String field, String value);

    /**
     * hget 命令用于获取存储在哈希表中指定字段的值。
     *
     * @param key
     * @param field
     * @return
     */
    String hget(String key, String field);

    /**
     * Hmset 命令用于同时将多个 field-value (字段-值)对设置到哈希表中。
     * 此命令会覆盖哈希表中已存在的字段。
     *
     * @param key
     * @param hash
     */
    void hmset(String key, Map<String, String> hash);

    /**
     * 删除一个或多个哈希表字段
     *
     * @param key
     * @param fields
     * @return 被成功删除字段的数量，不包括被忽略的字段。
     */
    Long hdel(String key, String... fields);

    /**
     * Hmget 命令用于返回哈希表中，一个或多个给定字段的值。
     *
     * @param key
     * @param fields
     * @return 如果指定的字段不存在于哈希表，那么返回一个 nil 值。
     */
    List<String> hmget(String key, String... fields);

    /**
     * 获取在哈希表中指定 key 的所有字段和值
     *
     * @param key
     * @return
     */
    Map<String, String> hgetAll(String key);

    /**
     * Hexists 命令用于查看哈希表的指定字段是否存在。
     *
     * @param key
     * @param field
     * @return
     */
    Boolean hexists(String key, String field);

    //=================Redis hash 操作结束   ========================

    //=================发布订阅======================================

    /**
     * 将信息发送到指定的频道
     *
     * @param channel
     * @param message
     */
    void publish(String channel, String message);

    /**
     * 订阅给定的频道的信息。
     *
     * @param pubSub
     * @param channel
     */
    void subscribe(JedisPubSub pubSub, String channel);

    /**
     * @param key,min,max
     * @return 被移除成员的数量
     * @author Zpjeck
     * @date 2019/11/8
     * @Description: 删除 从min 到max 之间的数据 （zset）
     */
    Long zremrangeByRank(String key, Long min, Long max);
}
