package br.com.guilhermedealmeidafreitas.creditos.constants;

/**
 * Constantes centralizadas para mensagens de erro do sistema.
 * 
 * REFATORAÇÃO DRY: Centraliza todas as mensagens de erro que estavam
 * espalhadas pelos handlers, eliminando duplicação e melhorando a
 * consistência das mensagens em todo o sistema.
 * 
 * @author Guilherme de Almeida Freitas
 */
public class ErrorMessages {
    
    // ==================== VALIDAÇÃO DE STRINGS ====================
    
    /**
     * Mensagem para campo string que não pode estar vazio.
     */
    public static final String STRING_CANNOT_BE_EMPTY = "Campo '%s' não pode ser vazio";
    
    /**
     * Mensagem para campo que deve ser uma string.
     */
    public static final String STRING_MUST_BE_STRING = "Campo '%s' deve ser uma string";
    
    /**
     * Mensagem para campo string opcional que está vazio.
     */
    public static final String STRING_OPTIONAL_EMPTY = "Campo '%s' é opcional e está vazio";
    
    // ==================== VALIDAÇÃO DE NÚMEROS ====================
    
    /**
     * Mensagem para campo que deve ser um número positivo.
     */
    public static final String NUMBER_MUST_BE_POSITIVE = "Campo '%s' deve ser um número positivo";
    
    /**
     * Mensagem para campo que deve ser um número inteiro.
     */
    public static final String NUMBER_MUST_BE_INTEGER = "Campo '%s' deve ser um número inteiro";
    
    /**
     * Mensagem para campo que deve estar dentro de um range.
     */
    public static final String NUMBER_OUT_OF_RANGE = "Campo '%s' deve estar entre %d e %d";
    
    /**
     * Mensagem para parâmetro min que deve ser menor ou igual a max.
     */
    public static final String MIN_MUST_BE_LESS_OR_EQUAL_MAX = "Parâmetro 'min' deve ser menor ou igual a 'max'";
    
    // ==================== VALIDAÇÃO DE PAGINAÇÃO ====================
    
    /**
     * Mensagem para campo de ordenação inválido.
     */
    public static final String INVALID_SORT_FIELD = "Campo de ordenação '%s' não é válido. Campos válidos: %s";
    
    /**
     * Mensagem para direção de ordenação inválida.
     */
    public static final String INVALID_SORT_DIRECTION = "Direção de ordenação deve ser 'ASC' ou 'DESC'";
    
    /**
     * Mensagem para parâmetro sortDirection que deve ser ASC ou DESC.
     */
    public static final String SORT_DIRECTION_MUST_BE_ASC_OR_DESC = "Parâmetro 'sortDirection' deve ser 'ASC' ou 'DESC'";
    
    /**
     * Mensagem para campo de ordenação que deve ser string.
     */
    public static final String SORT_FIELD_MUST_BE_STRING = "Campo de ordenação deve ser uma string";
    
    /**
     * Mensagem para direção de ordenação que deve ser string.
     */
    public static final String SORT_DIRECTION_MUST_BE_STRING = "Direção de ordenação deve ser uma string";
    
    // ==================== MENSAGENS DE SUCESSO ====================
    
    /**
     * Mensagem para página não especificada.
     */
    public static final String PAGE_NOT_SPECIFIED = "Página não especificada, usando padrão";
    
    /**
     * Mensagem para página negativa corrigida.
     */
    public static final String PAGE_NEGATIVE_CORRECTED = "Página negativa corrigida para 0";
    
    /**
     * Mensagem para página validada com sucesso.
     */
    public static final String PAGE_VALIDATED_SUCCESS = "Página validada com sucesso";
    
    /**
     * Mensagem para tamanho não especificado.
     */
    public static final String SIZE_NOT_SPECIFIED = "Tamanho não especificado, usando padrão";
    
    /**
     * Mensagem para tamanho inválido corrigido.
     */
    public static final String SIZE_INVALID_CORRECTED = "Tamanho inválido corrigido para padrão";
    
    /**
     * Mensagem para tamanho que excedeu limite.
     */
    public static final String SIZE_EXCEEDED_LIMIT = "Tamanho excedeu limite, corrigido para máximo";
    
    /**
     * Mensagem para tamanho validado com sucesso.
     */
    public static final String SIZE_VALIDATED_SUCCESS = "Tamanho validado com sucesso";
    
    /**
     * Mensagem para campo de ordenação não especificado.
     */
    public static final String SORT_FIELD_NOT_SPECIFIED = "Campo de ordenação não especificado, usando padrão";
    
    /**
     * Mensagem para campo de ordenação validado com sucesso.
     */
    public static final String SORT_FIELD_VALIDATED_SUCCESS = "Campo de ordenação validado com sucesso";
    
    /**
     * Mensagem para direção de ordenação não especificada.
     */
    public static final String SORT_DIRECTION_NOT_SPECIFIED = "Direção de ordenação não especificada, usando padrão";
    
    /**
     * Mensagem para direção de ordenação validada com sucesso.
     */
    public static final String SORT_DIRECTION_VALIDATED_SUCCESS = "Direção de ordenação validada com sucesso";
    
    /**
     * Mensagem para parâmetros de paginação validados com sucesso.
     */
    public static final String PAGEABLE_VALIDATED_SUCCESS = "Parâmetros de paginação validados com sucesso";
    
    /**
     * Mensagem para erro na validação de parâmetros de paginação.
     */
    public static final String PAGEABLE_VALIDATION_ERROR = "Erro na validação de parâmetros de paginação: %s";
    
    // ==================== MENSAGENS GENÉRICAS ====================
    
    /**
     * Mensagem para tipo de validação não suportado.
     */
    public static final String VALIDATION_TYPE_NOT_SUPPORTED = "Tipo de validação não suportado: %s";
    
    /**
     * Mensagem para campo opcional.
     */
    public static final String FIELD_IS_OPTIONAL = "Campo '%s' é opcional";
    
    // ==================== MÉTODOS UTILITÁRIOS ====================
    
    /**
     * Formata uma mensagem com os argumentos fornecidos.
     * 
     * @param template Template da mensagem com placeholders %s
     * @param args Argumentos para substituir os placeholders
     * @return Mensagem formatada
     */
    public static String format(String template, Object... args) {
        return String.format(template, args);
    }
    
    /**
     * Formata uma mensagem de erro para campo string vazio.
     * 
     * @param fieldName Nome do campo
     * @return Mensagem formatada
     */
    public static String stringCannotBeEmpty(String fieldName) {
        return format(STRING_CANNOT_BE_EMPTY, fieldName);
    }
    
    /**
     * Formata uma mensagem de erro para campo que deve ser string.
     * 
     * @param fieldName Nome do campo
     * @return Mensagem formatada
     */
    public static String stringMustBeString(String fieldName) {
        return format(STRING_MUST_BE_STRING, fieldName);
    }
    
    /**
     * Formata uma mensagem de erro para campo que deve ser número positivo.
     * 
     * @param fieldName Nome do campo
     * @return Mensagem formatada
     */
    public static String numberMustBePositive(String fieldName) {
        return format(NUMBER_MUST_BE_POSITIVE, fieldName);
    }
    
    /**
     * Formata uma mensagem de erro para campo de ordenação inválido.
     * 
     * @param sortField Campo de ordenação inválido
     * @param validFields Campos válidos
     * @return Mensagem formatada
     */
    public static String invalidSortField(String sortField, String validFields) {
        return format(INVALID_SORT_FIELD, sortField, validFields);
    }
    
    /**
     * Formata uma mensagem de erro para tipo de validação não suportado.
     * 
     * @param validationType Tipo de validação
     * @return Mensagem formatada
     */
    public static String validationTypeNotSupported(String validationType) {
        return format(VALIDATION_TYPE_NOT_SUPPORTED, validationType);
    }
    
    /**
     * Formata uma mensagem de erro para parâmetros de paginação.
     * 
     * @param errorMessage Mensagem de erro original
     * @return Mensagem formatada
     */
    public static String pageableValidationError(String errorMessage) {
        return format(PAGEABLE_VALIDATION_ERROR, errorMessage);
    }
    
    /**
     * Construtor privado para evitar instanciação da classe utilitária.
     */
    private ErrorMessages() {
        throw new UnsupportedOperationException("Classe utilitária não deve ser instanciada");
    }
}

