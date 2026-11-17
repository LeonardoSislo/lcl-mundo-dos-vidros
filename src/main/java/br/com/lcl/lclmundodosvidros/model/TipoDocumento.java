package br.com.lcl.lclmundodosvidros.model;

public enum TipoDocumento {

    ORCAMENTO("Orçamento"),
    PEDIDO("Pedido");

    private final String descricao;

    TipoDocumento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}