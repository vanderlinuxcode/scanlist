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
        this.usuarioId = usuarioId;
        this.produtoDAO = new ProdutoDAOImpl();

        System.out.println("DEBUG ProdutoService criado:");
        System.out.println("  usuarioId recebido: " + usuarioId);
    }
    
public void adicionarProduto(String nome, double valor, int quantidade) {
        System.out.println("=== DEBUG ProdutoService.adicionarProduto ===");
        System.out.println("usuarioId: " + this.usuarioId);
        System.out.println("nome: " + nome);
        System.out.println("valor: " + valor);
        System.out.println("quantidade: " + quantidade);
        
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setValor(valor);
        produto.setQuantidade(quantidade);
        produto.setUsuarioId(this.usuarioId);
        
        System.out.println("DEBUG: Produto criado - usuarioId definido: " + produto.getUsuarioId());
        System.out.println("DEBUG: Chamando produtoDAO.salvar...");
        
        produtoDAO.salvar(produto);
        
        System.out.println("DEBUG: Produto salvo no DAO");
    }

    public Produto adicionarProdutoVoz(ProdutoVoz produtoVoz) {
        Produto produto = new Produto(produtoVoz.getNome(), produtoVoz.getValor(), usuarioId);
        produto.setQuantidade(produtoVoz.getQuantidade());
        produto.setCategoria("Adicionado por Voz");
        return produtoDAO.salvar(produto);
    }
    
    public List<Produto> listarProdutos() {
        return produtoDAO.buscarPorUsuario(usuarioId);
    }
    
    public boolean removerProduto(String produtoId) {
        return produtoDAO.remover(produtoId);
    }
    
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

public List<Produto> listarProdutosPorUsuario(String usuarioId) {
    System.out.println("=== DEBUG ProdutoService.listarProdutosPorUsuario ===");
    System.out.println("Buscando produtos para usuarioId: " + usuarioId);
    
    List<Produto> produtos = produtoDAO.listarPorUsuarioId(usuarioId);
    System.out.println("DEBUG: " + produtos.size() + " produtos encontrados");
    
    return produtos;
}
public void removerProdutoPorNome(String nome, String usuarioId) {
    produtoDAO.removerPorNomeEUsuario(nome, usuarioId);
}

public void limparListaUsuario(String usuarioId) {
    produtoDAO.removerTodosPorUsuario(usuarioId);
}
}