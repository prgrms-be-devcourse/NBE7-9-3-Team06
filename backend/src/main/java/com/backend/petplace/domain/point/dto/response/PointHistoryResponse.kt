package com.backend.petplace.domain.point.dto.response

import com.backend.petplace.domain.point.dto.PointTransaction
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "내 포인트 내역 응답 DTO")
data class PointHistoryResponse(
    @Schema(description = "현재 보유중인 총 포인트", example = "1500")
    val totalPoints: Int,

    @Schema(description = "포인트 적립/사용 내역 목록")
    val history: List<PointTransaction>
) 