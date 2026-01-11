package com.mercadoscan;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.mercadoscan.view.LoginView;

public class MainApp {
    @SuppressWarnings("UseSpecificCatch")
    public static void main(String[] args) {
        // Usar SwingUtilities para thread safety
        SwingUtilities.invokeLater(() -> {
            try {
                // Configurar Look and Feel do sistema
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                // Iniciar tela de login
                LoginView loginView = new LoginView();
                loginView.setVisible(true);
                
                System.out.println("🚀 MercadoScan Desktop iniciado!");
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                    "Erro ao iniciar aplicação: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
