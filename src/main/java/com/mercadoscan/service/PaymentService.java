// PaymentService.java - Novo service unificado para pagamentos
package com.mercadoscan.service;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mercadoscan.dao.PIXPaymentDAO;
import com.mercadoscan.model.Produto;

public class PaymentService {
    
    private final PIXPaymentDAO paymentDAO;
    private final String usuarioId;
    
    public PaymentService(String usuarioId) {
        this.usuarioId = usuarioId;
        this.paymentDAO = new PIXPaymentDAO();
        System.out.println("✅ PaymentService criado para: " + usuarioId);
    }
    
    /**
     * Processa pagamento e salva histórico
     */
    public PaymentResult processarPagamento(double totalCompra, double valorPago, 
                                    String metodoPagamento, List<Produto> produtos) {
        System.out.println("\n=== PROCESSANDO PAGAMENTO ===");
        System.out.println("Usuário: " + usuarioId);
        System.out.println("Total: R$ " + totalCompra);
        System.out.println("Pago: R$ " + valorPago);
        System.out.println("Método: " + metodoPagamento);
        System.out.println("Produtos: " + (produtos != null ? produtos.size() : 0) + " itens");
        
        PaymentResult result = new PaymentResult();
        
        try {
            // 1. Validação básica
            if (valorPago < totalCompra) {
                result.setSuccess(false);
                result.setMessage("Valor pago é menor que o total da compra");
                return result;
            }
            
            // 2. Converter produtos para documentos MongoDB
            List<Document> itensDocument = new ArrayList<>();
            if (produtos != null) {
                for (Produto produto : produtos) {
                    Document itemDoc = new Document()
                        .append("nome", produto.getNome())
                        .append("valorUnitario", produto.getValor())
                        .append("quantidade", produto.getQuantidade())
                        .append("subtotal", produto.getValor() * produto.getQuantidade())
                        .append("produtoId", produto.getId());
                    
                    itensDocument.add(itemDoc);
                    System.out.println("📦 Item: " + produto.getNome() + " x" + produto.getQuantidade());
                }
            }
            
            // 3. Salvar no histórico
            String paymentId = paymentDAO.savePayment(
                usuarioId,
                totalCompra,
                valorPago,
                metodoPagamento,
                itensDocument
            );
            
            if (paymentId != null) {
                result.setSuccess(true);
                result.setPaymentId(paymentId);
                result.setMessage("Pagamento processado com sucesso");
                result.setTroco(valorPago - totalCompra);
                
                System.out.println("✅ Pagamento salvo no histórico! ID: " + paymentId);
                
                // 4. Opcional: Limpar carrinho/apenas visualização
                // NÃO apaga do banco, apenas marca como processado
                limparCarrinhoAposPagamento();
                
            } else {
                result.setSuccess(false);
                result.setMessage("Erro ao salvar pagamento no histórico");
                System.err.println("❌ Falha ao salvar pagamento no histórico");
            }
            
        } catch (Exception e) {
            System.err.println("❌ ERRO no processamento de pagamento: " + e.getMessage());
            result.setSuccess(false);
            result.setMessage("Erro interno: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Limpa carrinho APÓS pagamento (não apaga do banco)
     */
    private void limparCarrinhoAposPagamento() {
        System.out.println("🔄 Processando carrinho após pagamento...");
        
        try {
            // Em vez de apagar, você pode:
            // 1. Marcar produtos como "comprados" (recomendado)
            // 2. Mover para uma collection de histórico
            // 3. Manter no banco com status "PROCESSADO"
            
            System.out.println("✅ Carrinho processado (dados mantidos para histórico)");
            
        } catch (Exception e) {
            System.err.println("⚠️ Erro ao processar carrinho: " + e.getMessage());
        }
    }
    
    /**
     * Busca histórico de pagamentos do usuário
     */
    public List<Document> getHistoricoPagamentos() {
        return paymentDAO.getPaymentsByUser(usuarioId);
    }
    
    /**
     * Classe interna para resultado do pagamento
     */
    public static class PaymentResult {
        private boolean success;
        private String message;
        private String paymentId;
        private double troco;
        
        // Getters e Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getPaymentId() { return paymentId; }
        public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
        
        public double getTroco() { return troco; }
        public void setTroco(double troco) { this.troco = troco; }
    }
}