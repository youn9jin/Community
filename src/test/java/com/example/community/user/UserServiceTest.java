package com.example.community.user;

import com.example.community.auth.RefreshTokenRepository;
import com.example.community.global.exception.DuplicateEmailException;
import com.example.community.image.ImageRepository;
import com.example.community.image.service.ImageService;
import com.example.community.user.dto.SignUpRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private UserService userService;

    @Test
    void 중복된_이메일로_회원가입하면_예외가_발생한다() {
        // given
        String email = "test@example.com";
        SignUpRequestDTO request = new SignUpRequestDTO(email, "nickname", "password");
        given(userRepository.existsByEmail(email)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.signUp(request, null))
                .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void 정상적인_이메일과_비밀번호로_회원가입하면_비밀번호가_암호화되어_저장된다() {
        // given
        String email = "test@example.com";
        String nickname = "nickname";
        String password = "password";
        String encodedPassword = "encoded-password";
        SignUpRequestDTO request = new SignUpRequestDTO(email, nickname, password);
        User savedUser = new User(email, nickname, encodedPassword);

        given(userRepository.existsByEmail(email)).willReturn(false);
        given(userRepository.existsByNickname(nickname)).willReturn(false);
        given(passwordEncoder.encode(password)).willReturn(encodedPassword);
        given(userRepository.save(any(User.class))).willReturn(savedUser);

        // when
        userService.signUp(request, null);

        // then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPassword()).isEqualTo(encodedPassword);
    }
}
