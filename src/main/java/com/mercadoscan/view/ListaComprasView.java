package com.mercadoscan.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
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

import com.mercadoscan.controller.PagamentoController;
import com.mercadoscan.controller.ProdutoController;
import com.mercadoscan.model.Produto;

public class ListaComprasView extends JFrame {
    
    private final String usuarioNome;
    private final ProdutoController produtoController;
    private final PagamentoController pagamentoController;
    
    // Componentes
    private JTable tabelaProdutos;
    private DefaultTableModel tableModel;
    private JLabel lblTotal;
    private JLabel lblContador;
    private JButton btnAdicionarManual;
    private JButton btnRemover;
    private JButton btnLimpar;
    private JButton btnFinalizar;
    private JButton btnHistorico;
    @SuppressWarnings("FieldMayBeFinal")
    private String usuarioId;
    
    // 📊 Lista LOCAL de produtos (SOMENTE o que está na tela AGORA)
    @SuppressWarnings("FieldMayBeFinal")
    private List<Produto> produtosNaTela = new ArrayList<>();
    
    // Construtor - INICIA SEMPRE VAZIO
    public ListaComprasView(String usuarioId, String usuarioNome) {
        super("🛒 MercadoScan - NOVA LISTA DE COMPRAS");
        
        this.usuarioId = usuarioId;
        this.usuarioNome = usuarioNome;
        
        System.out.println("\n=== INICIANDO NOVA LISTA DE COMPRAS ===");
        System.out.println("✅ UsuarioId: " + usuarioId);
        System.out.println("✅ UsuarioNome: " + usuarioNome);
        System.out.println("✅ Lista atual: VAZIA (0 itens)");
        System.out.println("✅ Histórico: NÃO carregado");
        
        this.produtoController = new ProdutoController(usuarioId);
        this.pagamentoController = new PagamentoController();
        
        initComponents();
        configurarJanela();
        criarMenu();
        
        setVisible(true);
    }

    public ListaComprasView(PagamentoController pagamentoController, ProdutoController produtoController, String usuarioNome) throws HeadlessException {
        this.pagamentoController = pagamentoController;
        this.produtoController = produtoController;
        this.usuarioNome = usuarioNome;
    }

    public ListaComprasView(PagamentoController pagamentoController, ProdutoController produtoController, String usuarioNome, GraphicsConfiguration gc) {
        super(gc);
        this.pagamentoController = pagamentoController;
        this.produtoController = produtoController;
        this.usuarioNome = usuarioNome;
    }

    public ListaComprasView(PagamentoController pagamentoController, ProdutoController produtoController, String usuarioNome, String title) throws HeadlessException {
        super(title);
        this.pagamentoController = pagamentoController;
        this.produtoController = produtoController;
        this.usuarioNome = usuarioNome;
    }

    public ListaComprasView(PagamentoController pagamentoController, ProdutoController produtoController, String usuarioNome, String title, GraphicsConfiguration gc) {
        super(title, gc);
        this.pagamentoController = pagamentoController;
        this.produtoController = produtoController;
        this.usuarioNome = usuarioNome;
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Painel superior
        JPanel panelTop = new JPanel(new BorderLayout(10, 10));
        panelTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblTitulo = new JLabel("🛒 NOVA LISTA DE COMPRAS - MercadoScan");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(0, 100, 0));
        panelTop.add(lblTitulo, BorderLayout.NORTH);
        
        JLabel lblBemVindo = new JLabel("Usuário: " + usuarioNome + " | Lista atual: 0 itens");
        lblBemVindo.setFont(new Font("Arial", Font.PLAIN, 14));
        panelTop.add(lblBemVindo, BorderLayout.CENTER);
        
        add(panelTop, BorderLayout.NORTH);
        
        // Tabela de produtos (SEMPRE inicia VAZIA)
        String[] colunas = {"Produto", "Valor Unitário (R$)", "Quantidade", "Subtotal (R$)"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabela não editável
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 1 || column == 3) return Double.class;
                if (column == 2) return Integer.class;
                return String.class;
            }
        };
        
        tabelaProdutos = new JTable(tableModel);
        tabelaProdutos.setRowHeight(30);
        tabelaProdutos.getColumnModel().getColumn(0).setPreferredWidth(200);
        tabelaProdutos.getColumnModel().getColumn(1).setPreferredWidth(120);
        tabelaProdutos.getColumnModel().getColumn(2).setPreferredWidth(80);
        tabelaProdutos.getColumnModel().getColumn(3).setPreferredWidth(120);
        
        JScrollPane scrollPane = new JScrollPane(tabelaProdutos);
        scrollPane.setBorder(BorderFactory.createTitledBorder("🆕 LISTA ATUAL (NOVOS ITENS)"));
        add(scrollPane, BorderLayout.CENTER);
        
        // Painel inferior
        JPanel panelBottom = new JPanel(new BorderLayout(10, 10));
        panelBottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Painel de estatísticas
        JPanel panelStats = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 5));
        lblContador = new JLabel("Itens na lista: 0");
        lblContador.setFont(new Font("Arial", Font.PLAIN, 14));
        
        lblTotal = new JLabel("Total: R$ 0,00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotal.setForeground(Color.BLUE);
        
        panelStats.add(lblContador);
        panelStats.add(lblTotal);
        panelBottom.add(panelStats, BorderLayout.NORTH);
        
        // Painel de botões PRINCIPAIS (só para lista atual)
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        
        btnAdicionarManual = criarBotao("➕ Adicionar Produto", new Color(60, 179, 113));
        btnAdicionarManual.addActionListener(e -> adicionarProdutoNaLista());
        
        btnRemover = criarBotao("🗑️ Remover Selecionado", new Color(220, 20, 60));
        btnRemover.addActionListener(e -> removerProdutoSelecionado());
        
        btnLimpar = criarBotao("🧹 Limpar Lista", new Color(255, 140, 0));
        btnLimpar.addActionListener(e -> limparListaAtual());
        
        btnFinalizar = criarBotao("✅ Finalizar Compra", new Color(50, 205, 50));
        btnFinalizar.addActionListener(e -> finalizarCompraAtual());
        
        // Botão SEPARADO para histórico
        btnHistorico = criarBotao("📋 Ver Histórico", new Color(100, 149, 237));
        btnHistorico.addActionListener(e -> mostrarHistoricoSeparado());
        
        panelBotoes.add(btnAdicionarManual);
        panelBotoes.add(btnRemover);
        panelBotoes.add(btnLimpar);
        panelBotoes.add(btnFinalizar);
        panelBotoes.add(btnHistorico);
        
        panelBottom.add(panelBotoes, BorderLayout.CENTER);
        add(panelBottom, BorderLayout.SOUTH);
    }
    
    private JButton criarBotao(String texto, Color cor) {
        JButton botao = new JButton(texto);
        botao.setBackground(cor);
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setFont(new Font("Arial", Font.BOLD, 12));
        botao.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return botao;
    }
    
    private void configurarJanela() {
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private void criarMenu() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu menuLista = new JMenu("Lista");
        
        JMenuItem itemNovaLista = new JMenuItem("🆕 Nova Lista");
        itemNovaLista.addActionListener(e -> confirmarNovaLista());
        
        JMenuItem itemVerHistorico = new JMenuItem("📊 Ver Histórico");
        itemVerHistorico.addActionListener(e -> mostrarHistoricoSeparado());
        
        JMenuItem itemSair = new JMenuItem("🚪 Sair");
        itemSair.addActionListener(e -> System.exit(0));
        
        menuLista.add(itemNovaLista);
        menuLista.add(itemVerHistorico);
        menuLista.addSeparator();
        menuLista.add(itemSair);
        
        JMenu menuAjuda = new JMenu("Ajuda");
        JMenuItem itemSobre = new JMenuItem("ℹ️ Sobre");
        itemSobre.addActionListener(e -> mostrarSobre());
        menuAjuda.add(itemSobre);
        
        menuBar.add(menuLista);
        menuBar.add(menuAjuda);
        setJMenuBar(menuBar);
    }
    
    // ==================== MÉTODOS DA LISTA ATUAL ====================
    
    private void adicionarProdutoNaLista() {
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
            "➕ ADICIONAR PRODUTO À LISTA ATUAL", JOptionPane.OK_CANCEL_OPTION,
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
                double subtotal = valor * quantidade;
                
                // ✅ 1. Cria novo produto (NÃO salva no banco ainda)
                Produto produto = new Produto();
                produto.setNome(nome);
                produto.setValor(valor);
                produto.setQuantidade(quantidade);
                
                // ✅ 2. Adiciona à tabela (SÓ na interface)
                Object[] row = {nome, valor, quantidade, subtotal};
                tableModel.addRow(row);
                
                // ✅ 3. Adiciona à lista local (memória)
                produtosNaTela.add(produto);
                
                // ✅ 4. Atualiza estatísticas
                atualizarEstatisticas();
                
                System.out.println("✅ Produto adicionado à LISTA ATUAL: " + nome);
                System.out.println("   Quantidade: " + quantidade + " | Valor: R$ " + valor);
                System.out.println("   Total de itens na lista: " + produtosNaTela.size());
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Valor inválido! Use números (ex: 5.99)",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void removerProdutoSelecionado() {
        int selectedRow = tabelaProdutos.getSelectedRow();
        if (selectedRow >= 0) {
            String produtoNome = (String) tableModel.getValueAt(selectedRow, 0);
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Remover '" + produtoNome + "' da lista atual?",
                "Confirmar Remoção", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // ✅ Remove da tabela
                tableModel.removeRow(selectedRow);
                
                // ✅ Remove da lista local
                if (selectedRow < produtosNaTela.size()) {
                    produtosNaTela.remove(selectedRow);
                }
                
                // ✅ Atualiza estatísticas
                atualizarEstatisticas();
                
                System.out.println("✅ Produto removido da LISTA ATUAL: " + produtoNome);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Selecione um produto na tabela para remover",
                "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void limparListaAtual() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "A lista já está vazia!",
                "Lista Vazia",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            """
            Deseja limpar TODA a lista atual?
            
            \u26a0\ufe0f Esta a\u00e7\u00e3o remover\u00e1 """ + produtosNaTela.size() + " itens.\n" +
            "⚠️ Os dados NÃO serão salvos no histórico.\n" +
            "⚠️ Para salvar, use 'Finalizar Compra' primeiro.",
            "Limpar Lista Atual",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // ✅ Limpa COMPLETAMENTE (só na interface/memória)
            tableModel.setRowCount(0);
            produtosNaTela.clear();
            atualizarEstatisticas();
            
            System.out.println("\n=== LISTA ATUAL LIMPA ===");
            System.out.println("✅ Todos os itens removidos da memória");
            System.out.println("✅ Banco de dados NÃO foi afetado");
            System.out.println("✅ Nova lista iniciada do ZERO");
            
            JOptionPane.showMessageDialog(this, """
                                                \u2705 LISTA LIMPA COM SUCESSO!
                                                
                                                Todos os itens foram removidos.
                                                A lista agora est\u00e1 vazia.
                                                O hist\u00f3rico do MongoDB N\u00c3O foi afetado.""",
                "Lista Limpa",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
   private void finalizarCompraAtual() {
    if (produtosNaTela.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "A lista está vazia! Adicione produtos antes de finalizar.",
            "Lista Vazia",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    double totalCompra = calcularTotalListaAtual();
    
    // Diálogo de pagamento
    JDialog dialogPagamento = new JDialog(this, "💳 FINALIZAR COMPRA", true);
    dialogPagamento.setLayout(new BorderLayout(10, 10));
    dialogPagamento.setSize(500, 350);
    dialogPagamento.setLocationRelativeTo(this);
    
    // Painel principal
    JPanel panelMain = new JPanel(new BorderLayout(10, 10));
    panelMain.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    
    // Resumo da compra
    JPanel panelResumo = new JPanel(new BorderLayout());
    JLabel lblResumo = new JLabel("<html><center><b>📋 RESUMO DA COMPRA</b><br>" +
        "Itens: " + produtosNaTela.size() + "<br>" +
        "Total: <font color='green'><b>R$ " + String.format("%.2f", totalCompra) + 
        "</b></font></center></html>");
    lblResumo.setFont(new Font("Arial", Font.PLAIN, 14));
    panelResumo.add(lblResumo, BorderLayout.NORTH);
    
    // Forma de pagamento
    JPanel panelForma = new JPanel(new GridLayout(2, 2, 10, 10));
    panelForma.setBorder(BorderFactory.createTitledBorder("Forma de Pagamento"));
    
    JRadioButton rbDinheiro = new JRadioButton("💵 Dinheiro");
    JRadioButton rbCartao = new JRadioButton("💳 Cartão");
    JRadioButton rbPix = new JRadioButton("📱 PIX");
    JRadioButton rbOutro = new JRadioButton("🔧 Outro");
    
    ButtonGroup bgPagamento = new ButtonGroup();
    bgPagamento.add(rbDinheiro);
    bgPagamento.add(rbCartao);
    bgPagamento.add(rbPix);
    bgPagamento.add(rbOutro);
    rbDinheiro.setSelected(true);
    
    panelForma.add(rbDinheiro);
    panelForma.add(rbCartao);
    panelForma.add(rbPix);
    panelForma.add(rbOutro);
    
    // Botões
    JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
    JButton btnConfirmar = new JButton("✅ Confirmar e Salvar");
    JButton btnCancelar = new JButton("❌ Cancelar");
    
    btnConfirmar.setBackground(new Color(0, 150, 0));
    btnConfirmar.setForeground(Color.WHITE);
    btnConfirmar.setFont(new Font("Arial", Font.BOLD, 12));
    
    panelBotoes.add(btnConfirmar);
    panelBotoes.add(btnCancelar);
    
    // Montagem
    panelMain.add(panelResumo, BorderLayout.NORTH);
    panelMain.add(panelForma, BorderLayout.CENTER);
    panelMain.add(panelBotoes, BorderLayout.SOUTH);
    
    dialogPagamento.add(panelMain);
    
    // ✅ CORREÇÃO: Ações dentro do mesmo escopo
    btnConfirmar.addActionListener(e -> {
        // ✅ Determina forma de pagamento AQUI MESMO (não chama método externo)
        String formaPagamento;
        if (rbDinheiro.isSelected()) {
            formaPagamento = "DINHEIRO";
        } else if (rbCartao.isSelected()) {
            formaPagamento = "CARTAO";
        } else if (rbPix.isSelected()) {
            formaPagamento = "PIX";
        } else {
            formaPagamento = "OUTRO";
        }
        
        // ✅ Confirmação final (usa formaPagamento determinada acima)
        int confirm = JOptionPane.showConfirmDialog(dialogPagamento,
            "<html><b>CONFIRMAR FINALIZAÇÃO DA COMPRA?</b><br><br>" +
            "📊 <b>Resumo:</b><br>" +
            "• Itens: " + produtosNaTela.size() + "<br>" +
            "• Total: R$ " + String.format("%.2f", totalCompra) + "<br>" +
            "• Forma: " + formaPagamento + "<br><br>" +
            "⚠️ <b>Atenção:</b><br>" +
            "• Dados serão salvos no histórico<br>" +
            "• Lista atual será limpa<br>" +
            "• Nova lista iniciada automaticamente</html>",
            "Confirmação Final",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // ✅ Salva a compra passando formaPagamento
            salvarCompraNoHistorico(formaPagamento, totalCompra);
            dialogPagamento.dispose();
        }
    });
    
    btnCancelar.addActionListener(ev -> dialogPagamento.dispose());
    
    dialogPagamento.setVisible(true);
}
    
    private void salvarCompraNoHistorico(String formaPagamento, double totalCompra) {
        System.out.println("\n=== SALVANDO COMPRA NO HISTÓRICO ===");
        
        try {
            // ✅ 1. Salva cada produto no banco (histórico)
            for (Produto produto : produtosNaTela) {
                produtoController.adicionarProduto(
                    produto.getNome(), 
                    produto.getValor(), 
                    produto.getQuantidade()
                );
                System.out.println("   ✅ Salvo: " + produto.getNome() + 
                                " (R$ " + produto.getValor() + " x " + 
                        produto.getQuantidade() + ")");
            }
            
            // ✅ 2. Registra pagamento
            boolean pagamentoSalvo = pagamentoController.processarPagamento(
                usuarioId, totalCompra, formaPagamento);
            
            if (pagamentoSalvo) {
                // ✅ 3. Mostra comprovante
                mostrarComprovanteFinalizacao(formaPagamento, totalCompra);
                
                // ✅ 4. Limpa a lista atual (interface)
                tableModel.setRowCount(0);
                produtosNaTela.clear();
                atualizarEstatisticas();
                
                // ✅ 5. Mensagem de sucesso
                JOptionPane.showMessageDialog(this,
                    "<html><center><b>✅ COMPRA FINALIZADA COM SUCESSO!</b><br><br>" +
                    "📊 <b>Resumo salvo:</b><br>" +
                    "• Data: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()) + "<br>" +
                    "• Itens: " + produtosNaTela.size() + "<br>" +
                    "• Total: <font color='green'><b>R$ " + String.format("%.2f", totalCompra) + 
                    "</b></font><br>" +
                    "• Forma: " + formaPagamento + "<br><br>" +
                    "💾 <b>Status:</b> Dados salvos no histórico<br>" +
                    "🆕 <b>Próximo passo:</b> Nova lista iniciada</center></html>",
                    "Compra Finalizada",
                    JOptionPane.INFORMATION_MESSAGE);
                
                System.out.println("✅ Compra salva no histórico do MongoDB!");
                System.out.println("✅ Lista atual limpa");
                System.out.println("✅ Nova lista iniciada automaticamente");
                
            } else {
                throw new Exception("Erro ao salvar pagamento no histórico");
            }
            
        } catch (Exception e) {
            System.err.println("❌ ERRO ao salvar compra: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                """
                \u274c ERRO AO SALVAR COMPRA
                
                Mensagem: """ + e.getMessage() + "\n\n" +
                "⚠️ Os dados NÃO foram salvos no histórico.\n" +
                "⚠️ A lista atual permanece intacta.",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // ==================== HISTÓRICO (SEPARADO) ====================
    
    private void mostrarHistoricoSeparado() {
        System.out.println("\n=== MOSTRANDO HISTÓRICO (DIÁLOGO SEPARADO) ===");
        
        try {
            List<Produto> historico = produtoController.listarProdutos();
            
            if (historico.isEmpty()) {
                JOptionPane.showMessageDialog(this, """
                                                    \ud83d\udced Hist\u00f3rico vazio!
                                                    
                                                    Nenhuma compra registrada ainda.
                                                    Finalize uma compra para criar hist\u00f3rico.""",
                    "Histórico Vazio",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Cria diálogo separado para histórico
            JDialog dialogHistorico = new JDialog(this, "📊 HISTÓRICO DE COMPRAS", true);
            dialogHistorico.setSize(800, 500);
            dialogHistorico.setLocationRelativeTo(this);
            
            JPanel panelMain = new JPanel(new BorderLayout(10, 10));
            panelMain.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            // Tabela de histórico
            String[] colunasHist = {"Produto", "Valor Unitário", "Quantidade", "Subtotal", "Data"};
            DefaultTableModel modelHist = new DefaultTableModel(colunasHist, 0);
            
            double totalHistorico = 0;
            for (Produto produto : historico) {
                double subtotal = produto.getValor() * produto.getQuantidade();
                totalHistorico += subtotal;
                
                modelHist.addRow(new Object[]{
                    produto.getNome(),
                    String.format("R$ %.2f", produto.getValor()),
                    produto.getQuantidade(),
                    String.format("R$ %.2f", subtotal),
                    new SimpleDateFormat("dd/MM/yyyy").format(new Date())
                });
            }
            
            JTable tabelaHist = new JTable(modelHist);
            JScrollPane scrollHist = new JScrollPane(tabelaHist);
            
            // Estatísticas do histórico
            JPanel panelStats = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JLabel lblStats = new JLabel(
                "<html><b>📈 Estatísticas do Histórico:</b><br>" +
                "Compras registradas: " + historico.size() + "<br>" +
                "Total gasto: <font color='green'><b>R$ " + String.format("%.2f", totalHistorico) + 
                "</b></font></html>");
            lblStats.setFont(new Font("Arial", Font.PLAIN, 14));
            panelStats.add(lblStats);
            
            // Botão para fechar
            JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton btnFechar = new JButton("Fechar Histórico");
            btnFechar.setBackground(new Color(100, 149, 237));
            btnFechar.setForeground(Color.WHITE);
            btnFechar.addActionListener(ev -> dialogHistorico.dispose());
            panelBotoes.add(btnFechar);
            
            // Aviso importante
            JLabel lblAviso = new JLabel(
                "<html><center><font color='gray'><i>" +
                "⚠️ Este é apenas o HISTÓRICO. Para nova lista, use os botões principais." +
                "</i></font></center></html>");
            lblAviso.setFont(new Font("Arial", Font.PLAIN, 12));
            
            panelMain.add(scrollHist, BorderLayout.CENTER);
            panelMain.add(panelStats, BorderLayout.NORTH);
            panelMain.add(lblAviso, BorderLayout.SOUTH);
            panelMain.add(panelBotoes, BorderLayout.SOUTH);
            
            dialogHistorico.add(panelMain);
            dialogHistorico.setVisible(true);
            
            System.out.println("✅ Histórico mostrado em diálogo separado");
            System.out.println("   Itens no histórico: " + historico.size());
            System.out.println("   Total histórico: R$ " + totalHistorico);
            
        } catch (HeadlessException e) {
            System.err.println("❌ ERRO ao mostrar histórico: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Erro ao carregar histórico: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // ==================== MÉTODOS AUXILIARES ====================
    
    private void confirmarNovaLista() {
        if (!produtosNaTela.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "<html><b>INICIAR NOVA LISTA?</b><br><br>" +
                "A lista atual tem " + produtosNaTela.size() + " itens.<br>" +
                "Total: " + lblTotal.getText() + "<br><br>" +
                "⚠️ <b>Atenção:</b><br>" +
                "• Esta ação removerá todos os itens da tela<br>" +
                "• Os dados NÃO serão salvos automaticamente<br>" +
                "• Para salvar, use 'Finalizar Compra' primeiro<br><br>" +
                "Deseja continuar?</html>",
                "Nova Lista",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        // Limpa tudo e inicia nova lista
        tableModel.setRowCount(0);
        produtosNaTela.clear();
        atualizarEstatisticas();
        
        setTitle("🛒 MercadoScan - NOVA LISTA DE COMPRAS");
        
        System.out.println("\n=== NOVA LISTA INICIADA ===");
        System.out.println("✅ Lista anterior limpa");
        System.out.println("✅ Nova lista vazia iniciada");
        System.out.println("✅ Histórico preservado no MongoDB");
        
        JOptionPane.showMessageDialog(this,
            "<html><center><b>🆕 NOVA LISTA INICIADA!</b><br><br>" +
            "✅ Lista anterior removida da tela<br>" +
            "✅ Nova lista vazia pronta para uso<br>" +
            "✅ Histórico do MongoDB preservado<br><br>" +
            "<font color='gray'><i>Adicione novos produtos à lista atual</i></font></center></html>",
            "Nova Lista",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void atualizarEstatisticas() {
        int quantidade = produtosNaTela.size();
        double total = calcularTotalListaAtual();
        
        lblContador.setText("Itens na lista: " + quantidade);
        lblTotal.setText("Total: R$ " + String.format("%.2f", total));
        
        // Atualiza título da janela
        if (quantidade > 0) {
            setTitle("🛒 MercadoScan - Lista (" + quantidade + " itens, Total: R$ " + 
                    String.format("%.2f", total) + ")");
        } else {
            setTitle("🛒 MercadoScan - NOVA LISTA DE COMPRAS");
        }
    }
    
    private double calcularTotalListaAtual() {
        double total = 0.0;
        for (Produto produto : produtosNaTela) {
            total += produto.getValor() * produto.getQuantidade();
        }
        return total;
    }
    
    private void mostrarComprovanteFinalizacao(String formaPagamento, double total) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String dataAtual = sdf.format(new Date());
        
        String comprovante = String.format(
            """
            ========================================
                COMPROVANTE DE COMPRA - MERCADOSCAN
            ========================================
            
            Data/Hora: %s
            Usuário: %s
            Forma de Pagamento: %s
            ----------------------------------------
            Total da Compra: R$ %.2f
            Itens: %d produtos
            ----------------------------------------
            Status: ✅ COMPRA REGISTRADA NO HISTÓRICO
            ========================================
            
            💾 Dados salvos no MongoDB
            🆕 Nova lista iniciada automaticamente
            ========================================
            """,
            dataAtual,
            usuarioNome,
            formaPagamento,
            total,
            produtosNaTela.size()
        );
        
        System.out.println("\n" + comprovante);
        
        JTextArea txtComprovante = new JTextArea(comprovante);
        txtComprovante.setEditable(false);
        txtComprovante.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(txtComprovante);
        scrollPane.setPreferredSize(new Dimension(450, 350));
        
        JOptionPane.showMessageDialog(this, scrollPane, "✅ Comprovante de Compra",
                                    JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarSobre() {
        JOptionPane.showMessageDialog(this, 
            """
            <html>
            <center><b>🛒 MERCADOSCAN v2.0</b></center><br>
            
            <b>🎯 SISTEMA DE LISTAS DE COMPRAS INTELIGENTE</b><br><br>
            
            <b>✅ FLUXO CORRETO:</b><br>
            1. <b>Nova Lista</b> → Sempre começa vazia<br>
            2. <b>Adicionar</b> → Só na lista atual<br>
            3. <b>Finalizar</b> → Salva no histórico<br>
            4. <b>Histórico</b> → Visualização separada<br><br>
            
            <b>💾 SEPARAÇÃO CLARA:</b><br>
            • <b>LISTA ATUAL:</b> Memória local (tela)<br>
            • <b>HISTÓRICO:</b> Banco MongoDB<br><br>
            
            <b>📊 BENEFÍCIOS:</b><br>
            • Não mistura listas<br>
            • Histórico preservado<br>
            • Interface limpa<br>
            • Controle total<br><br>
            
            <center><font color='gray'>© 2024 MercadoScan - Vander Linux</font></center>
            </html>""",
            "ℹ️ Sobre o Sistema", 
            JOptionPane.INFORMATION_MESSAGE);
    }
   
}