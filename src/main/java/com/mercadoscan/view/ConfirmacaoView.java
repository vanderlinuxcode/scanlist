package com.mercadoscan.view;

import com.mercadoscan.controller.UsuarioController;

import javax.swing.*;
import java.awt.*;

public class ConfirmacaoView extends JFrame {
    
    private final UsuarioController usuarioController;
    private final String telefone;
    private final String tokenGerado;
    
    private JTextField txtToken;
    private JButton btnConfirmar;
    
    public ConfirmacaoView(String telefone, String tokenGerado) {
        this.usuarioController = new UsuarioController();
        this.telefone = telefone;
        this.tokenGerado = tokenGerado;
        
        initComponents();
        configurarJanela();
    }
    
    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Título
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel lblTitulo = new JLabel("Confirmação de Conta");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblTitulo, gbc);
        
        // Mensagem
        gbc.gridy = 1;
        JTextArea txtMensagem = new JTextArea(
            "Enviamos um token de 6 dígitos para:\n" +
            telefone + "\n\n" +
            "Token gerado (para teste): " + tokenGerado + "\n" +
            "Digite o token recebido abaixo:"
        );
        txtMensagem.setEditable(false);
        txtMensagem.setOpaque(false);
        txtMensagem.setLineWrap(true);
        txtMensagem.setWrapStyleWord(true);
        panel.add(txtMensagem, gbc);
        
        // Campo token
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Token:"), gbc);
        
        gbc.gridx = 1;
        txtToken = new JTextField(10);
        panel.add(txtToken, gbc);
        
        // Botão
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        
        btnConfirmar = new JButton("Confirmar");
        btnConfirmar.addActionListener(e -> confirmarToken());
        panel.add(btnConfirmar, gbc);
        
        add(panel);
    }
    
    private void configurarJanela() {
        setTitle("Confirmação - MercadoScan");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
    }
    
    private void confirmarToken() {
        String token = txtToken.getText().trim();
        
        if (token.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Digite o token recebido",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        boolean confirmado = usuarioController.confirmarToken(telefone, token);
        
        if (confirmado) {
            JOptionPane.showMessageDialog(this,
                "Conta confirmada com sucesso!\nVocê já pode fazer login.",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }
}
