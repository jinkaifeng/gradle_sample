package com.lieluobo.core.constants

import com.lieluobo.core.constants.enums.ExceptionSeverityEnum

interface ExceptionConsts {
    companion object {
        val UnknownCode = "unknown"
        val UnknownMessage = "unknown"
        val ModuleBusiness = "business"
        val ModuleSystem = "system"
        val ModuleRemote = "remote"
        val SeveritySlight = ExceptionSeverityEnum.Slight
        val SeverityMiddle = ExceptionSeverityEnum.Middle
        val SeverityCritical = ExceptionSeverityEnum.Critical
        val manual: Short = 0
    }
}
