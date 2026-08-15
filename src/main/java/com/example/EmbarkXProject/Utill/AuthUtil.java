package com.example.EmbarkXProject.Utill;


import com.example.EmbarkXProject.Model.Users;
import com.example.EmbarkXProject.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    @Autowired
    UserRepository userRepository;

    public String getLoggedInUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users user = userRepository.findByUsername(authentication.getName());

        if(user == null)
            throw new UsernameNotFoundException("Username not found");
        return user.getEmail();
    }

    public Users getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users user = userRepository.findByUsername(authentication.getName());
        if(user == null)
            throw new UsernameNotFoundException("Username not found");
        return user;
    }
}
