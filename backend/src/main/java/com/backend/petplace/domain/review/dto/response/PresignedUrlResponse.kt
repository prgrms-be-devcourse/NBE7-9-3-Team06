package com.backend.petplace.domain.review.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Presigned URL 응답 DTO")
data class PresignedUrlResponse (
    @Schema(description = "클라이언트가 PUT 요청 보낼 URL")
    val presignedUrl: String,

    @Schema(description = "업로드 완료 후 서버에 전달할 S3 파일 경로")
    val s3FilePath: String
)
