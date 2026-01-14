// PIXPaymentRequest.java
package com.mercadoscan.model;

import java.math.BigDecimal;

public class PIXPaymentRequest {
    private String usuarioId;
    private String transactionId;
    private BigDecimal amount;
    private String description;
    private String payerName;
    private String payerDocument; // CPF/CNPJ
    private String payerEmail;
    
    // Getters e Setters
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getPayerName() { return payerName; }
    public void setPayerName(String payerName) { this.payerName = payerName; }
    
    public String getPayerDocument() { return payerDocument; }
    public void setPayerDocument(String payerDocument) { this.payerDocument = payerDocument; }
    
    public String getPayerEmail() { return payerEmail; }
    public void setPayerEmail(String payerEmail) { this.payerEmail = payerEmail; }
}