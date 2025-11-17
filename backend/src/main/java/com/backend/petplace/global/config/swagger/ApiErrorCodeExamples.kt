package com.backend.petplace.global.config.swagger

import com.backend.petplace.global.response.ErrorCode

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiErrorCodeExamples(
    vararg val value: ErrorCode
)
