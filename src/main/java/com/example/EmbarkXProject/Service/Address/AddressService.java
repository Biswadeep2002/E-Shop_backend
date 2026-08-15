package com.example.EmbarkXProject.Service.Address;

import com.example.EmbarkXProject.Model.Users;
import com.example.EmbarkXProject.Payload.Address.AddressDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO, Users user);

    List<AddressDTO> getAddressess();

    AddressDTO getAddressessById(long addressId);

    List<AddressDTO> getAddressesByUser(Users user);

    AddressDTO updateAddress(AddressDTO addressDTO, long addressId);

    ResponseEntity<String> deleteAddress(long addressId);
}
