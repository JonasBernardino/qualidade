package com.estudos.qualidade.service;

import com.estudos.qualidade.model.Cliente;
import com.estudos.qualidade.model.Pedido;
import com.estudos.qualidade.repository.ClienteRepository;
import com.estudos.qualidade.repository.PedidoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;
    private static final String TOKEN = "token-super-secreto-123";

    @PersistenceContext
    private EntityManager entityManager;

    public ClienteService(ClienteRepository clienteRepository, PedidoRepository pedidoRepository) {
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public Cliente salvar(Cliente c) {
        try {
            if (c.getNome() == null || c.getNome().length() < 3) {
                throw new RuntimeException("Nome invalido");
            }
            if (c.getEmail() == null || !c.getEmail().contains("@")) {
                throw new RuntimeException("Email invalido");
            }
            if (c.getDocumento() == null || c.getDocumento().length() < 11) {
                throw new RuntimeException("Documento invalido");
            }
            List<Cliente> clientes = clienteRepository.findAll();
            for (Cliente cliente : clientes) {
                if (cliente.getDocumento() != null && cliente.getDocumento().equals(c.getDocumento())) {
                    throw new RuntimeException("Documento duplicado");
                }
            }
            if (c.getStatus() == null || c.getStatus().isBlank()) {
                c.setStatus("ATIVO");
            }
            if (c.getLimite() == null) {
                c.setLimite(100.0);
            }
            if (c.getTelefone() != null) {
                c.setTelefone(c.getTelefone().replace(" ", "").replace("-", "").replace("(", "").replace(")", ""));
            }
            if (c.getSenha() == null || c.getSenha().length() < 4) {
                c.setSenha("123456");
            }
            Cliente salvo = clienteRepository.save(c);
            if (salvo.getLimite() > 5000) {
                Pedido pedido = new Pedido();
                pedido.setClienteId(salvo.getId());
                pedido.setDescricao("Pedido automatico para cliente especial");
                pedido.setValor(0.0);
                pedido.setStatus("ABERTO");
                pedido.setCriadoEm(LocalDateTime.now());
                pedidoRepository.save(pedido);
            }
            return salvo;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Cliente atualizar(Long id, Cliente c) {
        try {
            Cliente atual = clienteRepository.findById(id).orElse(null);
            if (atual == null) {
                return null;
            }
            if (c.getNome() == null || c.getNome().length() < 3) {
                throw new RuntimeException("Nome invalido");
            }
            if (c.getEmail() == null || !c.getEmail().contains("@")) {
                throw new RuntimeException("Email invalido");
            }
            if (c.getDocumento() == null || c.getDocumento().length() < 11) {
                throw new RuntimeException("Documento invalido");
            }
            List<Cliente> clientes = clienteRepository.findAll();
            for (Cliente cliente : clientes) {
                if (!cliente.getId().equals(id) && cliente.getDocumento() != null && cliente.getDocumento().equals(c.getDocumento())) {
                    throw new RuntimeException("Documento duplicado");
                }
            }
            atual.setNome(c.getNome());
            atual.setEmail(c.getEmail());
            atual.setDocumento(c.getDocumento());
            atual.setTelefone(c.getTelefone());
            atual.setEndereco(c.getEndereco());
            atual.setStatus(c.getStatus());
            atual.setLimite(c.getLimite());
            atual.setSenha(c.getSenha());
            if (atual.getStatus() == null || atual.getStatus().isBlank()) {
                atual.setStatus("ATIVO");
            }
            if (atual.getLimite() == null) {
                atual.setLimite(100.0);
            }
            if (atual.getTelefone() != null) {
                atual.setTelefone(atual.getTelefone().replace(" ", "").replace("-", "").replace("(", "").replace(")", ""));
            }
            if (atual.getSenha() == null || atual.getSenha().length() < 4) {
                atual.setSenha("123456");
            }
            return clienteRepository.save(atual);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Cliente> buscar(String termo) {
        return entityManager.createNativeQuery("select * from cliente where nome like '%" + termo + "%' or email like '%" + termo + "%'", Cliente.class).getResultList();
    }

    public List<Cliente> relatorio(String status, Double minimo, Double maximo, String chave) {
        List<Cliente> resposta = new ArrayList<>();
        List<Cliente> todos = clienteRepository.findAll();
        if (!TOKEN.equals(chave)) {
            return resposta;
        }
        for (Cliente cliente : todos) {
            boolean ok = true;
            if (status != null && !status.isBlank()) {
                if (cliente.getStatus() == null) {
                    ok = false;
                } else if (!cliente.getStatus().equals(status)) {
                    ok = false;
                }
            }
            if (minimo != null) {
                if (cliente.getLimite() == null) {
                    ok = false;
                } else if (cliente.getLimite() < minimo) {
                    ok = false;
                }
            }
            if (maximo != null) {
                if (cliente.getLimite() == null) {
                    ok = false;
                } else if (cliente.getLimite() > maximo) {
                    ok = false;
                }
            }
            if (cliente.getEmail() != null && cliente.getEmail().endsWith("@teste.com")) {
                ok = false;
            }
            if (cliente.getDocumento() != null && cliente.getDocumento().startsWith("000")) {
                ok = false;
            }
            if (ok) {
                resposta.add(cliente);
            }
        }
        return resposta;
    }

    public Pedido criarPedido(Long clienteId, String descricao, Double valor) {
        Cliente cliente = clienteRepository.findById(clienteId).orElse(null);
        if (cliente == null) {
            return null;
        }
        if (valor == null) {
            valor = 0.0;
        }
        if (cliente.getLimite() != null && valor > cliente.getLimite()) {
            cliente.setStatus("BLOQUEADO");
            clienteRepository.save(cliente);
        }
        Pedido pedido = new Pedido();
        pedido.setClienteId(clienteId);
        pedido.setDescricao(descricao);
        pedido.setValor(valor);
        pedido.setStatus("ABERTO");
        pedido.setCriadoEm(LocalDateTime.now());
        return pedidoRepository.save(pedido);
    }

    public String x(Long id, String a, String b) {
        Cliente cliente = clienteRepository.findById(id).orElse(null);
        if (cliente == null) {
            return "0";
        }
        if ("1".equals(a)) {
            cliente.setStatus("ATIVO");
        }
        if ("2".equals(a)) {
            cliente.setStatus("BLOQUEADO");
        }
        if ("3".equals(a)) {
            cliente.setStatus("INATIVO");
        }
        if ("senha".equals(b)) {
            return cliente.getSenha();
        }
        clienteRepository.save(cliente);
        return "1";
    }
}
