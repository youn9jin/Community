package com.example.community.user;

import com.example.community.global.ResponseWrapper;
import com.example.community.user.dto.SignUpRequestDTO;
import com.example.community.user.dto.SignUpResponseDTO;
import com.example.community.user.dto.UpdateUserRequestDTO;
import com.example.community.user.dto.UserInfoResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<SignUpResponseDTO>> signUp(
            @Valid @RequestBody SignUpRequestDTO request) {

        SignUpResponseDTO response = userService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.success("registeration success", response));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseWrapper<UserInfoResponseDTO>> getUserInfo(@PathVariable Integer userId){
        UserInfoResponseDTO response = userService.getUserInfo(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseWrapper.success("user Information load completed", response));
    }


    @PatchMapping
    public ResponseEntity<ResponseWrapper<UserInfoResponseDTO>> updateUserInfo(
            @Valid @RequestBody UpdateUserRequestDTO request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) authentication.getPrincipal();
        UserInfoResponseDTO response = userService.updateUserInfo(userId, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseWrapper.success("user information update completed", response));
    }

    // 이메일 중복 여부 확인
    @GetMapping(params = "email")
    public ResponseEntity<ResponseWrapper<?>> checkEmailDuplicate(@RequestParam String email) {
        userService.checkEmailDuplicate(email);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseWrapper.success("does not have same email", null));
    }

    // 닉네임 중복 여부 확인
    @GetMapping(params = "nickname")
    public ResponseEntity<ResponseWrapper<?>> checkNicknameDuplicate(@RequestParam String nickname) {
        userService.checkNickNameDuplicate(nickname);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseWrapper.success("does not have same nickname", null));
    }

}
