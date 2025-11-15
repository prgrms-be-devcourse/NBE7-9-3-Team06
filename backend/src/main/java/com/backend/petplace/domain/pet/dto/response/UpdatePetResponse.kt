package com.backend.petplace.domain.pet.dto.response

import com.backend.petplace.domain.pet.entity.Gender
import com.backend.petplace.domain.pet.entity.Pet
import java.time.LocalDate

class UpdatePetResponse(

    val id : Long?,
    val name : String?,
    val gender : Gender?,
    val birthDate : LocalDate?,
    val type : String?
)
{
    companion object{
        @JvmStatic
        fun from(pet : Pet) : UpdatePetResponse {
            return UpdatePetResponse(
                id = pet.id,
                name = pet.name,
                gender = pet.gender,
                birthDate = pet.birthDate,
                type = pet.type
            )
        }
    }
}