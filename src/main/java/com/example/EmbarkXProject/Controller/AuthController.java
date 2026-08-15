package com.example.EmbarkXProject.Controller;

import com.example.EmbarkXProject.Config.AppConstants;
import com.example.EmbarkXProject.Payload.AuthenticationResult;
import com.example.EmbarkXProject.Payload.Register.RegisterRequest;
import com.example.EmbarkXProject.Payload.Response.MessageResponse;
import com.example.EmbarkXProject.Payload.login.LoginRequest;
import com.example.EmbarkXProject.Service.Auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    @Autowired
    AuthService authService;

    @GetMapping("/auth/home")
    public String hello(){
        return "Yup";
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        System.out.println("loggin in");

        AuthenticationResult authenticationResult = authService.login(loginRequest);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,authenticationResult.getJwtCookie().toString()).body(authenticationResult.getResponse());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest){
        return authService.register(registerRequest);
    }

    @GetMapping("/auth/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication){
        return ResponseEntity.ok().body(authService.getCurrentUserDetails(authentication));
    }

    @PostMapping("/auth/signout")
    public ResponseEntity<?> signoutUser(){
        System.out.println("loggin out");
        ResponseCookie responseCookie = authService.logoutUser();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,responseCookie.toString())
                .body(new MessageResponse("You have been signed out"));
    }

    @GetMapping("/auth/sellers")
    public ResponseEntity<?> getAllSellers(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber
    ) {
        Sort sortByAndOrder = Sort.by(AppConstants.USERS_SORT_BY).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, Integer.parseInt(AppConstants.PAGE_SIZE), sortByAndOrder);
        return ResponseEntity.ok(authService.getAllSellers(pageDetails));
    }

}
