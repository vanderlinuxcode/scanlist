package com.mercadoscan.dao;

import java.util.List;

import com.mercadoscan.model.Produto;

public interface ProdutoDAO {
    Produto salvar(Produto produto);
    List<Produto> buscarPorUsuario(String usuarioId);
    boolean remover(String produtoId);
    boolean removerTodosDoUsuario(String usuarioId);
    List<Produto> buscarPorNome(String nome, String usuarioId);
    double calcularTotalUsuario(String usuarioId);
    List<Produto> listarPorUsuarioId(String usuarioId);
    void removerPorNomeEUsuario(String nome, String usuarioId);
    void removerTodosPorUsuario(String usuarioId);
}