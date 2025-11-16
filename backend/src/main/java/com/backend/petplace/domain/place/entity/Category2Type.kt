package com.backend.petplace.domain.place.entity

enum class Category2Type(
    val koLabel: String
) {
    VET_PHARMACY("동물약국"),
    MUSEUM("박물관"),
    CAFE("카페"),
    VET_HOSPITAL("동물병원"),
    PET_SUPPLIES("반려동물용품"),
    GROOMING("미용"),
    ART_CENTER("문예회관"),
    PENSION("펜션"),
    RESTAURANT("식당"),
    DESTINATION("여행지"),
    DAYCARE("위탁관리"),
    ART_MUSEUM("미술관"),
    ETC("기타")
}