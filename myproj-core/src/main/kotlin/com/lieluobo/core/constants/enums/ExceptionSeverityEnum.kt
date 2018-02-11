package com.lieluobo.core.constants.enums


import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.builder.ToStringBuilder

enum class ExceptionSeverityEnum private constructor(val code: String, val text: String, val value: Int) {
    Critical("C1", "critical", 1), Middle("M2", "middle", 2), Slight("S3", "slight", 3);

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }

    companion object {

        fun getByCode(code: String): ExceptionSeverityEnum? {
            if (StringUtils.isEmpty(code)) {
                return null
            }
            for (severity in values()) {
                if (severity.code == code) {
                    return severity
                }
            }
            return null
        }

        fun getByName(name: String): ExceptionSeverityEnum? {
            if (StringUtils.isEmpty(name)) {
                return null
            }
            for (severity in values()) {
                if (severity.name == name) {
                    return severity
                }
            }
            return null
        }

        fun getByValue(value: Int): ExceptionSeverityEnum? {
            for (severity in values()) {
                if (severity.value == value) {
                    return severity
                }
            }
            return null
        }
    }
}
