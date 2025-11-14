package com.backend.petplace.domain.review.entity

import com.backend.petplace.domain.place.entity.Place
import com.backend.petplace.domain.point.entity.Point
import com.backend.petplace.domain.user.entity.User
import com.backend.petplace.global.entity.BaseEntity
import jakarta.persistence.*

@Entity
class Review (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reviewId")
    val id: Long? = null, // 코틀린 객체: DB 들어가기 전까지 ID가 없음을 표현

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "placeId", nullable = false)
    val place: Place,

    @Column(columnDefinition = "TEXT", nullable = false)
    var content: String,

    @Column(nullable = false)
    var rating: Int,

    var imageUrl: String?

) : BaseEntity() {

    @OneToOne(mappedBy = "review", fetch = FetchType.LAZY)
    val point: Point? = null

    companion object {
        @JvmStatic
        fun createNewReview(
            user: User,
            place: Place,
            content: String,
            rating: Int,
            imageUrl: String?
        ): Review? {
            return Review (
                user = user,
                place = place,
                content = content,
                rating = rating,
                imageUrl = imageUrl
            )
        }
    }
}
