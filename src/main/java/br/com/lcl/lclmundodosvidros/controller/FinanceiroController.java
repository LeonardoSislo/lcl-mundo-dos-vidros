package br.com.lcl.lclmundodosvidros.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.lcl.lclmundodosvidros.dto.ResumoFinanceiro;
import br.com.lcl.lclmundodosvidros.model.TipoDeServico;
import br.com.lcl.lclmundodosvidros.repository.TipoDeServicoRepository;
import br.com.lcl.lclmundodosvidros.service.FinanceiroService;
import java.time.LocalDateTime;

import br.com.lcl.lclmundodosvidros.model.TipoDeServico;

import br.com.lcl.lclmundodosvidros.service.RelatorioFinanceiroPdfService;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import br.com.lcl.lclmundodosvidros.dto.ResumoFinanceiro;
import br.com.lcl.lclmundodosvidros.model.TipoDeServico;

@Controller
public class FinanceiroController {

    private final RelatorioFinanceiroPdfService pdfService;
    private final FinanceiroService financeiroService;
    private final TipoDeServicoRepository tipoRepository;

    public FinanceiroController(
            FinanceiroService financeiroService,
            TipoDeServicoRepository tipoRepository,
            RelatorioFinanceiroPdfService pdfService) {

        this.financeiroService = financeiroService;
        this.tipoRepository = tipoRepository;
        this.pdfService = pdfService;
    }

    @GetMapping("/financeiro")
    public String abrirFinanceiro(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataInicial,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataFinal,

            @RequestParam(required = false)
            Integer tipo,

            @RequestParam(defaultValue = "")
            String cliente,

            Model model) {

        if (dataInicial == null && dataFinal == null) {
            LocalDate hoje = LocalDate.now();

            dataInicial = hoje.withDayOfMonth(1);
            dataFinal = hoje;
        }

        ResumoFinanceiro resumo =
                financeiroService.gerarResumo(
                        dataInicial,
                        dataFinal,
                        tipo,
                        cliente
                );

        model.addAttribute("resumo", resumo);

        model.addAttribute(
                "tipos",
                tipoRepository.findAllByOrderByTipoAsc()
        );

        model.addAttribute("dataInicial", dataInicial);
        model.addAttribute("dataFinal", dataFinal);
        model.addAttribute("tipoSelecionado", tipo);
        model.addAttribute("clientePesquisado", cliente);

        return "financeiro/resumo";
    }

    @GetMapping("/financeiro/imprimir")
        public String imprimirFinanceiro(
                @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                LocalDate dataInicial,

                @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                LocalDate dataFinal,

                @RequestParam(required = false)
                Integer tipo,

                @RequestParam(defaultValue = "")
                String cliente,

                Model model) {

            if (dataInicial == null || dataFinal == null) {
                LocalDate hoje = LocalDate.now();

                if (dataInicial == null) {
                    dataInicial = hoje.withDayOfMonth(1);
                }

                if (dataFinal == null) {
                    dataFinal = hoje;
                }
            }

            if (dataInicial.isAfter(dataFinal)) {
                LocalDate dataTemporaria = dataInicial;
                dataInicial = dataFinal;
                dataFinal = dataTemporaria;
            }

            ResumoFinanceiro resumo =
                    financeiroService.gerarResumo(
                            dataInicial,
                            dataFinal,
                            tipo,
                            cliente
                    );

            String nomeTipo = tipoRepository.findById(tipo == null ? -1 : tipo)
                    .map(TipoDeServico::getTipo)
                    .orElse("Todos os tipos");

            model.addAttribute("resumo", resumo);
            model.addAttribute("dataInicial", dataInicial);
            model.addAttribute("dataFinal", dataFinal);
            model.addAttribute("clientePesquisado", cliente);
            model.addAttribute("tipoSelecionadoNome", nomeTipo);
            model.addAttribute("emitidoEm", LocalDateTime.now());

            return "financeiro/impressao";
        }


        @GetMapping(
                    value = "/financeiro/pdf",
                    produces = MediaType.APPLICATION_PDF_VALUE
                )
                public ResponseEntity<byte[]> baixarPdf(
                        @RequestParam(required = false)
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                        LocalDate dataInicial,

                        @RequestParam(required = false)
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                        LocalDate dataFinal,

                        @RequestParam(required = false)
                        Integer tipo,

                        @RequestParam(defaultValue = "")
                        String cliente) {

                    LocalDate hoje = LocalDate.now();

                    if (dataInicial == null) {
                        dataInicial = hoje.withDayOfMonth(1);
                    }

                    if (dataFinal == null) {
                        dataFinal = hoje;
                    }

                    if (dataInicial.isAfter(dataFinal)) {
                        LocalDate temporaria = dataInicial;
                        dataInicial = dataFinal;
                        dataFinal = temporaria;
                    }

                    ResumoFinanceiro resumo =
                            financeiroService.gerarResumo(
                                    dataInicial,
                                    dataFinal,
                                    tipo,
                                    cliente
                            );

                    String nomeTipo = tipo == null
                            ? "Todos os tipos"
                            : tipoRepository.findById(tipo)
                                    .map(TipoDeServico::getTipo)
                                    .orElse("Tipo não encontrado");

                    String nomeCliente =
                            cliente == null || cliente.isBlank()
                                    ? "Todos os clientes"
                                    : cliente.trim();

                    byte[] pdf = pdfService.gerarPdf(
                            resumo,
                            dataInicial,
                            dataFinal,
                            nomeTipo,
                            nomeCliente
                    );

                    String nomeArquivo =
                            "resumo-financeiro-"
                                    + dataInicial
                                    + "-a-"
                                    + dataFinal
                                    + ".pdf";

                    HttpHeaders cabecalhos = new HttpHeaders();

                    cabecalhos.setContentType(MediaType.APPLICATION_PDF);

                    cabecalhos.setContentDisposition(
                            ContentDisposition.attachment()
                                    .filename(nomeArquivo)
                                    .build()
                    );

                    cabecalhos.setContentLength(pdf.length);

                    return ResponseEntity
                            .ok()
                            .headers(cabecalhos)
                            .body(pdf);
                }
}