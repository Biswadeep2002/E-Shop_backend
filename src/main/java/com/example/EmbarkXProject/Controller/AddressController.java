package com.example.EmbarkXProject.Controller;

import com.example.EmbarkXProject.Model.Users;
import com.example.EmbarkXProject.Payload.Address.AddressDTO;
import com.example.EmbarkXProject.Service.Address.AddressService;
import com.example.EmbarkXProject.Utill.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth/addresses")
public class AddressController {

    @Autowired
    AuthUtil authUtil;

    @Autowired
    AddressService addressService;

    @PostMapping("/create")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO){
        Users user = authUtil.getLoggedInUser();
        AddressDTO savedAddressDTO = addressService.createAddress(addressDTO, user);
        return new ResponseEntity<>(savedAddressDTO, HttpStatus.CREATED);
    }

    @GetMapping("/get")
    public ResponseEntity<List<AddressDTO>> getAddresses() {
        List<AddressDTO> addressList = addressService.getAddressess();
        return new ResponseEntity<>(addressList, HttpStatus.OK);
    }

    @GetMapping("/getById/{addressId}")
    public ResponseEntity<AddressDTO> getAddresses(@PathVariable long addressId){
        AddressDTO addressDTO = addressService.getAddressessById(addressId);
        return new ResponseEntity<>(addressDTO, HttpStatus.OK);
    }

    @GetMapping("/getByUser")
    public ResponseEntity<List<AddressDTO>> getAddressesByUser(){
        Users user = authUtil.getLoggedInUser();
        List<AddressDTO> addressDTOList = addressService.getAddressesByUser(user);
        return new ResponseEntity<>(addressDTOList, HttpStatus.OK);
    }

    @PutMapping("/update/{addressId}")
    public ResponseEntity<AddressDTO> updateAddresses(@RequestBody AddressDTO addressDTO, @PathVariable  long addressId){
        AddressDTO updatedAddressDTO = addressService.updateAddress(addressDTO,addressId);
        return new ResponseEntity<>(updatedAddressDTO, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{addressId}")
    public ResponseEntity<String> deleteAddresses(@PathVariable  long addressId){
        return addressService.deleteAddress(addressId);
    }
}
