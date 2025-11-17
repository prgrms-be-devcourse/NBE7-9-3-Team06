package com.backend.petplace.global.response

import org.springframework.http.HttpStatus

enum class ResponseCode(
    val code: String,
    val status: HttpStatus,
    val message: String

) {
    OK("200", HttpStatus.OK, "정상적으로 완료되었습니다."),
    CREATED("201", HttpStatus.CREATED, "정상적으로 생성되었습니다."),
    BAD_REQUEST("400", HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    UNAUTHORIZED("401", HttpStatus.UNAUTHORIZED, "권한 정보가 없습니다."),
    INTERNAL_SERVER_ERROR("500", HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러 입니다.");
}

