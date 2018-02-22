package com.lieluobo.web.controller

import com.alibaba.fastjson.JSON
import com.baomidou.mybatisplus.mapper.EntityWrapper
import com.baomidou.mybatisplus.toolkit.StringUtils
import com.example.demo.common.exception.DemoBusinessException
import com.example.demo.common.exception.DemoBusinessExceptionCode
import com.lieluobo.common.cache.RedisCacheManagerFactory
import com.lieluobo.common.constants.Constants
import com.lieluobo.dal.model.User
import com.lieluobo.service.UserService
import com.lieluobo.web.controller.req.SaveUserReq
import com.lieluobo.web.controller.resp.GatewayResp
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(Constants.USER)
class UserController {

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var factory: RedisCacheManagerFactory

    @RequestMapping(StringUtils.EMPTY, method = [RequestMethod.GET])
    fun findAll(): GatewayResp<List<User>> {
        return GatewayResp(this.userService.selectList(null))
    }

    @RequestMapping(Constants.GET_USER_BY_NAME, method = [RequestMethod.GET])
    fun findByLastName(@PathVariable name: String): GatewayResp<List<User>> {
        return GatewayResp(this.userService.findUserByName(name))
    }

    @RequestMapping(StringUtils.EMPTY, method = [RequestMethod.POST])
    fun save(@RequestBody req: SaveUserReq): GatewayResp<User> {
        val user = User(null, req.name, req.mobile, req.idCard)

        // 判断是否存在相同手机的用户
        var cond = User(null, null, req.mobile, null)
        var wrapper = EntityWrapper(cond)
        var list = userService.selectList(wrapper)
        if (list != null && list.size != 0) {
            throw Exception("手机号已存在！")
        }

        cond = User(null, req.name, null, null)
        println(cond)
        wrapper = EntityWrapper(cond)
        println(wrapper)
        list = userService.selectList(wrapper)
        if (list != null && list.size != 0) {
            throw Exception("姓名相同！")
        }

        this.userService.insert(user)
        return selectCache(user.id as Long)
    }

    @RequestMapping(Constants.UPDATE_USER, method = [RequestMethod.POST])
    fun update(@RequestBody req: SaveUserReq, @PathVariable("id") id: Long): GatewayResp<User> {
        val user = User(id, req.name, req.mobile
                , req.idCard)
        this.userService.updateById(user)
        factory.getCommRedis()!!.del("COMMON_USER_CACHE_BY_ID:" + id)
        return selectCache(id)
    }

    @RequestMapping(Constants.DELETE_USER, method = [RequestMethod.POST])
    fun delete(@PathVariable("id") id: Long): GatewayResp<User> {
        if (this.userService.deleteById(id)) {
            throw Exception("id不存在")
        }
        factory.getCommRedis()!!.del("COMMON_USER_CACHE_BY_ID:" + id)
        return selectCache(id)
    }

    /**
     * 第一次从数据库取,之后从缓存取
     */
    @RequestMapping(Constants.USER_CACHE, method = [RequestMethod.GET])
    fun selectCache(@PathVariable("id") id: Long): GatewayResp<User> {

        val manager = factory.getCommRedis()
        manager ?: throw DemoBusinessException(DemoBusinessExceptionCode.SYSTEM_ERROR)

        val str = manager.getString("COMMON_USER_CACHE_BY_ID:" + id)
        if (str.isNullOrEmpty()) {
            val user = this.userService.selectById(id)
            user ?: return GatewayResp()
            manager.set("COMMON_USER_CACHE_BY_ID:" + id, JSON.toJSONString(user))
            return GatewayResp(user)
        }
        val user = JSON.parseObject(str, User::class.java)
        return GatewayResp(user)
    }

}