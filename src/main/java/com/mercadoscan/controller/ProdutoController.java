package com.mercadoscan.controller;

import java.util.List;

import javax.swing.JOptionPane;

import com.mercadoscan.model.Produto;
import com.mercadoscan.model.ProdutoVoz;
import com.mercadoscan.service.ProdutoService;

public class ProdutoController {
    
    private final ProdutoService produtoService;
    
    public ProdutoController(String usuarioId) {
        this.produtoService = new ProdutoService(usuarioId);
    }
    
    public void adicionarProduto(String nome, double valor, int quantidade) {
        try {
            if (nome == null || nome.trim().isEmpty()) {
                throw new IllegalArgumentException("Nome do produto é obrigatório");
            }
            
            if (valor <= 0) {
                throw new IllegalArgumentException("Valor deve ser maior que zero");
            }
            
            if (quantidade <= 0) {
                throw new IllegalArgumentException("Quantidade deve ser maior que zero");
            }
            
            produtoService.adicionarProduto(nome, valor, quantidade);
            
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null,
                e.getMessage(), "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            throw e;
        }
    }
    
    public void adicionarProdutoVoz(ProdutoVoz produtoVoz) {
        try {
            produtoService.adicionarProdutoVoz(produtoVoz);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                "Erro ao adicionar produto por voz: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public List<Produto> listarProdutos() {
        return produtoService.listarProdutos();
    }
    
    public void removerProduto(String produtoId) {
        boolean sucesso = produtoService.removerProduto(produtoId);
        
        if (!sucesso) {
            JOptionPane.showMessageDialog(null,
                "Erro ao remover produto", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void removerProdutoPorNome(String nome) {
        boolean sucesso = produtoService.removerProdutoPorNome(nome);
        
        if (!sucesso) {
            JOptionPane.showMessageDialog(null,
                "Produto não encontrado: " + nome, 
                "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    public void limparLista() {
        int confirm = JOptionPane.showConfirmDialog(null,
            "Tem certeza que deseja limpar toda a lista de compras?",
            "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            produtoService.limparLista();
            JOptionPane.showMessageDialog(null,
                "Lista de compras limpa com sucesso!",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public double calcularTotal() {
        return produtoService.calcularTotal();
    }
    
    public int contarProdutos() {
        return produtoService.contarProdutos();
    }
    
    public void finalizarCompra() {
        double total = calcularTotal();
        int quantidade = contarProdutos();
        
        if (quantidade == 0) {
            JOptionPane.showMessageDialog(null,
                "Lista de compras vazia!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
    JOptionPane.showMessageDialog(null,
    """
    ✅ Compra finalizada com sucesso!
    
    Itens: %d
    Total: R$ %.2f
    
    Obrigado pela compra!""".formatted(quantidade, total),
    "Compra Finalizada", JOptionPane.INFORMATION_MESSAGE);

// Limpar lista após compra
produtoService.limparLista();
    }
}