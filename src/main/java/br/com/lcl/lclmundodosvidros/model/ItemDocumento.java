package br.com.lcl.lclmundodosvidros.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "item_documento")
public class ItemDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item")
    private Integer idItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "fk_documento_id_documento",
        nullable = false
    )
    private DocumentoComercial documento;

    @NotNull(message = "Informe a quantidade.")
    @DecimalMin(
        value = "0.01",
        message = "A quantidade deve ser maior que zero."
    )
    @Column(
        name = "quantidade",
        nullable = false,
        precision = 10,
        scale = 2
    )
    private BigDecimal quantidade = BigDecimal.ONE;

    @NotBlank(message = "Informe a descrição do item.")
    @Column(
        name = "descricao",
        nullable = false,
        columnDefinition = "TEXT"
    )
    private String descricao;

    @Column(name = "largura", precision = 10, scale = 2)
    private BigDecimal largura;

    @Column(name = "altura", precision = 10, scale = 2)
    private BigDecimal altura;

    @Size(max = 10)
    @Column(name = "unidade_medida", length = 10)
    private String unidadeMedida = "mm";

    @NotNull(message = "Informe o valor unitário.")
    @DecimalMin(
        value = "0.00",
        message = "O valor unitário não pode ser negativo."
    )
    @Column(
        name = "valor_unitario",
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal valorUnitario = BigDecimal.ZERO;

    @Column(
        name = "valor_total",
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(name = "ordem_item", nullable = false)
    private Integer ordemItem = 1;

    public ItemDocumento() {
    }

    @PrePersist
    @PreUpdate
    public void calcularValorTotal() {
        BigDecimal quantidadeCalculada =
                quantidade == null
                        ? BigDecimal.ZERO
                        : quantidade;

        BigDecimal valorCalculado =
                valorUnitario == null
                        ? BigDecimal.ZERO
                        : valorUnitario;

        valorTotal = quantidadeCalculada
                .multiply(valorCalculado);
    }

    public Integer getIdItem() {
        return idItem;
    }

    public void setIdItem(Integer idItem) {
        this.idItem = idItem;
    }

    public DocumentoComercial getDocumento() {
        return documento;
    }

    public void setDocumento(
            DocumentoComercial documento) {

        this.documento = documento;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getLargura() {
        return largura;
    }

    public void setLargura(BigDecimal largura) {
        this.largura = largura;
    }

    public BigDecimal getAltura() {
        return altura;
    }

    public void setAltura(BigDecimal altura) {
        this.altura = altura;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(
            String unidadeMedida) {

        this.unidadeMedida = unidadeMedida;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(
            BigDecimal valorUnitario) {

        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Integer getOrdemItem() {
        return ordemItem;
    }

    public void setOrdemItem(Integer ordemItem) {
        this.ordemItem = ordemItem;
    }
}