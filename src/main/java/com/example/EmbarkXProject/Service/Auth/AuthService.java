package com.example.EmbarkXProject.Service.Auth;

import com.example.EmbarkXProject.Payload.AuthenticationResult;
import com.example.EmbarkXProject.Payload.Register.RegisterRequest;
import com.example.EmbarkXProject.Payload.UserResponse;
import com.example.EmbarkXProject.Payload.login.LoginRequest;
import com.example.EmbarkXProject.security.UserInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface AuthService {
    AuthenticationResult login(LoginRequest loginRequest);

    ResponseEntity<?> register(RegisterRequest registerRequest);

    UserInfo getCurrentUserDetails(Authentication authentication);

    ResponseCookie logoutUser();

    UserResponse getAllSellers(Pageable pageDetails);
}
