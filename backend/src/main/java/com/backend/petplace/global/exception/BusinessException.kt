package com.backend.petplace.global.exception

import com.backend.petplace.global.response.ErrorCode

class BusinessException : RuntimeException {
    val errorCode: ErrorCode

    constructor(errorCode: ErrorCode) :
            super(errorCode.message) {
        this.errorCode = errorCode
    }

    constructor(errorCode: ErrorCode, cause: Throwable?) :
            super(errorCode.message, cause) {
        this.errorCode = errorCode
    }
}
