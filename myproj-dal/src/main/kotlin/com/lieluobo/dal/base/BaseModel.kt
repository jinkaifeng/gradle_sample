package com.lieluobo.dal.base

import com.baomidou.mybatisplus.annotations.TableId
import com.baomidou.mybatisplus.enums.IdType
import java.io.Serializable

open class BaseModel(@TableId(type = IdType.AUTO)
                     var id: Long? = 0) : Serializable {

}