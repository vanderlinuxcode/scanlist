package com.mercadoscan.dao;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mercadoscan.model.Produto;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;

public class ProdutoDAOImpl implements ProdutoDAO {
    
    private final MongoCollection<Document> collection;
    
    public ProdutoDAOImpl() {
        MongoClient mongoClient = MongoClients.create("mongodb://192.168.24.128:27017");
        MongoDatabase database = mongoClient.getDatabase("mercadoscan_db");
        this.collection = database.getCollection("produtos");
        System.out.println("✅ ProdutoDAOImpl inicializado");
    }
    
    @Override
    public Produto salvar(Produto produto) {
        System.out.println("\n=== DEBUG SALVAR PRODUTO ===");
        System.out.println("📦 Produto recebido:");
        System.out.println("   Nome: " + produto.getNome());
        System.out.println("   Valor: " + produto.getValor());
        System.out.println("   Quantidade: " + produto.getQuantidade());
        System.out.println("   UsuarioId: " + produto.getUsuarioId());
        
        try {
            Document doc = toDocument(produto);
            System.out.println("📄 Documento para inserir: " + doc.toJson());
            
            // INSERÇÃO SIMPLES - NÃO chama fromDocument depois!
            @SuppressWarnings("unused")
            InsertOneResult result = collection.insertOne(doc);
            System.out.println("✅ Inserção realizada");
            
            // Pega o ID do documento inserido
            ObjectId insertedId = doc.getObjectId("_id");
            if (insertedId != null) {
                produto.setId(insertedId.toString());
                System.out.println("✅ ID gerado: " + produto.getId());
            } else {
                System.err.println("⚠️ ID não foi gerado automaticamente");
            }
            
            return produto; // Retorna o mesmo produto com ID atualizado
            
        } catch (Exception e) {
            System.err.println("❌ ERRO ao salvar produto: " + e.getMessage());
            throw new RuntimeException("Erro ao salvar produto no banco", e);
        }
    }
    
    @Override
    public List<Produto> buscarPorUsuario(String usuarioId) {
        System.out.println("\n=== DEBUG buscarPorUsuario ===");
        System.out.println("UsuarioId: " + usuarioId);
        
        List<Produto> produtos = new ArrayList<>();
        
        try (MongoCursor<Document> cursor = collection.find(
            Filters.eq("usuarioId", usuarioId)).iterator()) {
            
            int count = 0;
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                count++;
                System.out.println("📄 Documento " + count + ": " + doc.toJson());
                
                Produto produto = fromDocument(doc);
                produtos.add(produto);
            }
            
            System.out.println("✅ Total encontrado: " + count + " produtos");
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar produtos: " + e.getMessage());
        }
        
        return produtos;
    }
    
    @Override
    public List<Produto> listarPorUsuarioId(String usuarioId) {
        // Reutiliza o método buscarPorUsuario
        System.out.println("DEBUG: listarPorUsuarioId chamado para: " + usuarioId);
        return buscarPorUsuario(usuarioId);
    }
    
    @Override
    public boolean remover(String produtoId) {
        try {
            System.out.println("DEBUG: Removendo produto ID: " + produtoId);
            DeleteResult result = collection.deleteOne(Filters.eq("_id", new ObjectId(produtoId)));
            boolean removido = result.getDeletedCount() > 0;
            System.out.println("✅ Produto removido: " + removido);
            return removido;
        } catch (Exception e) {
            System.err.println("❌ Erro ao remover produto: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean removerTodosDoUsuario(String usuarioId) {
        try {
            System.out.println("DEBUG: Removendo todos produtos do usuário: " + usuarioId);
            DeleteResult result = collection.deleteMany(Filters.eq("usuarioId", usuarioId));
            System.out.println("✅ " + result.getDeletedCount() + " produtos removidos");
            return result.getDeletedCount() > 0;
        } catch (Exception e) {
            System.err.println("❌ Erro ao remover produtos: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public void removerPorNomeEUsuario(String nome, String usuarioId) {
        try {
            System.out.println("DEBUG: Removendo produto '" + nome + "' do usuário: " + usuarioId);
            DeleteResult result = collection.deleteOne(
                Filters.and(
                    Filters.eq("nome", nome),
                    Filters.eq("usuarioId", usuarioId)
                )
            );
            System.out.println("✅ Produto removido: " + result.getDeletedCount() + " deletado(s)");
        } catch (Exception e) {
            System.err.println("❌ Erro ao remover produto por nome: " + e.getMessage());
        }
    }
    
    @Override
    public void removerTodosPorUsuario(String usuarioId) {
        removerTodosDoUsuario(usuarioId); // Reutiliza implementação
    }
    
    @Override
    public List<Produto> buscarPorNome(String nome, String usuarioId) {
        System.out.println("\n=== DEBUG buscarPorNome ===");
        System.out.println("Nome: " + nome + ", UsuarioId: " + usuarioId);
        
        List<Produto> produtos = new ArrayList<>();
        
        try (MongoCursor<Document> cursor = collection.find(
            Filters.and(
                Filters.eq("usuarioId", usuarioId),
                Filters.regex("nome", ".*" + nome + ".*", "i")
            )).iterator()) {
            
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                produtos.add(fromDocument(doc));
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar por nome: " + e.getMessage());
        }
        
        System.out.println("✅ Produtos encontrados: " + produtos.size());
        return produtos;
    }
    
    @Override
    public double calcularTotalUsuario(String usuarioId) {
        System.out.println("\n=== DEBUG calcularTotalUsuario ===");
        
        double total = 0.0;
        List<Produto> produtos = buscarPorUsuario(usuarioId);
        
        for (Produto produto : produtos) {
            double subtotal = produto.getValor() * produto.getQuantidade();
            total += subtotal;
            System.out.println("   " + produto.getNome() + ": R$ " + subtotal);
        }
        
        System.out.println("✅ Total: R$ " + total);
        return total;
    }
    
    // ==================== MÉTODOS PRIVADOS AUXILIARES ====================
    
    private Document toDocument(Produto produto) {
        System.out.println("DEBUG: Convertendo Produto para Document");
        
        Document doc = new Document()
            .append("nome", produto.getNome())
            .append("valor", produto.getValor())
            .append("quantidade", produto.getQuantidade())
            .append("usuarioId", produto.getUsuarioId())
            .append("dataCriacao", new Date());
        
        // Se o produto já tem ID (atualização), inclui
        if (produto.getId() != null && !produto.getId().isEmpty()) {
            try {
                doc.append("_id", new ObjectId(produto.getId()));
            } catch (Exception e) {
                System.err.println("⚠️ ID inválido: " + produto.getId());
            }
        }
        
        return doc;
    }
    
    // ⚠️ MÉTODO PRIVADO - não faz parte da interface!
    private Produto fromDocument(Document doc) {
        System.out.println("DEBUG: Convertendo Document para Produto");
        
        Produto produto = new Produto();
        
        try {
            // ID
            ObjectId objectId = doc.getObjectId("_id");
            if (objectId != null) {
                produto.setId(objectId.toString());
            }
            
            // Campos obrigatórios
            produto.setNome(doc.getString("nome"));
            produto.setUsuarioId(doc.getString("usuarioId"));
            
            // Valor (pode vir em diferentes formatos)
            Object valorObj = doc.get("valor");
            if (valorObj != null) {
                switch (valorObj) {
                    case Double aDouble -> produto.setValor(aDouble);
                    case Integer integer -> produto.setValor(integer.doubleValue());
                    case String string -> {
                        try {
                            produto.setValor(Double.parseDouble(string));
                        } catch (NumberFormatException e) {
                            produto.setValor(0.0);
                        }
                    }
                    default -> {
                    }
                }
            } else {
                produto.setValor(0.0);
            }
            
            // Quantidade
            Object qtdObj = doc.get("quantidade");
            if (qtdObj != null) {
                if (qtdObj instanceof Integer integer) {
                    produto.setQuantidade(integer);
                } else if (!(qtdObj instanceof Double aDouble)) {
                    if (qtdObj instanceof String string) {
                        try {
                            produto.setQuantidade(Integer.parseInt(string));
                        } catch (NumberFormatException e) {
                            produto.setQuantidade(1);
                        }
                    }
                } else {
                    produto.setQuantidade(aDouble.intValue());
                }
            } else {
                produto.setQuantidade(1);
            }
            
            System.out.println("✅ Produto convertido: " + produto.getNome());
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao converter documento: " + e.getMessage());
            System.err.println("Documento: " + doc.toJson());
        }
        
        return produto;
    }
    
    @SuppressWarnings("unused")
    private LocalDateTime toLocalDateTime(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}