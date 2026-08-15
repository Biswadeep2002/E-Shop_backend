package com.example.EmbarkXProject.Payload;

import com.example.EmbarkXProject.security.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseCookie;

@Data
@AllArgsConstructor
public class AuthenticationResult {
    private final UserInfo response;
    private final ResponseCookie jwtCookie;

}
