package com.mercadoscan.service;

import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mercadoscan.dao.UsuarioDAO;
import com.mercadoscan.dao.UsuarioDAOImpl;
import com.mercadoscan.model.Usuario;
import com.mercadoscan.util.ValidadorUtil;

/**
 * Service layer - Contém a lógica de negócio
 */
public class UsuarioService {
    
    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);
    private static final int TOKEN_LENGTH = 6;
    
    private final UsuarioDAO usuarioDAO;
    private final Random random;
    
    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAOImpl();
        this.random = new Random();
    }
    
    /**
     * Cadastra um novo usuário
     */
    public String cadastrarUsuario(Usuario usuario) throws IllegalArgumentException {
        logger.info("📝 Iniciando cadastro para: {}", usuario.getCpf());
        
        // Validações
        validarDadosUsuario(usuario);
        
        // Verificar duplicidade
        if (usuarioDAO.existeCpf(usuario.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
        
        if (usuarioDAO.existeTelefone(usuario.getTelefone())) {
            throw new IllegalArgumentException("Telefone já cadastrado");
        }
        
        // Gerar token
        String token = gerarToken();
        usuario.setTokenConfirmacao(token);
        
        // Salvar
        Usuario usuarioSalvo = usuarioDAO.salvar(usuario);
        
        logger.info("✅ Usuário cadastrado com sucesso: {}", usuarioSalvo.getCpf());
        
        // Simular envio de SMS
        simularEnvioSMS(usuario.getTelefone(), token);
        
        return token;
    }
    
    /**
     * Confirma token recebido
     */
    public boolean confirmarToken(String telefone, String token) {
        logger.info("🔐 Confirmando token para: {}", telefone);
        
        if (!ValidadorUtil.validarTelefone(telefone)) {
            throw new IllegalArgumentException("Telefone inválido");
        }
        
        if (token == null || token.length() != TOKEN_LENGTH) {
            throw new IllegalArgumentException("Token inválido");
        }
        
        boolean confirmado = usuarioDAO.confirmarConta(telefone, token);
        
        if (confirmado) {
            logger.info("✅ Conta confirmada: {}", telefone);
        } else {
            logger.warn("❌ Token inválido para: {}", telefone);
        }
        
        return confirmado;
    }
    
    /**
     * Realiza login
     */
    public boolean realizarLogin(String cpf, String senha) {
        logger.info("🔑 Tentando login para CPF: {}", cpf);
        
        if (!ValidadorUtil.validarCPF(cpf)) {
            throw new IllegalArgumentException("CPF inválido");
        }
        
        if (!ValidadorUtil.validarSenha4Digitos(senha)) {
            throw new IllegalArgumentException("Senha deve ter 4 dígitos");
        }
        
        boolean loginValido = usuarioDAO.verificarLogin(cpf, senha);
        
        logger.info("🔑 Login {} para CPF: {}", 
            loginValido ? "bem-sucedido" : "falhou", cpf);
        
        return loginValido;
    }
    
    /**
     * Busca usuário por CPF
     */
    public Optional<Usuario> buscarPorCpf(String cpf) {
        if (!ValidadorUtil.validarCPF(cpf)) {
            return Optional.empty();
        }
        
        return usuarioDAO.buscarPorCpf(cpf);
    }
    
    // Métodos privados
    private void validarDadosUsuario(Usuario usuario) {
        if (!ValidadorUtil.validarNome(usuario.getNome())) {
            throw new IllegalArgumentException("Nome deve ter pelo menos 2 caracteres");
        }
        
        if (!ValidadorUtil.validarCPF(usuario.getCpf())) {
            throw new IllegalArgumentException("CPF inválido");
        }
        
        if (!ValidadorUtil.validarTelefone(usuario.getTelefone())) {
            throw new IllegalArgumentException("Telefone inválido");
        }
        
        if (!ValidadorUtil.validarSenha4Digitos(usuario.getSenha())) {
            throw new IllegalArgumentException("Senha deve ter 4 dígitos");
        }
    }
    
    private String gerarToken() {
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            token.append(random.nextInt(10));
        }
        return token.toString();
    }
    
    private void simularEnvioSMS(String telefone, String token) {
        try {
            // Simular delay de envio
            Thread.sleep(1000);
            
            System.out.println(String.format("""
                        
                        ╔══════════════════════════════════════════╗
                        ║        📱 SIMULAÇÃO DE SMS               ║
                        ╠══════════════════════════════════════════╣
                        ║ Para: \
                        %s\
                        ║
                        ║ Token: \
                        %s\
                        ║
                        ╚══════════════════════════════════════════╝
                        """, String.format("%-30s", telefone), String.format("%-30s", token)));
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
