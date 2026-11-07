package br.com.lcl.lclmundodosvidros.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.lcl.lclmundodosvidros.model.Servico;
import br.com.lcl.lclmundodosvidros.repository.ClienteRepository;
import br.com.lcl.lclmundodosvidros.repository.ServicoRepository;
import br.com.lcl.lclmundodosvidros.repository.TipoDeServicoRepository;
import jakarta.validation.Valid;

@Controller
public class ServicoController {

    private final ServicoRepository servicoRepository;
    private final ClienteRepository clienteRepository;
    private final TipoDeServicoRepository tipoDeServicoRepository;

    public ServicoController(ServicoRepository servicoRepository, ClienteRepository clienteRepository, TipoDeServicoRepository tipoDeServicoRepository) {
        this.servicoRepository = servicoRepository;
        this.clienteRepository = clienteRepository;
        this.tipoDeServicoRepository = tipoDeServicoRepository;
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

    @GetMapping("/servicos/novo")
    public String novoServico(Model model) {

        model.addAttribute(
                "servico",
                new Servico());
        model.addAttribute(
                "clientes",
                clienteRepository.findAll());
        model.addAttribute(
                "tipos",
                tipoDeServicoRepository.findAllByOrderByTipoAsc());
        return "servicos/formulario";
    }

    @PostMapping("/servicos/salvar")
    public String salvarServico(
            @Valid Servico servico,
            BindingResult resultado,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (resultado.hasErrors()) {

            model.addAttribute(
                    "clientes",
                    clienteRepository.findAll());
            model.addAttribute(
                    "tipos",
                    tipoDeServicoRepository.findAllByOrderByTipoAsc());
            return "servicos/formulario";
        }

        servicoRepository.save(servico);

        redirectAttributes.addFlashAttribute(
                "mensagem",
                "Serviço cadastrado com sucesso.");

        return "redirect:/servicos";
    }
}
