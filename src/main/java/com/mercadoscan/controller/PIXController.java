// PIXController.java
package com.mercadoscan.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mercadoscan.model.PIXPaymentRequest;
import com.mercadoscan.model.PIXPaymentResponse;
import com.mercadoscan.service.PIXService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("unused")
public class PIXController {
    
    private final PIXService pixService;
    private final Gson gson;
    private HttpServer server;
    
    public PIXController() {
        this.pixService = new PIXService();
        this.gson = new Gson();
        System.out.println("✅ PIXController inicializado");
    }
    
    /**
     * Inicia o servidor HTTP para a API PIX
     */
    public void startServer(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        
        // Define as rotas
        server.createContext("/api/pix/create", new CreatePIXHandler());
        server.createContext("/api/pix/status", new StatusHandler());
        server.createContext("/api/pix/webhook", new WebhookHandler());
        server.createContext("/api/pix/qrcode", new QRCodeHandler());
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("🚀 Servidor PIX iniciado na porta " + port);
        System.out.println("📌 Endpoints disponíveis:");
        System.out.println("   POST   /api/pix/create   - Criar pagamento PIX");
        System.out.println("   GET    /api/pix/status   - Verificar status");
        System.out.println("   POST   /api/pix/webhook  - Webhook para notificações");
        System.out.println("   GET    /api/pix/qrcode   - Obter QR Code");
    }
    
    /**
     * Para o servidor
     */
    public void stopServer() {
        if (server != null) {
            server.stop(0);
            System.out.println("🛑 Servidor PIX parado");
        }
    }
    
    // Handlers para cada endpoint
    
    class CreatePIXHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }
            
            try {
                // Ler body da requisição
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                
                System.out.println("📥 Recebida requisição PIX: " + body);
                
                // Converter JSON para objeto
                PIXPaymentRequest request = gson.fromJson(body, PIXPaymentRequest.class);
                
                // Validar request
                if (request.getAmount() == null || request.getUsuarioId() == null) {
                    sendResponse(exchange, 400, "{\"error\":\"Campos obrigatórios faltando\"}");
                    return;
                }
                
                // Processar pagamento
                PIXPaymentResponse response = pixService.createPIXPayment(request);
                
                // Enviar resposta
                String jsonResponse = gson.toJson(response);
                sendResponse(exchange, response.isSuccess() ? 200 : 400, jsonResponse);
                
            } catch (JsonSyntaxException | IOException e) {
                System.err.println("❌ Erro no handler: " + e.getMessage());
                sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
            }
        }
    }
    
    class StatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }
            
            try {
                // Extrair transactionId da query string
                String query = exchange.getRequestURI().getQuery();
                String transactionId = extractParam(query, "transactionId");
                
                if (transactionId == null || transactionId.isEmpty()) {
                    sendResponse(exchange, 400, "{\"error\":\"transactionId é obrigatório\"}");
                    return;
                }
                
                // Verificar status
                PIXPaymentResponse response = pixService.checkPaymentStatus(transactionId);
                
                // Enviar resposta
                String jsonResponse = gson.toJson(response);
                sendResponse(exchange, response.isSuccess() ? 200 : 404, jsonResponse);
                
            } catch (IOException e) {
                System.err.println("❌ Erro no status handler: " + e.getMessage());
                sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
            }
        }
    }
    
    class WebhookHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }
            
            try {
                // Ler body
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                
                System.out.println("🔔 Webhook recebido: " + body);
                
                // Processar webhook
                boolean processed = pixService.processWebhook(body);
                
                // Enviar resposta
                String response = processed ? 
                    "{\"status\":\"processed\"}" : 
                    "{\"status\":\"error\"}";
                sendResponse(exchange, processed ? 200 : 400, response);
                
            } catch (IOException e) {
                System.err.println("❌ Erro no webhook handler: " + e.getMessage());
                sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
            }
        }
    }
    
    class QRCodeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }
            
            try {
                // Extrair transactionId da query
                String query = exchange.getRequestURI().getQuery();
                String transactionId = extractParam(query, "transactionId");
                
                if (transactionId == null || transactionId.isEmpty()) {
                    sendResponse(exchange, 400, "{\"error\":\"transactionId é obrigatório\"}");
                    return;
                }
                
                // Aqui você buscaria o QR Code do banco
                // Por enquanto, retorna um exemplo
                String html = generateQRCodeHTML(transactionId);
                
                exchange.getResponseHeaders().set("Content-Type", "text/html");
                sendResponse(exchange, 200, html);
                
            } catch (IOException e) {
                System.err.println("❌ Erro no QR Code handler: " + e.getMessage());
                sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
            }
        }
    }
    
    // Métodos auxiliares
    
    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
    
    private String extractParam(String query, String paramName) {
        if (query == null) return null;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2 && paramName.equals(keyValue[0])) {
                return keyValue[1];
            }
        }
        return null;
    }
    
    private String generateQRCodeHTML(String transactionId) {
        return "<html>" +
               "<head><title>QR Code PIX</title></head>" +
               "<body style='text-align: center; padding: 20px;'>" +
               "<h2>💰 Pagamento PIX</h2>" +
               "<p>Escaneie o QR Code abaixo para pagar</p>" +
               "<div style='padding: 20px; background: white; display: inline-block;'>" +
               "<img src='https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" + 
               "pix://" + transactionId + "' alt='QR Code'>" +
               "</div>" +
               "<p>Transaction ID: " + transactionId + "</p>" +
               "<p><small>Válido por 30 minutos</small></p>" +
               "</body></html>";
    }
}