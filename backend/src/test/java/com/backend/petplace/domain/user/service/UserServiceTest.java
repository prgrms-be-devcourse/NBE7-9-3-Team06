package com.backend.petplace.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

import com.backend.petplace.domain.email.entity.EmailAuthCode;
import com.backend.petplace.domain.email.repository.EmailAuthCodeRepository;
import com.backend.petplace.domain.email.service.EmailAuthCodeService;
import com.backend.petplace.domain.user.dto.request.UserSignupRequest;
import com.backend.petplace.domain.user.dto.response.UserSignupResponse;
import com.backend.petplace.domain.user.entity.User;
import com.backend.petplace.domain.user.repository.UserRepository;
import com.backend.petplace.global.jwt.JwtTokenProvider;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private EmailAuthCodeRepository emailAuthCodeRepository;

  @Mock
  private EmailAuthCode emailAuthCode;

  @InjectMocks
  private UserService userService;

  @Test
  @DisplayName("회원가입 성공")
  void signup_success() {
    // given
    UserSignupRequest request = new UserSignupRequest
        ("귀여운강아지", "asdf123!@",
            "email@naver.com", "1234567",
            "서울특별시 강남구 테헤란로 12-34",
            "123456", "상세주소"
        );
    given(userRepository.existsByNickName("귀여운강아지")).willReturn(false);
    given(userRepository.existsByEmail("email@naver.com")).willReturn(false);
    given(passwordEncoder.encode("asdf123!@")).willReturn("encodedPW");
    given(emailAuthCodeRepository.findByEmailAndAuthCode(
        "email@naver.com","1234567")).willReturn(Optional.of(emailAuthCode)
    );
    given(emailAuthCode.isVerified()).willReturn(true);

    // when
    userService.signup(request);

    // then
    then(userRepository).should().save(any(User.class)); // “userRepository.save()가 User 객체를 넘겨서 정확히 1번 호출되었는지 확인해라.”
  }
}