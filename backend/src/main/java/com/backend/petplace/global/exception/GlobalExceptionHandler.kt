package com.backend.petplace.global.exception

import com.backend.petplace.global.response.ApiResponse
import com.backend.petplace.global.response.ErrorCode
import com.backend.petplace.global.response.ResponseCode
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException
    ): ResponseEntity<ApiResponse<String>?> {

        val errorMessage = ex.bindingResult
            .allErrors
            .joinToString(", ") { it.defaultMessage ?: "Validation error" }

        log.warn("Validation failed: {}", errorMessage)

        val response = ApiResponse(
            code = ResponseCode.BAD_REQUEST.code,
            message = ResponseCode.BAD_REQUEST.message,
            data = errorMessage
        )

        return ResponseEntity(response, ResponseCode.BAD_REQUEST.status)
    }

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(
        ex: BusinessException
    ): ResponseEntity<ApiResponse<Void?>> {

        val errorCode: ErrorCode = ex.errorCode
        log.error(
            "Business exception occurred: Code - {}, Message - {}",
            errorCode.code,
            errorCode.message
        )

        val response = ApiResponse.error<Void?>(errorCode)

        return ResponseEntity(response, errorCode.status)
    }
}
