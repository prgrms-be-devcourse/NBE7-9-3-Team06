package com.backend.petplace.domain.user.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UserLoginRequest (
    @NotBlank(message = "이름은 필수입니다.")
    @Size(min = 2, max = 12, message = "이름은 2 ~ 12자까지 가능합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]+$", message = "영문, 숫자, 한글만 사용할 수 있습니다.")
    val nickName: String?,

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 12, message = "비밀번호는 8 ~ 12자까지 가능합니다.")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+{}\\[\\]:;<>,.?~\\-=/])[A-Za-z\\d!@#$%^&*()_+{}\\[\\]:;<>,.?~\\-=/]+$",
        message = "영어, 숫자, 특수문자를 모두 포함해야 합니다.")
    val password: String?
)