package br.com.lcl.lclmundodosvidros.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import br.com.lcl.lclmundodosvidros.model.Servico;
import br.com.lcl.lclmundodosvidros.repository.ClienteRepository;
import br.com.lcl.lclmundodosvidros.repository.ServicoRepository;

@Controller
public class HomeController {

    private final ClienteRepository clienteRepository;
    private final ServicoRepository servicoRepository;

    public HomeController(
            ClienteRepository clienteRepository,
            ServicoRepository servicoRepository) {

        this.clienteRepository = clienteRepository;
        this.servicoRepository = servicoRepository;
    }

    @GetMapping("/")
    public String abrirPaginaInicial(Model model) {
        YearMonth mesAtual = YearMonth.now();

        LocalDate primeiroDiaMes = mesAtual.atDay(1);
        LocalDate ultimoDiaMes = mesAtual.atEndOfMonth();

        List<Servico> todosServicos =
                servicoRepository.findAllByOrderByIdServicoDesc();

        List<Servico> servicosConcluidosNoMes =
                todosServicos.stream()
                        .filter(this::servicoConcluido)
                        .filter(servico ->
                                servico.getDataInstalacao() != null)
                        .filter(servico ->
                                !servico.getDataInstalacao()
                                        .isBefore(primeiroDiaMes))
                        .filter(servico ->
                                !servico.getDataInstalacao()
                                        .isAfter(ultimoDiaMes))
                        .toList();

        BigDecimal totalBrutoMes =
                servicosConcluidosNoMes.stream()
                        .map(Servico::getValorCobrado)
                        .filter(valor -> valor != null)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        model.addAttribute(
                "quantidadeClientes",
                clienteRepository.count()
        );

        model.addAttribute(
                "quantidadePendentes",
                servicoRepository
                        .countByStatusServicoIgnoreCase("Pendente")
        );

        model.addAttribute(
                "quantidadeEmAndamento",
                servicoRepository
                        .countByStatusServicoIgnoreCase(
                                "Em andamento"
                        )
        );

        model.addAttribute(
                "quantidadeConcluidos",
                servicoRepository
                        .countByStatusServicoIgnoreCase(
                                "Concluído"
                        )
        );

        model.addAttribute(
                "quantidadeExecutadosMes",
                servicosConcluidosNoMes.size()
        );

        model.addAttribute(
                "totalBrutoMes",
                totalBrutoMes
        );

        model.addAttribute(
                "servicosRecentes",
                servicoRepository
                        .findTop6ByOrderByIdServicoDesc()
        );

        model.addAttribute(
                "nomeMes",
                formatarMes(mesAtual)
        );

        return "index";
    }

    private boolean servicoConcluido(Servico servico) {
        return servico.getStatusServico() != null
                && servico.getStatusServico()
                        .trim()
                        .equalsIgnoreCase("Concluído");
    }

    private String formatarMes(YearMonth mes) {
        Locale brasil = new Locale("pt", "BR");

        String nomeMes = mes.getMonth()
                .getDisplayName(
                        TextStyle.FULL,
                        brasil
                );

        return nomeMes.substring(0, 1).toUpperCase()
                + nomeMes.substring(1)
                + " "
                + mes.getYear();
    }
}