package com.lieluobo.common.cache

import com.lieluobo.core.cache.DefaultRedisCachemanager
import com.lieluobo.core.cache.RedisCacheManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.io.Serializable

@Service
class RedisCacheManagerFactory {

    @Autowired
    lateinit var commonCacheRedisTemplate: RedisTemplate<String, Serializable>

    var default = DefaultRedisCachemanager()

    /**
     * 平台公共缓存
     */
    var commonCacheRedisManager: RedisCacheManager? = null

    /**
     * 会话缓存
     */
    var sessionCacheRedisManager: RedisCacheManager? = null

    /**
     * 默认缓存
     */
    var defaultCacheRedisManager: RedisCacheManager? = null


    private fun createRedisManager(dbIndex: Int): RedisCacheManager {

        val redisManager = DefaultRedisCachemanager()
        redisManager.redisTemplate = commonCacheRedisTemplate
        redisManager.dbIndex = dbIndex
        return redisManager
    }

    fun getCommRedis(): RedisCacheManager? {
        if (commonCacheRedisManager == null) {
            commonCacheRedisManager = createRedisManager(0)
        }
        return commonCacheRedisManager
    }

    fun getSessionRedis(): RedisCacheManager? {
        if (sessionCacheRedisManager == null) {
            sessionCacheRedisManager = createRedisManager(5)
        }
        return sessionCacheRedisManager
    }

    fun getDefaultRedis(): RedisCacheManager? {
        if (defaultCacheRedisManager == null) {
            defaultCacheRedisManager = createRedisManager(6)
        }
        return defaultCacheRedisManager
    }

}
