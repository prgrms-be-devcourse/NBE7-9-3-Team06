package com.backend.petplace.global.response


data class ApiResponse<T> (
    val code: String,
    val message: String,
    val data: T?
) {
    companion object {
        fun <T> success(data: T?): ApiResponse<T?> {
            return ApiResponse(ResponseCode.OK.code, ResponseCode.OK.message, data)
        }

        @JvmStatic
        fun <T> success(): ApiResponse<T?> {
            return ApiResponse(ResponseCode.OK.code, ResponseCode.OK.message, null)
        }

        fun <T> create(): ApiResponse<T?> {
            return ApiResponse(ResponseCode.CREATED.code,ResponseCode.CREATED.message,null)
        }

        fun <T> create(data: T?): ApiResponse<T?> {
            return ApiResponse(ResponseCode.CREATED.code, ResponseCode.CREATED.message, data)
        }

        fun <T> error(code: ResponseCode): ApiResponse<T?> {
            return ApiResponse(code.code, code.message, null)
        }

        @JvmStatic
        fun <T> error(code: ErrorCode): ApiResponse<T?> {
            return ApiResponse(code.code, code.message, null)
        }
    }
}