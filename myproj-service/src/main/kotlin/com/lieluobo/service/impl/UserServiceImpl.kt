package com.lieluobo.service.impl

import com.baomidou.mybatisplus.service.impl.ServiceImpl
import com.example.demo.common.exception.DemoBusinessException
import com.lieluobo.dal.dao.UserDAO
import com.lieluobo.dal.model.User
import com.lieluobo.service.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


/**
 *
 *
 * 服务实现类
 *
 *
 * @author kaifengjin
 * @since 2018-02-01
 */
@Service
class UserServiceImpl : ServiceImpl<UserDAO, User>(), UserService {

    override fun findUserById(id: Long): User {
        return baseMapper.selectById(id)
    }

    @Transactional(rollbackFor = [DemoBusinessException::class])
    override fun findUserByName(name: String): List<User> {
        return baseMapper.findByName(name)
    }
}