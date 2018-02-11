package com.example.demo.common.exception

import org.apache.commons.lang3.StringUtils


enum class DemoBusinessExceptionCode(val code: String, val text: String) {
    RPC_ERROR("-999998", " 远程调用异常"),
    SYSTEM_ERROR("-999999", " 系统异常");

    /**
     * 通过code获取对应名称描述
     *
     * @param code
     * @return name
     */
    fun getText(code: Byte?): String {
        if (code == null) {
            return StringUtils.EMPTY
        }
        for (em in DemoBusinessExceptionCode.values()) {
            if (em.code.equals(code)) {
                return em.text
            }
        }
        return StringUtils.EMPTY
    }
}