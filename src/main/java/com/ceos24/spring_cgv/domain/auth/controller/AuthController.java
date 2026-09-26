package com.ceos24.spring_cgv.domain.auth.controller;

import com.ceos24.spring_cgv.domain.auth.dto.request.LoginRequest;
import com.ceos24.spring_cgv.domain.auth.dto.request.SignUpRequest;
import com.ceos24.spring_cgv.domain.auth.dto.response.LoginResponse;
import com.ceos24.spring_cgv.domain.auth.dto.response.SignUpResponse;
import com.ceos24.spring_cgv.domain.auth.exception.code.AuthSuccessCode;
import com.ceos24.spring_cgv.domain.auth.service.AuthService;
import com.ceos24.spring_cgv.global.apipayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증", description = "인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @SecurityRequirements
    @Operation(summary = "회원가입", description = "새로운 회원가입을 진행합니다.")
    @PostMapping("/signup")
    public ApiResponse<SignUpResponse> signUp(
            @Valid @RequestBody SignUpRequest request
            ){
        return ApiResponse.onSuccess(AuthSuccessCode.SIGNUP_OK, authService.signUp(request));
    }

    @SecurityRequirements
    @Operation(summary = "로그인", description = "로그인을 진행합니다.")
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
            ){
        return ApiResponse.onSuccess(AuthSuccessCode.LOGIN_OK, authService.login(request));
    }
}
