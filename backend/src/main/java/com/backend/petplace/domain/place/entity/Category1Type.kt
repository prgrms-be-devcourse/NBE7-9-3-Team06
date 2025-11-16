package com.backend.petplace.domain.place.entity

enum class Category1Type(
    val koLabel: String
) {
    PET_MEDICAL("반려의료"),
    PET_TRAVEL("반려동반여행"),
    PET_CAFE_RESTAURANT("반려동물식당카페"),
    PET_SERVICE("반려동물 서비스"),
    ETC("기타")
}