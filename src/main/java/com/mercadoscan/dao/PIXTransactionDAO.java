package com.mercadoscan.dao;

import com.mercadoscan.model.PIXTransaction;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class PIXTransactionDAO {
    
    private final MongoCollection<Document> collection;
    
    public PIXTransactionDAO() {
        MongoClient mongoClient = MongoClients.create("mongodb://192.168.24.128:27017");
        MongoDatabase database = mongoClient.getDatabase("mercadoscan_db");
        this.collection = database.getCollection("pix_transactions");
        System.out.println("✅ PIXTransactionDAO inicializado");
    }
    
    public PIXTransaction save(PIXTransaction transaction) {
        try {
            Document doc = toDocument(transaction);
            collection.insertOne(doc);
            
            // Atualiza com o ID gerado
            transaction.setId(doc.getObjectId("_id").toString());
            return transaction;
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao salvar transação PIX: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    public PIXTransaction findByTransactionId(String transactionId) {
        try {
            Document doc = collection.find(Filters.eq("transactionId", transactionId)).first();
            return doc != null ? fromDocument(doc) : null;
        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar transação: " + e.getMessage());
            return null;
        }
    }
    
    public PIXTransaction findByUsuarioId(String usuarioId) {
        try {
            Document doc = collection.find(Filters.eq("usuarioId", usuarioId))
                .sort(new Document("createdAt", -1))
                .first();
            return doc != null ? fromDocument(doc) : null;
        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar transação por usuário: " + e.getMessage());
            return null;
        }
    }
    
    public boolean updateStatus(String transactionId, String status, String endToEndId) {
        try {
            Bson filter = Filters.eq("transactionId", transactionId);
            Bson update = Updates.combine(
                Updates.set("status", status),
                Updates.set("updatedAt", LocalDateTime.now())
            );
            
            if (endToEndId != null) {
                update = Updates.combine(update, Updates.set("endToEndId", endToEndId));
            }
            
            return collection.updateOne(filter, update).getModifiedCount() > 0;
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao atualizar status: " + e.getMessage());
            return false;
        }
    }
    
    public List<PIXTransaction> findPendingTransactions() {
        List<PIXTransaction> transactions = new ArrayList<>();
        try {
            Bson filter = Filters.in("status", "PENDING", "CREATED");
            for (Document doc : collection.find(filter)) {
                transactions.add(fromDocument(doc));
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar transações pendentes: " + e.getMessage());
        }
        return transactions;
    }
    
    private Document toDocument(PIXTransaction transaction) {
        return new Document()
            .append("transactionId", transaction.getTransactionId())
            .append("usuarioId", transaction.getUsuarioId())
            .append("amount", transaction.getAmount())
            .append("description", transaction.getDescription())
            .append("qrCodeBase64", transaction.getQrCodeBase64())
            .append("qrCodeText", transaction.getQrCodeText())
            .append("status", transaction.getStatus())
            .append("createdAt", LocalDateTime.now())
            .append("updatedAt", LocalDateTime.now())
            .append("expiresAt", transaction.getExpiresAt())
            .append("payerName", transaction.getPayerName())
            .append("payerDocument", transaction.getPayerDocument())
            .append("payerEmail", transaction.getPayerEmail());
    }
    
    private PIXTransaction fromDocument(Document doc) {
        PIXTransaction transaction = new PIXTransaction();
        transaction.setId(doc.getObjectId("_id").toString());
        transaction.setTransactionId(doc.getString("transactionId"));
        transaction.setUsuarioId(doc.getString("usuarioId"));
        transaction.setAmount(doc.getDouble("amount"));
        transaction.setDescription(doc.getString("description"));
        transaction.setQrCodeBase64(doc.getString("qrCodeBase64"));
        transaction.setQrCodeText(doc.getString("qrCodeText"));
        transaction.setStatus(doc.getString("status"));
        transaction.setPayerName(doc.getString("payerName"));
        transaction.setPayerDocument(doc.getString("payerDocument"));
        transaction.setPayerEmail(doc.getString("payerEmail"));
        return transaction;
    }
}