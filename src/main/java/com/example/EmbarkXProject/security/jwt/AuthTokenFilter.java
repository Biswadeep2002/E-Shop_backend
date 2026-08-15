package com.example.EmbarkXProject.security.jwt;

import com.example.EmbarkXProject.Service.Security.UserDetailsImpl;
import com.example.EmbarkXProject.Service.Security.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserDetailsServiceImpl userDetailsService;


//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        String path = request.getServletPath();
//        // Skip JWT filter for these endpoints
//        return path.equals("/login") || path.equals("/register") || path.equals("addresses/create");
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path.equals("/auth/login") || path.equals("/register") || path.equals("/auth/product/get")) {
            filterChain.doFilter(request, response);
            return;
        }


        try {
            String jwt = jwtUtils.getJwtFromCookie(request);
            String username;
//            if(jwt == null)
//                System.out.println("Null jwt");
            if (jwt != null && jwtUtils.verifyJwt(jwt)) {
                username = jwtUtils.generateUsernameFromToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
//            else
//                System.out.println("Verify not");
        }catch (Exception e){
            System.out.println(e.getMessage());;
        }

        filterChain.doFilter(request, response);
    }

}
