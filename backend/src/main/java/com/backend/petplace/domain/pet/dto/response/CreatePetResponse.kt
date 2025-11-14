package com.backend.petplace.domain.pet.dto.response

import com.backend.petplace.domain.pet.entity.Gender
import com.backend.petplace.domain.pet.entity.Pet
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "반려동물 생성 확인용 응답 데이터")
data class CreatePetResponse(
    val id: Long?,
    val name: String?,
    val gender: Gender?,
    val birthDate: LocalDate?,
    val type: String?
) {
    companion object {
        @JvmStatic
        fun from(pet: Pet): CreatePetResponse {
            return CreatePetResponse(
                id = pet.id,
                name = pet.name,
                gender = pet.gender,
                birthDate = pet.birthDate,
                type = pet.type
            )
        }
    }
}