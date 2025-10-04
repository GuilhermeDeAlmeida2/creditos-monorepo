package br.com.guilhermedealmeidafreitas.creditos.controller;

import br.com.guilhermedealmeidafreitas.creditos.dto.PaginatedCreditoResponse;
import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import br.com.guilhermedealmeidafreitas.creditos.service.CreditoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/creditos")
@Tag(name = "Créditos", description = "API para gerenciamento de créditos constituídos")
public class CreditoController {
    
    @Autowired
    private CreditoService creditoService;
    
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
        
        Credito credito = creditoService.buscarCreditoPorNumero(numeroCredito);
        
        if (credito == null) {
            return ResponseEntity.notFound().build();
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
        
        List<Credito> creditos = creditoService.buscarCreditosPorNfse(numeroNfse);
        
        if (creditos.isEmpty()) {
            return ResponseEntity.notFound().build();
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
        
        // Validar parâmetros
        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        if (size > 100) size = 100; // Limite máximo
        
        // Validar campo de ordenação
        String[] camposValidos = {
            "id", "numeroCredito", "numeroNfse", "dataConstituicao", 
            "valorIssqn", "tipoCredito", "simplesNacional", "aliquota", 
            "valorFaturado", "valorDeducao", "baseCalculo"
        };
        
        boolean campoValido = false;
        for (String campo : camposValidos) {
            if (campo.equals(sortBy)) {
                campoValido = true;
                break;
            }
        }
        
        if (!campoValido) {
            sortBy = "dataConstituicao"; // Campo padrão se inválido
        }
        
        // Validar direção da ordenação
        Sort.Direction direction;
        if ("asc".equalsIgnoreCase(sortDirection)) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }
        
        // Criar Pageable com ordenação
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        // Buscar créditos por NFS-e com paginação e ordenação
        PaginatedCreditoResponse response = creditoService.buscarCreditosPorNfseComPaginacao(numeroNfse, pageable);
        
        if (response.getContent().isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/teste/gerar")
    @Operation(
        summary = "Gerar registros de teste",
        description = "Gera 300 registros aleatórios válidos para teste do sistema. Cria 10 NFS-e diferentes, cada uma com 30 créditos associados."
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
            responseCode = "500",
            description = "Erro interno do servidor ao gerar registros"
        )
    })
    public ResponseEntity<?> gerarRegistrosTeste() {
        try {
            int registrosGerados = creditoService.gerarRegistrosTeste();
            return ResponseEntity.ok().body(new java.util.HashMap<String, Object>() {{
                put("registrosGerados", registrosGerados);
                put("mensagem", "Registros de teste gerados com sucesso");
            }});
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new java.util.HashMap<String, Object>() {{
                put("erro", "Erro ao gerar registros de teste: " + e.getMessage());
            }});
        }
    }
    
    @DeleteMapping("/teste/deletar")
    @Operation(
        summary = "Deletar registros de teste",
        description = "Remove todos os registros de teste (com prefixo TESTE) do sistema"
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
            responseCode = "500",
            description = "Erro interno do servidor ao deletar registros"
        )
    })
    public ResponseEntity<?> deletarRegistrosTeste() {
        try {
            int registrosDeletados = creditoService.deletarRegistrosTeste();
            return ResponseEntity.ok().body(new java.util.HashMap<String, Object>() {{
                put("registrosDeletados", registrosDeletados);
                put("mensagem", "Registros de teste deletados com sucesso");
            }});
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new java.util.HashMap<String, Object>() {{
                put("erro", "Erro ao deletar registros de teste: " + e.getMessage());
            }});
        }
    }
}

