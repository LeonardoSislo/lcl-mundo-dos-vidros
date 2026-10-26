package br.com.lcl.lclmundodosvidros.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "contato")
public class Contato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contato")
    private Integer idContato;

    @Size(
        max = 20,
        message = "O telefone deve ter no máximo 20 caracteres."
    )
    @Column(name = "telefone", length = 20)
    private String telefone;

    @Email(message = "Informe um e-mail válido.")
    @Size(
        max = 255,
        message = "O e-mail deve ter no máximo 255 caracteres."
    )
    @Column(name = "email", length = 255)
    private String email;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "fk_Cliente_id_Cliente",
        nullable = false,
        unique = true
    )
    private Cliente cliente;

    public Contato() {
    }

    public Integer getIdContato() {
        return idContato;
    }

    public void setIdContato(Integer idContato) {
        this.idContato = idContato;
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

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}