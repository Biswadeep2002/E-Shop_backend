package com.example.EmbarkXProject.Service.Address;

import com.example.EmbarkXProject.Exceptions.exceptions.ResourceNotFoundException;
import com.example.EmbarkXProject.Model.Address;
import com.example.EmbarkXProject.Model.Users;
import com.example.EmbarkXProject.Payload.Address.AddressDTO;
import com.example.EmbarkXProject.Repository.AddressRepository;
import com.example.EmbarkXProject.Repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService{

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, Users user) {
        Address address = modelMapper.map(addressDTO, Address.class);
        address.setUser(user);
        List<Address> addressesList = user.getAddresses();
        addressesList.add(address);
        user.setAddresses(addressesList);
        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddressess() {
        List<Address> addresses= addressRepository.findAll();
        List<AddressDTO> addressDTOList = addresses.stream()
                                            .map(address -> modelMapper.map(address, AddressDTO.class)).toList();
        return addressDTOList;
    }

    @Override
    public AddressDTO getAddressessById(long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address", "AddressId",addressId));
        AddressDTO addressDTO = modelMapper.map(address, AddressDTO.class);
        return addressDTO;
    }

    @Override
    public List<AddressDTO> getAddressesByUser(Users user) {
        List<Address> addresses = user.getAddresses();
        List<AddressDTO> addressDTOList = addresses.stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();
        return addressDTOList;
    }

    @Override
    public AddressDTO updateAddress(AddressDTO addressDTO, long addressId) {
        Address addressFromDatabase = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address", "AddressId", addressId));

        addressFromDatabase.setBuildingName(addressDTO.getBuildingName());
        addressFromDatabase.setStreet(addressDTO.getStreet());
        addressFromDatabase.setState(addressDTO.getState());
        addressFromDatabase.setPincode(addressDTO.getPincode());
        addressFromDatabase.setCity(addressDTO.getCity());
        addressFromDatabase.setCountry(addressDTO.getCountry());

        Address updatedAddress = addressRepository.save(addressFromDatabase);
        Users user = addressFromDatabase.getUser();

        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        user.getAddresses().add(updatedAddress);
        userRepository.save(user);

        return modelMapper.map(updatedAddress,AddressDTO.class);
    }

    @Override
    public ResponseEntity<String> deleteAddress(long addressId) {

        Address addressFromDatabase = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address", "AddressId", addressId));

        Users user = addressFromDatabase.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));

        addressRepository.delete(addressFromDatabase);
        return new ResponseEntity<>("Address Deleted Successfully", HttpStatus.OK);
    }

}
