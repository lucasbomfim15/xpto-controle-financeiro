package com.xpto.controlefinanceiro.modules.address.repository;

import com.xpto.controlefinanceiro.modules.address.dtos.AddressResponseDTO;
import com.xpto.controlefinanceiro.modules.address.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findByCustomerId(UUID customerId);

    @Query(value = "SELECT * FROM fn_enderecos_cliente(:customerId)", nativeQuery = true)
    List<AddressResponseDTO> findAddressesByCustomerId(@Param("customerId") UUID customerId);


}
