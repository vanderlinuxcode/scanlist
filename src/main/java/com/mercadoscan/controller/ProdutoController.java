package com.mercadoscan.controller;

import java.util.List;

import javax.swing.JOptionPane;

import com.mercadoscan.model.Produto;
import com.mercadoscan.model.ProdutoVoz;
import com.mercadoscan.service.ProdutoService;

public class ProdutoController {
    
    private final ProdutoService produtoService;
    private final String usuarioId;
    
    public ProdutoController(String usuarioId) {
        this.usuarioId = usuarioId;
        this.produtoService = new ProdutoService(usuarioId);

        System.out.println("DEBUG ProdutoController criado:");
        System.out.println("  usuarioId recebido: " + usuarioId);
    }
    
public void adicionarProduto(String nome, double valor, int quantidade) {
        try {
            System.out.println("=== DEBUG ProdutoController.adicionarProduto ===");
            System.out.println("usuarioId: " + this.usuarioId);
            System.out.println("nome: " + nome);
            System.out.println("valor: " + valor);
            System.out.println("quantidade: " + quantidade);
            
            if (nome == null || nome.trim().isEmpty()) {
                throw new IllegalArgumentException("Nome do produto é obrigatório");
            }
            
            if (valor <= 0) {
                throw new IllegalArgumentException("Valor deve ser maior que zero");
            }
            
            if (quantidade <= 0) {
                throw new IllegalArgumentException("Quantidade deve ser maior que zero");
            }
            
            System.out.println("DEBUG: Chamando produtoService.adicionarProduto...");
            produtoService.adicionarProduto(nome, valor, quantidade);
            System.out.println("DEBUG: produtoService.adicionarProduto concluído");
            
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Erro de validação: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                e.getMessage(), "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            throw e;
        } catch (Exception e) {
            System.err.println("❌ Erro inesperado no controller: " + e.getMessage());
            throw new RuntimeException("Erro ao adicionar produto", e);
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
    System.out.println("DEBUG ProdutoController.listarProdutos()");
    System.out.println("UsuarioId para listagem: " + this.usuarioId);
    
    List<Produto> produtos = produtoService.listarProdutosPorUsuario(this.usuarioId);
    System.out.println("DEBUG: " + produtos.size() + " produtos encontrados");
    
    return produtos;
}
    
    public void removerProduto(String produtoId) {
       boolean sucesso = produtoService.removerProduto(produtoId);
        
       if (!sucesso) {
            JOptionPane.showMessageDialog(null,
               "Erro ao remover produto", "Erro", JOptionPane.ERROR_MESSAGE);
       }
   }
    
    public void removerProdutoPorNome(String produtoNome) {
    System.out.println("DEBUG: Removendo produto: " + produtoNome);
    produtoService.removerProdutoPorNome(produtoNome);
}
    

    
public double calcularTotal() {
    List<Produto> produtos = listarProdutos();
    double total = 0.0;
    
    for (Produto produto : produtos) {
        total += produto.getSubtotal();
    }
    
    System.out.println("DEBUG: Total calculado: R$ " + total);
    return total;
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

   // >>>>>>> este apaga toda a compra
//produtoService.limparLista();
 //Limpar lista após compra
    }

   // public String getUsuarioId() {
 //      return usuarioId;
  //  }
}
