package com.lieluobo.core.exception

import com.lieluobo.core.constants.enums.ExceptionSeverityEnum
import org.apache.commons.lang3.builder.ToStringBuilder


open class BusinessException : BaseException {

    constructor(code: String, message: String, severity: ExceptionSeverityEnum, cause: Throwable) : super("business", code, message, severity, cause) {}

    constructor(code: String, severity: ExceptionSeverityEnum, cause: Throwable) : super("business", code, null, severity, cause) {}

    constructor(code: String, message: String, severity: ExceptionSeverityEnum) : super("business", code, message, severity) {}

    constructor(code: String, severity: ExceptionSeverityEnum) : super("business", code, null, severity) {}

    constructor(code: String, message: String) : super("business", code, message, null) {}

    constructor(message: String) : super("business", null, message, null) {}

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }
}