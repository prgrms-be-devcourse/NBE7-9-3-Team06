package com.backend.petplace.domain.mypage.dto

import com.backend.petplace.domain.user.entity.User
import java.time.LocalDateTime

data class MyPageUserInfo(
    val id: Long,
    val nickname: String,
    val userEmail: String,
    val createdDate: LocalDateTime?, //createdDate가 nullable이므로
    val address: String,
    val point: Int,
    val earnablePoints: Int,
    val totalReviews: Int
) {
    companion object {
        fun from(user: User, earnablePoints: Int, totalReviews: Int): MyPageUserInfo {
            return MyPageUserInfo(
                id = user.id!!,
                nickname = user.nickName,
                userEmail = user.email,
                createdDate = user.createdDate,
                address = "${user.address} ${user.addressDetail}",
                point = user.totalPoint,
                earnablePoints = earnablePoints,
                totalReviews = totalReviews
            )
        }
    }
}