// PIXPaymentDAO.java - Novo DAO específico para pagamentos
package com.mercadoscan.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class PIXPaymentDAO {
    
    private final MongoCollection<Document> collection;
    
    public PIXPaymentDAO() {
        try {
            MongoClient mongoClient = MongoClients.create("mongodb://192.168.24.128:27017");
            MongoDatabase database = mongoClient.getDatabase("mercadoscan_db");
            this.collection = database.getCollection("pagamentos");
            System.out.println("✅ PIXPaymentDAO inicializado com sucesso");
            
            // Verifica se a collection existe
            boolean collectionExists = database.listCollectionNames()
                .into(new ArrayList<>())
                .contains("pagamentos");
            
            if (!collectionExists) {
                database.createCollection("pagamentos");
                System.out.println("📁 Collection 'pagamentos' criada");
            }
            
        } catch (Exception e) {
            System.err.println("❌ ERRO ao inicializar PIXPaymentDAO: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    public String savePayment(String usuarioId, double totalCompra, double valorPago, 
                              String metodoPagamento, List<Document> itens) {
        System.out.println("\n=== SALVANDO PAGAMENTO NO HISTÓRICO ===");
        
        try {
            // Criar documento do pagamento
            Document pagamentoDoc = new Document()
                .append("usuarioId", usuarioId)
                .append("totalCompra", totalCompra)
                .append("valorPago", valorPago)
                .append("troco", valorPago - totalCompra)
                .append("metodoPagamento", metodoPagamento)
                .append("data", new Date())
                .append("status", "CONCLUIDO")
                .append("quantidadeItens", itens != null ? itens.size() : 0);
            
            // Adicionar itens se existirem
            if (itens != null && !itens.isEmpty()) {
                pagamentoDoc.append("itens", itens);
            }
            
            System.out.println("📄 Documento de pagamento:");
            System.out.println(pagamentoDoc.toJson());
            
            // Inserir no MongoDB
            collection.insertOne(pagamentoDoc);
            
            String insertedId = pagamentoDoc.getObjectId("_id").toString();
            System.out.println("✅ Pagamento salvo com sucesso! ID: " + insertedId);
            
            // Verificar se realmente foi salvo
            Document verificado = collection.find(Filters.eq("_id", new ObjectId(insertedId))).first();
            if (verificado != null) {
                System.out.println("✅ Confirmação: Pagamento encontrado no banco!");
                return insertedId;
            } else {
                System.err.println("❌ ALERTA: Pagamento não encontrado após inserção!");
                return null;
            }
            
        } catch (Exception e) {
            System.err.println("❌ ERRO ao salvar pagamento: " + e.getMessage());
            return null;
        }
    }
    
    public List<Document> getPaymentsByUser(String usuarioId) {
        List<Document> pagamentos = new ArrayList<>();
        
        try {
            for (Document doc : collection.find(Filters.eq("usuarioId", usuarioId))) {
                pagamentos.add(doc);
            }
            System.out.println("📊 Pagamentos encontrados para " + usuarioId + ": " + pagamentos.size());
        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar pagamentos: " + e.getMessage());
        }
        
        return pagamentos;
    }
    
    public Document getPaymentById(String paymentId) {
        try {
            return collection.find(Filters.eq("_id", new ObjectId(paymentId))).first();
        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar pagamento por ID: " + e.getMessage());
            return null;
        }
    }
    
    public boolean deletePayment(String paymentId) {
        try {
            return collection.deleteOne(Filters.eq("_id", new ObjectId(paymentId))).getDeletedCount() > 0;
        } catch (Exception e) {
            System.err.println("❌ Erro ao deletar pagamento: " + e.getMessage());
            return false;
        }
    }
}