package com.mercadoscan.service;

import java.util.List;

import com.mercadoscan.dao.ProdutoDAO;
import com.mercadoscan.dao.ProdutoDAOImpl;
import com.mercadoscan.model.Produto;
import com.mercadoscan.model.ProdutoVoz;

public class ProdutoService {
    
    private final ProdutoDAO produtoDAO;
    private final String usuarioId;
    
    public ProdutoService(String usuarioId) {
        // VALIDAÇÃO
        if (usuarioId == null || usuarioId.trim().isEmpty()) {
            throw new IllegalArgumentException("usuarioId não pode ser nulo");
        }
        
        this.usuarioId = usuarioId.trim();
        this.produtoDAO = new ProdutoDAOImpl();
        
        System.out.println("\n=== DEBUG ProdutoService ===");
        System.out.println("✅ Service criado para usuarioId: " + this.usuarioId);
    }
    
    public Produto adicionarProduto(String nome, double valor, int quantidade) {
        System.out.println("\n=== DEBUG adicionarProduto ===");
        System.out.println("Nome: " + nome);
        System.out.println("Valor: " + valor);
        System.out.println("Quantidade: " + quantidade);
        System.out.println("UsuarioId: " + usuarioId);
        
        try {
            Produto produto = new Produto();
            produto.setNome(nome);
            produto.setValor(valor);
            produto.setQuantidade(quantidade);
            produto.setUsuarioId(usuarioId);
            
            System.out.println("📦 Produto criado, chamando DAO...");
            Produto produtoSalvo = produtoDAO.salvar(produto);
            System.out.println("✅ Produto salvo com ID: " + produtoSalvo.getId());
            
            return produtoSalvo;
            
        } catch (Exception e) {
            System.err.println("❌ Erro no service ao adicionar produto: " + e.getMessage());
            throw new RuntimeException("Erro ao adicionar produto", e);
        }
    }
    
    public List<Produto> listarProdutosPorUsuario(String usuarioId) {
        System.out.println("\n=== DEBUG listarProdutosPorUsuario ===");
        System.out.println("UsuarioId: " + usuarioId);
        
        try {
            List<Produto> produtos = produtoDAO.listarPorUsuarioId(usuarioId);
            System.out.println("✅ Produtos encontrados: " + produtos.size());
            return produtos;
        } catch (Exception e) {
            System.err.println("❌ Erro ao listar produtos: " + e.getMessage());
            return List.of(); // Retorna lista vazia em caso de erro
        }
    }
    
    // Outros métodos permanecem iguais...
    
    public List<Produto> listarProdutos() {
        return produtoDAO.buscarPorUsuario(usuarioId);
    }
    
    public Produto adicionarProdutoVoz(ProdutoVoz produtoVoz) {
        Produto produto = new Produto(produtoVoz.getNome(), produtoVoz.getValor(), usuarioId);
        produto.setQuantidade(produtoVoz.getQuantidade());
        produto.setCategoria("Adicionado por Voz");
        return produtoDAO.salvar(produto);
    }
    
    //public boolean removerProduto(String produtoId) {
      //  return produtoDAO.remover(produtoId);
  //  }
    
    public boolean removerProdutoPorNome(String nome) {
        List<Produto> produtos = produtoDAO.buscarPorNome(nome, usuarioId);
        if (!produtos.isEmpty()) {
            return produtoDAO.remover(produtos.get(0).getId());
        }
        return false;
    }
    
    public boolean limparLista() {
    return produtoDAO.removerTodosDoUsuario(usuarioId);
    }
    
    public double calcularTotal() {
        return produtoDAO.calcularTotalUsuario(usuarioId);
    }
    
    public int contarProdutos() {
        return produtoDAO.buscarPorUsuario(usuarioId).size();
    }
    
    public void removerProdutoPorNome(String nome, String usuarioId) {
        produtoDAO.removerPorNomeEUsuario(nome, usuarioId);
    }
    
   // public void limparListaUsuario(String usuarioId) {
    //    produtoDAO.removerTodosPorUsuario(usuarioId);
   // }
}