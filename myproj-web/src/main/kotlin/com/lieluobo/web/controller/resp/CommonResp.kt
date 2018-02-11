package com.lieluobo.web.controller.resp

import io.swagger.annotations.ApiModelProperty


data class GatewayResp<T>(@ApiModelProperty("数据") var data: T? = null) {
    constructor(code: String, msg: String, data: T) : this(data) {
        this.code = code
        this.msg = msg
    }

    @ApiModelProperty(value = "编码 000000成功")
    var code = "000000"

    @ApiModelProperty(value = "消息")
    var msg = "成功"

    @ApiModelProperty(value = "是否成功，以此判定此次业务请求是否正常")
    var success = true
        get() = code == "000000"

    @ApiModelProperty(value = "时间戳，服务器时间")
    var timeStamp = System.currentTimeMillis().toString()

}