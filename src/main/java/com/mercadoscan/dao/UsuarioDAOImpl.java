package com.mercadoscan.dao;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mercadoscan.model.Usuario;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

/**
 * Implementação MongoDB do UsuarioDAO
 */
public class UsuarioDAOImpl implements UsuarioDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(UsuarioDAOImpl.class);
    private static final String COLLECTION_NAME = "usuarios";
    
    private final MongoCollection<Document> collection;
    
    public UsuarioDAOImpl() {
        try {
            // Conexão com MongoDB
            MongoClient mongoClient = MongoClients.create("mongodb://192.168.24.128:27017");
            MongoDatabase database = mongoClient.getDatabase("mercadoscan_db");
            this.collection = database.getCollection(COLLECTION_NAME);
            
            logger.info("✅ Conectado ao MongoDB: mercadoscan_db");
            
        } catch (Exception e) {
            logger.error("❌ Erro ao conectar ao MongoDB", e);
            throw new RuntimeException("Falha na conexão com o banco de dados", e);
        }
        logger.info("✅ Conectado ao MongoDB: mercadoscan_db");
    }
    
    @Override
    public Usuario salvar(Usuario usuario) {
        try {
            Document doc = toDocument(usuario);
            collection.insertOne(doc);
            
            usuario.setId(doc.getObjectId("_id").toString());
            logger.info("✅ Usuário salvo: {}", usuario.getCpf());
            
            return usuario;
            
        } catch (Exception e) {
            logger.error("❌ Erro ao salvar usuário", e);
            throw new RuntimeException("Erro ao salvar usuário", e);
        }
    }
    
    @Override
    public Optional<Usuario> buscarPorId(String id) {
        try {
            Document doc = collection.find(Filters.eq("_id", new ObjectId(id))).first();
            return Optional.ofNullable(fromDocument(doc));
            
        } catch (Exception e) {
            logger.error("❌ Erro ao buscar usuário por ID: {}", id, e);
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<Usuario> buscarPorCpf(String cpf) {
        try {
            Document doc = collection.find(Filters.eq("cpf", cpf)).first();
            return Optional.ofNullable(fromDocument(doc));
            
        } catch (Exception e) {
            logger.error("❌ Erro ao buscar usuário por CPF: {}", cpf, e);
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<Usuario> buscarPorTelefone(String telefone) {
        try {
            Document doc = collection.find(Filters.eq("telefone", telefone)).first();
            return Optional.ofNullable(fromDocument(doc));
            
        } catch (Exception e) {
            logger.error("❌ Erro ao buscar usuário por telefone: {}", telefone, e);
            return Optional.empty();
        }
    }
    
    @Override
    public boolean atualizarToken(String telefone, String token) {
        try {
            collection.updateOne(
                Filters.eq("telefone", telefone),
                Updates.set("tokenConfirmacao", token)
            );
            logger.info("✅ Token atualizado para: {}", telefone);
            return true;
            
        } catch (Exception e) {
            logger.error("❌ Erro ao atualizar token", e);
            return false;
        }
    }
    
    @Override
    public boolean confirmarConta(String telefone, String token) {
        try {
            // Buscar usuário
            Document doc = collection.find(Filters.eq("telefone", telefone)).first();
            if (doc == null) return false;
            
            // Verificar token
            String tokenArmazenado = doc.getString("tokenConfirmacao");
            if (tokenArmazenado == null || !tokenArmazenado.equals(token)) {
                return false;
            }
            
            // Atualizar
            collection.updateOne(
                Filters.eq("telefone", telefone),
                Updates.combine(
                    Updates.set("ativo", true),
                    Updates.set("dataConfirmacao", new Date()),
                    Updates.unset("tokenConfirmacao")
                )
            );
            
            logger.info("✅ Conta confirmada: {}", telefone);
            return true;
            
        } catch (Exception e) {
            logger.error("❌ Erro ao confirmar conta", e);
            return false;
        }
    }
    
    @Override
    public boolean deletar(String id) {
        try {
            collection.deleteOne(Filters.eq("_id", new ObjectId(id)));
            logger.info("✅ Usuário deletado: {}", id);
            return true;
            
        } catch (Exception e) {
            logger.error("❌ Erro ao deletar usuário", e);
            return false;
        }
    }
    
    @Override
    public boolean verificarLogin(String cpf, String senha) {
        try {
            Document doc = collection.find(
                Filters.and(
                    Filters.eq("cpf", cpf),
                    Filters.eq("senha", senha),
                    Filters.eq("ativo", true)
                )
            ).first();
            
            boolean valido = doc != null;
            logger.info("🔐 Login {} para CPF: {}", valido ? "válido" : "inválido", cpf);
            return valido;
            
        } catch (Exception e) {
            logger.error("❌ Erro ao verificar login", e);
            return false;
        }
    }
    
    @Override
    public boolean existeCpf(String cpf) {
        try {
            long count = collection.countDocuments(Filters.eq("cpf", cpf));
            return count > 0;
            
        } catch (Exception e) {
            logger.error("❌ Erro ao verificar CPF", e);
            return false;
        }
    }
    
    @Override
    public boolean existeTelefone(String telefone) {
        try {
            long count = collection.countDocuments(Filters.eq("telefone", telefone));
            return count > 0;
            
        } catch (Exception e) {
            logger.error("❌ Erro ao verificar telefone", e);
            return false;
        }
    }
    
    // Helper methods
    private Document toDocument(Usuario usuario) {
        Document doc = new Document()
                .append("nome", usuario.getNome())
                .append("cpf", usuario.getCpf())
                .append("telefone", usuario.getTelefone())
                .append("senha", usuario.getSenha())
                .append("tokenConfirmacao", usuario.getTokenConfirmacao())
                .append("ativo", usuario.isAtivo())
                .append("dataCadastro", toDate(usuario.getDataCadastro()))
                .append("dataConfirmacao", usuario.getDataConfirmacao() != null ? 
                    toDate(usuario.getDataConfirmacao()) : null);
        
        if (usuario.getId() != null) {
            doc.append("_id", new ObjectId(usuario.getId()));
        }
        
        return doc;
    }
    
    private Usuario fromDocument(Document doc) {
        if (doc == null) return null;
        
        Usuario usuario = new Usuario();
        usuario.setId(doc.getObjectId("_id").toString());
        usuario.setNome(doc.getString("nome"));
        usuario.setCpf(doc.getString("cpf"));
        usuario.setTelefone(doc.getString("telefone"));
        usuario.setSenha(doc.getString("senha"));
        usuario.setTokenConfirmacao(doc.getString("tokenConfirmacao"));
        usuario.setAtivo(doc.getBoolean("ativo", false));
        
        Date dataCadastro = doc.getDate("dataCadastro");
        if (dataCadastro != null) {
            usuario.setDataCadastro(toLocalDateTime(dataCadastro));
        }
        
        Date dataConfirmacao = doc.getDate("dataConfirmacao");
        if (dataConfirmacao != null) {
            usuario.setDataConfirmacao(toLocalDateTime(dataConfirmacao));
        }
        
        return usuario;
    }
    
    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    
    private LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
