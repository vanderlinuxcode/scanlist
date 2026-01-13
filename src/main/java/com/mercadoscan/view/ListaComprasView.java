package com.mercadoscan.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import com.mercadoscan.controller.ProdutoController;
import com.mercadoscan.model.Produto;

public class ListaComprasView extends JFrame {
    
    private final String usuarioNome;
    private final ProdutoController produtoController;
    
    // Componentes
    private JTable tabelaProdutos;
    private DefaultTableModel tableModel;
    private JLabel lblTotal;
    private JLabel lblContador;
    private JButton btnAdicionarManual;
    private JButton btnRemover;
    private JButton btnLimpar;
    private JButton btnFinalizar;
    private String usuarioId;
    
    public ListaComprasView(String usuarioId, String usuarioNome) {
    this.usuarioId = usuarioId;
    this.usuarioNome = usuarioNome;
    
    System.out.println("=== CONSTRUTOR ListaComprasView ===");
    System.out.println("UsuarioId: " + usuarioId);
    System.out.println("UsuarioNome: " + usuarioNome);
    
    this.produtoController = new ProdutoController(usuarioId);
        
    initComponents();
    configurarJanela();
    carregarProdutos();
    criarMenu();
}

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Painel superior
        JPanel panelTop = new JPanel(new BorderLayout(10, 10));
        panelTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblTitulo = new JLabel("🛒 MercadoScan - Lista de Compras");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(0, 100, 0));
        panelTop.add(lblTitulo, BorderLayout.NORTH);
        
        JLabel lblBemVindo = new JLabel("Bem-vindo, " + usuarioNome + "!");
        lblBemVindo.setFont(new Font("Arial", Font.PLAIN, 14));
        panelTop.add(lblBemVindo, BorderLayout.CENTER);
        
        add(panelTop, BorderLayout.NORTH);
        
        // Tabela de produtos
        String[] colunas = {"Produto", "Valor Unitário", "Quantidade", "Subtotal"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Apenas quantidade editável
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 2 ? Integer.class : String.class;
            }
        };
        
        tabelaProdutos = new JTable(tableModel);
        tabelaProdutos.setRowHeight(30);
        tabelaProdutos.getColumnModel().getColumn(0).setPreferredWidth(200);
        tabelaProdutos.getColumnModel().getColumn(1).setPreferredWidth(100);
        tabelaProdutos.getColumnModel().getColumn(2).setPreferredWidth(80);
        tabelaProdutos.getColumnModel().getColumn(3).setPreferredWidth(100);
        
        // Listener para alteração de quantidade
        tabelaProdutos.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 2) { // Coluna de quantidade
                int row = e.getFirstRow();
                Object value = tableModel.getValueAt(row, 2);
                if (value instanceof Integer) {
                    // Aqui você pode atualizar no banco se necessário
                    atualizarEstatisticas();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tabelaProdutos);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Itens da Lista"));
        add(scrollPane, BorderLayout.CENTER);
        
        // Painel inferior
        JPanel panelBottom = new JPanel(new BorderLayout(10, 10));
        panelBottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Painel de estatísticas
        JPanel panelStats = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        lblContador = new JLabel("Itens: 0");
        lblContador.setFont(new Font("Arial", Font.PLAIN, 14));
        
        lblTotal = new JLabel("Total: R$ 0,00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotal.setForeground(Color.BLUE);
        
        panelStats.add(lblContador);
        panelStats.add(lblTotal);
        panelBottom.add(panelStats, BorderLayout.NORTH);
        
        // Painel de botões
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        
        btnAdicionarManual = criarBotao("➕ Adicionar Manual", new Color(60, 179, 113));
        btnAdicionarManual.addActionListener(e -> adicionarManual());
        
        btnRemover = criarBotao("🗑️ Remover", new Color(220, 20, 60));
        btnRemover.addActionListener(e -> removerProduto());
        
        btnLimpar = criarBotao("🧹 Limpar Tudo", new Color(255, 140, 0));
       // btnLimpar.addActionListener(e -> limparLista());
        
        btnFinalizar = criarBotao("✅ Finalizar Compra", new Color(50, 205, 50));
        btnFinalizar.addActionListener(e -> finalizarCompra());
        
        panelBotoes.add(btnAdicionarManual);
        panelBotoes.add(btnRemover);
        panelBotoes.add(btnLimpar);
        panelBotoes.add(btnFinalizar);
        
        panelBottom.add(panelBotoes, BorderLayout.CENTER);
        add(panelBottom, BorderLayout.SOUTH);
    }
    
    private JButton criarBotao(String texto, Color cor) {
        JButton botao = new JButton(texto);
        botao.setBackground(cor);
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setOpaque(true);
        botao.setPreferredSize(new Dimension(150, 35));
        return botao;
    }
    
    private void configurarJanela() {
        setTitle("MercadoScan - Lista de Compras");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private void criarMenu() {
        JMenuBar menuBar = new JMenuBar();
        
        // Menu Arquivo
        JMenu menuArquivo = new JMenu("Arquivo");
        JMenuItem itemSair = new JMenuItem("Sair");
        itemSair.addActionListener(e -> System.exit(0));
        menuArquivo.add(itemSair);
        
        // Menu Ajuda
        JMenu menuAjuda = new JMenu("Ajuda");
        JMenuItem itemSobre = new JMenuItem("Sobre");
        itemSobre.addActionListener(e -> mostrarSobre());
        menuAjuda.add(itemSobre);
        
        menuBar.add(menuArquivo);
        menuBar.add(menuAjuda);
        setJMenuBar(menuBar);
    }
    
   private void carregarProdutos() {
    System.out.println("=== DEBUG carregarProdutos ===");
    System.out.println("UsuarioId para busca: " + this.usuarioId);
    
    tableModel.setRowCount(0);
    
    List<Produto> produtos = produtoController.listarProdutos();
    System.out.println("DEBUG: Número de produtos retornados: " + produtos.size());
    
    for (Produto produto : produtos) {
        System.out.println("DEBUG Produto: " + produto.getNome() + 
                         " | Valor: " + produto.getValor() + 
                         " | Quant: " + produto.getQuantidade() +
                         " | UsuarioId: " + produto.getUsuarioId());
        
        Object[] row = {
            produto.getNome(),
            String.format("R$ %.2f", produto.getValor()),
            produto.getQuantidade(),
            String.format("R$ %.2f", produto.getSubtotal())
        };
        tableModel.addRow(row);
    }
    
    atualizarEstatisticas();
}
    
    private void atualizarEstatisticas() {
        int quantidade = tableModel.getRowCount();
        double total = produtoController.calcularTotal();
        
        lblContador.setText("Itens: " + quantidade);
        lblTotal.setText(String.format("Total: R$ %.2f", total));
    }
    
private void adicionarManual() {
    JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
    JTextField txtNome = new JTextField(20);
    JTextField txtValor = new JTextField();
    JSpinner spnQuantidade = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
    
    panel.add(new JLabel("Nome do produto:"));
    panel.add(txtNome);
    panel.add(new JLabel("Valor (R$):"));
    panel.add(txtValor);
    panel.add(new JLabel("Quantidade:"));
    panel.add(spnQuantidade);
    
    int result = JOptionPane.showConfirmDialog(this, panel,
        "Adicionar Produto", JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.PLAIN_MESSAGE);
    
    if (result == JOptionPane.OK_OPTION) {
        try {
            String nome = txtNome.getText().trim();
            if (nome.isEmpty()) {
                throw new IllegalArgumentException("Nome é obrigatório");
            }
            
            double valor = Double.parseDouble(txtValor.getText().replace(",", "."));
            if (valor <= 0) {
                throw new IllegalArgumentException("Valor deve ser positivo");
            }
            
            int quantidade = (Integer) spnQuantidade.getValue();
            
            // DEBUG: Verifique se está chamando corretamente
            System.out.println("=== DEBUG ListaComprasView ===");
            System.out.println("Chamando produtoController.adicionarProduto()");
            System.out.println("UsuarioId: " + this.usuarioId);
            System.out.println("Nome: " + nome);
            System.out.println("Valor: " + valor);
            System.out.println("Quantidade: " + quantidade);
            
            produtoController.adicionarProduto(nome, valor, quantidade);
            carregarProdutos();
            
            JOptionPane.showMessageDialog(this,
                "Produto adicionado com sucesso!",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Valor inválido! Use números (ex: 5.99)",
                "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (HeadlessException e) {
            System.err.println("❌ ERRO inesperado em adicionarManual: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Erro ao adicionar produto: " + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
    
    private void removerProduto() {
        int selectedRow = tabelaProdutos.getSelectedRow();
        if (selectedRow >= 0) {
            String produtoNome = (String) tableModel.getValueAt(selectedRow, 0);
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Remover '" + produtoNome + "' da lista?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                produtoController.removerProdutoPorNome(produtoNome);
                carregarProdutos();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Selecione um produto na tabela para remover",
                "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
        
    private void finalizarCompra() {
        int quantidade = tableModel.getRowCount();
        if (quantidade == 0) {
            JOptionPane.showMessageDialog(this,
                "Lista de compras vazia!\nAdicione produtos primeiro.",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Mostrar resumo
        StringBuilder resumo = new StringBuilder();
        resumo.append("RESUMO DA COMPRA\n");
        resumo.append("================\n\n");
        
        double total = 0;
        for (int i = 0; i < quantidade; i++) {
            String produto = (String) tableModel.getValueAt(i, 0);
            String quantidadeItem = tableModel.getValueAt(i, 2).toString();
            String subtotal = (String) tableModel.getValueAt(i, 3);
            
            // Extrair valor numérico do subtotal
            String subtotalClean = subtotal.replace("R$", "").replace(",", ".").trim();
            total += Double.parseDouble(subtotalClean);
            
            resumo.append(String.format("• %s x%s = %s\n", produto, quantidadeItem, subtotal));
        }
        
        resumo.append("\n================\n");
        resumo.append(String.format("TOTAL: R$ %.2f\n", total));
        resumo.append(String.format("Quantidade de itens: %d\n\n", quantidade));
        
        // Opções de pagamento
        JPanel panelPagamento = new JPanel(new GridLayout(2, 2, 5, 5));
        JRadioButton rbDinheiro = new JRadioButton("Dinheiro");
        JRadioButton rbCartao = new JRadioButton("Cartão");
        JRadioButton rbPix = new JRadioButton("PIX");
        JRadioButton rbOutro = new JRadioButton("Outro");
        
        ButtonGroup bgPagamento = new ButtonGroup();
        bgPagamento.add(rbDinheiro);
        bgPagamento.add(rbCartao);
        bgPagamento.add(rbPix);
        bgPagamento.add(rbOutro);
        rbDinheiro.setSelected(true);
        
        panelPagamento.add(rbDinheiro);
        panelPagamento.add(rbCartao);
        panelPagamento.add(rbPix);
        panelPagamento.add(rbOutro);
        
        JPanel panelFinal = new JPanel(new BorderLayout(10, 10));
        panelFinal.add(new JScrollPane(new JTextArea(resumo.toString())), BorderLayout.CENTER);
        panelFinal.add(new JLabel("Forma de pagamento:"), BorderLayout.NORTH);
        panelFinal.add(panelPagamento, BorderLayout.SOUTH);
        
        int result = JOptionPane.showConfirmDialog(this, panelFinal,
            "Finalizar Compra", JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.INFORMATION_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            produtoController.finalizarCompra();
            carregarProdutos();
        }
    }
    
    private void mostrarSobre() {
        JOptionPane.showMessageDialog(this, """
                                            \ud83d\uded2 MercadoScan v1.0
                                            
                                            Sistema de lista de compras
                                            Desenvolvido para simplificar suas compras
                                            
                                            Funcionalidades:
                                            \u2022 Adicionar produtos manualmente
                                            \u2022 Gerenciar quantidades
                                            \u2022 Calcular total automaticamente
                                            \u2022 Finalizar compra com resumo
                                            
                                            \u00a9 2024 MercadoScan""",
            "Sobre", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }
}