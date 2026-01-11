package com.mercadoscan.model;

import java.time.LocalDateTime;

public class Produto {
    private String id;
    private String nome;
    private double valor;
    private int quantidade;
    private String categoria;
    private LocalDateTime dataAdicao;
    private String usuarioId;
    
    public Produto() {
        this.dataAdicao = LocalDateTime.now();
        this.quantidade = 1;
        this.categoria = "Geral";
    }
    
    public Produto(String nome, double valor, String usuarioId) {
        this();
        this.nome = nome;
        this.valor = valor;
        this.usuarioId = usuarioId;
    }
    
    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    public LocalDateTime getDataAdicao() { return dataAdicao; }
    public void setDataAdicao(LocalDateTime dataAdicao) { this.dataAdicao = dataAdicao; }
    
    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    
    public double getSubtotal() {
        return valor * quantidade;
    }
    
    @Override
    public String toString() {
        return String.format("%s - R$ %.2f x%d = R$ %.2f", 
            nome, valor, quantidade, getSubtotal());
    }
}
