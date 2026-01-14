package com.mercadoscan.view;

import com.mercadoscan.model.PIXPaymentRequest;
import com.mercadoscan.model.PIXPaymentResponse;
import com.google.gson.Gson;

import javax.swing.*;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.concurrent.ExecutionException;

public class PIXPaymentGUI extends JFrame {
    
    private final String usuarioId;
    private final BigDecimal totalCompra;
    private String transactionId;
    private Timer statusTimer;
    
    private JLabel qrCodeLabel;
    private JTextArea pixCodeTextArea;
    private JLabel statusLabel;
    private JButton copyButton;
    private JButton checkStatusButton;
    
    // Para comunicação com a API local
    private static final String API_BASE_URL = "http://localhost:8080";
    private final HttpClient httpClient;
    private final Gson gson;
    
    public PIXPaymentGUI(String usuarioId, BigDecimal totalCompra) {
        this.usuarioId = usuarioId;
        this.totalCompra = totalCompra;
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
        
        initComponents();
        createPIXPayment();
    }
    
    private void initComponents() {
        setTitle("Pagamento via PIX - MercadoScan");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        
        // Painel superior - Informações
        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("💳 Pagamento via PIX", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel amountLabel = new JLabel("Valor: R$ " + totalCompra, SwingConstants.CENTER);
        amountLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JLabel instructionLabel = new JLabel(
            "<html><center>Escaneie o QR Code abaixo<br>ou copie o código PIX</center></html>", 
            SwingConstants.CENTER
        );
        
        infoPanel.add(titleLabel);
        infoPanel.add(amountLabel);
        infoPanel.add(instructionLabel);
        
        // Painel central - QR Code
        JPanel qrPanel = new JPanel(new BorderLayout());
        qrPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        qrCodeLabel = new JLabel("Gerando QR Code...", SwingConstants.CENTER);
        qrCodeLabel.setPreferredSize(new Dimension(250, 250));
        
        qrPanel.add(qrCodeLabel, BorderLayout.CENTER);
        
        // Painel do código PIX
        JPanel codePanel = new JPanel(new BorderLayout());
        codePanel.setBorder(BorderFactory.createTitledBorder("Código PIX"));
        
        pixCodeTextArea = new JTextArea();
        pixCodeTextArea.setEditable(false);
        pixCodeTextArea.setLineWrap(true);
        pixCodeTextArea.setRows(3);
        
        JScrollPane scrollPane = new JScrollPane(pixCodeTextArea);
        codePanel.add(scrollPane, BorderLayout.CENTER);
        
        copyButton = new JButton("📋 Copiar Código");
        copyButton.setEnabled(false);
        copyButton.addActionListener(e -> copyPIXCode());
        
        codePanel.add(copyButton, BorderLayout.SOUTH);
        
        // Painel de status
        JPanel statusPanel = new JPanel(new FlowLayout());
        
        statusLabel = new JLabel("Status: Aguardando pagamento...");
        statusPanel.add(statusLabel);
        
        checkStatusButton = new JButton("🔄 Verificar Status");
        checkStatusButton.addActionListener(e -> checkPaymentStatus());
        statusPanel.add(checkStatusButton);
        
        // Painel inferior - Botões
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton openPixAppButton = new JButton("📱 Abrir App PIX");
        openPixAppButton.addActionListener(e -> openPIXApp());
        buttonPanel.add(openPixAppButton);
        
        JButton closeButton = new JButton("Fechar");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        
        // Adiciona todos os painéis
        add(infoPanel, BorderLayout.NORTH);
        add(qrPanel, BorderLayout.CENTER);
        add(codePanel, BorderLayout.SOUTH);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(statusPanel, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);
        
        // Configura timer para verificar status automaticamente
        statusTimer = new Timer(10000, e -> checkPaymentStatus()); // A cada 10 segundos
        statusTimer.setRepeats(true);
    }
    
    private void createPIXPayment() {
        SwingWorker<PIXPaymentResponse, Void> worker = new SwingWorker<>() {
            @Override
            protected PIXPaymentResponse doInBackground() throws Exception {
                // Preparar request
                PIXPaymentRequest request = new PIXPaymentRequest();
                request.setUsuarioId(usuarioId);
                request.setTransactionId("PIX-" + System.currentTimeMillis());
                request.setAmount(totalCompra);
                request.setDescription("Compra MercadoScan - " + LocalDateTime.now());
                request.setPayerName("Cliente MercadoScan");
                request.setPayerDocument("000.000.000-00");
                request.setPayerEmail("cliente@email.com");
                
                // Chamar API
                String jsonRequest = gson.toJson(request);
                HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/api/pix/create"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();
                
                HttpResponse<String> response = httpClient.send(httpRequest, 
                    HttpResponse.BodyHandlers.ofString());
                
                return gson.fromJson(response.body(), PIXPaymentResponse.class);
            }
            
            @Override
            protected void done() {
                try {
                    PIXPaymentResponse response = get();
                    
                    if (response.isSuccess()) {
                        transactionId = response.getTransactionId();
                        
                        // Exibir QR Code
                        if (response.getQrCodeBase64() != null && 
                            !response.getQrCodeBase64().isEmpty()) {
                            displayQRCode(response.getQrCodeBase64());
                        }
                        
                        // Exibir código PIX
                        pixCodeTextArea.setText(response.getQrCodeText());
                        copyButton.setEnabled(true);
                        
                        // Iniciar verificação automática
                        statusTimer.start();
                        
                    } else {
                        JOptionPane.showMessageDialog(PIXPaymentGUI.this,
                            "Erro ao criar pagamento PIX: " + response.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (HeadlessException | InterruptedException | ExecutionException e) {
                    JOptionPane.showMessageDialog(PIXPaymentGUI.this,
                        "Erro na comunicação: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void displayQRCode(String base64Image) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            ImageIcon qrIcon = new ImageIcon(imageBytes);
            Image scaledImage = qrIcon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
            qrCodeLabel.setIcon(new ImageIcon(scaledImage));
            qrCodeLabel.setText("");
        } catch (Exception e) {
            qrCodeLabel.setText("❌ Erro ao exibir QR Code");
        }
    }
    
    private void copyPIXCode() {
        String pixCode = pixCodeTextArea.getText();
        java.awt.Toolkit.getDefaultToolkit()
            .getSystemClipboard()
            .setContents(new java.awt.datatransfer.StringSelection(pixCode), null);
        
        JOptionPane.showMessageDialog(this,
            "Código PIX copiado para a área de transferência!",
            "Sucesso",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void checkPaymentStatus() {
        if (transactionId == null) return;
        
        SwingWorker<PIXPaymentResponse, Void> worker = new SwingWorker<>() {
            @Override
            protected PIXPaymentResponse doInBackground() throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE_URL + "/api/pix/status?transactionId=" + transactionId))
                    .GET()
                    .build();
                
                HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
                
                return gson.fromJson(response.body(), PIXPaymentResponse.class);
            }
            
            @Override
            protected void done() {
                try {
                    PIXPaymentResponse response = get();
                    
                    if (response.isSuccess()) {
                        String status = response.getStatus();
                        updateStatusUI(status);
                        
                        if ("CONFIRMED".equals(status)) {
                            statusTimer.stop();
                            JOptionPane.showMessageDialog(PIXPaymentGUI.this,
                                "✅ Pagamento confirmado!\nObrigado pela compra.",
                                "Pagamento Efetuado",
                                JOptionPane.INFORMATION_MESSAGE);
                            
                            // Fechar janela após pagamento confirmado
                            dispose();
                            
                        } else if ("EXPIRED".equals(status) || "FAILED".equals(status)) {
                            statusTimer.stop();
                            JOptionPane.showMessageDialog(PIXPaymentGUI.this,
                                "Pagamento expirado ou falhou.\nPor favor, gere um novo PIX.",
                                "Pagamento Não Efetuado",
                                JOptionPane.WARNING_MESSAGE);
                        }
                    }
                    
                } catch (HeadlessException | InterruptedException | ExecutionException e) {
                    System.err.println("Erro ao verificar status: " + e.getMessage());
                }
            }
        };
        
        worker.execute();
    }
    
    private void updateStatusUI(String status) {
        switch (status) {
            case "CREATED" -> {
                statusLabel.setText("Status: Aguardando pagamento...");
                statusLabel.setForeground(Color.BLUE);
            }
            case "PENDING" -> {
                statusLabel.setText("Status: Pagamento detectado...");
                statusLabel.setForeground(Color.ORANGE);
            }
            case "CONFIRMED" -> {
                statusLabel.setText("Status: ✅ Pagamento confirmado!");
                statusLabel.setForeground(Color.GREEN);
            }
            case "EXPIRED" -> {
                statusLabel.setText("Status: ⏰ Pagamento expirado");
                statusLabel.setForeground(Color.RED);
            }
            case "FAILED" -> {
                statusLabel.setText("Status: ❌ Pagamento falhou");
                statusLabel.setForeground(Color.RED);
            }
        }
    }
    
    private void openPIXApp() {
        if (pixCodeTextArea.getText() != null && !pixCodeTextArea.getText().isEmpty()) {
            try {
                // Tenta abrir o código PIX em um app externo
                String pixCode = pixCodeTextArea.getText();
                String url = "pix://" + pixCode;
                
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(new URI(url));
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Copie o código PIX e cole no seu app de pagamento.",
                        "Informação",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (HeadlessException | IOException | URISyntaxException e) {
                JOptionPane.showMessageDialog(this,
                    "Copie o código PIX manualmente.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    // Método para abrir a tela de pagamento PIX
    public static void showPaymentWindow(String usuarioId, double total) {
        SwingUtilities.invokeLater(() -> {
            PIXPaymentGUI pixWindow = new PIXPaymentGUI(
                usuarioId, 
                BigDecimal.valueOf(total)
            );
            pixWindow.setVisible(true);
        });
    }
}