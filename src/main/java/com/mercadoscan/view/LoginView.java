package com.mercadoscan.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.mercadoscan.controller.UsuarioController;

public class LoginView extends JFrame {
    
    private final UsuarioController usuarioController;
    
    // Componentes
    private JTextField txtCpf;
    private JPasswordField txtSenha;
    private JButton btnLogin;
    private JButton btnCadastrar;
    
    public LoginView() {
        this.usuarioController = new UsuarioController();
        initComponents();
        configurarJanela();
    }
    
    private void initComponents() {
        // Painel principal
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Título
        JLabel lblTitulo = new JLabel("MercadoScan");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);
        
        // CPF
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("CPF:"), gbc);
        
        gbc.gridx = 1;
        txtCpf = new JTextField(15);
        txtCpf.setToolTipText("Digite seu CPF (apenas números)");
        panel.add(txtCpf, gbc);
        
        // Senha
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Senha (4 dígitos):"), gbc);
        
        gbc.gridx = 1;
        txtSenha = new JPasswordField(15);
        txtSenha.setToolTipText("Digite sua senha de 4 dígitos");
        panel.add(txtSenha, gbc);
        
        // Botões
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        
        btnLogin = new JButton("Entrar");
        btnLogin.setPreferredSize(new Dimension(100, 30));
        btnLogin.addActionListener(e -> realizarLogin());
        
        btnCadastrar = new JButton("Cadastrar");
        btnCadastrar.setPreferredSize(new Dimension(100, 30));
        btnCadastrar.addActionListener(e -> abrirCadastro());
        
        panelBotoes.add(btnLogin);
        panelBotoes.add(btnCadastrar);
        
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(panelBotoes, gbc);
        
        // Rodapé
        JLabel lblRodape = new JLabel("Sistema ScanList-Automation v1.0");
        lblRodape.setHorizontalAlignment(SwingConstants.CENTER);
        lblRodape.setFont(new Font("Arial", Font.ITALIC, 10));
        
        gbc.gridy = 4;
        panel.add(lblRodape, gbc);
        
        add(panel);
    }
    
    private void configurarJanela() {
        setTitle("MercadoScan - Sistema de Compras - Voz");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centralizar
        setResizable(false);
    }
    
private void realizarLogin() {
    String cpf = txtCpf.getText().trim();
    String senha = new String(txtSenha.getPassword());
    
    if (cpf.isEmpty() || senha.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Preencha todos os campos",
            "Aviso", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    boolean sucesso = usuarioController.realizarLogin(cpf, senha);
    
    if (sucesso) {
    // Buscar usuário para obter o nome
    usuarioController.buscarUsuarioPorCpf(cpf).ifPresent(usuario -> {
        // Fechar tela de login
        dispose();
        
        // Abrir tela principal de lista de compras
        SwingUtilities.invokeLater(() -> {
            ListaComprasView listaView = new ListaComprasView(
                usuario.getId(), usuario.getNome());
            listaView.setVisible(true);
        }); });
    }
}
    private void abrirCadastro() {
        new CadastroView().setVisible(true);
    }
}
