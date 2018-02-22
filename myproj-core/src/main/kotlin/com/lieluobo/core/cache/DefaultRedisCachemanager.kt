package com.lieluobo.core.cache


import com.alibaba.fastjson.JSON
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.TimeoutUtils
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.util.Assert
import org.springframework.util.CollectionUtils
import redis.clients.jedis.BinaryClient

import java.io.Serializable
import java.util.*
import java.util.concurrent.TimeUnit

class DefaultRedisCachemanager : RedisCacheManager {
    /**
     * RedisTemplate
     */
    /**
     * 设置 redisTemplate
     *
     * @param redisTemplate the redisTemplate to set
     */
    var redisTemplate: RedisTemplate<String, Serializable>? = null

    /**
     * 获取DefaultSerializer
     *
     * @return JdkSerializationRedisSerializer
     */
    /**
     * @param defRedisSerializer the defRedisSerializer to set
     */
    override var defRedisSerializer: RedisSerializer<Any>? = null
//        get() {
//            if (field == null) {
//                return redisTemplate!!.defaultSerializer as JdkSerializationRedisSerializer
//            } else return field
//        }

    fun getDefReSerializer(): RedisSerializer<Any>? {
        if (defRedisSerializer == null) {
            defRedisSerializer = redisTemplate!!.defaultSerializer as JdkSerializationRedisSerializer
        }
        return defRedisSerializer
    }

    var dbIndex: Int = 0

    /**
     * 获取StringSerializer
     *
     * @return StringSerializer
     */
    override val stringSerializer: RedisSerializer<String>
        get() = redisTemplate!!.stringSerializer

    fun init() {
        // set redisTemplate dbIndex
    }

    /**
     * @param pattern
     * @return values
     * @throws Exception
     */
    @Throws(Exception::class)
    override fun keys(pattern: String): Set<String> {
        return execute(RedisCallback<Set<String>> { connection ->
            selectDb(connection)
            val patternBytes = stringSerializer.serialize(pattern)
            val keysBytes = connection.keys(patternBytes) ?: return@RedisCallback emptySet()

            val keySet = HashSet<String>(keysBytes.size)
            for (bytes in keysBytes) {
                keySet.add(stringSerializer.deserialize(bytes))
            }
            keySet
        })

    }

    /**
     * 保存至缓存
     *
     * @param key
     * @param value
     * @param timeout 单位秒
     */
    @Throws(Exception::class)
    override fun set(key: String, value: ByteArray, timeout: Long) {
        if (value != null) {
            execute(RedisCallback<Any> { connection ->
                selectDb(connection)
                val keybytes = stringSerializer.serialize(key)
                connection.setEx(keybytes, timeout, value)
                null
            })
        }
    }

    /**
     * 保存至缓存
     *
     * @param key
     * @param value
     */
    @Throws(Exception::class)
    override fun set(key: String, value: ByteArray) {
        if (value != null) {
            execute(RedisCallback<Any> { connection ->
                selectDb(connection)
                val bytes = stringSerializer.serialize(key)
                connection.set(bytes, value)
                null
            })
        }
    }

    @Throws(Exception::class)
    override fun set(key: String, `object`: Serializable, timeout: Long) {
        this[key, getDefReSerializer()!!.serialize(`object`)] = timeout
    }

    @Throws(Exception::class)
    override fun set(key: String, `object`: Serializable) {
        this[key] = getDefReSerializer()!!.serialize(`object`)
    }

    /**
     * 从缓存中读取
     *
     * @param key
     * @return value
     */
    @Throws(Exception::class)
    override fun get(key: String): Any {
        return execute(RedisCallback<Any> { connection ->
            selectDb(connection)
            val keyBytes = stringSerializer.serialize(key)
            val valueBytes = connection.get(keyBytes)
            var o: Any? = null
            if (valueBytes != null) {
                try {
                    o = getDefReSerializer()!!.deserialize(valueBytes)
                } catch (e: Exception) {
                    // LOGGER.warn("不能反序列化，取原byte数组 key:" + key);
                    o = valueBytes
                }

            }
            o
        })
    }

    @Throws(Exception::class)
    override fun <T> get(key: String, t: Class<T>): T {
        val `object` = this[key]
        return if (`object` is ByteArray) {
            JSON.parseObject(String(`object`), t) as T
        } else {
            `object` as T
        }
    }

    /**
     * 从缓存中移除
     *
     * @param key
     * @throws Exception
     */
    @Throws(Exception::class)
    override fun del(key: String): Long {
        return execute(RedisCallback { connection ->
            selectDb(connection)
            connection.del(stringSerializer.serialize(key))
        })
    }

    @Throws(Exception::class)
    override fun hSet(key: String, fieldKey: String, value: Serializable): Boolean {
        return if (value != null) {
            execute(RedisCallback { connection ->
                selectDb(connection)
                connection.hSet(stringSerializer.serialize(key),
                        stringSerializer.serialize(fieldKey), getDefReSerializer()!!.serialize(value))
            })
        } else false
    }


    @Throws(Exception::class)
    override fun <T : Serializable> hMSet(key: String, fieldMap: Map<String, T>) {
        if (fieldMap != null && !fieldMap.isEmpty()) {
            execute(RedisCallback<Any> { connection ->
                selectDb(connection)
                val cacheValue = HashMap<ByteArray, ByteArray>(fieldMap.size)
                for (fk in fieldMap.keys) {
                    val bk = stringSerializer.serialize(fk)
                    val bv = getDefReSerializer()!!.serialize(fieldMap[fk])
                    cacheValue[bk] = bv
                }
                connection.hMSet(stringSerializer.serialize(key), cacheValue)
                null
            })
        }
    }

    @Throws(Exception::class)
    override fun hGet(key: String, fieldKey: String): Any {
        return execute(RedisCallback<Any> { connection ->
            selectDb(connection)
            val valueBytes = connection.hGet(stringSerializer.serialize(key),
                    stringSerializer.serialize(fieldKey))
            if (valueBytes != null && valueBytes.size > 0) {
                getDefReSerializer()!!.deserialize(valueBytes)
            } else null
        })
    }

    @Throws(Exception::class)
    override fun hDel(key: String, vararg fieldKeys: String): Long {
        return if (fieldKeys != null && fieldKeys.size > 0) {
            execute(RedisCallback { connection ->
                selectDb(connection)
                val fieldkeyBytes = arrayOfNulls<ByteArray>(fieldKeys.size)
                for (i in fieldKeys.indices) {
                    fieldkeyBytes[i] = stringSerializer.serialize(fieldKeys[i])
                }
                connection.hDel(stringSerializer.serialize(key), *fieldkeyBytes)
            })
        } else -1L
    }

    @Throws(Exception::class)
    override fun <T> hGetAll(key: String, clazz: Class<T>): Map<String, T> {
        return execute(RedisCallback<Map<String, T>> { connection ->
            selectDb(connection)
            val byteMap = connection.hGetAll(stringSerializer.serialize(key))
            if (byteMap != null && !byteMap.isEmpty()) {
                val valueMap = HashMap<String, T>()
                for (bk in byteMap.keys) {
                    val vk = stringSerializer.deserialize(bk)
                    val vv = getDefReSerializer()!!.deserialize(byteMap[bk]) as T
                    valueMap[vk] = vv
                }
                return@RedisCallback valueMap
            }
            emptyMap()
        })
    }

    @Throws(Exception::class)
    override fun leftPop(key: String): ByteArray {
        return execute(RedisCallback { connection ->
            selectDb(connection)
            val keyBytes = stringSerializer.serialize(key)
            connection.lPop(keyBytes)
        })
    }

    @Throws(Exception::class)
    override fun rightPush(key: String, value: ByteArray): Boolean {
        execute(RedisCallback { connection ->
            selectDb(connection)
            val keyBytes = stringSerializer.serialize(key)
            connection.rPush(keyBytes, value)
        })
        return true

    }

    @Throws(Exception::class)
    override fun expire(key: String, timeout: Long, unit: TimeUnit): Boolean {
        val rawTimeout = TimeoutUtils.toSeconds(timeout, unit)
        return execute(RedisCallback { connection ->
            selectDb(connection)
            val bytes = stringSerializer.serialize(key)
            connection.expire(bytes, rawTimeout)
        })
    }

    @Throws(Exception::class)
    override fun publish(channel: String, msg: String): Long {
        return execute(RedisCallback { connection ->
            selectDb(connection)
            var ret: Long = 0
            try {
                ret = connection.publish(stringSerializer.serialize(channel),
                        stringSerializer.serialize(msg))!!
            } catch (e: Exception) {
            }

            ret
        })

    }

    @Throws(Exception::class)
    override fun zSize(key: String): Long {
        return execute(RedisCallback { connection ->
            selectDb(connection)
            val keyBytes = stringSerializer.serialize(key)
            connection.zCard(keyBytes)
        })
    }

    @Throws(Exception::class)
    override fun zCount(key: String, min: Double, max: Double): Long {
        return execute(RedisCallback { connection ->
            selectDb(connection)
            connection.zCount(stringSerializer.serialize(key), min, max)
        })
    }

    @Throws(Exception::class)
    override fun zRangeByScore(key: String, min: Double, max: Double, offset: Long,
                               count: Long): Set<ByteArray> {
        return execute(RedisCallback { connection ->
            selectDb(connection)
            val keyBytes = stringSerializer.serialize(key)
            connection.zRangeByScore(keyBytes, min, max, offset, count)
        })
    }

    @Throws(Exception::class)
    override fun zRemove(key: String, vararg value: ByteArray): Long {
        return execute(RedisCallback { connection ->
            selectDb(connection)
            val keyBytes = stringSerializer.serialize(key)
            connection.zRem(keyBytes, *value)
        }, true)
    }

    @Throws(Exception::class)
    override fun zAdd(key: String, value: ByteArray, score: Double): Boolean {
        return execute(RedisCallback { connection ->
            selectDb(connection)
            val keyBytes = stringSerializer.serialize(key)
            connection.zAdd(keyBytes, score, value)
        }, true)
    }

    @Throws(Exception::class)
    override fun getset(key: String, values: ByteArray, time: Long, timeUnit: TimeUnit): ByteArray {
        val rawTimeout = TimeoutUtils.toSeconds(time, timeUnit)
        return execute(RedisCallback { connection ->
            selectDb(connection)
            val keyBytes = stringSerializer.serialize(key)
            val returnValue = connection.getSet(keyBytes, values)
            if (rawTimeout > 0) {
                connection.expire(keyBytes, rawTimeout)
            }
            returnValue
        })
    }

    @Throws(Exception::class)
    override fun zRevRangeByScore(key: String, min: Double, max: Double): Set<ByteArray> {
        return execute(RedisCallback { connection ->
            selectDb(connection)
            val keyBytes = stringSerializer.serialize(key)
            connection.zRevRangeByScore(keyBytes, min, max)
        })
    }

    @Throws(Exception::class)
    override fun zRevRangeByScore(key: String, min: Double, max: Double, offset: Long,
                                  count: Long): Set<ByteArray> {
        return execute(RedisCallback { connection ->
            selectDb(connection)
            val keyBytes = stringSerializer.serialize(key)
            connection.zRevRangeByScore(keyBytes, min, max, offset, count)
        })
    }

    @Throws(Exception::class)
    override fun zRangeByScore(key: String, min: Double, max: Double): Set<ByteArray> {
        return execute(RedisCallback { connection ->
            selectDb(connection)
            val keyBytes = stringSerializer.serialize(key)
            connection.zRangeByScore(keyBytes, min, max)
        })
    }

    @Throws(Exception::class)
    override fun delete(keys: List<String>) {
        if (CollectionUtils.isEmpty(keys)) {
            return
        }
        val rawKeys = rawKeys(keys)
        execute(RedisCallback<Any> { connection ->
            selectDb(connection)
            connection.del(*rawKeys)
            null
        }, true)

    }

    @Throws(Exception::class)
    override fun zRemoveRangeByScore(key: String, min: Double, max: Double): Long {
        return execute(RedisCallback { connection ->
            selectDb(connection)
            val keyBytes = stringSerializer.serialize(key)
            connection.zRemRangeByScore(keyBytes, min, max)
        })
    }

    @Throws(Exception::class)
    override fun increment(key: String, delta: Long?): Long {
        val rawKey = rawKey(key)
        return execute(RedisCallback { connection ->
            selectDb(connection)
            connection.incrBy(rawKey, delta!!)
        }, true)
    }

    internal fun rawKey(key: String): ByteArray {
        Assert.notNull(key, "non null key required")
        return if (this.redisTemplate!!.stringSerializer == null) {
            key.toByteArray()
        } else this.redisTemplate!!.stringSerializer.serialize(key)
    }

    private fun rawKeys(keys: Collection<String>): Array<ByteArray?> {
        val rawKeys = arrayOfNulls<ByteArray>(keys.size)

        var i = 0
        for (key in keys) {
            rawKeys[i++] = stringSerializer.serialize(key)
        }

        return rawKeys
    }

    /**
     * 切换DB
     *
     * @param connection
     */
    private fun selectDb(connection: RedisConnection?) {
        if (connection != null) {
            if (connection is BinaryClient) {
                val binaryClient = connection as BinaryClient?
                if (binaryClient!!.db == (dbIndex as Long)) {
                    return
                }
            }
            connection.select(dbIndex)
        }
    }

    /**
     * 执行redis
     *
     * @param action
     * @return T
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun <T> execute(action: RedisCallback<T>): T {
        try {
            return redisTemplate!!.execute(action)
        } catch (e: Exception) {
            LOGGER.error("执行redis发生异常:" + e.message, e)
            if (e is DataAccessException) {
                if (e.cause != null) {
                    LOGGER.error("执行redis发生DataAccessException.Cause" + e.message, e)
                } else {
                    LOGGER.error("执行redis发生DataAccessException:" + e.message, e)
                }
            }
            throw e
        }

    }

    /**
     * 执行redis
     *
     * @param action
     * @return T
     */
    private fun <T> execute(action: RedisCallback<T>, expose: Boolean): T {
        try {
            return redisTemplate!!.execute(action, expose)
        } catch (e: Exception) {
            LOGGER.error("执行redis发生异常:" + e.message, e)
            if (e is DataAccessException) {
                if (e.cause != null) {
                    LOGGER.error("执行redis发生DataAccessException.Cause" + e.message, e)
                } else {
                    LOGGER.error("执行redis发生DataAccessException:" + e.message, e)
                }
            }
            throw e
        }

    }

    @Throws(Exception::class)
    override fun listSize(key: String): Long {
        return execute(RedisCallback { connection ->
            selectDb(connection)
            val keyBytes = stringSerializer.serialize(key)
            connection.lLen(keyBytes)
        }, true)
    }

    @Throws(Exception::class)
    override fun expire(key: String, timeout: Long): Boolean {
        return this.expire(key, timeout, TimeUnit.SECONDS)
    }

    @Throws(Exception::class)
    override fun getset(key: String, values: ByteArray, time: Long): ByteArray {
        return this.getset(key, values, time, TimeUnit.SECONDS)
    }


    @Throws(Exception::class)
    override fun set(key: String, value: String, timeout: Long) {
        if (value != null) {
            execute(RedisCallback<Any> { connection ->
                selectDb(connection)
                val keybytes = stringSerializer.serialize(key)
                connection.setEx(keybytes, timeout, stringSerializer.serialize(value))
                null
            })
        }
    }


    @Throws(Exception::class)
    override fun set(key: String, value: String) {
        if (value != null) {
            execute(RedisCallback<String> { connection ->
                selectDb(connection)
                val bytes = stringSerializer.serialize(key)
                connection.set(bytes, stringSerializer.serialize(value))
                null
            })
        }
    }


    @Throws(Exception::class)
    override fun getString(key: String): String? {
        return execute(RedisCallback { connection ->
            selectDb(connection)
            val keyBytes = stringSerializer.serialize(key)
            val valueBytes = connection.get(keyBytes)
            if (valueBytes == null || valueBytes.isEmpty()) {
                null
            } else {
                stringSerializer.deserialize(valueBytes)
            }
        })
    }

    companion object {


        /**
         * 日志
         */
        private val LOGGER = LoggerFactory.getLogger(DefaultRedisCachemanager::class.java)
    }


}
