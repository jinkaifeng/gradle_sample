package com.lieluobo.core.cache

import org.springframework.data.redis.serializer.RedisSerializer

import java.io.Serializable
import java.util.concurrent.TimeUnit

interface RedisCacheManager : CacheManager {

    /**
     * 获取DefaultSerializer
     *
     * @return JdkSerializationRedisSerializer
     */
    val defRedisSerializer: RedisSerializer<Any>?

    /**
     * 获取StringSerializer
     *
     * @return StringSerializer
     */
    val stringSerializer: RedisSerializer<String>


    /**
     * 模糊查询，获取匹配的key
     *
     * @param pattern
     * @return values
     * @throws Exception
     */
    @Throws(Exception::class)
    fun keys(pattern: String): Set<String>

    /**
     * hash set
     *
     * @param key
     * @param fieldKey
     * @param value
     * @return result
     * @throws Exception
     */
    @Throws(Exception::class)
    fun hSet(key: String, fieldKey: String, value: Serializable): Boolean

    /**
     * 多字段hashset,value为序列化对象
     *
     * @param key
     * @param fieldMap
     * @return result
     * @throws Exception
     */
    @Throws(Exception::class)
    fun <T : Serializable> hMSet(key: String, fieldMap: Map<String, T>)

    /**
     * 从缓存中的map中获取值,value为序列化对象
     *
     * @param key
     * @param fieldKey
     * @return value
     * @throws Exception
     */
    @Throws(Exception::class)
    fun hGet(key: String, fieldKey: String): Any

    /**
     * hash删除
     *
     * @param key
     * @param fieldKeys
     * @return result
     * @throws Exception
     */
    @Throws(Exception::class)
    fun hDel(key: String, vararg fieldKeys: String): Long

    /**
     * 获取hashMap所有对象
     *
     * @param key
     * @param clazz
     * @return map
     * @throws Exception
     */
    @Throws(Exception::class)
    fun <T> hGetAll(key: String, clazz: Class<T>): Map<String, T>

    /**
     * 队列移出
     *
     * @param key
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun leftPop(key: String): ByteArray

    /**
     * 加入队列
     *
     * @param key
     * @param value
     * @throws Exception
     */
    @Throws(Exception::class)
    fun rightPush(key: String, value: ByteArray): Boolean

    /**
     * 设置超时时间
     *
     * @param key
     * @param timeout
     * @param unit
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun expire(key: String, timeout: Long, unit: TimeUnit): Boolean

    /**
     * 设置超时时间 单位秒
     *
     * @param key
     * @param timeout
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun expire(key: String, timeout: Long): Boolean

    /**
     * 发布消息.
     *
     * @param channel
     * @param msg
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun publish(channel: String, msg: String): Long

    /**
     * set获取长度
     *
     * @param key
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun zSize(key: String): Long

    /**
     * set统计排序后的数据在min和max之间的数量
     *
     * @param key
     * @param min
     * @param max
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun zCount(key: String, min: Double, max: Double): Long

    @Throws(Exception::class)
    fun zRangeByScore(key: String, min: Double, max: Double, offset: Long,
                      count: Long): Set<ByteArray>

    /**
     * set移除
     *
     * @param key
     * @param value
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun zRemove(key: String, vararg value: ByteArray): Long

    /**
     * set添加或更新key的值
     *
     * @param key
     * @param value
     * @param score
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun zAdd(key: String, value: ByteArray, score: Double): Boolean

    /**
     * 获取并赋值
     *
     * @param key
     * @param values
     * @param time
     * @param timeUnit
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun getset(key: String, values: ByteArray, time: Long, timeUnit: TimeUnit): ByteArray

    /**
     * 获取并赋值
     *
     * @param key
     * @param values
     * @param time   单位秒
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun getset(key: String, values: ByteArray, time: Long): ByteArray

    /**
     * 获取值在min到max直接的key，且倒序排列
     *
     * @param key
     * @param min
     * @param max
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun zRevRangeByScore(key: String, min: Double, max: Double): Set<ByteArray>

    /**
     * 获取score在min到max直接的key，且倒序排列. offset count参数类似于 mysql 的 limit offset,count
     *
     * @param key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun zRevRangeByScore(key: String, min: Double, max: Double, offset: Long, count: Long): Set<ByteArray>

    /**
     * 取score介于min到max之间的key
     *
     * @param key
     * @param min
     * @param max
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun zRangeByScore(key: String, min: Double, max: Double): Set<ByteArray>

    /**
     * 删除多个key
     *
     * @param keys
     * @throws Exception
     */
    @Throws(Exception::class)
    fun delete(keys: List<String>)

    /**
     * 删除score介于min到max之间的key
     *
     * @param key
     * @param min
     * @param max
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun zRemoveRangeByScore(key: String, min: Double, max: Double): Long

    /**
     * 计算下一个递增key值
     *
     * @param key
     * @param delta
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun increment(key: String, delta: Long?): Long

    /**
     * 获取队列长度
     *
     * @param key
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun listSize(key: String): Long


}
