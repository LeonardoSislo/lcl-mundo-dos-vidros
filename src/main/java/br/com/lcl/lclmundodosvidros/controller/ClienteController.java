package br.com.lcl.lclmundodosvidros.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.lcl.lclmundodosvidros.model.Cliente;
import br.com.lcl.lclmundodosvidros.repository.ClienteRepository;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.lcl.lclmundodosvidros.model.Contato;

import java.util.List;



@Controller
public class ClienteController {

    private final ClienteRepository clienteRepository;

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @GetMapping("/clientes")
        public String listarClientes(
                @RequestParam(
                        name = "nome",
                        required = false,
                        defaultValue = ""
                ) String nome,
                @RequestParam(
                        name = "ordem",
                        required = false,
                        defaultValue = "asc"
                ) String ordem,
                Model model) {

            String nomePesquisado = nome.trim();
            boolean ordemDecrescente = "desc".equalsIgnoreCase(ordem);

            List<Cliente> clientes;

            if (nomePesquisado.isBlank()) {

                if (ordemDecrescente) {
                    clientes = clienteRepository.findAllByOrderByNomeDesc();
                } else {
                    clientes = clienteRepository.findAllByOrderByNomeAsc();
                }

            } else {

                if (ordemDecrescente) {
                    clientes =
                            clienteRepository
                                    .findByNomeContainingIgnoreCaseOrderByNomeDesc(
                                            nomePesquisado
                                    );
                } else {
                    clientes =
                            clienteRepository
                                    .findByNomeContainingIgnoreCaseOrderByNomeAsc(
                                            nomePesquisado
                                    );
                }
            }

            model.addAttribute("clientes", clientes);
            model.addAttribute("nomePesquisado", nomePesquisado);
            model.addAttribute(
                    "ordemSelecionada",
                    ordemDecrescente ? "desc" : "asc"
            );

            return "clientes/lista";
        }

    @GetMapping("/clientes/novo")
    public String abrirFormulario(Model model) {
    Cliente cliente = new Cliente();
    cliente.setContato(new Contato());

    model.addAttribute("cliente", cliente);

    return "clientes/formulario";
    }



    @GetMapping("/clientes/{id}/editar")
    public String editarCliente(
        @PathVariable Integer id,
        Model model,
        RedirectAttributes atributos) {

    return clienteRepository.findById(id)
            .map(cliente -> {

                if (cliente.getContato() == null) {
                    cliente.setContato(new Contato());
                }

                model.addAttribute("cliente", cliente);

                return "clientes/formulario";
            })
            .orElseGet(() -> {
                atributos.addFlashAttribute(
                        "mensagemErro",
                        "Cliente não encontrado.");

                return "redirect:/clientes";
            });
}

    @PostMapping("/clientes/salvar")
public String salvarCliente(
        @Valid Cliente cliente,
        BindingResult resultado,
        RedirectAttributes atributos) {

    if (resultado.hasErrors()) {
        return "clientes/formulario";
    }

    boolean alteracao = cliente.getIdCliente() != null;

    if (alteracao) {
        Cliente clienteExistente = clienteRepository
                .findById(cliente.getIdCliente())
                .orElse(null);

        if (clienteExistente == null) {
            atributos.addFlashAttribute(
                    "mensagemErro",
                    "Cliente não encontrado.");

            return "redirect:/clientes";
        }

        clienteExistente.setNome(cliente.getNome());
        clienteExistente.setEndereco(cliente.getEndereco());
        clienteExistente.setBairro(cliente.getBairro());
        clienteExistente.setCidade(cliente.getCidade());

        atualizarContato(clienteExistente, cliente.getContato());

        clienteRepository.save(clienteExistente);

        atributos.addFlashAttribute(
                "mensagem",
                "Cliente e contato atualizados com sucesso.");

    } else {
        prepararContato(cliente);

        clienteRepository.save(cliente);

        atributos.addFlashAttribute(
                "mensagem",
                "Cliente e contato cadastrados com sucesso.");
    }

    return "redirect:/clientes";
}

@PostMapping("/clientes/excluir")
public String excluirCliente(
        @RequestParam Integer id,
        RedirectAttributes atributos) {

    Cliente cliente = clienteRepository.findById(id).orElse(null);

    if (cliente == null) {
        atributos.addFlashAttribute(
                "mensagemErro",
                "Cliente não encontrado.");

        return "redirect:/clientes";
    }

    try {
        clienteRepository.delete(cliente);
        clienteRepository.flush();

        atributos.addFlashAttribute(
                "mensagem",
                "Cliente e contato excluídos com sucesso.");

    } catch (DataIntegrityViolationException exception) {
        atributos.addFlashAttribute(
                "mensagemErro",
                "Não foi possível excluir o cliente porque existem "
                        + "serviços vinculados a esse cadastro.");
    }

    return "redirect:/clientes";
}

private void prepararContato(Cliente cliente) {
    Contato contato = cliente.getContato();

    if (contato != null) {
        contato.setCliente(cliente);
    }
}

    private void atualizarContato(
        Cliente clienteExistente,
        Contato contatoFormulario) {

    if (contatoFormulario == null) {
        return;
    }

    Contato contatoExistente = clienteExistente.getContato();

    if (contatoExistente == null) {
        contatoExistente = new Contato();
        clienteExistente.setContato(contatoExistente);
    }

    contatoExistente.setTelefone(contatoFormulario.getTelefone());
    contatoExistente.setEmail(contatoFormulario.getEmail());
    contatoExistente.setCliente(clienteExistente);
}
}