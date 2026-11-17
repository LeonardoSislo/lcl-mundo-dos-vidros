package br.com.lcl.lclmundodosvidros.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.lcl.lclmundodosvidros.model.Servico;

public interface ServicoRepository
        extends JpaRepository<Servico, Integer>,
                JpaSpecificationExecutor<Servico> {

    List<Servico> findAllByOrderByIdServicoDesc();

    List<Servico> findTop6ByOrderByIdServicoDesc();

    long countByStatusServicoIgnoreCase(
            String statusServico
    );
}