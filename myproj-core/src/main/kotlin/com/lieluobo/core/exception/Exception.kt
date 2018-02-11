package com.lieluobo.core.exception

import com.lieluobo.core.constants.ExceptionConsts
import com.lieluobo.core.constants.enums.ExceptionSeverityEnum
import com.lieluobo.core.utils.ExtStrings
import org.apache.commons.lang3.builder.ToStringBuilder

open class BaseException : Exception {
    var module = "unknown"
    var code = "unknown"
    var severity = ExceptionConsts.SeveritySlight
    var isHandled = false

    constructor() {}

    constructor(module: String, code: String, message: String?, severity: ExceptionSeverityEnum?, cause: Throwable) : super(ExtStrings.cleanAs(message, "unknown"), cause) {
        this.module = ExtStrings.cleanAs(module, "unknown")
        this.code = ExtStrings.cleanAs(code, "unknown")
        if (severity != null) {
            this.severity = severity
        }
    }

    constructor(module: String, code: String?, message: String?, severity: ExceptionSeverityEnum?) : super(ExtStrings.cleanAs(message, "unknown")) {
        this.module = ExtStrings.cleanAs(module, "unknown")
        this.code = ExtStrings.cleanAs(code, "unknown")
        if (severity != null) {
            this.severity = severity
        }
    }

    constructor(code: String, message: String?, severity: ExceptionSeverityEnum?) : super(ExtStrings.cleanAs(message, "unknown")) {
        this.code = ExtStrings.cleanAs(code, "unknown")
        if (severity != null) {
            this.severity = severity
        }
    }

    constructor(code: String?, severity: ExceptionSeverityEnum?) {
        this.code = ExtStrings.cleanAs(code, "unknown")
        if (severity != null) {
            this.severity = severity
        }
    }

    constructor(code: String, message: String?) : super(ExtStrings.cleanAs(message, "unknown")) {
        this.code = ExtStrings.cleanAs(code, "unknown")
    }

    constructor(message: String) : super(ExtStrings.cleanAs(message, "unknown"))

    override fun toString(): String {
        return ToStringBuilder.reflectionToString(this)
    }

}
