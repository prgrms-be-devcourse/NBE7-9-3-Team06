package com.backend.petplace.domain.pet.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Pattern
import java.time.LocalDate

@Schema(description = "반려동물 수정 요청 데이터")
data class UpdatePetRequest(
    //data class
    @field:Pattern(
        regexp = "^[a-zA-Z0-9가-힣]*$",
        message = "특수문자는 입력할 수 없습니다."
    )
    @Schema(description = "수정할 반려동물 이름 (특수문자 불가)")
    val name: String? = null,

    @field:Pattern(
        regexp = "Male|Female",
        message = "성별은, 남자, 여자 중 하나만 입력할 수 있습니다."
    )
    @field:Schema(description = "수정할 반려동물 성별 (남, 여 하나 선택 가능)")
    val gender: String? = null,

    @field:Schema(description = "수정할 반려동물 생년월일 (모를 경우 빈칸)")
    val birthDate: LocalDate? = null,

    @field:Schema(description = "수정할 반려동물 품종 (모를 경우 빈칸)")
    val type: String? = null
) {

    fun isValidName(): Boolean {
        return !name.isNullOrBlank()
    }
}
