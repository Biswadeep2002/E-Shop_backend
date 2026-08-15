package com.example.EmbarkXProject.Repository;

import com.example.EmbarkXProject.Model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
