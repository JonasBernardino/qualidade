package com.estudos.qualidade.controller;

import com.estudos.qualidade.model.Cliente;
import com.estudos.qualidade.model.Pedido;
import com.estudos.qualidade.repository.ClienteRepository;
import com.estudos.qualidade.repository.PedidoRepository;
import com.estudos.qualidade.service.ClienteService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;

    public ClienteController(ClienteService clienteService, ClienteRepository clienteRepository, PedidoRepository pedidoRepository) {
        this.clienteService = clienteService;
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping
    public List<Cliente> todos() {
        return clienteRepository.findAll();
    }

    @PostMapping
    public Cliente novo(@RequestBody Cliente cliente) {
        return clienteService.salvar(cliente);
    }

    @PutMapping("/{id}")
    public Cliente altera(@PathVariable Long id, @RequestBody Cliente cliente) {
        return clienteService.atualizar(id, cliente);
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> apagar(@PathVariable Long id) {
        Map<String, Object> resposta = new HashMap<>();
        clienteRepository.deleteById(id);
        resposta.put("ok", true);
        resposta.put("id", id);
        return resposta;
    }

    @GetMapping("/buscar")
    public List<Cliente> procurar(@RequestParam String q) {
        return clienteService.buscar(q);
    }

    @GetMapping("/relatorio")
    public List<Cliente> r(@RequestParam(required = false) String status, @RequestParam(required = false) Double minimo, @RequestParam(required = false) Double maximo, @RequestParam String chave) {
        return clienteService.relatorio(status, minimo, maximo, chave);
    }

    @PostMapping("/{id}/pedido")
    public Pedido pedido(@PathVariable Long id, @RequestParam String descricao, @RequestParam Double valor) {
        return clienteService.criarPedido(id, descricao, valor);
    }

    @GetMapping("/{id}/pedidos")
    public List<Pedido> pedidos(@PathVariable Long id) {
        return pedidoRepository.findByClienteId(id);
    }

    @PostMapping("/{id}/x")
    public String x(@PathVariable Long id, @RequestParam String a, @RequestParam String b) {
        return clienteService.x(id, a, b);
    }
}
