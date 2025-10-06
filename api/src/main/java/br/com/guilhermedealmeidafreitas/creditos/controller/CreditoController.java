package br.com.guilhermedealmeidafreitas.creditos.controller;

import br.com.guilhermedealmeidafreitas.creditos.dto.PaginatedCreditoResponse;
import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import br.com.guilhermedealmeidafreitas.creditos.service.CreditoService;
import br.com.guilhermedealmeidafreitas.creditos.service.ControllerValidationService;
import br.com.guilhermedealmeidafreitas.creditos.config.TestFeaturesConfig;
import br.com.guilhermedealmeidafreitas.creditos.exception.CreditoNotFoundException;
import br.com.guilhermedealmeidafreitas.creditos.exception.TestDataException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/creditos")
@Tag(name = "Créditos", description = "API para gerenciamento de créditos constituídos")
public class CreditoController {
    
    private final CreditoService creditoService;
    private final TestFeaturesConfig testFeaturesConfig;
    private final ControllerValidationService validationService;
    
    /**
     * Construtor para injeção de dependências seguindo o Dependency Inversion Principle (DIP).
     * Torna as dependências explícitas e facilita testes unitários.
     */
    public CreditoController(CreditoService creditoService, 
                           TestFeaturesConfig testFeaturesConfig,
                           ControllerValidationService validationService) {
        this.creditoService = creditoService;
        this.testFeaturesConfig = testFeaturesConfig;
        this.validationService = validationService;
    }
    
    @GetMapping("/credito/{numeroCredito}")
    @Operation(
        summary = "Buscar crédito por número do crédito",
        description = "Retorna os detalhes de um crédito específico com base no número do crédito constituído"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Crédito encontrado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Credito.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Crédito não encontrado para o número informado"
        )
    })
    public ResponseEntity<Credito> buscarCreditoPorNumero(
            @Parameter(description = "Número identificador do crédito", required = true)
            @PathVariable String numeroCredito) {
        
        // Validar parâmetros usando o serviço de validação
        validationService.validateStringInput(numeroCredito, "Número do crédito");
        
        // Buscar crédito por número
        Credito credito = creditoService.buscarCreditoPorNumero(numeroCredito);
        
        if (credito == null) {
            throw new CreditoNotFoundException(numeroCredito, "número do crédito");
        }
        
        return ResponseEntity.ok(credito);
    }
    
    @GetMapping("/{numeroNfse}")
    @Operation(
        summary = "Buscar créditos por NFS-e",
        description = "Retorna uma lista de créditos constituídos com base no número da NFS-e"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de créditos encontrados",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Credito.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Nenhum crédito encontrado para o número da NFS-e informado"
        )
    })
    public ResponseEntity<List<Credito>> buscarCreditosPorNfse(
            @Parameter(description = "Número identificador da NFS-e", required = true)
            @PathVariable String numeroNfse) {
        
        // Validar parâmetros usando o serviço de validação
        validationService.validateStringInput(numeroNfse, "Número da NFS-e");
        
        // Buscar créditos por NFS-e
        List<Credito> creditos = creditoService.buscarCreditosPorNfse(numeroNfse);
        
        if (creditos.isEmpty()) {
            throw new CreditoNotFoundException(numeroNfse, "número da NFS-e");
        }
        
        return ResponseEntity.ok(creditos);
    }
    
    @GetMapping("/paginated/{numeroNfse}")
    @Operation(
        summary = "Buscar créditos por NFS-e com paginação",
        description = "Retorna uma lista paginada de créditos constituídos com base no número da NFS-e"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista paginada de créditos encontrados",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PaginatedCreditoResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Nenhum crédito encontrado para o número da NFS-e informado"
        )
    })
    public ResponseEntity<PaginatedCreditoResponse> buscarCreditosPorNfseComPaginacao(
            @Parameter(description = "Número identificador da NFS-e", required = true)
            @PathVariable String numeroNfse,
            
            @Parameter(description = "Número da página (começando em 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Tamanho da página", example = "10")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Campo para ordenação", example = "dataConstituicao")
            @RequestParam(defaultValue = "dataConstituicao") String sortBy,
            
            @Parameter(description = "Direção da ordenação (asc ou desc)", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        // Validar parâmetros usando o serviço de validação
        validationService.validateStringInput(numeroNfse, "Número da NFS-e");
        Pageable pageable = validationService.validateAndCreatePageable(page, size, sortBy, sortDirection);
        
        // Buscar créditos por NFS-e com paginação e ordenação
        PaginatedCreditoResponse response = creditoService.buscarCreditosPorNfseComPaginacao(numeroNfse, pageable);
        
        if (response.getContent().isEmpty()) {
            throw new CreditoNotFoundException(numeroNfse, "número da NFS-e");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/teste/gerar")
    @Operation(
        summary = "Gerar registros de teste",
        description = "Gera 300 registros aleatórios válidos para teste do sistema. Cria 10 NFS-e diferentes, cada uma com 30 créditos associados. Disponível apenas em ambiente de desenvolvimento."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Registros de teste gerados com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(type = "object", example = "{\"registrosGerados\": 300}")
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Funcionalidade não disponível neste ambiente"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Erro interno do servidor ao gerar registros"
        )
    })
    public ResponseEntity<?> gerarRegistrosTeste() {
        if (!testFeaturesConfig.isEnabled()) {
            throw new TestDataException("Funcionalidade de teste não está disponível neste ambiente", "gerarRegistrosTeste");
        }
        
        int registrosGerados = creditoService.gerarRegistrosTeste();
        return ResponseEntity.ok().body(new java.util.HashMap<String, Object>() {{
            put("registrosGerados", registrosGerados);
            put("mensagem", "Registros de teste gerados com sucesso");
        }});
    }
    
    @DeleteMapping("/teste/deletar")
    @Operation(
        summary = "Deletar registros de teste",
        description = "Remove todos os registros de teste (com prefixo TESTE) do sistema. Disponível apenas em ambiente de desenvolvimento."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Registros de teste deletados com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(type = "object", example = "{\"registrosDeletados\": 300}")
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Funcionalidade não disponível neste ambiente"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Erro interno do servidor ao deletar registros"
        )
    })
    public ResponseEntity<?> deletarRegistrosTeste() {
        if (!testFeaturesConfig.isEnabled()) {
            throw new TestDataException("Funcionalidade de teste não está disponível neste ambiente", "deletarRegistrosTeste");
        }
        
        int registrosDeletados = creditoService.deletarRegistrosTeste();
        return ResponseEntity.ok().body(new java.util.HashMap<String, Object>() {{
            put("registrosDeletados", registrosDeletados);
            put("mensagem", "Registros de teste deletados com sucesso");
        }});
    }
}

