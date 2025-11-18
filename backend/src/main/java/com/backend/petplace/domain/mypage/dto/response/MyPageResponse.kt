package com.backend.petplace.domain.mypage.dto.response

import com.backend.petplace.domain.mypage.dto.MyPageUserInfo
import com.backend.petplace.domain.mypage.dto.MyPageUserPets
import com.backend.petplace.domain.mypage.dto.MyPageUserPoints
import com.backend.petplace.domain.review.dto.response.MyReviewResponse
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "마이페이지 조회용 API")
data class MyPageResponse(
    val userInfo: MyPageUserInfo,
    val reviews: List<MyReviewResponse>,
    val points: List<MyPageUserPoints>,
    val pets: List<MyPageUserPets>
)
