package com.mercadoscan.controller;

import java.awt.HeadlessException;
import java.util.Optional;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mercadoscan.model.Usuario;
import com.mercadoscan.service.UsuarioService;

/**
 * Controller - Mediação entre View e Service
 */
public class UsuarioController {
    
    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);
    
    private final UsuarioService usuarioService;
    
    public UsuarioController() {
        this.usuarioService = new UsuarioService();
    }
    
    /**
     * Cadastra usuário e retorna token
     */
    public String cadastrarUsuario(String nome, String cpf, String telefone, String senha) {
        try {
            Usuario usuario = new Usuario(nome, cpf, telefone, senha);
            String token = usuarioService.cadastrarUsuario(usuario);
            
            JOptionPane.showMessageDialog(null,
                "Cadastro realizado!\nToken enviado para: " + telefone,
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            
            return token;
            
        } catch (IllegalArgumentException e) {
            mostrarErro("Erro de Validação", e.getMessage());
            return null;
            
        } catch (HeadlessException e) {
            logger.error("Erro ao cadastrar usuário", e);
            mostrarErro("Erro", "Falha no cadastro: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Confirma token
     */
    public boolean confirmarToken(String telefone, String token) {
        try {
            boolean confirmado = usuarioService.confirmarToken(telefone, token);
            
            if (confirmado) {
                JOptionPane.showMessageDialog(null,
                    "Conta confirmada com sucesso!",
                    "Confirmação", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                    "Token inválido ou expirado",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
            
            return confirmado;
            
        } catch (IllegalArgumentException e) {
            mostrarErro("Erro de Validação", e.getMessage());
            return false;
        }
    }
    
    /**
     * Realiza login
     */
    public boolean realizarLogin(String cpf, String senha) {
        try {
            boolean loginSucesso = usuarioService.realizarLogin(cpf, senha);
            
            if (loginSucesso) {
                JOptionPane.showMessageDialog(null,
                    "Acesso concedido!",
                    "Obrigado por estar aqui", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                    "CPF, senha inválidos ou conta não confirmada",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
            
            return loginSucesso;
            
        } catch (IllegalArgumentException e) {
            mostrarErro("Erro de Validação", e.getMessage());
            return false;
        }
    }
    
    /**
     * Busca usuário
     */
    public Optional<Usuario> buscarUsuario(String cpf) {
        try {
            return usuarioService.buscarPorCpf(cpf);
        } catch (Exception e) {
            logger.error("Erro ao buscar usuário", e);
            return Optional.empty();
        }
    }
    
    // Método auxiliar
    private void mostrarErro(String titulo, String mensagem) {
        JOptionPane.showMessageDialog(null,
            mensagem,
            titulo, JOptionPane.ERROR_MESSAGE);
    }

    public Optional<Usuario> buscarUsuarioPorCpf(String cpf) {
    // Chama o service ou DAO para buscar o usuário
    return usuarioService.buscarPorCpf(cpf);
    }
}
