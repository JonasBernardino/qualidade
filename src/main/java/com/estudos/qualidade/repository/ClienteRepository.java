package com.estudos.qualidade.repository;

import com.estudos.qualidade.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByStatus(String status);

    List<Cliente> findByEmail(String email);
}
