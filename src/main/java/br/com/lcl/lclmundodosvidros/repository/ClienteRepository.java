package br.com.lcl.lclmundodosvidros.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.lcl.lclmundodosvidros.model.Cliente;

public interface ClienteRepository
        extends JpaRepository<Cliente, Integer> {

    List<Cliente> findByNomeContainingIgnoreCaseOrderByNomeAsc(
            String nome
    );

    List<Cliente> findByNomeContainingIgnoreCaseOrderByNomeDesc(
            String nome
    );

    List<Cliente> findAllByOrderByNomeAsc();

    List<Cliente> findAllByOrderByNomeDesc();
}