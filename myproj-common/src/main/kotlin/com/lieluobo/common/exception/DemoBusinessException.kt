package com.example.demo.common.exception

import com.lieluobo.core.exception.BusinessException

class DemoBusinessException : BusinessException {

    constructor (message: String) : super(message)

    constructor(code: DemoBusinessExceptionCode) : super(code.code, code.text)

    constructor(code: String, text: String) : super(code, text)

}