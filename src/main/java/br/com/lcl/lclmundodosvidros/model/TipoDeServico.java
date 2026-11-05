package br.com.lcl.lclmundodosvidros.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tipo_de_servico")
public class TipoDeServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Tipo")
    private Integer idTipo;

    @NotBlank(message = "O tipo de serviço é obrigatório.")
    @Size(
        max = 255,
        message = "O tipo de serviço deve ter no máximo 255 caracteres."
    )
    @Column(name = "tipo", nullable = false, length = 255)
    private String tipo;

    public TipoDeServico() {
    }

    public Integer getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(Integer idTipo) {
        this.idTipo = idTipo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}