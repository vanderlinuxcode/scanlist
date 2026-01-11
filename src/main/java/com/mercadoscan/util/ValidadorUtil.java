package com.mercadoscan.util;

import java.util.InputMismatchException;

/**
 * Utilitário de validações
 */
public class ValidadorUtil {
    
    /**
     * Valida CPF
     */
    public static boolean validarCPF(String cpf) {
        if (cpf == null) return false;
        
        cpf = cpf.replaceAll("[^0-9]", "");
        
        if (cpf.length() != 11) return false;
        
        // CPFs com todos dígitos iguais são inválidos
        if (cpf.matches("(\\d)\\1{10}")) return false;
        
        try {
            char dig10, dig11;
            int sm, i, r, num, peso;
            
            // Primeiro dígito verificador
            sm = 0;
            peso = 10;
            for (i = 0; i < 9; i++) {
                num = (int)(cpf.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }
            
            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig10 = '0';
            else
                dig10 = (char)(r + 48);
            
            // Segundo dígito verificador
            sm = 0;
            peso = 11;
            for (i = 0; i < 10; i++) {
                num = (int)(cpf.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }
            
            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig11 = '0';
            else
                dig11 = (char)(r + 48);
            
            return (dig10 == cpf.charAt(9)) && (dig11 == cpf.charAt(10));
            
        } catch (InputMismatchException e) {
            return false;
        }
    }
    
    /**
     * Valida telefone brasileiro
     */
    public static boolean validarTelefone(String telefone) {
        if (telefone == null) return false;
        
        telefone = telefone.replaceAll("[^0-9]", "");
        
        // Celular brasileiro: 11 dígitos (com DDD)
        return telefone.length() == 11;
    }
    
    /**
     * Valida senha de 4 dígitos
     */
    public static boolean validarSenha4Digitos(String senha) {
        return senha != null && senha.matches("\\d{4}");
    }
    
    /**
     * Valida nome
     */
    public static boolean validarNome(String nome) {
        return nome != null && nome.trim().length() >= 2;
    }
    
    /**
     * Formata CPF
     */
    public static String formatarCPF(String cpf) {
        if (cpf == null) return "";
        
        cpf = cpf.replaceAll("[^0-9]", "");
        
        if (cpf.length() != 11) return cpf;
        
        return cpf.substring(0, 3) + "." +
               cpf.substring(3, 6) + "." +
               cpf.substring(6, 9) + "-" +
               cpf.substring(9, 11);
    }
    
    /**
     * Formata telefone
     */
    public static String formatarTelefone(String telefone) {
        if (telefone == null) return "";
        
        telefone = telefone.replaceAll("[^0-9]", "");
        
        if (telefone.length() != 11) return telefone;
        
        return "(" + telefone.substring(0, 2) + ") " +
               telefone.substring(2, 7) + "-" +
               telefone.substring(7, 11);
    }
}
