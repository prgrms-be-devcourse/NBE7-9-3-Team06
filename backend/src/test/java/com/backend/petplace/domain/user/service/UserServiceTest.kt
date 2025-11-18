package com.backend.petplace.domain.user.service

import com.backend.petplace.domain.email.entity.EmailAuthCode
import com.backend.petplace.domain.email.repository.EmailAuthCodeRepository
import com.backend.petplace.domain.user.dto.request.UserSignupRequest
import com.backend.petplace.domain.user.entity.User
import com.backend.petplace.domain.user.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.then
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var emailAuthCodeRepository: EmailAuthCodeRepository

    @Mock
    private lateinit var emailAuthCode: EmailAuthCode

    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userService = UserService(
            userRepository = userRepository,
            passwordEncoder = passwordEncoder,
            jwtTokenProvider = org.mockito.kotlin.mock(), // JwtTokenProvider는 mock 처리
            emailAuthCodeRepository = emailAuthCodeRepository
        )

        // save() 호출 시 user 객체의 id를 리플렉션으로 강제 세팅
        whenever(userRepository.save(any<User>())).thenAnswer { invocation ->
            val user = invocation.arguments[0] as User
            val field = user.javaClass.getDeclaredField("id")
            field.isAccessible = true
            field.set(user, 1L) // val이라도 리플렉션으로 값 주입 가능
            user
        }
    }

    @Test
    @DisplayName("회원가입 성공")
    fun signup_success() {
        // given
        val request = UserSignupRequest(
            nickName = "귀여운강아지",
            password = "asdf123!@",
            email = "email@naver.com",
            authCode = "1234567",
            address = "서울특별시 강남구 테헤란로 12-34",
            zipcode = "123456",
            addressDetail = "상세주소"
        )

        whenever(userRepository.existsByNickName("귀여운강아지")).thenReturn(false)
        whenever(userRepository.existsByEmail("email@naver.com")).thenReturn(false)
        whenever(passwordEncoder.encode("asdf123!@")).thenReturn("encodedPW")
        whenever(emailAuthCodeRepository.findByEmailAndAuthCode("email@naver.com", "1234567"))
            .thenReturn(emailAuthCode)
        whenever(emailAuthCode.verified).thenReturn(true)

        // when
        userService.signup(request)

        // then
        then(userRepository).should().save(any<User>())
    }
}