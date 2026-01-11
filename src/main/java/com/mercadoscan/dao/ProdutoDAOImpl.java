package com.mercadoscan.dao;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.BsonValue;
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
    }
    
 @Override
public Produto salvar(Produto produto) {
    System.out.println("=== DEBUG ProdutoDAOImpl.salvar INICIO ===");
    
    try {
        Document doc = toDocument(produto);
        System.out.println("DEBUG: Document para inserir: " + doc.toJson());
        
        // VERIFIQUE A COLLECTION
        System.out.println("DEBUG: Collection name: " + collection.getNamespace());
        
        // Tente inserir e capture o resultado
        InsertOneResult result = collection.insertOne(doc);
        System.out.println("DEBUG: Resultado da inserção: " + result);
        
        if (result != null) {
            BsonValue insertedId = result.getInsertedId();
            
            if (insertedId != null) {
                System.out.println("DEBUG: InsertedId tipo: " + insertedId.getBsonType());
                
                // Verificar se é ObjectId
                if (insertedId.isObjectId()) {
                    // Usar getter seguro
                    org.bson.BsonObjectId bsonObjectId = insertedId.asObjectId();
                    if (bsonObjectId != null) {
                        ObjectId objectId = bsonObjectId.getValue();
                        if (objectId != null) {
                            produto.setId(objectId.toString());
                            System.out.println("✅ Produto salvo com sucesso! ID: " + produto.getId());
                        } else {
                            System.err.println("❌ ObjectId é null!");
                        }
                    } else {
                        System.err.println("❌ BsonObjectId é null!");
                    }
                } else {
                    // Se não for ObjectId, usar alternativa
                    System.out.println("⚠️ InsertedId não é ObjectId, usando valor como string");
                    produto.setId(insertedId.toString());
                }
            } else {
                System.err.println("❌ ERRO: InsertedId é null!");
                // Tentar obter ID do documento
                ObjectId objectId = doc.getObjectId("_id");
                if (objectId != null) {
                    produto.setId(objectId.toString());
                    System.out.println("✅ ID obtido do documento: " + produto.getId());
                }
            }
        } else {
            System.err.println("❌ ERRO: Result é null!");
        }
        
        // VERIFICAÇÃO APÓS INSERÇÃO
        if (produto.getId() != null) {
            try {
                Document filtro = new Document("_id", new ObjectId(produto.getId()));
                Document encontrado = collection.find(filtro).first();
                if (encontrado != null) {
                    System.out.println("✅ Confirmação: Produto encontrado após inserção!");
                } else {
                    System.err.println("❌ ALERTA: Produto NÃO encontrado após inserção!");
                }
            } catch (Exception e) {
                System.err.println("❌ Erro na verificação: " + e.getMessage());
            }
        }
        
   } catch (Exception e) {
        System.err.println("❌ EXCEÇÃO NO TESTE:");
        System.err.println("Mensagem: " + e.getMessage());
        System.err.println("\nStack Trace:");
    }
    
    System.out.println("=== DEBUG ProdutoDAOImpl.salvar FIM ===");
    return produto;
}
    
    @Override
    public List<Produto> buscarPorUsuario(String usuarioId) {
        List<Produto> produtos = new ArrayList<>();
        
        for (Document doc : collection.find(Filters.eq("usuarioId", usuarioId))) {
            produtos.add(fromDocument(doc));
        }
        
        return produtos;
    }
    
    @Override
    public boolean remover(String produtoId) {
        try {
            collection.deleteOne(Filters.eq("_id", new ObjectId(produtoId)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public boolean removerTodosDoUsuario(String usuarioId) {
        try {
            collection.deleteMany(Filters.eq("usuarioId", usuarioId));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public List<Produto> buscarPorNome(String nome, String usuarioId) {
        List<Produto> produtos = new ArrayList<>();
        
        for (Document doc : collection.find(
            Filters.and(
                Filters.eq("usuarioId", usuarioId),
                Filters.regex("nome", ".*" + nome + ".*", "i")
            )
        )) {
            produtos.add(fromDocument(doc));
        }
        
        return produtos;
    }
    
    @Override
    public double calcularTotalUsuario(String usuarioId) {
        double total = 0.0;
        List<Produto> produtos = buscarPorUsuario(usuarioId);
        
        for (Produto produto : produtos) {
            total += produto.getSubtotal();
        }
        
        return total;
    }
    
private Document toDocument(Produto produto) {
    System.out.println("DEBUG: Criando Document para MongoDB");
    
    Document doc = new Document()
        .append("nome", produto.getNome())
        .append("valor", produto.getValor())
        .append("quantidade", produto.getQuantidade())
        .append("usuarioId", produto.getUsuarioId())
        .append("dataCriacao", new Date());
    
    System.out.println("DEBUG: Document final: " + doc.toJson());
    return doc;
}
    
   private Produto fromDocument(Document doc) {
    Produto produto = new Produto();
    
    // Extrair ObjectId e converter para String
    ObjectId objectId = doc.getObjectId("_id");
    if (objectId != null) {
        produto.setId(objectId.toString());
    }
    
    produto.setNome(doc.getString("nome"));
    produto.setValor(doc.getDouble("valor"));
    produto.setQuantidade(doc.getInteger("quantidade"));
    produto.setUsuarioId(doc.getString("usuarioId"));
    
    // Verificar se tem dataCriacao
    if (doc.containsKey("dataCriacao")) {
        @SuppressWarnings("unused")
        Date dataCriacao = doc.getDate("dataCriacao");
        // Se quiser pode setar em um campo dataCriacao no Produto
    }
    
    System.out.println("DEBUG: Produto convertido: " + produto.getNome() + 
                    " (ID: " + produto.getId() + ")");
    
    return produto;
}
    
    @SuppressWarnings("unused")
    private LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    @Override
public List<Produto> listarPorUsuarioId(String usuarioId) {
    System.out.println("=== DEBUG ProdutoDAOImpl.listarPorUsuarioId ===");
    System.out.println("Buscando no MongoDB para usuarioId: " + usuarioId);
    
    List<Produto> produtos = new ArrayList<>();
    
    // Criar filtro para buscar produtos deste usuário
    Document filtro = new Document("usuarioId", usuarioId);
    System.out.println("DEBUG: Filtro MongoDB: " + filtro.toJson());
    
    try (MongoCursor<Document> cursor = collection.find(filtro).iterator()) {
        int count = 0;
        while (cursor.hasNext()) {
            Document doc = cursor.next();
            System.out.println("DEBUG: Document encontrado " + (++count) + ": " + doc.toJson());
            
            Produto produto = fromDocument(doc);
            produtos.add(produto);
        }
        System.out.println("DEBUG: Total de documentos encontrados: " + count);
    } catch (Exception e) {
        System.err.println("❌ ERRO ao listar produtos do MongoDB: " + e.getMessage());
    }
    
    System.out.println("DEBUG: Retornando " + produtos.size() + " produtos");
    return produtos;
}
@Override
public void removerPorNomeEUsuario(String nome, String usuarioId) {
    Document filtro = new Document()
        .append("nome", nome)
        .append("usuarioId", usuarioId);
    
    collection.deleteOne(filtro);
    System.out.println("DEBUG: Produto removido: " + nome);
}

@Override
public void removerTodosPorUsuario(String usuarioId) {
    Document filtro = new Document("usuarioId", usuarioId);
    
    DeleteResult result = collection.deleteMany(filtro);
    System.out.println("DEBUG: " + result.getDeletedCount() + " produtos removidos do usuário: " + usuarioId);
}

}