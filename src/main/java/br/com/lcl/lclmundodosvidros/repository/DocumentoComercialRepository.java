package br.com.lcl.lclmundodosvidros.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.lcl.lclmundodosvidros.model.DocumentoComercial;
import br.com.lcl.lclmundodosvidros.model.TipoDocumento;

public interface DocumentoComercialRepository
        extends JpaRepository<DocumentoComercial, Integer>,
                JpaSpecificationExecutor<DocumentoComercial> {

    List<DocumentoComercial>
            findAllByOrderByDataEmissaoDescIdDocumentoDesc();

    Optional<DocumentoComercial> findByNumero(String numero);

    boolean existsByNumero(String numero);

    long countByTipo(TipoDocumento tipo);

    long countByTipoAndNumeroStartingWith(
            TipoDocumento tipo,
            String prefixo
    );

    @EntityGraph(attributePaths = {
        "cliente",
        "itens"
    })
    Optional<DocumentoComercial>
            findByIdDocumento(Integer idDocumento);
}