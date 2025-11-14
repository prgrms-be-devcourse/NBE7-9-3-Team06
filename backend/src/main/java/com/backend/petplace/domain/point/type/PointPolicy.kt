package com.backend.petplace.domain.point.type

enum class PointPolicy(val value: Int) {
    DAILY_LIMIT(1000),
    REVIEW_PHOTO_POINTS(100),
    REVIEW_TEXT_POINTS(50)
}
