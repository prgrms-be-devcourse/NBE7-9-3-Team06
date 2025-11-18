package com.backend.petplace.domain.mypage.dto

import com.backend.petplace.domain.pet.entity.Gender
import java.time.LocalDate

data class MyPageUserPets(
    val id: Long,
    val name: String,
    val gender: Gender,
    val birthDate: LocalDate?,
    val type: String?
)
