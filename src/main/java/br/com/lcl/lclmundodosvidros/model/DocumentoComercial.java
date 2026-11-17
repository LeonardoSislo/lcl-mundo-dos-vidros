package br.com.lcl.lclmundodosvidros.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "documento_comercial")
public class DocumentoComercial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_documento")
    private Integer idDocumento;

    @NotBlank(message = "O número do documento é obrigatório.")
    @Size(max = 30)
    @Column(name = "numero", nullable = false, length = 30)
    private String numero;

    @NotNull(message = "Selecione o tipo do documento.")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoDocumento tipo;

    @NotNull(message = "Informe a data de emissão.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @NotBlank(message = "Selecione o status.")
    @Size(max = 30)
    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @NotNull(message = "Selecione o cliente.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "fk_cliente_id_cliente",
        nullable = false
    )
    private Cliente cliente;

    @NotBlank(message = "Informe o nome do cliente.")
    @Size(max = 255)
    @Column(name = "nome_cliente", length = 255)
    private String nomeCliente;

    @Size(max = 20)
    @Column(name = "cpf_cnpj", length = 20)
    private String cpfCnpj;

    @Size(max = 20)
    @Column(name = "telefone", length = 20)
    private String telefone;

    @Email(message = "Informe um e-mail válido.")
    @Size(max = 255)
    @Column(name = "email", length = 255)
    private String email;

    @Size(max = 255)
    @Column(name = "endereco_obra", length = 255)
    private String enderecoObra;

    @Size(max = 255)
    @Column(name = "bairro_obra", length = 255)
    private String bairroObra;

    @Size(max = 255)
    @Column(name = "cidade_obra", length = 255)
    private String cidadeObra;

    @Size(max = 2)
    @Column(name = "estado_obra", length = 2)
    private String estadoObra;

    @Size(max = 10)
    @Column(name = "cep_obra", length = 10)
    private String cepObra;

    @Column(
        name = "subtotal",
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(
        name = "desconto",
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal desconto = BigDecimal.ZERO;

    @Column(
        name = "acrescimo",
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal acrescimo = BigDecimal.ZERO;

    @Column(
        name = "valor_total",
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(
        name = "condicao_pagamento",
        columnDefinition = "TEXT"
    )
    private String condicaoPagamento;

    @Size(max = 255)
    @Column(name = "prazo_entrega", length = 255)
    private String prazoEntrega;

    @Column(
        name = "observacoes",
        columnDefinition = "TEXT"
    )
    private String observacoes;

    @Column(
        name = "condicoes_gerais",
        columnDefinition = "TEXT"
    )
    private String condicoesGerais;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Valid
    @OneToMany(
        mappedBy = "documento",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @OrderBy("ordemItem ASC")
    private List<ItemDocumento> itens = new ArrayList<>();

    public DocumentoComercial() {
    }

    @PrePersist
    public void antesDeSalvar() {
        LocalDateTime agora = LocalDateTime.now();

        criadoEm = agora;
        atualizadoEm = agora;
        calcularTotais();
    }

    @PreUpdate
    public void antesDeAtualizar() {
        atualizadoEm = LocalDateTime.now();
        calcularTotais();
    }

    public void adicionarItem(ItemDocumento item) {
        item.setDocumento(this);
        itens.add(item);
    }

    public void removerItem(ItemDocumento item) {
        itens.remove(item);
        item.setDocumento(null);
    }

    public void prepararRelacionamentos() {
        for (int indice = 0; indice < itens.size(); indice++) {
            ItemDocumento item = itens.get(indice);

            item.setDocumento(this);
            item.setOrdemItem(indice + 1);
            item.calcularValorTotal();
        }
    }

    public void calcularTotais() {
        prepararRelacionamentos();

        subtotal = itens.stream()
                .map(ItemDocumento::getValorTotal)
                .filter(valor -> valor != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal descontoCalculado =
                valorOuZero(desconto);

        BigDecimal acrescimoCalculado =
                valorOuZero(acrescimo);

        valorTotal = subtotal
                .subtract(descontoCalculado)
                .add(acrescimoCalculado);

        if (valorTotal.signum() < 0) {
            valorTotal = BigDecimal.ZERO;
        }
    }

    private BigDecimal valorOuZero(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }

    public Integer getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(Integer idDocumento) {
        this.idDocumento = idDocumento;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public TipoDocumento getTipo() {
        return tipo;
    }

    public void setTipo(TipoDocumento tipo) {
        this.tipo = tipo;
    }

    public LocalDate getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(LocalDate dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEnderecoObra() {
        return enderecoObra;
    }

    public void setEnderecoObra(String enderecoObra) {
        this.enderecoObra = enderecoObra;
    }

    public String getBairroObra() {
        return bairroObra;
    }

    public void setBairroObra(String bairroObra) {
        this.bairroObra = bairroObra;
    }

    public String getCidadeObra() {
        return cidadeObra;
    }

    public void setCidadeObra(String cidadeObra) {
        this.cidadeObra = cidadeObra;
    }

    public String getEstadoObra() {
        return estadoObra;
    }

    public void setEstadoObra(String estadoObra) {
        this.estadoObra = estadoObra;
    }

    public String getCepObra() {
        return cepObra;
    }

    public void setCepObra(String cepObra) {
        this.cepObra = cepObra;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getAcrescimo() {
        return acrescimo;
    }

    public void setAcrescimo(BigDecimal acrescimo) {
        this.acrescimo = acrescimo;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getCondicaoPagamento() {
        return condicaoPagamento;
    }

    public void setCondicaoPagamento(
            String condicaoPagamento) {

        this.condicaoPagamento = condicaoPagamento;
    }

    public String getPrazoEntrega() {
        return prazoEntrega;
    }

    public void setPrazoEntrega(String prazoEntrega) {
        this.prazoEntrega = prazoEntrega;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getCondicoesGerais() {
        return condicoesGerais;
    }

    public void setCondicoesGerais(
            String condicoesGerais) {

        this.condicoesGerais = condicoesGerais;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(
            LocalDateTime atualizadoEm) {

        this.atualizadoEm = atualizadoEm;
    }

    public List<ItemDocumento> getItens() {
        return itens;
    }

    public void setItens(List<ItemDocumento> itens) {
        this.itens = itens == null
                ? new ArrayList<>()
                : itens;

        prepararRelacionamentos();
    }
}