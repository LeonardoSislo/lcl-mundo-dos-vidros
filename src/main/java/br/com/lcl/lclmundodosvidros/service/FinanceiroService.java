package br.com.lcl.lclmundodosvidros.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import br.com.lcl.lclmundodosvidros.dto.ResumoFinanceiro;
import br.com.lcl.lclmundodosvidros.model.Servico;
import br.com.lcl.lclmundodosvidros.repository.ServicoRepository;
import jakarta.persistence.criteria.Predicate;

@Service
public class FinanceiroService {

    private final ServicoRepository servicoRepository;

    public FinanceiroService(
            ServicoRepository servicoRepository) {

        this.servicoRepository = servicoRepository;
    }

    public ResumoFinanceiro gerarResumo(
            LocalDate dataInicial,
            LocalDate dataFinal,
            Integer tipoId,
            String nomeCliente) {

        String clientePesquisado = nomeCliente == null
                ? ""
                : nomeCliente.trim();

        Specification<Servico> filtros =
                (root, query, criteriaBuilder) -> {

            List<Predicate> condicoes = new ArrayList<>();

            condicoes.add(
                    criteriaBuilder.equal(
                            criteriaBuilder.lower(
                                    root.get("statusServico")
                            ),
                            "concluído"
                    )
            );

            condicoes.add(
                    criteriaBuilder.isNotNull(
                            root.get("dataInstalacao")
                    )
            );

            if (dataInicial != null) {
                condicoes.add(
                        criteriaBuilder.greaterThanOrEqualTo(
                                root.get("dataInstalacao"),
                                dataInicial
                        )
                );
            }

            if (dataFinal != null) {
                condicoes.add(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("dataInstalacao"),
                                dataFinal
                        )
                );
            }

            if (tipoId != null) {
                condicoes.add(
                        criteriaBuilder.equal(
                                root.get("tipoDeServico")
                                        .get("idTipo"),
                                tipoId
                        )
                );
            }

            if (!clientePesquisado.isBlank()) {
                condicoes.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("cliente")
                                                .get("nome")
                                ),
                                "%" + clientePesquisado
                                        .toLowerCase() + "%"
                        )
                );
            }

            return criteriaBuilder.and(
                    condicoes.toArray(new Predicate[0])
            );
        };

        Sort ordenacao = Sort.by(
                Sort.Order.desc("dataInstalacao"),
                Sort.Order.desc("idServico")
        );

        List<Servico> servicos = servicoRepository.findAll(
                filtros,
                ordenacao
        );

        BigDecimal entradas = servicos.stream()
                .map(Servico::getValorCobrado)
                .map(this::valorOuZero)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saidas = servicos.stream()
                .map(Servico::getValorCusto)
                .map(this::valorOuZero)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ResumoFinanceiro(
                servicos,
                entradas,
                saidas
        );
    }

    private BigDecimal valorOuZero(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }
}