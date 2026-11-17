package br.com.lcl.lclmundodosvidros.dto;

import java.math.BigDecimal;
import java.util.List;

import br.com.lcl.lclmundodosvidros.model.Servico;

public class ResumoFinanceiro {

    private final List<Servico> servicos;
    private final long quantidadeServicos;
    private final BigDecimal totalEntradas;
    private final BigDecimal totalSaidas;
    private final BigDecimal lucro;

    public ResumoFinanceiro(
            List<Servico> servicos,
            BigDecimal totalEntradas,
            BigDecimal totalSaidas) {

        this.servicos = servicos;
        this.quantidadeServicos = servicos.size();
        this.totalEntradas = totalEntradas;
        this.totalSaidas = totalSaidas;
        this.lucro = totalEntradas.subtract(totalSaidas);
    }

    public List<Servico> getServicos() {
        return servicos;
    }

    public long getQuantidadeServicos() {
        return quantidadeServicos;
    }

    public BigDecimal getTotalEntradas() {
        return totalEntradas;
    }

    public BigDecimal getTotalSaidas() {
        return totalSaidas;
    }

    public BigDecimal getLucro() {
        return lucro;
    }
}