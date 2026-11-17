package br.com.lcl.lclmundodosvidros.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.lcl.lclmundodosvidros.model.DocumentoComercial;
import br.com.lcl.lclmundodosvidros.repository.ClienteRepository;
import br.com.lcl.lclmundodosvidros.service.DocumentoComercialService;
import jakarta.validation.Valid;

@Controller
public class DocumentoComercialController {

    private final DocumentoComercialService documentoService;
    private final ClienteRepository clienteRepository;

    public DocumentoComercialController(
            DocumentoComercialService documentoService,
            ClienteRepository clienteRepository) {

        this.documentoService = documentoService;
        this.clienteRepository = clienteRepository;
    }

    @GetMapping("/documentos")
    public String listarDocumentos(Model model) {
        model.addAttribute(
                "documentos",
                documentoService.listarTodos()
        );

        return "documentos/lista";
    }

    @GetMapping("/orcamentos/novo")
    public String novoOrcamento(Model model) {
        model.addAttribute(
                "documento",
                documentoService.criarNovoOrcamento()
        );

        carregarClientes(model);

        return "documentos/formulario";
    }

    @GetMapping("/pedidos/novo")
    public String novoPedido(Model model) {
        model.addAttribute(
                "documento",
                documentoService.criarNovoPedido()
        );

        carregarClientes(model);

        return "documentos/formulario";
    }

    @PostMapping("/documentos/salvar")
    public String salvarDocumento(
            @Valid DocumentoComercial documento,
            BindingResult resultado,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (resultado.hasErrors()) {
            carregarClientes(model);

            return "documentos/formulario";
        }

        try {
            DocumentoComercial documentoSalvo =
                    documentoService.salvar(documento);

            redirectAttributes.addFlashAttribute(
                    "mensagem",
                    documentoSalvo.getTipo().getDescricao()
                            + " salvo com sucesso."
            );

            return "redirect:/documentos/"
                    + documentoSalvo.getIdDocumento()
                    + "/consultar";

        } catch (IllegalArgumentException exception) {
            model.addAttribute(
                    "mensagemErro",
                    exception.getMessage()
            );

            carregarClientes(model);

            return "documentos/formulario";

        } catch (DataIntegrityViolationException exception) {
            model.addAttribute(
                    "mensagemErro",
                    "Não foi possível salvar o documento. "
                            + "Confira se o número já existe."
            );

            carregarClientes(model);

            return "documentos/formulario";
        }
    }

    @GetMapping("/documentos/{id}/consultar")
    public String consultarDocumento(
            @PathVariable Integer id,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            model.addAttribute(
                    "documento",
                    documentoService.buscarComItens(id)
            );

            return "documentos/detalhes";

        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute(
                    "mensagemErro",
                    exception.getMessage()
            );

            return "redirect:/documentos";
        }
    }

    private void carregarClientes(Model model) {
        model.addAttribute(
                "clientes",
                clienteRepository.findAllByOrderByNomeAsc()
        );
    }
}