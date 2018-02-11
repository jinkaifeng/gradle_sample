package com.lieluobo.service.test

import com.lieluobo.common.cache.RedisCacheManagerFactory
import com.lieluobo.dal.dao.UserDAO
import com.lieluobo.service.UserService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
@ContextConfiguration(locations = ["classpath*:spring/myproj-*.xml"])
class CacheTest {

    @Autowired
    lateinit var factory: RedisCacheManagerFactory

    @Autowired
    lateinit var userDAO: UserDAO

    @Autowired
    lateinit var userService: UserService

    @Test
    fun contextLoads() {
        factory.getCommRedis()!!.set("11", "11")
        println(factory.getCommRedis()!!.getString("11"))
    }

    @Test
    fun contextLoads2() {
        println(userDAO.findByName("金凯峰"))
    }

    @Test
    fun contextLoads3() {
        println(userService.findUserByName("金凯峰"))
    }
}