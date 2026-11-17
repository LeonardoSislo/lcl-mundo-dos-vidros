package br.com.lcl.lclmundodosvidros.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.lcl.lclmundodosvidros.model.Servico;
import br.com.lcl.lclmundodosvidros.repository.ClienteRepository;
import br.com.lcl.lclmundodosvidros.repository.ServicoRepository;
import br.com.lcl.lclmundodosvidros.repository.TipoDeServicoRepository;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;

import br.com.lcl.lclmundodosvidros.model.TipoDeServico;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

@Controller
public class ServicoController {

    private final ServicoRepository servicoRepository;
    private final ClienteRepository clienteRepository;
    private final TipoDeServicoRepository tipoDeServicoRepository;

    public ServicoController(
            ServicoRepository servicoRepository,
            ClienteRepository clienteRepository,
            TipoDeServicoRepository tipoDeServicoRepository) {

        this.servicoRepository = servicoRepository;
        this.clienteRepository = clienteRepository;
        this.tipoDeServicoRepository = tipoDeServicoRepository;
    }

    @GetMapping("/servicos")
                        public String listarServicos(
                                @RequestParam(defaultValue = "") String cliente,
                                @RequestParam(defaultValue = "") String descricao,
                                @RequestParam(required = false) Integer tipo,
                                @RequestParam(defaultValue = "") String status,
                                @RequestParam(defaultValue = "recente") String ordem,
                                Model model) {

                        String clientePesquisado = cliente.trim();
                        String descricaoPesquisada = descricao.trim();
                        String statusSelecionado = status.trim();

                        Specification<Servico> filtros = (root, query, criteriaBuilder) -> {
                                List<Predicate> condicoes = new ArrayList<>();

                                if (!clientePesquisado.isBlank()) {
                                condicoes.add(
                                        criteriaBuilder.like(
                                                criteriaBuilder.lower(
                                                        root.get("cliente").get("nome")
                                                ),
                                                "%" + clientePesquisado.toLowerCase() + "%"
                                        )
                                );
                                }

                                if (!descricaoPesquisada.isBlank()) {
                                condicoes.add(
                                        criteriaBuilder.like(
                                                criteriaBuilder.lower(root.get("descricao")),
                                                "%" + descricaoPesquisada.toLowerCase() + "%"
                                        )
                                );
                                }

                                if (tipo != null) {
                                condicoes.add(
                                        criteriaBuilder.equal(
                                                root.get("tipoDeServico").get("idTipo"),
                                                tipo
                                        )
                                );
                                }

                                if (!statusSelecionado.isBlank()) {
                                condicoes.add(
                                        criteriaBuilder.equal(
                                                criteriaBuilder.lower(
                                                        root.get("statusServico")
                                                ),
                                                statusSelecionado.toLowerCase()
                                        )
                                );
                                }

                                return criteriaBuilder.and(
                                        condicoes.toArray(new Predicate[0])
                                );
                        };

                        Sort ordenacao;

                        if ("antiga".equalsIgnoreCase(ordem)) {
                                ordenacao = Sort.by(
                                        Sort.Order.asc("dataVenda").nullsLast(),
                                        Sort.Order.asc("idServico")
                                );
                        } else {
                                ordenacao = Sort.by(
                                        Sort.Order.desc("dataVenda").nullsLast(),
                                        Sort.Order.desc("idServico")
                                );
                        }

                        List<Servico> servicos = servicoRepository.findAll(
                                filtros,
                                ordenacao
                        );

                        model.addAttribute("servicos", servicos);

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

                        model.addAttribute(
                                "tipos",
                                tipoDeServicoRepository.findAllByOrderByTipoAsc()
                        );

                        model.addAttribute("clientePesquisado", clientePesquisado);
                        model.addAttribute("descricaoPesquisada", descricaoPesquisada);
                        model.addAttribute("tipoSelecionado", tipo);
                        model.addAttribute("statusSelecionado", statusSelecionado);
                        model.addAttribute("ordemSelecionada", ordem);

                        return "servicos/lista";
}

        @GetMapping("/servicos/{id}/consultar")
                public String consultarServico(
                        @PathVariable Integer id,
                        Model model,
                        RedirectAttributes redirectAttributes) {

                Servico servico = servicoRepository.findById(id).orElse(null);

                if (servico == null) {
                        redirectAttributes.addFlashAttribute(
                                "mensagemErro",
                                "Serviço não encontrado."
                        );

                        return "redirect:/servicos";
                }

                model.addAttribute("servico", servico);

                return "servicos/detalhes";
        }

    @GetMapping("/servicos/novo")
    public String novoServico(Model model) {
        Servico servico = new Servico();
        servico.setStatusServico("Pendente");

        model.addAttribute("servico", servico);
        carregarDadosFormulario(model);

        return "servicos/formulario";
    }

        @PostMapping("/servicos/salvar")
                public String salvarServico(
                        @Valid Servico servico,
                        BindingResult resultado,
                        Model model,
                        RedirectAttributes redirectAttributes) {

                validarDatas(servico, resultado);

                if (resultado.hasErrors()) {
                        carregarDadosFormulario(model);

                        return "servicos/formulario";
                }

                boolean novoServico = servico.getIdServico() == null;

                servicoRepository.save(servico);

                redirectAttributes.addFlashAttribute(
                        "mensagem",
                        novoServico
                                ? "Serviço cadastrado com sucesso."
                                : "Serviço atualizado com sucesso."
                );

                return "redirect:/servicos";
        }

    @GetMapping("/servicos/{id}/editar")
    public String editarServico(
            @PathVariable Integer id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Servico servico = servicoRepository.findById(id).orElse(null);

        if (servico == null) {
            redirectAttributes.addFlashAttribute(
                    "mensagemErro",
                    "Serviço não encontrado."
            );

            return "redirect:/servicos";
        }

        model.addAttribute("servico", servico);
        carregarDadosFormulario(model);

        return "servicos/formulario";
    }

    @PostMapping("/servicos/excluir")
    public String excluirServico(
            @RequestParam Integer id,
            RedirectAttributes redirectAttributes) {

        Servico servico = servicoRepository.findById(id).orElse(null);

        if (servico == null) {
            redirectAttributes.addFlashAttribute(
                    "mensagemErro",
                    "Serviço não encontrado."
            );

            return "redirect:/servicos";
        }

        servicoRepository.delete(servico);

        redirectAttributes.addFlashAttribute(
                "mensagem",
                "Serviço excluído com sucesso."
        );

        return "redirect:/servicos";
    }

    @PostMapping("/servicos/tipos/adicionar")
                @ResponseBody
                public ResponseEntity<Map<String, Object>> adicionarTipoServico(
                        @RequestParam String tipo) {

                Map<String, Object> resposta = new HashMap<>();

                String nomeTipo = tipo == null ? "" : tipo.trim();

                if (nomeTipo.isEmpty()) {
                        resposta.put("sucesso", false);
                        resposta.put(
                                "mensagem",
                                "Informe o nome do tipo de serviço."
                        );

                        return ResponseEntity.badRequest().body(resposta);
                }

                if (nomeTipo.length() > 255) {
                        resposta.put("sucesso", false);
                        resposta.put(
                                "mensagem",
                                "O tipo deve ter no máximo 255 caracteres."
                        );

                        return ResponseEntity.badRequest().body(resposta);
                }

                if (tipoDeServicoRepository.existsByTipoIgnoreCase(nomeTipo)) {
                        resposta.put("sucesso", false);
                        resposta.put(
                                "mensagem",
                                "Esse tipo de serviço já está cadastrado."
                        );

                        return ResponseEntity.badRequest().body(resposta);
                }

                TipoDeServico novoTipo = new TipoDeServico();
                novoTipo.setTipo(nomeTipo);

                novoTipo = tipoDeServicoRepository.save(novoTipo);

                resposta.put("sucesso", true);
                resposta.put("id", novoTipo.getIdTipo());
                resposta.put("tipo", novoTipo.getTipo());
                resposta.put(
                        "mensagem",
                        "Tipo de serviço cadastrado com sucesso."
                );

                return ResponseEntity.ok(resposta);
}


    private void carregarDadosFormulario(Model model) {
        model.addAttribute(
                "clientes",
                clienteRepository.findAllByOrderByNomeAsc()
        );

        model.addAttribute(
                "tipos",
                tipoDeServicoRepository.findAllByOrderByTipoAsc()
        );
    }


        private void validarDatas(
        Servico servico,
        BindingResult resultado) {

                if (servico.getDataVenda() == null
                        || servico.getDataInstalacao() == null) {
                        return;
                }

                if (servico.getDataInstalacao()
                        .isBefore(servico.getDataVenda())) {

                        resultado.rejectValue(
                                "dataInstalacao",
                                "dataInstalacao.invalida",
                                "A data da execução não pode ser anterior "
                                        + "à data da venda."
                        );
                }
}
}