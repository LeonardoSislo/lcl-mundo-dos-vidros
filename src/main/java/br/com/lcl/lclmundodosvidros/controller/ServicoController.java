package br.com.lcl.lclmundodosvidros.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import br.com.lcl.lclmundodosvidros.repository.ServicoRepository;

@Controller
public class ServicoController {

    private final ServicoRepository servicoRepository;

    public ServicoController(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    @GetMapping("/servicos")
    public String listarServicos(Model model) {
        model.addAttribute(
                "servicos",
                servicoRepository.findAllByOrderByIdServicoDesc()
        );

        model.addAttribute(
                "quantidadePendentes",
                servicoRepository.countByStatusServicoIgnoreCase("Pendente")
        );

        model.addAttribute(
                "quantidadeEmAndamento",
                servicoRepository
                        .countByStatusServicoIgnoreCase("Em andamento")
        );

        model.addAttribute(
                "quantidadeConcluidos",
                servicoRepository.countByStatusServicoIgnoreCase("Concluído")
        );

        return "servicos/lista";
    }
}