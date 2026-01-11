package com.mercadoscan.model;

public class ProdutoVoz {
    private String nome;
    private double valor;
    private int quantidade;
    private String comandoOriginal;
    
    public ProdutoVoz() {
        this.quantidade = 1;
    }
    
    public ProdutoVoz(String nome, double valor) {
        this();
        this.nome = nome;
        this.valor = valor;
    }
    
    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    
    public String getComandoOriginal() { return comandoOriginal; }
    public void setComandoOriginal(String comandoOriginal) { 
        this.comandoOriginal = comandoOriginal; 
    }
    
    public double getSubtotal() {
        return valor * quantidade;
    }
    
    @Override
    public String toString() {
        return String.format("%s x%d - R$ %.2f cada (Total: R$ %.2f)", 
            nome, quantidade, valor, getSubtotal());
    }
}
