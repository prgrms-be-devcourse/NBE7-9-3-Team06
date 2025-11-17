package com.backend.petplace.domain.place.entity.mapper

import com.backend.petplace.domain.place.entity.Category1Type
import com.backend.petplace.domain.place.entity.Category2Type

object CategoryMapper {

    fun mapCategory1(koLabel: String?): Category1Type =
        when (koLabel?.trim()) {
            "반려의료" -> Category1Type.PET_MEDICAL
            "반려동반여행" -> Category1Type.PET_TRAVEL
            "반려동물식당카페" -> Category1Type.PET_CAFE_RESTAURANT
            "반려동물 서비스" -> Category1Type.PET_SERVICE
            else -> Category1Type.ETC
        }

    fun mapCategory2(koLabel: String?): Category2Type =
        when (koLabel?.trim()) {
            "동물약국" -> Category2Type.VET_PHARMACY
            "박물관" -> Category2Type.MUSEUM
            "카페" -> Category2Type.CAFE
            "동물병원" -> Category2Type.VET_HOSPITAL
            "반려동물용품" -> Category2Type.PET_SUPPLIES
            "미용" -> Category2Type.GROOMING
            "문예회관" -> Category2Type.ART_CENTER
            "펜션" -> Category2Type.PENSION
            "식당" -> Category2Type.RESTAURANT
            "여행지" -> Category2Type.DESTINATION
            "위탁관리" -> Category2Type.DAYCARE
            "미술관" -> Category2Type.ART_MUSEUM
            else -> Category2Type.ETC
        }
}
