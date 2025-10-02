package br.com.guilhermedealmeidafreitas.creditos.controller;

import br.com.guilhermedealmeidafreitas.creditos.dto.PaginatedResponse;
import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import br.com.guilhermedealmeidafreitas.creditos.repository.CreditoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    private CreditoRepository creditoRepository;
    
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
        
        List<Credito> creditos = creditoRepository.findByNumeroNfse(numeroNfse);
        
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
                schema = @Schema(implementation = PaginatedResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Nenhum crédito encontrado para o número da NFS-e informado"
        )
    })
    public ResponseEntity<PaginatedResponse<Credito>> buscarCreditosPorNfseComPaginacao(
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
        
        // Criar Pageable com ordenação padrão por data de constituição (mais recente primeiro)
        Sort sort = Sort.by(Sort.Direction.DESC, "dataConstituicao");
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Buscar créditos por NFS-e com paginação
        Page<Credito> creditosPage = creditoRepository.findByNumeroNfse(numeroNfse, pageable);
        
        if (creditosPage.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Converter para DTO
        PaginatedResponse<Credito> response = new PaginatedResponse<>(
            creditosPage.getContent(),
            creditosPage.getNumber(),
            creditosPage.getSize(),
            creditosPage.getTotalElements(),
            creditosPage.getTotalPages(),
            creditosPage.isFirst(),
            creditosPage.isLast(),
            creditosPage.hasNext(),
            creditosPage.hasPrevious()
        );
        
        return ResponseEntity.ok(response);
    }
}

