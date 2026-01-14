// PIXWebhookRequest.java
package com.mercadoscan.model                                               ;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PIXWebhookRequest {
    private String event; // "payment.confirmed", "payment.failed"
    private String transactionId;
    private String endToEndId;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private String payerName;
    private String status;
    
    // Getters e Setters
    public String getEvent() { return event; }
    public void setEvent(String event) { this.event = event; }
    
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    
    public String getEndToEndId() { return endToEndId; }
    public void setEndToEndId(String endToEndId) { this.endToEndId = endToEndId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public String getPayerName() { return payerName; }
    public void setPayerName(String payerName) { this.payerName = payerName; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}