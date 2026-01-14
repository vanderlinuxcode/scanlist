package com.mercadoscan.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mercadoscan.dao.PIXTransactionDAO;
import com.mercadoscan.model.PIXPaymentRequest;
import com.mercadoscan.model.PIXPaymentResponse;
import com.mercadoscan.model.PIXTransaction;

@SuppressWarnings("unused")
public class PIXService {
    
    private final PIXTransactionDAO pixTransactionDAO;
    @SuppressWarnings("unused")
    private final HttpClient httpClient;
    private final Gson gson;
    
    // Configuração do PIX (em produção, use variáveis de ambiente)
    @SuppressWarnings("unused")
    private static final String PIX_PROVIDER_URL = "https://api.pagar.me/1/transactions";
    private static final String PIX_API_KEY = "ak_test_YOUR_API_KEY"; // Substitua
    private static final int PIX_EXPIRATION_MINUTES = 30;
    
    public PIXService() {
        this.pixTransactionDAO = new PIXTransactionDAO();
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
        System.out.println("✅ PIXService inicializado");
    }
    
    /**
     * Cria uma cobrança PIX
     */
    public PIXPaymentResponse createPIXPayment(PIXPaymentRequest request) {
        System.out.println("=== CRIANDO PAGAMENTO PIX ===");
        
        try {
            // 1. Gerar Transaction ID único
            String transactionId = "PIX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            
            // 2. Criar payload para a API de PIX
            JsonObject payload = new JsonObject();
            payload.addProperty("api_key", PIX_API_KEY);
            payload.addProperty("amount", request.getAmount().multiply(BigDecimal.valueOf(100)).intValue()); // Em centavos
            payload.addProperty("payment_method", "pix");
            payload.addProperty("pix_expiration_date", LocalDateTime.now()
                .plusMinutes(PIX_EXPIRATION_MINUTES)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
            
            JsonObject customer = new JsonObject();
            customer.addProperty("name", request.getPayerName());
            customer.addProperty("email", request.getPayerEmail());
            customer.addProperty("document", request.getPayerDocument());
            payload.add("customer", customer);
            
            // 3. Chamar API do provedor PIX (simulação/real)
            PIXPaymentResponse response = simulatePIXPayment(payload, transactionId, request);
            
            // 4. Salvar transação no banco
            saveTransaction(request, transactionId, response);
            
            return response;
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao criar pagamento PIX: " + e.getMessage());
            return createErrorResponse("Erro ao processar pagamento PIX");
        }
    }
    
    /**
     * Simula resposta da API PIX (para desenvolvimento)
     * Em produção, substitua por chamada real a Pagar.me, Gerencianet, etc.
     */
    private PIXPaymentResponse simulatePIXPayment(@SuppressWarnings("unused") JsonObject payload, String transactionId, 
                                                PIXPaymentRequest request) {
        PIXPaymentResponse response = new PIXPaymentResponse();
        
        try {
            // Em produção, descomente este código:
            /*
            HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(PIX_PROVIDER_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(payload)))
                .build();
            
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, 
                HttpResponse.BodyHandlers.ofString());
            
            JsonObject responseJson = gson.fromJson(httpResponse.body(), JsonObject.class);
            */
            
            // SIMULAÇÃO (remova em produção)
            System.out.println("🔧 Simulando pagamento PIX...");
            
            // Gera QR Code simulado (em produção use biblioteca como ZXing)
            String qrCodeText = generatePIXCode(
                transactionId, 
                request.getAmount(), 
                request.getPayerName()
            );
            
            // Gera imagem QR Code base64 simulada
            String qrCodeBase64 = generateMockQRCodeBase64(qrCodeText);
            
            response.setSuccess(true);
            response.setMessage("Pagamento PIX criado com sucesso");
            response.setTransactionId(transactionId);
            response.setQrCodeBase64(qrCodeBase64);
            response.setQrCodeText(qrCodeText);
            response.setPaymentUrl("pix://" + qrCodeText);
            response.setExpiration(LocalDateTime.now().plusMinutes(PIX_EXPIRATION_MINUTES));
            response.setStatus("CREATED");
            response.setAmount(request.getAmount());
            
            System.out.println("✅ PIX simulado criado: " + transactionId);
            
        } catch (Exception e) {
            System.err.println("❌ Erro na simulação PIX: " + e.getMessage());
            response.setSuccess(false);
            response.setMessage("Falha na simulação do PIX");
        }
        
        return response;
    }
    
    /**
     * Gera código PIX no formato padrão BRCode
     */
    private String generatePIXCode(String transactionId, BigDecimal amount, @SuppressWarnings("unused") String merchantName) {
        // Formato simplificado do PIX BRCode
        // Em produção, implemente conforme normas do BCB
        StringBuilder pixCode = new StringBuilder();
        
        // Código do país (BR)
        pixCode.append("000201");
        
        // Código do Merchant
        pixCode.append("26580014BR.GOV.BCB.PIX");
        
        // Chave PIX (usando transactionId como chave temporária)
        pixCode.append("0136").append(String.format("%02d", transactionId.length())).append(transactionId);
        
        // Nome do recebedor
        String name = "MercadoScan";
        pixCode.append("0104").append(String.format("%02d", name.length())).append(name);
        
        // Cidade do recebedor
        String city = "Sao Paulo";
        pixCode.append("0115").append(String.format("%02d", city.length())).append(city);
        
        // Valor (opcional)
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            String amountStr = amount.toString().replace(".", "").replace(",", "");
            pixCode.append("54").append(String.format("%02d", amountStr.length())).append(amountStr);
        }
        
        // Código do país (BR)
        pixCode.append("5802BR");
        
        // Categoria do merchant
        pixCode.append("5913MERCADOSCAN.COM");
        
        // Postal code (opcional)
        pixCode.append("6008SAOPAULO");
        
        // CRC16
        pixCode.append("6304");
        
        return pixCode.toString();
    }
    
    /**
     * Gera QR Code base64 mock (em produção use ZXing)
     */
    private String generateMockQRCodeBase64(String qrCodeText) {
        try {
            // Simula um QR Code simples
            // Em produção, use: com.google.zxing
            BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
            
            // Preenche com padrão simulado
            for (int y = 0; y < 200; y++) {
                for (int x = 0; x < 200; x++) {
                    int color = ((x / 20) + (y / 20)) % 2 == 0 ? 0x000000 : 0xFFFFFF;
                    image.setRGB(x, y, color);
                }
            }
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
            
        } catch (IOException e) {
            System.err.println("❌ Erro ao gerar QR Code mock: " + e.getMessage());
            return "";
        }
    }
    
    /**
     * Salva transação no banco
     */
    private void saveTransaction(PIXPaymentRequest request, String transactionId, 
                                PIXPaymentResponse response) {
        try {
            PIXTransaction transaction = new PIXTransaction();
            transaction.setTransactionId(transactionId);
            transaction.setUsuarioId(request.getUsuarioId());
            transaction.setAmount(request.getAmount().doubleValue());
            transaction.setDescription(request.getDescription());
            transaction.setQrCodeBase64(response.getQrCodeBase64());
            transaction.setQrCodeText(response.getQrCodeText());
            transaction.setStatus("CREATED");
            transaction.setExpiresAt(response.getExpiration());
            transaction.setPayerName(request.getPayerName());
            transaction.setPayerDocument(request.getPayerDocument());
            transaction.setPayerEmail(request.getPayerEmail());
            
            pixTransactionDAO.save(transaction);
            System.out.println("✅ Transação PIX salva no banco: " + transactionId);
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao salvar transação: " + e.getMessage());
        }
    }
    
    /**
     * Verifica status de uma transação
     */
    public PIXPaymentResponse checkPaymentStatus(String transactionId) {
        System.out.println("=== VERIFICANDO STATUS PIX ===");
        
        try {
            PIXTransaction transaction = pixTransactionDAO.findByTransactionId(transactionId);
            
            if (transaction == null) {
                return createErrorResponse("Transação não encontrada");
            }
            
            PIXPaymentResponse response = new PIXPaymentResponse();
            response.setTransactionId(transactionId);
            response.setAmount(BigDecimal.valueOf(transaction.getAmount()));
            response.setStatus(transaction.getStatus());
            
            // Simula verificação de status
            // Em produção, consulte a API do provedor PIX
            String simulatedStatus = simulateStatusCheck(transaction);
            
            if (!simulatedStatus.equals(transaction.getStatus())) {
                pixTransactionDAO.updateStatus(transactionId, simulatedStatus, null);
                response.setStatus(simulatedStatus);
            }
            
            response.setSuccess(true);
            response.setMessage("Status verificado: " + response.getStatus());
            
            return response;
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao verificar status: " + e.getMessage());
            return createErrorResponse("Erro ao verificar status");
        }
    }
    
    /**
     * Simula verificação de status
     */
    private String simulateStatusCheck(PIXTransaction transaction) {
        // Lógica de simulação
        // Em produção, consulte a API real
        LocalDateTime now = LocalDateTime.now();
        
        if (now.isAfter(transaction.getExpiresAt())) {
            return "EXPIRED";
        }
        
        // 30% de chance de estar confirmado após 1 minuto
        if (transaction.getCreatedAt() != null && 
            transaction.getCreatedAt().plusMinutes(1).isBefore(now)) {
            if (Math.random() < 0.3) {
                return "CONFIRMED";
            }
        }
        
        return transaction.getStatus();
    }
    
    /**
     * Processa webhook do provedor PIX
     */
    public boolean processWebhook(String payload) {
        try {
            JsonObject webhookData = gson.fromJson(payload, JsonObject.class);
            
            String event = webhookData.get("event").getAsString();
            String transactionId = webhookData.get("transactionId").getAsString();
            String endToEndId = webhookData.has("endToEndId") ? 
                webhookData.get("endToEndId").getAsString() : null;
            
            String status = "FAILED";
            if (null != event) switch (event) {
                case "payment.confirmed" -> status = "CONFIRMED";
                case "payment.failed" -> status = "FAILED";
                case "payment.expired" -> status = "EXPIRED";
                default -> {
                }
            }
            
            // Atualiza status no banco
            boolean updated = pixTransactionDAO.updateStatus(transactionId, status, endToEndId);
            
            if (updated && "CONFIRMED".equals(status)) {
                // Aqui você pode processar a confirmação do pagamento
                processConfirmedPayment(transactionId);
            }
            
            return updated;
            
        } catch (JsonSyntaxException e) {
            System.err.println("❌ Erro ao processar webhook: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Processa pagamento confirmado
     */
    private void processConfirmedPayment(String transactionId) {
        try {
            PIXTransaction transaction = pixTransactionDAO.findByTransactionId(transactionId);
            if (transaction != null) {
                System.out.println("💰 Pagamento PIX confirmado!");
                System.out.println("   Transaction: " + transactionId);
                System.out.println("   Usuário: " + transaction.getUsuarioId());
                System.out.println("   Valor: R$ " + transaction.getAmount());
                
                // Aqui você pode integrar com o seu sistema de produtos
                // Ex: marcar produtos como pagos, gerar comprovante, etc.
                
                // Exemplo: Chamar serviço de pagamento existente
                // PagamentoService pagamentoService = new PagamentoService(transaction.getUsuarioId());
                // pagamentoService.registrarPagamento(transaction.getAmount(), "PIX");
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao processar pagamento confirmado: " + e.getMessage());
        }
    }
    
    private PIXPaymentResponse createErrorResponse(String message) {
        PIXPaymentResponse response = new PIXPaymentResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
}