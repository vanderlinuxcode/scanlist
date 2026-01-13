// 1. Crie uma classe para gerenciar a conexão (Singleton)
package com.mercadoscan.dao;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    
    private MongoDBConnection() {} // Construtor privado
    
    public static synchronized MongoDatabase getDatabase() {
        if (database == null) {
            try {
                // Use uma URL de configuração
                String connectionString = "mongodb://192.168.24.128:27017";
                mongoClient = MongoClients.create(connectionString);
                database = mongoClient.getDatabase("mercadoscan_db");
                System.out.println("✅ Conexão MongoDB estabelecida");
            } catch (Exception e) {
                System.err.println("❌ Erro ao conectar ao MongoDB: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return database;
    }
    
    public static synchronized void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null;
            System.out.println("🔌 Conexão MongoDB fechada");
        }
    }
}