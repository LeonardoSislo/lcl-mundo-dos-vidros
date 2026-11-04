package br.com.lcl.lclmundodosvidros.controller;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.lcl.lclmundodosvidros.model.Cliente;
import br.com.lcl.lclmundodosvidros.model.Contato;
import br.com.lcl.lclmundodosvidros.repository.ClienteRepository;
import jakarta.validation.Valid;

@Controller
public class ClienteController {

    private final ClienteRepository clienteRepository;

    public ClienteController(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @GetMapping("/clientes")
    public String listarClientes(
            @RequestParam(defaultValue = "") String nome,
            @RequestParam(defaultValue = "asc") String ordem,
            Model model) {

        String pesquisa = nome.trim();
        List<Cliente> clientes;

        if (pesquisa.isEmpty()) {
            clientes = ordem.equalsIgnoreCase("desc")
                    ? clienteRepository.findAllByOrderByNomeDesc()
                    : clienteRepository.findAllByOrderByNomeAsc();
        } else {
            clientes = ordem.equalsIgnoreCase("desc")
                    ? clienteRepository
                            .findByNomeContainingIgnoreCaseOrderByNomeDesc(pesquisa)
                    : clienteRepository
                            .findByNomeContainingIgnoreCaseOrderByNomeAsc(pesquisa);
        }

        model.addAttribute("clientes", clientes);
        model.addAttribute("nomePesquisado", pesquisa);
        model.addAttribute(
                "ordemSelecionada",
                ordem.equalsIgnoreCase("desc") ? "desc" : "asc"
        );

        return "clientes/lista";
    }

    @GetMapping("/clientes/novo")
    public String novoCliente(Model model) {
        Cliente cliente = new Cliente();
        cliente.setContato(new Contato());

        model.addAttribute("cliente", cliente);

        return "clientes/formulario";
    }

    @GetMapping("/clientes/{id}/editar")
    public String editarCliente(
            @PathVariable Integer id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Cliente cliente = clienteRepository.findById(id).orElse(null);

        if (cliente == null) {
            redirectAttributes.addFlashAttribute(
                    "mensagemErro",
                    "Cliente não encontrado."
            );

            return "redirect:/clientes";
        }

        if (cliente.getContato() == null) {
            cliente.setContato(new Contato());
        }

        model.addAttribute("cliente", cliente);

        return "clientes/formulario";
    }

    @PostMapping("/clientes/salvar")
    public String salvarCliente(
            @Valid Cliente cliente,
            BindingResult resultado,
            RedirectAttributes redirectAttributes) {

        if (resultado.hasErrors()) {
            return "clientes/formulario";
        }

        if (cliente.getIdCliente() == null) {
            cadastrarCliente(cliente, redirectAttributes);
        } else {
            boolean atualizado = atualizarCliente(cliente, redirectAttributes);

            if (!atualizado) {
                return "redirect:/clientes";
            }
        }

        return "redirect:/clientes";
    }

    @PostMapping("/clientes/excluir")
    public String excluirCliente(
            @RequestParam Integer id,
            RedirectAttributes redirectAttributes) {

        Cliente cliente = clienteRepository.findById(id).orElse(null);

        if (cliente == null) {
            redirectAttributes.addFlashAttribute(
                    "mensagemErro",
                    "Cliente não encontrado."
            );

            return "redirect:/clientes";
        }

        try {
            clienteRepository.delete(cliente);
            clienteRepository.flush();

            redirectAttributes.addFlashAttribute(
                    "mensagem",
                    "Cliente e contato excluídos com sucesso."
            );
        } catch (DataIntegrityViolationException exception) {
            redirectAttributes.addFlashAttribute(
                    "mensagemErro",
                    "Não foi possível excluir o cliente porque existem "
                            + "serviços vinculados a esse cadastro."
            );
        }

        return "redirect:/clientes";
    }

    private void cadastrarCliente(
            Cliente cliente,
            RedirectAttributes redirectAttributes) {

        vincularContato(cliente);
        clienteRepository.save(cliente);

        redirectAttributes.addFlashAttribute(
                "mensagem",
                "Cliente e contato cadastrados com sucesso."
        );
    }

    private boolean atualizarCliente(
            Cliente dadosFormulario,
            RedirectAttributes redirectAttributes) {

        Cliente clienteSalvo = clienteRepository
                .findById(dadosFormulario.getIdCliente())
                .orElse(null);

        if (clienteSalvo == null) {
            redirectAttributes.addFlashAttribute(
                    "mensagemErro",
                    "Cliente não encontrado."
            );

            return false;
        }

        clienteSalvo.setNome(dadosFormulario.getNome());
        clienteSalvo.setEndereco(dadosFormulario.getEndereco());
        clienteSalvo.setBairro(dadosFormulario.getBairro());
        clienteSalvo.setCidade(dadosFormulario.getCidade());

        atualizarContato(clienteSalvo, dadosFormulario.getContato());

        clienteRepository.save(clienteSalvo);

        redirectAttributes.addFlashAttribute(
                "mensagem",
                "Cliente e contato atualizados com sucesso."
        );

        return true;
    }

    private void vincularContato(Cliente cliente) {
        if (cliente.getContato() != null) {
            cliente.getContato().setCliente(cliente);
        }
    }

    private void atualizarContato(
            Cliente clienteSalvo,
            Contato dadosContato) {

        if (dadosContato == null) {
            return;
        }

        Contato contatoSalvo = clienteSalvo.getContato();

        if (contatoSalvo == null) {
            contatoSalvo = new Contato();
            clienteSalvo.setContato(contatoSalvo);
        }

        contatoSalvo.setTelefone(dadosContato.getTelefone());
        contatoSalvo.setEmail(dadosContato.getEmail());
        contatoSalvo.setCliente(clienteSalvo);
    }
}