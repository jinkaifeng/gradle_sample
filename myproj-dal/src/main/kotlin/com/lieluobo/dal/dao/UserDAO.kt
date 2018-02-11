package com.lieluobo.dal.dao

import com.lieluobo.dal.base.BaseMapper
import com.lieluobo.dal.model.User
import org.apache.ibatis.annotations.Param

interface UserDAO : BaseMapper<User> {

    fun findByName(@Param("name") name: String): List<User>
}