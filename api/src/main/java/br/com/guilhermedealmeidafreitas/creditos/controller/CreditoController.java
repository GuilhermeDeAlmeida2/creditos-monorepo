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
            @RequestParam(defaultValue = "10") int size) {
        
        // Validar parâmetros
        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        if (size > 100) size = 100; // Limite máximo
        
        // Criar Pageable
        Pageable pageable = PageRequest.of(page, size);
        
        // Buscar créditos por NFS-e com paginação
        PaginatedCreditoResponse response = creditoService.buscarCreditosPorNfseComPaginacao(numeroNfse, pageable);
        
        if (response.getContent().isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(response);
    }
}

