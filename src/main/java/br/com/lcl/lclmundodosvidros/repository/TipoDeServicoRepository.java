package br.com.lcl.lclmundodosvidros.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.lcl.lclmundodosvidros.model.TipoDeServico;

public interface TipoDeServicoRepository
        extends JpaRepository<TipoDeServico, Integer> {

    List<TipoDeServico> findAllByOrderByTipoAsc();
}