package com.example.community.user;

import com.example.community.global.ResponseWrapper;
import com.example.community.user.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<SignUpResponseDTO>> signUp(
            @RequestPart("userData") @Valid SignUpRequestDTO request,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        SignUpResponseDTO response = userService.signUp(request, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.success("registeration success", response));
    }

    @PreAuthorize("#userId == #loginUserId") // 두 파라미터가 일치할 때만 메서드 실행 허용
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseWrapper<UserInfoResponseDTO>> getUserInfo(
            @PathVariable Integer userId,
            @AuthenticationPrincipal Integer loginUserId) {

        UserInfoResponseDTO response = userService.getUserInfo(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseWrapper.success("user Information load completed", response));
    }


    @PreAuthorize("#userId == #loginUserId")
    @PatchMapping("/{userId}")
    public ResponseEntity<ResponseWrapper<UserInfoResponseDTO>> updateUserInfo(
            @PathVariable Integer userId,
            @AuthenticationPrincipal Integer loginUserId,
            @Valid @RequestBody UpdateUserRequestDTO request) {

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

    //회원 탈퇴 controller
    @PreAuthorize("#userId == #loginUserId")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Integer userId,
            @AuthenticationPrincipal Integer loginUserId) {

        userService.deleteUser(userId);

        return ResponseEntity.noContent().build(); //204 응답
    }

    @PreAuthorize("#userId == #loginUserId")
    @PatchMapping("/{userId}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Integer userId,
            @AuthenticationPrincipal Integer loginUserId,
            @Valid @RequestBody PasswordChangeRequestDTO request){

        userService.changePassword(userId, request);
        return ResponseEntity.noContent().build(); //204 응답
    }
}
