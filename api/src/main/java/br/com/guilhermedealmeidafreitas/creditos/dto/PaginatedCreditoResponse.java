package br.com.guilhermedealmeidafreitas.creditos.dto;

import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Resposta paginada de créditos")
public class PaginatedCreditoResponse {
    
    @Schema(description = "Lista de créditos da página atual")
    private List<Credito> content;
    
    @Schema(description = "Número da página atual (baseado em 0)")
    private int page;
    
    @Schema(description = "Tamanho da página")
    private int size;
    
    @Schema(description = "Total de elementos")
    private long totalElements;
    
    @Schema(description = "Total de páginas")
    private int totalPages;
    
    @Schema(description = "Indica se é a primeira página")
    private boolean first;
    
    @Schema(description = "Indica se é a última página")
    private boolean last;
    
    @Schema(description = "Indica se existe próxima página")
    private boolean hasNext;
    
    @Schema(description = "Indica se existe página anterior")
    private boolean hasPrevious;
    
    // Construtores
    public PaginatedCreditoResponse() {}
    
    public PaginatedCreditoResponse(List<Credito> content, int page, int size, long totalElements, int totalPages, 
                                   boolean first, boolean last, boolean hasNext, boolean hasPrevious) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }
    
    // Getters e Setters
    public List<Credito> getContent() {
        return content;
    }
    
    public void setContent(List<Credito> content) {
        this.content = content;
    }
    
    public int getPage() {
        return page;
    }
    
    public void setPage(int page) {
        this.page = page;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    
    public long getTotalElements() {
        return totalElements;
    }
    
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
    
    public int getTotalPages() {
        return totalPages;
    }
    
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    
    public boolean isFirst() {
        return first;
    }
    
    public void setFirst(boolean first) {
        this.first = first;
    }
    
    public boolean isLast() {
        return last;
    }
    
    public void setLast(boolean last) {
        this.last = last;
    }
    
    public boolean isHasNext() {
        return hasNext;
    }
    
    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }
    
    public boolean isHasPrevious() {
        return hasPrevious;
    }
    
    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }
}
