package com.example.EmbarkXProject.Payload;


import com.example.EmbarkXProject.Model.Roles;
import com.example.EmbarkXProject.Payload.Address.AddressDTO;
import com.example.EmbarkXProject.Payload.Cart.CartDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private long userId;
    private String userName;
    private String email;
    private String password;
    private Set<Roles> roles = new HashSet<>();
    private AddressDTO address;
    private CartDTO cart;
}
