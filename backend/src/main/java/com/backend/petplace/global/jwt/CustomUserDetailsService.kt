package com.backend.petplace.global.jwt

import com.backend.petplace.domain.user.repository.UserRepository
import com.backend.petplace.global.exception.BusinessException
import com.backend.petplace.global.response.ErrorCode
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(idAsString: String): UserDetails {
        val userId = idAsString.toLongOrNull()
            ?: throw BusinessException(ErrorCode.NOT_FOUND_MEMBER)

        val user = userRepository.findById(userId)
            .orElseThrow { BusinessException(ErrorCode.NOT_FOUND_MEMBER) }

        return CustomUserDetails(user)
    }
}