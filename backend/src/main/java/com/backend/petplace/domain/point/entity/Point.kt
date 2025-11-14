package com.backend.petplace.domain.point.entity

import com.backend.petplace.domain.place.entity.Place
import com.backend.petplace.domain.point.type.PointPolicy
import com.backend.petplace.domain.review.entity.Review
import com.backend.petplace.domain.user.entity.User
import com.backend.petplace.global.entity.BaseEntity
import jakarta.persistence.*
import java.time.LocalDate

@Table(
    name = "point",
    uniqueConstraints = [UniqueConstraint(
        name = "UK_point_review_id",
        columnNames = ["reviewId"]
    ), UniqueConstraint(
        name = "UK_point_user_place_date", columnNames = ["userId", "placeId", "rewardDate"]
    )]
)

@Entity
class Point (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pointId")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "placeId", nullable = false)
    val place: Place,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewId")
    val review: Review?,

    @Column(nullable = false)
    val amount: Int,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val description: PointDescription,

    @Column(nullable = false)
    val rewardDate: LocalDate

) : BaseEntity() {
    companion object {
        fun createFromReview(review: Review): Point {
            val hasImage = !review.imageUrl.isNullOrBlank()

            val amount =
                if (hasImage) PointPolicy.REVIEW_PHOTO_POINTS.getValue() else PointPolicy.REVIEW_TEXT_POINTS.getValue()
            val description =
                if (hasImage) PointDescription.REVIEW_PHOTO else PointDescription.REVIEW_TEXT

            return Point(
                user = review.user,
                place = review.place,
                review = review,
                amount = amount,
                description = description,
                rewardDate = LocalDate.now()
            )
        }
    }
}
