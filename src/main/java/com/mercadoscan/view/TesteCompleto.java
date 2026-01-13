package com.mercadoscan.view;


import java.util.List;

import com.mercadoscan.model.Produto;
import com.mercadoscan.service.ProdutoService;


public class TesteCompleto {
    public static void main(String[] args) {
        try {
            System.out.println("🧪 TESTE COMPLETO PRODUTO DAO 🧪\n");
            
            String usuarioId = "6962264381e11d5cfec7fc0a";
            ProdutoService service = new ProdutoService(usuarioId);
            
            // 1. Teste adicionar produto
            System.out.println("\n1. TESTE ADICIONAR PRODUTO:");
            Produto produto = service.adicionarProduto("PRODUTO_Manivela", 42.50, 10);
            System.out.println("✅ Produto salvo: " + produto.getNome() + 
                            " ID: " + produto.getId());
            
            // 2. Teste listar produtos
            System.out.println("\n2. TESTE LISTAR PRODUTOS:");
            List<Produto> produtos = service.listarProdutosPorUsuario(usuarioId);
            System.out.println("✅ Total de produtos: " + produtos.size());
            for (Produto p : produtos) {
                System.out.println("   - " + p.getNome() + " (R$ " + p.getValor() + ")");
            }
            
            // 3. Teste calcular total
            System.out.println("\n3. TESTE CALCULAR TOTAL:");
            double total = service.calcularTotal();
            System.out.println("✅ Total: R$ " + total);
            
            System.out.println("\n🎉 TODOS OS TESTES CONCLUÍDOS!");
            
        } catch (Exception e) {
            System.err.println("\n❌ ERRO NO TESTE: " + e.getMessage());
        }
    }
}