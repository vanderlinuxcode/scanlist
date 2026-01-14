// PIXTransaction.java
package com.mercadoscan.model;

import java.time.LocalDateTime;

public class PIXTransaction {
    private String id;
    private String transactionId;
    private String usuarioId;
    private double amount;
    private String description;
    private String qrCodeBase64;
    private String qrCodeText;
    private String status; // CREATED, PENDING, CONFIRMED, EXPIRED, FAILED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expiresAt;
    private String endToEndId;
    private String payerName;
    private String payerDocument;
    private String payerEmail;
    
    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getQrCodeBase64() { return qrCodeBase64; }
    public void setQrCodeBase64(String qrCodeBase64) { this.qrCodeBase64 = qrCodeBase64; }
    
    public String getQrCodeText() { return qrCodeText; }
    public void setQrCodeText(String qrCodeText) { this.qrCodeText = qrCodeText; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public String getEndToEndId() { return endToEndId; }
    public void setEndToEndId(String endToEndId) { this.endToEndId = endToEndId; }
    
    public String getPayerName() { return payerName; }
    public void setPayerName(String payerName) { this.payerName = payerName; }
    
    public String getPayerDocument() { return payerDocument; }
    public void setPayerDocument(String payerDocument) { this.payerDocument = payerDocument; }
    
    public String getPayerEmail() { return payerEmail; }
    public void setPayerEmail(String payerEmail) { this.payerEmail = payerEmail; }
}