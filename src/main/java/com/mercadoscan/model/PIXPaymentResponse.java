// PIXPaymentResponse.java
package com.mercadoscan.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PIXPaymentResponse {
    private boolean success;
    private String message;
    private String transactionId;
    private String qrCodeBase64;
    private String qrCodeText;
    private String paymentUrl;
    private LocalDateTime expiration;
    private String status;
    private BigDecimal amount;
    
    // Getters e Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public String getQrCodeBase64() { return qrCodeBase64; }
    public void setQrCodeBase64(String qrCodeBase64) { this.qrCodeBase64 = qrCodeBase64; }
    
    public String getQrCodeText() { return qrCodeText; }
    public void setQrCodeText(String qrCodeText) { this.qrCodeText = qrCodeText; }
    
    public String getPaymentUrl() { return paymentUrl; }
    public void setPaymentUrl(String paymentUrl) { this.paymentUrl = paymentUrl; }
    
    public LocalDateTime getExpiration() { return expiration; }
    public void setExpiration(LocalDateTime expiration) { this.expiration = expiration; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}