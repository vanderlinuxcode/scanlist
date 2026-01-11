package com.mercadoscan.view;

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

import com.mercadoscan.controller.UsuarioController;

public class CadastroView extends JFrame {
    
    private final UsuarioController usuarioController;
    
    // Componentes
    private JTextField txtNome;
    private JTextField txtCpf;
    private JTextField txtTelefone;
    private JPasswordField txtSenha;
    private JButton btnCadastrar;
    
    public CadastroView() {
        this.usuarioController = new UsuarioController();
        initComponents();
        configurarJanela();
    }
    
    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Título
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel lblTitulo = new JLabel("Cadastro de Usuário");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblTitulo, gbc);
        
        // Campos
        gbc.gridwidth = 1;
        
        // Nome
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Nome:"), gbc);
        
        gbc.gridx = 1;
        txtNome = new JTextField(20);
        panel.add(txtNome, gbc);
        
        // CPF
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("CPF:"), gbc);
        
        gbc.gridx = 1;
        txtCpf = new JTextField(20);
        panel.add(txtCpf, gbc);
        
        // Telefone
        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Telefone:"), gbc);
        
        gbc.gridx = 1;
        txtTelefone = new JTextField(20);
        panel.add(txtTelefone, gbc);
        
        // Senha
        gbc.gridy = 4;
        gbc.gridx = 0;
        panel.add(new JLabel("Senha (4 dígitos):"), gbc);
        
        gbc.gridx = 1;
        txtSenha = new JPasswordField(20);
        panel.add(txtSenha, gbc);
        
        // Botão
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        
        btnCadastrar = new JButton("Cadastrar");
        btnCadastrar.addActionListener(e -> realizarCadastro());
        panel.add(btnCadastrar, gbc);
        
        add(panel);
    }
    
    private void configurarJanela() {
        setTitle("Cadastro - MercadoScan");
        setSize(350, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
    }
    
    private void realizarCadastro() {
        String nome = txtNome.getText().trim();
        String cpf = txtCpf.getText().trim();
        String telefone = txtTelefone.getText().trim();
        String senha = new String(txtSenha.getPassword());
        
        // Validação básica
        if (nome.isEmpty() || cpf.isEmpty() || telefone.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Preencha todos os campos",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String token = usuarioController.cadastrarUsuario(nome, cpf, telefone, senha);
        
        if (token != null) {
            // Abrir tela de confirmação
            new ConfirmacaoView(telefone, token).setVisible(true);
            dispose();
        }
    }
}
