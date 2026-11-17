package br.com.lcl.lclmundodosvidros.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.lcl.lclmundodosvidros.model.Cliente;
import br.com.lcl.lclmundodosvidros.model.Contato;
import br.com.lcl.lclmundodosvidros.model.DocumentoComercial;
import br.com.lcl.lclmundodosvidros.model.ItemDocumento;
import br.com.lcl.lclmundodosvidros.model.TipoDocumento;
import br.com.lcl.lclmundodosvidros.repository.ClienteRepository;
import br.com.lcl.lclmundodosvidros.repository.DocumentoComercialRepository;

@Service
public class DocumentoComercialService {

    private static final String CONDICOES_GERAIS_PADRAO =
            """
            Orçamento sujeito a alteração dos valores após a confirmação
            das medidas na visita técnica.

            No valor total deste documento, considera-se que todos os itens
            serão instalados simultaneamente. Instalações parciais poderão
            ter acréscimos previamente combinados.

            O prazo de entrega começa a contar após a identificação
            do pagamento.

            Quebra de vidro após a instalação não está incluída na garantia,
            exceto quando ocorrer durante o ato da instalação.

            O horário de instalação é de segunda a sexta-feira,
            das 9h às 17h.
            """;

    private final DocumentoComercialRepository documentoRepository;
    private final ClienteRepository clienteRepository;

    public DocumentoComercialService(
            DocumentoComercialRepository documentoRepository,
            ClienteRepository clienteRepository) {

        this.documentoRepository = documentoRepository;
        this.clienteRepository = clienteRepository;
    }

    public DocumentoComercial criarNovoOrcamento() {
        DocumentoComercial documento =
                new DocumentoComercial();

        documento.setTipo(TipoDocumento.ORCAMENTO);
        documento.setNumero(
                gerarNumero(TipoDocumento.ORCAMENTO)
        );
        documento.setDataEmissao(LocalDate.now());
        documento.setStatus("Rascunho");
        documento.setDesconto(BigDecimal.ZERO);
        documento.setAcrescimo(BigDecimal.ZERO);
        documento.setCondicaoPagamento(
                "Sinal + restante a combinar."
        );
        documento.setPrazoEntrega(
                "7 a 10 dias úteis"
        );
        documento.setCondicoesGerais(
                CONDICOES_GERAIS_PADRAO
        );

        documento.adicionarItem(criarItemVazio());

        return documento;
    }

    public DocumentoComercial criarNovoPedido() {
        DocumentoComercial documento =
                new DocumentoComercial();

        documento.setTipo(TipoDocumento.PEDIDO);
        documento.setNumero(
                gerarNumero(TipoDocumento.PEDIDO)
        );
        documento.setDataEmissao(LocalDate.now());
        documento.setStatus("Aberto");
        documento.setDesconto(BigDecimal.ZERO);
        documento.setAcrescimo(BigDecimal.ZERO);
        documento.setPrazoEntrega(
                "7 a 10 dias úteis"
        );
        documento.setCondicoesGerais(
                CONDICOES_GERAIS_PADRAO
        );

        documento.adicionarItem(criarItemVazio());

        return documento;
    }

    @Transactional
    public DocumentoComercial salvar(
            DocumentoComercial documento) {

        if (documento.getCliente() == null
                || documento.getCliente().getIdCliente() == null) {

            throw new IllegalArgumentException(
                    "Selecione um cliente."
            );
        }

        Integer idCliente =
                documento.getCliente().getIdCliente();

        Cliente cliente = clienteRepository
                .findById(idCliente)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Cliente não encontrado."
                        )
                );

        documento.setCliente(cliente);
        preencherDadosDoCliente(documento, cliente);
        prepararItens(documento);
        documento.calcularTotais();

        return documentoRepository.save(documento);
    }

    @Transactional(readOnly = true)
    public DocumentoComercial buscarComItens(
            Integer idDocumento) {

        return documentoRepository
                .findByIdDocumento(idDocumento)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Documento não encontrado."
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<DocumentoComercial> listarTodos() {
        return documentoRepository
                .findAllByOrderByDataEmissaoDescIdDocumentoDesc();
    }

    private void prepararItens(
            DocumentoComercial documento) {

        List<ItemDocumento> itensValidos =
                new ArrayList<>();

        if (documento.getItens() != null) {
            for (ItemDocumento item : documento.getItens()) {
                if (item == null) {
                    continue;
                }

                boolean possuiDescricao =
                        item.getDescricao() != null
                                && !item.getDescricao()
                                        .isBlank();

                if (!possuiDescricao) {
                    continue;
                }

                item.setDocumento(documento);

                if (item.getQuantidade() == null) {
                    item.setQuantidade(BigDecimal.ONE);
                }

                if (item.getValorUnitario() == null) {
                    item.setValorUnitario(
                            BigDecimal.ZERO
                    );
                }

                if (item.getUnidadeMedida() == null
                        || item.getUnidadeMedida()
                                .isBlank()) {

                    item.setUnidadeMedida("mm");
                }

                item.setOrdemItem(
                        itensValidos.size() + 1
                );

                item.calcularValorTotal();
                itensValidos.add(item);
            }
        }

        if (itensValidos.isEmpty()) {
            throw new IllegalArgumentException(
                    "Adicione pelo menos um item ao documento."
            );
        }

        documento.setItens(itensValidos);
    }

    private void preencherDadosDoCliente(
            DocumentoComercial documento,
            Cliente cliente) {

        if (estaVazio(documento.getNomeCliente())) {
            documento.setNomeCliente(
                    cliente.getNome()
            );
        }

        if (estaVazio(documento.getEnderecoObra())) {
            documento.setEnderecoObra(
                    cliente.getEndereco()
            );
        }

        if (estaVazio(documento.getBairroObra())) {
            documento.setBairroObra(
                    cliente.getBairro()
            );
        }

        if (estaVazio(documento.getCidadeObra())) {
            documento.setCidadeObra(
                    cliente.getCidade()
            );
        }

        Contato contato = cliente.getContato();

        if (contato != null) {
            if (estaVazio(documento.getTelefone())) {
                documento.setTelefone(
                        contato.getTelefone()
                );
            }

            if (estaVazio(documento.getEmail())) {
                documento.setEmail(
                        contato.getEmail()
                );
            }
        }
    }

    private ItemDocumento criarItemVazio() {
        ItemDocumento item = new ItemDocumento();

        item.setQuantidade(BigDecimal.ONE);
        item.setValorUnitario(BigDecimal.ZERO);
        item.setUnidadeMedida("mm");
        item.setOrdemItem(1);

        return item;
    }

    private String gerarNumero(
            TipoDocumento tipo) {

        int ano = LocalDate.now().getYear();

        String prefixoTipo =
                tipo == TipoDocumento.ORCAMENTO
                        ? "ORC"
                        : "PED";

        String prefixo =
                prefixoTipo + "-" + ano + "-";

        long quantidade =
                documentoRepository
                        .countByTipoAndNumeroStartingWith(
                                tipo,
                                prefixo
                        );

        long sequencia = quantidade + 1;

        String numero;

        do {
            numero = prefixo
                    + String.format(
                            "%04d",
                            sequencia
                    );

            sequencia++;

        } while (
            documentoRepository.existsByNumero(numero)
        );

        return numero;
    }

    private boolean estaVazio(String valor) {
        return valor == null || valor.isBlank();
    }
}