package com.backend.petplace.domain.point.repository

import com.backend.petplace.domain.mypage.dto.MyPageUserPoints
import com.backend.petplace.domain.place.entity.Place
import com.backend.petplace.domain.point.dto.PointTransaction
import com.backend.petplace.domain.point.entity.Point
import com.backend.petplace.domain.review.entity.Review
import com.backend.petplace.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.security.core.parameters.P
import java.time.LocalDate

interface PointRepository : JpaRepository<Point, Long> {
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Point p WHERE p.user = :user AND p.rewardDate = :today")
    fun findTodaysPointsSumByUser(
        @Param("user") user: User,
        @Param("today") today: LocalDate
    ): Int // COALESCE 사용으로 무조건 0이상 나오므로 Int? 대신 Int 사용

    fun existsByUserAndPlaceAndRewardDate(user: User, place: Place, today: LocalDate): Boolean

    fun findByReview(review: Review?): Point? // Optional 제거

    @Query("""
        SELECT new com.backend.petplace.domain.mypage.dto.MyPageUserPoints(
            p.id, p.description, p.amount, p.createdDate
        ) 
        FROM Point p 
        WHERE p.user = :user
    """)
    fun findMyPagePointHistory(@Param("user") user: User): List<MyPageUserPoints> // null이 아닌 빈 리스트 반환하므로 ? 삭제

    @Query("""
        SELECT new com.backend.petplace.domain.point.dto.PointTransaction(
            p.id, pl.id, pl.name, pl.address, p.description, p.rewardDate, p.amount
        ) 
        FROM Point p 
        JOIN p.place pl 
        WHERE p.user = :user 
        ORDER BY p.id DESC
    """)
    fun findPointHistoryByUser(@Param("user") user: User): List<PointTransaction>
}
