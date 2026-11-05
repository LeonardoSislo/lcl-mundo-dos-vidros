package br.com.lcl.lclmundodosvidros.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.lcl.lclmundodosvidros.model.Servico;

public interface ServicoRepository
        extends JpaRepository<Servico, Integer> {

    List<Servico> findAllByOrderByIdServicoDesc();

    long countByStatusServicoIgnoreCase(String statusServico);
}