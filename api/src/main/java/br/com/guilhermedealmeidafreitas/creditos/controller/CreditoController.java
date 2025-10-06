package br.com.guilhermedealmeidafreitas.creditos.controller;

import br.com.guilhermedealmeidafreitas.creditos.dto.PaginatedCreditoResponse;
import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import br.com.guilhermedealmeidafreitas.creditos.service.CreditoService;
import br.com.guilhermedealmeidafreitas.creditos.service.ValidationService;
import br.com.guilhermedealmeidafreitas.creditos.config.TestFeaturesConfig;
import br.com.guilhermedealmeidafreitas.creditos.exception.CreditoExceptions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller simplificado usando os serviços consolidados
 */
@RestController
@RequestMapping("/api/creditos")
@Tag(name = "Créditos", description = "API para gerenciamento de créditos constituídos")
public class CreditoController {
    
    private final CreditoService creditoService;
    private final ValidationService validationService;
    private final TestFeaturesConfig testFeaturesConfig;
    
    /**
     * Construtor para injeção de dependências seguindo o Dependency Inversion Principle (DIP).
     * Torna as dependências explícitas e facilita testes unitários.
     */
    public CreditoController(CreditoService creditoService, 
                           ValidationService validationService,
                           TestFeaturesConfig testFeaturesConfig) {
        this.creditoService = creditoService;
        this.validationService = validationService;
        this.testFeaturesConfig = testFeaturesConfig;
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
        
        validationService.validateStringInput(numeroCredito, "Número do crédito");
        
        Credito credito = creditoService.buscarCreditoPorNumero(numeroCredito);
        if (credito == null) {
            throw CreditoExceptions.notFound(numeroCredito, "número do crédito");
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
        
        validationService.validateStringInput(numeroNfse, "Número da NFS-e");
        
        List<Credito> creditos = creditoService.buscarCreditosPorNfse(numeroNfse);
        
        if (creditos.isEmpty()) {
            throw CreditoExceptions.notFound(numeroNfse, "número da NFS-e");
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
        
        validationService.validateStringInput(numeroNfse, "Número da NFS-e");
        Pageable pageable = validationService.validateAndCreatePageable(page, size, sortBy, sortDirection);
        
        PaginatedCreditoResponse response = creditoService.buscarCreditosPorNfseComPaginacao(numeroNfse, pageable);
        
        if (response.getContent().isEmpty()) {
            throw CreditoExceptions.notFound(numeroNfse, "número da NFS-e");
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
    public ResponseEntity<Map<String, Object>> gerarRegistrosTeste() {
        if (!testFeaturesConfig.isEnabled()) {
            throw CreditoExceptions.notAvailable("Funcionalidade de teste não disponível neste ambiente");
        }
        
        int registrosGerados = creditoService.gerarRegistrosTeste();
        return ResponseEntity.ok(Map.of(
            "registrosGerados", registrosGerados,
            "mensagem", "Registros de teste gerados com sucesso"
        ));
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
    public ResponseEntity<Map<String, Object>> deletarRegistrosTeste() {
        if (!testFeaturesConfig.isEnabled()) {
            throw CreditoExceptions.notAvailable("Funcionalidade de teste não disponível neste ambiente");
        }
        
        int registrosDeletados = creditoService.deletarRegistrosTeste();
        return ResponseEntity.ok(Map.of(
            "registrosDeletados", registrosDeletados,
            "mensagem", "Registros de teste deletados com sucesso"
        ));
    }
}
