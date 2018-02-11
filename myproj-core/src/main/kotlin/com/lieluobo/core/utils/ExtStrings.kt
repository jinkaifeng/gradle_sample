package com.lieluobo.core.utils

import org.apache.commons.lang3.StringUtils

object ExtStrings {
    fun clean(clean: String?): String {
        return if (StringUtils.isEmpty(clean)) "" else clean!!.trim()
    }

    fun cleanAs(clean: String?, asDefault: String): String {
        return if (StringUtils.isEmpty(clean)) asDefault.trim() else clean!!.trim()
    }
}
