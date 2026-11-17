package br.com.lcl.lclmundodosvidros.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.lcl.lclmundodosvidros.model.ItemDocumento;

public interface ItemDocumentoRepository
        extends JpaRepository<ItemDocumento, Integer> {

    List<ItemDocumento>
            findByDocumentoIdDocumentoOrderByOrdemItemAsc(
                    Integer idDocumento
            );
}