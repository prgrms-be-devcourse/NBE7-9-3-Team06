package com.backend.petplace.domain.point.service

import com.backend.petplace.domain.point.dto.response.PointHistoryResponse
import com.backend.petplace.domain.point.entity.Point
import com.backend.petplace.domain.point.repository.PointRepository
import com.backend.petplace.domain.point.type.PointAddResult
import com.backend.petplace.domain.point.type.PointPolicy
import com.backend.petplace.domain.review.entity.Review
import com.backend.petplace.domain.user.entity.User
import com.backend.petplace.domain.user.repository.UserRepository
import com.backend.petplace.global.exception.BusinessException
import com.backend.petplace.global.response.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class PointService (
    private val pointRepository: PointRepository, // null 초기화 후 !! 남발 방지
    private val userRepository: UserRepository
){
    @Transactional
    fun addPointsForReview(user: User, review: Review): PointAddResult {
        val today = LocalDate.now()

        // 생성자 주입을 받으므로 null이 될 수 없음 -> !! 제거
        if (pointRepository.existsByUserAndPlaceAndRewardDate(user, review.place, today)) { // review.place (프로퍼티 접근)
            return PointAddResult.ALREADY_AWARDED
        }

        val todaysPoints = pointRepository.findTodaysPointsSumByUser(user, today)

        if (todaysPoints >= PointPolicy.DAILY_LIMIT.value) {
            return PointAddResult.DAILY_LIMIT_EXCEEDED
        }

        val point = Point.createFromReview(review)

        pointRepository.save(point)
        user.addPoints(point.amount)

        return PointAddResult.SUCCESS
    }

    @Transactional(readOnly = true)
    fun getPointHistory(userId: Long): PointHistoryResponse {
        val user = findUserById(userId)

        val history = pointRepository.findPointHistoryByUser(user) // 레포지토리와 타입 일치
        return PointHistoryResponse(user.totalPoint, history)
    }

    private fun findUserById(userId: Long): User {
        return userRepository.findById(userId) // Optional 제거
            .orElseThrow{ BusinessException(ErrorCode.NOT_FOUND_MEMBER) } // { } 자체가 람다이므로 Supply 불필요
    }
}