package com.mercadoscan.controller;

import com.mercadoscan.service.PaymentService;
import com.mercadoscan.service.PaymentService.PaymentResult;

public class PagamentoController {
    
    public boolean processarPagamento(String usuarioId, double valorPago, String formaPagamento) {
        System.out.println("\n=== CONTROLLER: PROCESSAR PAGAMENTO ===");
        System.out.println("Usuário: " + usuarioId);
        System.out.println("Valor: R$ " + valorPago);
        System.out.println("Forma: " + formaPagamento);
        
        try {
            PaymentService paymentService = new PaymentService(usuarioId);
            PaymentResult sucesso = paymentService.processarPagamento(valorPago, valorPago, formaPagamento, null);
            
            if (sucesso != null) {
                System.out.println("✅ Pagamento processado com sucesso!");
                System.out.println("⚠️ Dados NÃO foram apagados - mantidos para histórico");
            }
            
            return sucesso != null;
            
        } catch (Exception e) {
            System.err.println("❌ Erro no controller de pagamento: " + e.getMessage());
            return false;
        }
    }
}