package com.mercadoscan.dao;

import com.mercadoscan.model.Usuario;
import java.util.Optional;

/**
 * Data Access Object (DAO) para Usuario
 * Define as operações de persistência
 */
public interface UsuarioDAO {
    
    // Create
    Usuario salvar(Usuario usuario);
    
    // Read
    Optional<Usuario> buscarPorId(String id);
    Optional<Usuario> buscarPorCpf(String cpf);
    Optional<Usuario> buscarPorTelefone(String telefone);
    
    // Update
    boolean atualizarToken(String telefone, String token);
    boolean confirmarConta(String telefone, String token);
    
    // Delete
    boolean deletar(String id);
    
    // Validações
    boolean verificarLogin(String cpf, String senha);
    boolean existeCpf(String cpf);
    boolean existeTelefone(String telefone);
}
