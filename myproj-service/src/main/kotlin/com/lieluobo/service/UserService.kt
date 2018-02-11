package com.lieluobo.service

import com.baomidou.mybatisplus.service.IService
import com.lieluobo.dal.model.User

interface UserService : IService<User> {

    fun findUserById(id: Long): User

    fun findUserByName(name: String): List<User>

}