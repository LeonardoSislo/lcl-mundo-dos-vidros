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

    private static final String CONDICOES_GERAIS_ORCAMENTO =
        """
        1. Este orçamento está sujeito à alteração de valores após a confirmação das medidas durante a visita técnica.

        2. O valor total considera que todos os itens descritos neste orçamento serão produzidos e instalados simultaneamente. Caso a execução seja parcial, poderá haver acréscimo de valores, previamente informado e combinado com o contratante.

        3. O prazo de entrega e instalação começará a contar após a confirmação do orçamento e a identificação do pagamento inicial acordado.

        4. Os prazos informados são estimados em dias úteis e poderão ser alterados em razão da disponibilidade de materiais, condições climáticas, acesso ao local da obra ou situações fora do controle da contratada.

        5. As medidas, cores, modelos, espessuras dos vidros, tipos de acabamento e demais especificações deverão ser conferidos e aprovados pelo contratante antes do início da produção.

        6. Após a aprovação do orçamento e o início da produção, alterações solicitadas pelo contratante poderão resultar em revisão de prazo e acréscimo de valores.

        7. A quebra do vidro após a instalação não está coberta pela garantia. A garantia por quebra será aplicável somente quando o dano ocorrer durante o transporte, manuseio ou instalação realizada pela contratada.

        8. A garantia de instalação e manutenção será de 1 (um) ano, contada a partir da conclusão do serviço. A garantia não inclui quebra de vidro, mau uso, impactos, movimentações estruturais, infiltrações provenientes da obra, danos causados por terceiros ou substituição de acessórios sujeitos ao desgaste natural.

        9. A execução das instalações ocorrerá de segunda a sexta-feira, em horário comercial, normalmente entre 9h e 17h, no período da manhã ou da tarde.

        10. Não trabalhamos com agendamento de horário exato para instalação. O atendimento será realizado dentro do período comercial informado, conforme a programação da equipe.

        11. Caso seja solicitada instalação fora do horário comercial, inclusive após as 18h, poderá ser cobrado adicional noturno, previamente informado ao contratante.

        12. O contratante deverá garantir acesso livre e seguro ao local da instalação, bem como deixar o ambiente preparado para a execução do serviço.

        13. Serviços adicionais que não estejam descritos neste orçamento serão cobrados separadamente, mediante aprovação do contratante.

        14. A aprovação deste orçamento representa a concordância do contratante com os itens, medidas, valores, condições de pagamento, prazo e condições gerais descritas neste documento.
        """;

    private static final String CONDICOES_GERAIS_PEDIDO =
        """
        1. Este pedido foi elaborado com base nas medidas, especificações, quantidades, cores, modelos, acabamentos e valores aprovados pelo contratante.

        2. O valor total considera que todos os itens descritos neste pedido serão produzidos e instalados simultaneamente. Caso a execução seja parcial, poderá haver acréscimo de valores, previamente informado e combinado com o contratante.

        3. O prazo de entrega e instalação começará a contar após a identificação do pagamento inicial acordado.

        4. Os prazos informados são estimados em dias úteis e poderão ser alterados em razão da disponibilidade de materiais, condições climáticas, acesso ao local da obra ou situações fora do controle da contratada.

        5. Após a confirmação do pedido e o início da produção, qualquer alteração solicitada pelo contratante poderá resultar em revisão do valor e do prazo de entrega.

        6. Produtos fabricados sob medida não poderão ser alterados ou cancelados após o início da produção sem a avaliação dos custos dos materiais e serviços já executados.

        7. A quebra do vidro após a instalação não está coberta pela garantia. A garantia por quebra será aplicável somente quando o dano ocorrer durante o transporte, manuseio ou instalação realizada pela contratada.

        8. A garantia de instalação e manutenção será de 1 (um) ano, contada a partir da conclusão do serviço. A garantia não inclui quebra de vidro, mau uso, impactos, movimentações estruturais, infiltrações provenientes da obra, danos causados por terceiros ou substituição de acessórios sujeitos ao desgaste natural.

        9. A execução das instalações ocorrerá de segunda a sexta-feira, em horário comercial, normalmente entre 9h e 17h, no período da manhã ou da tarde.

        10. Não trabalhamos com agendamento de horário exato para instalação. O atendimento será realizado dentro do período comercial informado, conforme a programação da equipe.

        11. Caso seja solicitada instalação fora do horário comercial, inclusive após as 18h, poderá ser cobrado adicional noturno, previamente informado ao contratante.

        12. O contratante deverá garantir acesso livre e seguro ao local da instalação, bem como deixar o ambiente preparado para a execução do serviço.

        13. Serviços adicionais que não estejam descritos neste pedido serão cobrados separadamente, mediante aprovação do contratante.

        14. A confirmação deste pedido representa a concordância do contratante com os itens, medidas, valores, condições de pagamento, prazo e condições gerais descritas neste documento.
        """;

private static final String PAGAMENTO_PADRAO_ORCAMENTO =
        """
        Pagamento mediante sinal para confirmação do orçamento e início da produção. O saldo restante poderá ser parcelado em até 3 (três) vezes sem juros, conforme previamente combinado.
        """;

private static final String PAGAMENTO_PADRAO_PEDIDO =
        """
        Pagamento conforme condição previamente combinada.

        Banco Bradesco
        Agência: 0545
        Conta-corrente: 31.227-4
        Favorecido: Leonardo Diego Sislo
        Chave Pix CNPJ: 42.900.710/0001-18
        """;

private static final String PRAZO_PADRAO =
        "7 a 10 dias úteis após a identificação do pagamento inicial.";

    private final DocumentoComercialRepository documentoRepository;
    private final ClienteRepository clienteRepository;

    public DocumentoComercialService(
            DocumentoComercialRepository documentoRepository,
            ClienteRepository clienteRepository) {

        this.documentoRepository = documentoRepository;
        this.clienteRepository = clienteRepository;
    }

    /*
     * Criação dos documentos
     */

    public DocumentoComercial criarNovoOrcamento() {
            DocumentoComercial documento =
                    criarDocumentoBase(TipoDocumento.ORCAMENTO);

            documento.setStatus("Rascunho");

            documento.setCondicaoPagamento(
                    PAGAMENTO_PADRAO_ORCAMENTO
            );

            documento.setCondicoesGerais(
                    CONDICOES_GERAIS_ORCAMENTO
            );

            return documento;
        }

    public DocumentoComercial criarNovoPedido() {
            DocumentoComercial documento =
                    criarDocumentoBase(TipoDocumento.PEDIDO);

            documento.setStatus("Aberto");

            documento.setCondicaoPagamento(
                    PAGAMENTO_PADRAO_PEDIDO
            );

            documento.setCondicoesGerais(
                    CONDICOES_GERAIS_PEDIDO
            );

            return documento;
        }

    private DocumentoComercial criarDocumentoBase(
            TipoDocumento tipo) {

        DocumentoComercial documento =
                new DocumentoComercial();

        documento.setTipo(tipo);
        documento.setNumero(gerarNumero(tipo));
        documento.setDataEmissao(LocalDate.now());

        documento.setDesconto(BigDecimal.ZERO);
        documento.setAcrescimo(BigDecimal.ZERO);
        documento.setSubtotal(BigDecimal.ZERO);
        documento.setValorTotal(BigDecimal.ZERO);

        documento.setPrazoEntrega(
        PRAZO_PADRAO
        );


        documento.adicionarItem(criarItemVazio());

        return documento;
    }

    /*
     * Salvamento
     */

    @Transactional
    public DocumentoComercial salvar(
            DocumentoComercial dadosFormulario) {

        validarCliente(dadosFormulario);

        if (dadosFormulario.getIdDocumento() == null) {
            return cadastrarDocumento(dadosFormulario);
        }

        return atualizarDocumento(dadosFormulario);
    }

    private DocumentoComercial cadastrarDocumento(
            DocumentoComercial documento) {

        Cliente cliente = buscarCliente(
                documento.getCliente().getIdCliente()
        );

        documento.setCliente(cliente);

        preencherDadosDoClienteSeVazios(
                documento,
                cliente
        );

        prepararItensNovos(documento);
        documento.calcularTotais();

        return documentoRepository.save(documento);
    }

    private DocumentoComercial atualizarDocumento(
            DocumentoComercial dadosFormulario) {

        DocumentoComercial documentoSalvo =
                documentoRepository
                        .findByIdDocumento(
                                dadosFormulario.getIdDocumento()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Documento não encontrado."
                                )
                        );

        Cliente cliente = buscarCliente(
                dadosFormulario
                        .getCliente()
                        .getIdCliente()
        );

        copiarDadosEditaveis(
                documentoSalvo,
                dadosFormulario,
                cliente
        );

        sincronizarItens(
                documentoSalvo,
                dadosFormulario.getItens()
        );

        documentoSalvo.calcularTotais();

        return documentoRepository.save(documentoSalvo);
    }

    /*
     * Dados principais do documento
     */

    private void copiarDadosEditaveis(
            DocumentoComercial destino,
            DocumentoComercial origem,
            Cliente cliente) {

        /*
         * O número e o tipo não são alterados.
         *
         * Isso impede transformar um orçamento em pedido
         * simplesmente alterando campos do formulário.
         */
        destino.setDataEmissao(origem.getDataEmissao());
        destino.setStatus(origem.getStatus());
        destino.setCliente(cliente);

        destino.setNomeCliente(origem.getNomeCliente());
        destino.setCpfCnpj(origem.getCpfCnpj());
        destino.setTelefone(origem.getTelefone());
        destino.setEmail(origem.getEmail());

        destino.setEnderecoObra(origem.getEnderecoObra());
        destino.setBairroObra(origem.getBairroObra());
        destino.setCidadeObra(origem.getCidadeObra());
        destino.setEstadoObra(origem.getEstadoObra());
        destino.setCepObra(origem.getCepObra());

        destino.setDesconto(
                valorOuZero(origem.getDesconto())
        );

        destino.setAcrescimo(
                valorOuZero(origem.getAcrescimo())
        );

        destino.setCondicaoPagamento(
                origem.getCondicaoPagamento()
        );

        destino.setPrazoEntrega(
                origem.getPrazoEntrega()
        );

        destino.setObservacoes(
                origem.getObservacoes()
        );

        destino.setCondicoesGerais(
                origem.getCondicoesGerais()
        );
    }

    private void preencherDadosDoClienteSeVazios(
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

        if (contato == null) {
            return;
        }

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

    /*
     * Sincronização dos itens durante a edição
     */

    private void sincronizarItens(
            DocumentoComercial documentoSalvo,
            List<ItemDocumento> itensFormulario) {

        List<ItemDocumento> itensValidos =
                filtrarItensValidos(itensFormulario);

        removerItensExcluidos(
                documentoSalvo,
                itensValidos
        );

        for (int indice = 0;
                indice < itensValidos.size();
                indice++) {

            ItemDocumento dadosItem =
                    itensValidos.get(indice);

            ItemDocumento itemSalvo =
                    localizarItemExistente(
                            documentoSalvo,
                            dadosItem.getIdItem()
                    );

            if (itemSalvo == null) {
                itemSalvo = new ItemDocumento();

                documentoSalvo.adicionarItem(
                        itemSalvo
                );
            }

            copiarDadosItem(
                    itemSalvo,
                    dadosItem,
                    indice + 1
            );
        }
    }

    private void removerItensExcluidos(
            DocumentoComercial documento,
            List<ItemDocumento> itensFormulario) {

        documento.getItens().removeIf(
                itemSalvo ->
                        !itemEstaNoFormulario(
                                itemSalvo,
                                itensFormulario
                        )
        );
    }

    private boolean itemEstaNoFormulario(
            ItemDocumento itemSalvo,
            List<ItemDocumento> itensFormulario) {

        if (itemSalvo.getIdItem() == null) {
            return true;
        }

        return itensFormulario.stream()
                .anyMatch(itemFormulario ->
                        itemFormulario.getIdItem() != null
                                && itemFormulario
                                        .getIdItem()
                                        .equals(
                                            itemSalvo.getIdItem()
                                        )
                );
    }

    private ItemDocumento localizarItemExistente(
            DocumentoComercial documento,
            Integer idItem) {

        if (idItem == null) {
            return null;
        }

        return documento.getItens()
                .stream()
                .filter(item ->
                        idItem.equals(item.getIdItem())
                )
                .findFirst()
                .orElse(null);
    }

    private void copiarDadosItem(
            ItemDocumento destino,
            ItemDocumento origem,
            int ordem) {

        destino.setQuantidade(
                origem.getQuantidade() == null
                        ? BigDecimal.ONE
                        : origem.getQuantidade()
        );

        destino.setDescricao(
                origem.getDescricao().trim()
        );

        destino.setLargura(
                origem.getLargura()
        );

        destino.setAltura(
                origem.getAltura()
        );

        destino.setUnidadeMedida(
                estaVazio(origem.getUnidadeMedida())
                        ? "mm"
                        : origem.getUnidadeMedida()
        );

        destino.setValorUnitario(
                origem.getValorUnitario() == null
                        ? BigDecimal.ZERO
                        : origem.getValorUnitario()
        );

        destino.setOrdemItem(ordem);
        destino.calcularValorTotal();
    }

    /*
     * Preparação dos itens no cadastro
     */

    private void prepararItensNovos(
            DocumentoComercial documento) {

        List<ItemDocumento> itensValidos =
                filtrarItensValidos(
                        documento.getItens()
                );

        List<ItemDocumento> itensPreparados =
                new ArrayList<>();

        for (int indice = 0;
                indice < itensValidos.size();
                indice++) {

            ItemDocumento item =
                    itensValidos.get(indice);

            item.setDocumento(documento);

            copiarDadosItem(
                    item,
                    item,
                    indice + 1
            );

            itensPreparados.add(item);
        }

        documento.setItens(itensPreparados);
    }

    private List<ItemDocumento> filtrarItensValidos(
            List<ItemDocumento> itens) {

        List<ItemDocumento> itensValidos =
                new ArrayList<>();

        if (itens == null) {
            throw new IllegalArgumentException(
                    "Adicione pelo menos um item ao documento."
            );
        }

        for (ItemDocumento item : itens) {
            if (item == null) {
                continue;
            }

            if (item.getDescricao() == null
                    || item.getDescricao().isBlank()) {
                continue;
            }

            itensValidos.add(item);
        }

        if (itensValidos.isEmpty()) {
            throw new IllegalArgumentException(
                    "Adicione pelo menos um item ao documento."
            );
        }

        return itensValidos;
    }

    /*
     * Consultas
     */

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

    /*
     * Cliente
     */

    private void validarCliente(
            DocumentoComercial documento) {

        if (documento.getCliente() == null
                || documento
                        .getCliente()
                        .getIdCliente() == null) {

            throw new IllegalArgumentException(
                    "Selecione um cliente."
            );
        }
    }

    private Cliente buscarCliente(Integer idCliente) {
        return clienteRepository
                .findById(idCliente)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Cliente não encontrado."
                        )
                );
    }

    /*
     * Numeração
     */

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

    /*
     * Métodos auxiliares
     */

    private ItemDocumento criarItemVazio() {
        ItemDocumento item = new ItemDocumento();

        item.setQuantidade(BigDecimal.ONE);
        item.setValorUnitario(BigDecimal.ZERO);
        item.setUnidadeMedida("mm");
        item.setOrdemItem(1);

        return item;
    }

    private BigDecimal valorOuZero(
            BigDecimal valor) {

        return valor == null
                ? BigDecimal.ZERO
                : valor;
    }

    private boolean estaVazio(String valor) {
        return valor == null || valor.isBlank();
    }
}