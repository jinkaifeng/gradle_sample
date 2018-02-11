package com.lieluobo.core.cache

import java.io.Serializable

interface CacheManager {

    /**
     * 保存至缓存 byte[]
     *
     * @param key
     * @param value
     * @param timeout 单位秒
     * @throws Exception
     */
    @Throws(Exception::class)
    operator fun set(key: String, value: ByteArray, timeout: Long)

    /**
     * 保存至缓存 byte[]
     *
     * @param key
     * @param value
     * @throws Exception
     */
    @Throws(Exception::class)
    operator fun set(key: String, value: ByteArray)

    /**
     * 保存至缓存 object 序列化
     *
     * @param key
     * @param object
     * @param timeout 单位秒
     * @throws Exception
     */
    @Throws(Exception::class)
    operator fun set(key: String, `object`: Serializable, timeout: Long)

    /**
     * 保存至缓存 object 序列化
     *
     * @param key
     * @param object
     * @throws Exception
     */
    @Throws(Exception::class)
    operator fun set(key: String, `object`: Serializable)

    /**
     * 保存至缓存 String类型
     *
     * @param key
     * @param value
     * @param timeout 单位秒
     * @throws Exception
     */
    @Throws(Exception::class)
    operator fun set(key: String, value: String, timeout: Long)

    /**
     * 保存至缓存 String类型
     *
     * @param key
     * @param value
     * @throws Exception
     */
    @Throws(Exception::class)
    operator fun set(key: String, value: String)

    /**
     * 从缓存中读取
     *
     * @param key
     * @return value
     * @throws Exception
     */
    @Throws(Exception::class)
    operator fun get(key: String): Any

    /**
     * 从缓存中读取 String返回值
     *
     * @param key
     * @return value
     * @throws Exception
     */
    @Throws(Exception::class)
    fun getString(key: String): String

    /**
     * 从缓存中读取 到固定的对象
     *
     * @param key
     * @param t
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    operator fun <T> get(key: String, t: Class<T>): T

    /**
     * 从缓存中移除
     *
     * @param key
     * @return result
     * @throws Exception
     */
    @Throws(Exception::class)
    fun del(key: String): Long


}
