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
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class ProdutoDAOImpl implements ProdutoDAO {
    
    private final MongoCollection<Document> collection;
    
    public ProdutoDAOImpl() {
        MongoClient mongoClient = MongoClients.create("mongodb://192.168.24.128:27017");
        MongoDatabase database = mongoClient.getDatabase("mercadoscan_db");
        this.collection = database.getCollection("produtos");
    }
    
    @Override
    public Produto salvar(Produto produto) {
        Document doc = toDocument(produto);
        collection.insertOne(doc);
        produto.setId(doc.getObjectId("_id").toString());
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
    
    // Métodos auxiliares
    private Document toDocument(Produto produto) {
        return new Document()
                .append("nome", produto.getNome())
                .append("valor", produto.getValor())
                .append("quantidade", produto.getQuantidade())
                .append("categoria", produto.getCategoria())
                .append("dataAdicao", toDate(produto.getDataAdicao()))
                .append("usuarioId", produto.getUsuarioId());
    }
    
    private Produto fromDocument(Document doc) {
        if (doc == null) return null;
        
        Produto produto = new Produto();
        produto.setId(doc.getObjectId("_id").toString());
        produto.setNome(doc.getString("nome"));
        produto.setValor(doc.getDouble("valor"));
        produto.setQuantidade(doc.getInteger("quantidade", 1));
        produto.setCategoria(doc.getString("categoria"));
        produto.setUsuarioId(doc.getString("usuarioId"));
        
        Date dataAdicao = doc.getDate("dataAdicao");
        if (dataAdicao != null) {
            produto.setDataAdicao(toLocalDateTime(dataAdicao));
        }
        
        return produto;
    }
    
    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    
    private LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}