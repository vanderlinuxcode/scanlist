package com.mercadoscan.model;

import java.time.LocalDateTime;

/**
 * Model/Entidade Usuario
 * Representa um usuário no sistema
 */
public class Usuario {
    private String id;
    private String nome;
    private String cpf;
    private String telefone;
    private String senha;
    private String tokenConfirmacao;
    private boolean ativo;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataConfirmacao;
    
    // Construtor padrão
    public Usuario() {
        this.dataCadastro = LocalDateTime.now();
        this.ativo = false;
    }
    
    // Construtor com parâmetros
    public Usuario(String nome, String cpf, String telefone, String senha) {
        this();
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.senha = senha;
    }
    
    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    
    public String getTokenConfirmacao() { return tokenConfirmacao; }
    public void setTokenConfirmacao(String tokenConfirmacao) { 
        this.tokenConfirmacao = tokenConfirmacao; 
    }
    
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { 
        this.dataCadastro = dataCadastro; 
    }
    
    public LocalDateTime getDataConfirmacao() { return dataConfirmacao; }
    public void setDataConfirmacao(LocalDateTime dataConfirmacao) { 
        this.dataConfirmacao = dataConfirmacao; 
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                ", telefone='" + telefone + '\'' +
                ", ativo=" + ativo +
                '}';
    }
}
