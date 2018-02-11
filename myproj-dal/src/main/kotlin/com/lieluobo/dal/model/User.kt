package com.lieluobo.dal.model

import com.baomidou.mybatisplus.annotations.TableField
import com.baomidou.mybatisplus.annotations.TableName
import com.lieluobo.dal.base.BaseModel

@TableName("t_user")
class User(
        id: Long?,
        var name: String?,
        var mobile: String?,
        @TableField("id_card")
        var idCard: String?) : BaseModel(id) {
    override fun toString(): String {
        return "User(id=${super.id}, name=$name, mobile=$mobile, idCard=$idCard)"
    }
}