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
    }
    
    public Produto adicionarProduto(String nome, double valor, int quantidade) {
        Produto produto = new Produto(nome, valor, usuarioId);
        produto.setQuantidade(quantidade);
        return produtoDAO.salvar(produto);
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
}