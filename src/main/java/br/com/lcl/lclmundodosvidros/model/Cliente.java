package br.com.lcl.lclmundodosvidros.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;


@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Cliente")
    private Integer idCliente;

    @NotBlank(message = "O nome do cliente é obrigatório.")
    @Size(min = 3, max = 255, message = "O nome deve ter entre 3 e 255 caracteres.")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]+$", message = "Nome deve conter apenas letras")
    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @NotBlank(message = "O endereço é obrigatório.")
    @Size(min = 5, max = 255, message = "O endereço deve ter entre 5 e 255 caracteres.")
    @Column(name = "endereco", length = 255)
    private String endereco;

    @NotBlank(message = "O bairro é obrigatório.")
    @Size(min = 3, max = 255, message = "O bairro deve ter entre 3 e 255 caracteres.")
    @Column(name = "bairro", length = 255)
    private String bairro;

    @NotBlank(message = "A cidade é obrigatória.")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s]+$", message = "A cidade deve conter apenas letras.")
    @Column(name = "cidade", length = 255)
    private String cidade;

            @Valid
            @OneToOne(
                mappedBy = "cliente",
                cascade = CascadeType.ALL,
                orphanRemoval = true
            )
    private Contato contato;

    public Cliente() {
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public Contato getContato() {
    return contato;
}

public void setContato(Contato contato) {
    this.contato = contato;

    if (contato != null) {
        contato.setCliente(this);
    }
}

}