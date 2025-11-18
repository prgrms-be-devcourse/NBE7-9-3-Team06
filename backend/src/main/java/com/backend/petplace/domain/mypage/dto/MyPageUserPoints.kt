package com.backend.petplace.domain.mypage.dto

import com.backend.petplace.domain.point.entity.PointDescription
import java.time.LocalDateTime

data class MyPageUserPoints(
    val id: Long,
    val description: PointDescription,
    val amount: Int,
    val createdDate: LocalDateTime
)
