package com.example.EmbarkXProject.security.jwt;

import com.example.EmbarkXProject.Service.Security.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.ResourceBundle;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.cookie}")
    private String jwtCookie;

    private static final long JWT_EXPIRATION_MS = 60 * 60 * 1000;

    public String getJwtFromHeader(HttpServletRequest http){
        String bearer = http.getHeader("Authorization");

        if(bearer != null && bearer.startsWith("Bearer "))
            return bearer.substring(7);

        return null;
    }

    public String generateTokenFromUsername(UserDetails userDetails){
        String username = userDetails.getUsername();

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                .signWith(key())
                .compact();
    }

    public String getJwtFromCookie(HttpServletRequest httpServletRequest){
        Cookie cookie = WebUtils.getCookie(httpServletRequest,jwtCookie);

        if(cookie != null)
            return cookie.getValue();
        else
            return null;
    }

    public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal){
        String jwt = generateTokenFromUsername(userPrincipal);
        ResponseCookie cookie = ResponseCookie.from(jwtCookie,jwt)
                .path("/auth")
                .maxAge(JWT_EXPIRATION_MS / 1000)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();
        return cookie;
    }

    public String generateUsernameFromToken(String jwt){
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(jwt)
                .getPayload().getSubject();
    }


    public SecretKey key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public Boolean verifyJwt(String jwt){
        try {
            System.out.println("Jwt OK");
            Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(jwt);
            return true;
        }
        catch (ExpiredJwtException e) {
            System.out.println("JWT expired");
            return false;
        }
        catch (MalformedJwtException e){
            System.out.println("Jwt Not OK");
            System.out.println(e.getMessage());
            return false;
        } catch (SignatureException e) {
            System.out.println("Invalid signature");
            return false;

        } catch (IllegalArgumentException e) {
            System.out.println("Empty token");
            return false;
        }catch (Exception e) {
            System.out.println("JWT validation error: " + e.getMessage());
            return false;
        }

    }

    public ResponseCookie getCleanJwtCookie() {
        ResponseCookie cookie = ResponseCookie.from(jwtCookie, "")
                .path("/auth")
                .maxAge(0)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();
        return cookie;
    }
}

