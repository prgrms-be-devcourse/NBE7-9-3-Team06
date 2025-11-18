package com.backend.petplace.domain.user.service

import com.backend.petplace.domain.email.repository.EmailAuthCodeRepository
import com.backend.petplace.domain.user.dto.request.UserLoginRequest
import com.backend.petplace.domain.user.dto.request.UserSignupRequest
import com.backend.petplace.domain.user.dto.response.BoolResultResponse
import com.backend.petplace.domain.user.dto.response.UserLoginResponse
import com.backend.petplace.domain.user.dto.response.UserSignupResponse
import com.backend.petplace.domain.user.entity.User
import com.backend.petplace.domain.user.repository.UserRepository
import com.backend.petplace.global.exception.BusinessException
import com.backend.petplace.global.jwt.JwtTokenProvider
import com.backend.petplace.global.response.ErrorCode
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val emailAuthCodeRepository: EmailAuthCodeRepository
) {
    @Transactional
    fun signup(request: UserSignupRequest): UserSignupResponse {
        validateDuplicateNickName(requireNotNull(request.nickName))
        validateDuplicateEmail(requireNotNull(request.email))

        checkAuthCode(request)

        val user = User.create(request, passwordEncoder.encode(request.password))
        userRepository.save(user)

        return UserSignupResponse(requireNotNull(user.id))
    }

    @Transactional
    protected fun checkAuthCode(request: UserSignupRequest) {
        val emailAuthCode = emailAuthCodeRepository.findByEmailAndAuthCode(
            requireNotNull(request.email),
            requireNotNull(request.authCode)
        ) ?: throw BusinessException(ErrorCode.AUTH_CODE_NOT_FOUND)

        if (emailAuthCode.verified) {
            emailAuthCodeRepository.delete(emailAuthCode)
            return
        }
        throw BusinessException(ErrorCode.AUTH_CODE_NOT_VERIFIED)
    }

    @Transactional(readOnly = true)
    fun validateDuplicateNickName(nickName: String): BoolResultResponse {
        if (userRepository.existsByNickName(nickName)) {
            throw BusinessException(ErrorCode.DUPLICATE_NICKNAME)
        }
        return BoolResultResponse(true)
    }

    @Transactional(readOnly = true)
    fun validateDuplicateEmail(email: String): BoolResultResponse {
        if (userRepository.existsByEmail(email)) {
            throw BusinessException(ErrorCode.DUPLICATE_EMAIL)
        }
        return BoolResultResponse(true)
    }

    @Transactional(readOnly = true)
    fun login(request: UserLoginRequest): UserLoginResponse {
        val user = userRepository.findByNickName(requireNotNull(request.nickName))
            ?: throw BusinessException(ErrorCode.BAD_CREDENTIAL)

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw BusinessException(ErrorCode.BAD_CREDENTIAL)
        }

        return UserLoginResponse(jwtTokenProvider.generateAccessToken(user.id!!))
    }
}