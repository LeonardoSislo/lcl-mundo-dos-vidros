package br.com.lcl.lclmundodosvidros.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "servico")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servico")
    private Integer idServico;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "data_orcamento")
    private LocalDate dataOrcamento;

    @DecimalMin(
        value = "0.00",
        message = "O valor de custo não pode ser negativo."
    )
    @Column(name = "valor_custo", precision = 10, scale = 2)
    private BigDecimal valorCusto;

    @DecimalMin(
        value = "0.00",
        message = "O valor cobrado não pode ser negativo."
    )
    @Column(name = "valor_cobrado", precision = 10, scale = 2)
    private BigDecimal valorCobrado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_tipo_de_servico_id_Tipo")
    private TipoDeServico tipoDeServico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_Cliente_id_Cliente")
    private Cliente cliente;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "data_venda")
    private LocalDate dataVenda;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "data_instalacao")
    private LocalDate dataInstalacao;

    @Column(name = "obs_pagamento", columnDefinition = "TEXT")
    private String observacaoPagamento;

    @Column(name = "obs_execucao", columnDefinition = "TEXT")
    private String observacaoExecucao;

    @Size(
        max = 50,
        message = "O status deve ter no máximo 50 caracteres."
    )
    @Column(name = "status_servico", length = 50)
    private String statusServico;

    public Servico() {
    }

    public Integer getIdServico() {
        return idServico;
    }

    public void setIdServico(Integer idServico) {
        this.idServico = idServico;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataOrcamento() {
        return dataOrcamento;
    }

    public void setDataOrcamento(LocalDate dataOrcamento) {
        this.dataOrcamento = dataOrcamento;
    }

    public BigDecimal getValorCusto() {
        return valorCusto;
    }

    public void setValorCusto(BigDecimal valorCusto) {
        this.valorCusto = valorCusto;
    }

    public BigDecimal getValorCobrado() {
        return valorCobrado;
    }

    public void setValorCobrado(BigDecimal valorCobrado) {
        this.valorCobrado = valorCobrado;
    }

    public TipoDeServico getTipoDeServico() {
        return tipoDeServico;
    }

    public void setTipoDeServico(TipoDeServico tipoDeServico) {
        this.tipoDeServico = tipoDeServico;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public LocalDate getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDate dataVenda) {
        this.dataVenda = dataVenda;
    }

    public LocalDate getDataInstalacao() {
        return dataInstalacao;
    }

    public void setDataInstalacao(LocalDate dataInstalacao) {
        this.dataInstalacao = dataInstalacao;
    }

    public String getObservacaoPagamento() {
        return observacaoPagamento;
    }

    public void setObservacaoPagamento(String observacaoPagamento) {
        this.observacaoPagamento = observacaoPagamento;
    }

    public String getObservacaoExecucao() {
        return observacaoExecucao;
    }

    public void setObservacaoExecucao(String observacaoExecucao) {
        this.observacaoExecucao = observacaoExecucao;
    }

    public String getStatusServico() {
        return statusServico;
    }

    public void setStatusServico(String statusServico) {
        this.statusServico = statusServico;
    }
}