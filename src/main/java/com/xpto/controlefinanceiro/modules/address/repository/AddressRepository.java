package com.xpto.controlefinanceiro.modules.address.repository;

import com.xpto.controlefinanceiro.modules.address.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findByCustomerId(UUID customerId);
}
