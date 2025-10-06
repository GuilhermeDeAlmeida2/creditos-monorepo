package br.com.guilhermedealmeidafreitas.creditos.util;

/**
 * Utilitários para validação e parsing de tipos.
 * 
 * REFATORAÇÃO DRY: Centraliza lógica de conversão de tipos que estava
 * duplicada em múltiplos handlers, eliminando violações do princípio DRY.
 * 
 * @author Guilherme de Almeida Freitas
 */
public class ValidationUtils {
    
    /**
     * Construtor privado para evitar instanciação da classe utilitária.
     */
    private ValidationUtils() {
        throw new UnsupportedOperationException("Classe utilitária não deve ser instanciada");
    }
    
    /**
     * Converte um objeto para Integer.
     * 
     * @param value Valor a ser convertido
     * @param fieldName Nome do campo (para mensagens de erro)
     * @return Integer convertido
     * @throws IllegalArgumentException se não conseguir converter
     */
    public static int parseInteger(Object value, String fieldName) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                    String.format("Parâmetro '%s' deve ser um número inteiro", fieldName)
                );
            }
        } else {
            throw new IllegalArgumentException(
                String.format("Parâmetro '%s' deve ser um número", fieldName)
            );
        }
    }
    
    /**
     * Converte um objeto para Integer com valor padrão.
     * 
     * @param value Valor a ser convertido
     * @param fieldName Nome do campo (para mensagens de erro)
     * @param defaultValue Valor padrão se value for null
     * @return Integer convertido ou valor padrão
     * @throws IllegalArgumentException se não conseguir converter
     */
    public static int parseIntegerWithDefault(Object value, String fieldName, int defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return parseInteger(value, fieldName);
    }
    
    /**
     * Converte um objeto para String.
     * 
     * @param value Valor a ser convertido
     * @param fieldName Nome do campo (para mensagens de erro)
     * @return String convertida e trimada
     * @throws IllegalArgumentException se não conseguir converter
     */
    public static String parseString(Object value, String fieldName) {
        if (value == null) {
            return null;
        }
        if (!(value instanceof String)) {
            throw new IllegalArgumentException(
                String.format("Parâmetro '%s' deve ser uma string", fieldName)
            );
        }
        return ((String) value).trim();
    }
    
    /**
     * Converte um objeto para String com valor padrão.
     * 
     * @param value Valor a ser convertido
     * @param fieldName Nome do campo (para mensagens de erro)
     * @param defaultValue Valor padrão se value for null
     * @return String convertida e trimada ou valor padrão
     * @throws IllegalArgumentException se não conseguir converter
     */
    public static String parseStringWithDefault(Object value, String fieldName, String defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return parseString(value, fieldName);
    }
    
    /**
     * Converte um objeto para Number.
     * 
     * @param value Valor a ser convertido
     * @param fieldName Nome do campo (para mensagens de erro)
     * @return Number convertido
     * @throws IllegalArgumentException se não conseguir converter
     */
    public static Number parseNumber(Object value, String fieldName) {
        if (value instanceof Number) {
            return (Number) value;
        } else if (value instanceof String) {
            try {
                String stringValue = ((String) value).trim();
                if (stringValue.contains(".")) {
                    return Double.parseDouble(stringValue);
                } else {
                    return Integer.parseInt(stringValue);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                    String.format("Parâmetro '%s' deve ser um número", fieldName)
                );
            }
        } else {
            throw new IllegalArgumentException(
                String.format("Parâmetro '%s' deve ser um número", fieldName)
            );
        }
    }
    
    /**
     * Converte um objeto para Number com valor padrão.
     * 
     * @param value Valor a ser convertido
     * @param fieldName Nome do campo (para mensagens de erro)
     * @param defaultValue Valor padrão se value for null
     * @return Number convertido ou valor padrão
     * @throws IllegalArgumentException se não conseguir converter
     */
    public static Number parseNumberWithDefault(Object value, String fieldName, Number defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        return parseNumber(value, fieldName);
    }
    
    /**
     * Verifica se uma string é nula ou vazia.
     * 
     * @param value String a ser verificada
     * @return true se a string é nula ou vazia
     */
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
    
    /**
     * Verifica se uma string é nula, vazia ou contém apenas espaços.
     * 
     * @param value String a ser verificada
     * @return true se a string é nula, vazia ou contém apenas espaços
     */
    public static boolean isNullOrBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
    
    /**
     * Verifica se um objeto é nulo.
     * 
     * @param value Objeto a ser verificado
     * @return true se o objeto é nulo
     */
    public static boolean isNull(Object value) {
        return value == null;
    }
    
    /**
     * Aplica limites a um valor inteiro.
     * 
     * @param value Valor a ser limitado
     * @param min Valor mínimo
     * @param max Valor máximo
     * @return Valor limitado entre min e max
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }
    
    /**
     * Aplica limites a um valor double.
     * 
     * @param value Valor a ser limitado
     * @param min Valor mínimo
     * @param max Valor máximo
     * @return Valor limitado entre min e max
     */
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }
}
