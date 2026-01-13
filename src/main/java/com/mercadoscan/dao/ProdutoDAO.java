package com.mercadoscan.dao;

import java.util.List;

import com.mercadoscan.model.Produto;

public interface ProdutoDAO {
    // CRUD básico
    Produto salvar(Produto produto);
    List<Produto> buscarPorUsuario(String usuarioId);
    boolean remover(String produtoId);
    boolean removerTodosDoUsuario(String usuarioId);
    
    // Buscas específicas
    List<Produto> buscarPorNome(String nome, String usuarioId);
    double calcularTotalUsuario(String usuarioId);
    
    // NOVOS MÉTODOS QUE VOCÊ USA (precisa ter na interface!)
    List<Produto> listarPorUsuarioId(String usuarioId); // ← IMPLEMENTAR!
    void removerPorNomeEUsuario(String nome, String usuarioId);
    void removerTodosPorUsuario(String usuarioId);

}