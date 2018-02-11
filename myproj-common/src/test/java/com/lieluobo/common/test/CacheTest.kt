package com.lieluobo.common.test

import com.lieluobo.common.cache.RedisCacheManagerFactory
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
@ContextConfiguration(locations = ["classpath*:spring/myproj-test.xml", "classpath*:spring/myproj-redis.xml"])
class CacheTest {

    @Autowired
    lateinit var factory: RedisCacheManagerFactory

    @Test
    fun contextLoads() {
        factory.getCommRedis()!!.set("11", "11")
        println(factory.getCommRedis()!!.getString("11"))
    }
}