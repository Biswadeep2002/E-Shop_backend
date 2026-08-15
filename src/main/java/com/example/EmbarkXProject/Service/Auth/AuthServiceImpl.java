package com.example.EmbarkXProject.Service.Auth;

import com.example.EmbarkXProject.Model.AppRole;
import com.example.EmbarkXProject.Model.Cart;
import com.example.EmbarkXProject.Model.Roles;
import com.example.EmbarkXProject.Model.Users;
import com.example.EmbarkXProject.Payload.AuthenticationResult;
import com.example.EmbarkXProject.Payload.Register.RegisterRequest;
import com.example.EmbarkXProject.Payload.Response.MessageResponse;
import com.example.EmbarkXProject.Payload.UserDTO;
import com.example.EmbarkXProject.Payload.UserResponse;
import com.example.EmbarkXProject.Payload.login.LoginRequest;
import com.example.EmbarkXProject.Repository.CartRepository;
import com.example.EmbarkXProject.Repository.RoleRepository;
import com.example.EmbarkXProject.Repository.UserRepository;
import com.example.EmbarkXProject.Service.Security.UserDetailsImpl;
import com.example.EmbarkXProject.security.UserInfo;
import com.example.EmbarkXProject.security.jwt.JwtUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public AuthenticationResult login(LoginRequest loginRequest) {
        Authentication authentication;

        System.out.println("Login started");

            authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwt = jwtUtils.generateJwtCookie(userDetails);
        List<String> roles = userDetails.getAuthorities().stream().map(role -> role.getAuthority()).toList();

        System.out.println("Authorities = " + userDetails.getAuthorities());
        System.out.println("Roles = " + roles);

        UserInfo response = new UserInfo(userDetails.getId(), userDetails.getUsername(), roles, userDetails.getEmail(), jwt.toString());

        return new AuthenticationResult(response, jwt);

//        LoginResponse response = new LoginResponse(userDetails.getUsername(), roles);
//        System.out.println("before login");

    }

    @Override
    public ResponseEntity<?> register(RegisterRequest registerRequest) {

        System.out.println("Register started");
        if(userRepository.existsByUsername(registerRequest.getUsername()))
            return ResponseEntity.badRequest().body(new MessageResponse("Username already taken"));

        if(userRepository.existsByEmail(registerRequest.getEmail()))
            return ResponseEntity.badRequest().body(new MessageResponse("Email already taken"));

        Users user = new Users(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                encoder.encode(registerRequest.getPassword())
        );

        Set<String> strRoles = registerRequest.getRoles();
        System.out.println("Roels are" + strRoles);
        Set<Roles> roles = new HashSet<>();

        if(strRoles == null){
            Roles userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            roles.add(userRole);
        }
        else{
            strRoles.forEach(role -> {
                switch (role){
                    case "admin":
                        Roles adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Role not found"));
                        roles.add(adminRole);
                        break;

                    case "seller":
                        Roles sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                                .orElseThrow(() -> new RuntimeException("Role not found"));
                        roles.add(sellerRole);
                        break;

                    case "user":
                        Roles userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Role not found"));
                        roles.add(userRole);
                        break;
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);

        // Create empty cart for the new user
        Cart cart = new Cart();
        cart.setUser(user);
        cart.setTotalPrice(0.0);

        cartRepository.save(cart);


        return ResponseEntity.ok(new MessageResponse("Registration Successful"));
    }

    @Override
    public UserInfo getCurrentUserDetails(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                            .map(item -> item.getAuthority())
                            .toList();

        UserInfo response = new UserInfo(userDetails.getId(), userDetails.getUsername(), roles);

        return response;
    }

    @Override
    public ResponseCookie logoutUser() {
        return jwtUtils.getCleanJwtCookie();
    }

    @Override
    public UserResponse getAllSellers(Pageable pageDetails) {
        Page<Users> allUsers = userRepository.findByRoleName(AppRole.ROLE_SELLER, pageDetails);
        List<UserDTO> userDTOS = allUsers.getContent()
                .stream()
                .map(p -> modelMapper.map(p, UserDTO.class)).toList();

        UserResponse response = new UserResponse();
        response.setContent(userDTOS);
        response.setPageNumber(allUsers.getNumber());
        response.setPageSize(allUsers.getSize());
        response.setTotalElements(allUsers.getTotalElements());
        response.setTotalPages(allUsers.getTotalPages());
        response.setLastPage(allUsers.isLast());
        return response;
    }
}

