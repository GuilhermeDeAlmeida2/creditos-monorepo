package br.com.guilhermedealmeidafreitas.creditos.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PaginatedResponseTest {

    private PaginatedResponse<String> paginatedResponse;
    private List<String> content;

    @BeforeEach
    void setUp() {
        content = Arrays.asList("item1", "item2", "item3");
    }

    @Test
    void testConstrutorVazio() {
        // When
        paginatedResponse = new PaginatedResponse<>();

        // Then
        assertThat(paginatedResponse).isNotNull();
        assertThat(paginatedResponse.getContent()).isNull();
        assertThat(paginatedResponse.getPage()).isEqualTo(0);
        assertThat(paginatedResponse.getSize()).isEqualTo(0);
        assertThat(paginatedResponse.getTotalElements()).isEqualTo(0);
        assertThat(paginatedResponse.getTotalPages()).isEqualTo(0);
        assertThat(paginatedResponse.isFirst()).isFalse();
        assertThat(paginatedResponse.isLast()).isFalse();
        assertThat(paginatedResponse.isHasNext()).isFalse();
        assertThat(paginatedResponse.isHasPrevious()).isFalse();
    }

    @Test
    void testConstrutorComParametros() {
        // Given
        int page = 0;
        int size = 10;
        long totalElements = 25L;
        int totalPages = 3;
        boolean first = true;
        boolean last = false;
        boolean hasNext = true;
        boolean hasPrevious = false;

        // When
        paginatedResponse = new PaginatedResponse<>(
            content, page, size, totalElements, totalPages,
            first, last, hasNext, hasPrevious
        );

        // Then
        assertThat(paginatedResponse.getContent()).isEqualTo(content);
        assertThat(paginatedResponse.getPage()).isEqualTo(page);
        assertThat(paginatedResponse.getSize()).isEqualTo(size);
        assertThat(paginatedResponse.getTotalElements()).isEqualTo(totalElements);
        assertThat(paginatedResponse.getTotalPages()).isEqualTo(totalPages);
        assertThat(paginatedResponse.isFirst()).isEqualTo(first);
        assertThat(paginatedResponse.isLast()).isEqualTo(last);
        assertThat(paginatedResponse.isHasNext()).isEqualTo(hasNext);
        assertThat(paginatedResponse.isHasPrevious()).isEqualTo(hasPrevious);
    }

    @Test
    void testSettersEGetters() {
        // Given
        paginatedResponse = new PaginatedResponse<>();
        int page = 1;
        int size = 5;
        long totalElements = 15L;
        int totalPages = 3;
        boolean first = false;
        boolean last = false;
        boolean hasNext = true;
        boolean hasPrevious = true;

        // When
        paginatedResponse.setContent(content);
        paginatedResponse.setPage(page);
        paginatedResponse.setSize(size);
        paginatedResponse.setTotalElements(totalElements);
        paginatedResponse.setTotalPages(totalPages);
        paginatedResponse.setFirst(first);
        paginatedResponse.setLast(last);
        paginatedResponse.setHasNext(hasNext);
        paginatedResponse.setHasPrevious(hasPrevious);

        // Then
        assertThat(paginatedResponse.getContent()).isEqualTo(content);
        assertThat(paginatedResponse.getPage()).isEqualTo(page);
        assertThat(paginatedResponse.getSize()).isEqualTo(size);
        assertThat(paginatedResponse.getTotalElements()).isEqualTo(totalElements);
        assertThat(paginatedResponse.getTotalPages()).isEqualTo(totalPages);
        assertThat(paginatedResponse.isFirst()).isEqualTo(first);
        assertThat(paginatedResponse.isLast()).isEqualTo(last);
        assertThat(paginatedResponse.isHasNext()).isEqualTo(hasNext);
        assertThat(paginatedResponse.isHasPrevious()).isEqualTo(hasPrevious);
    }

    @Test
    void testContentVazio() {
        // Given
        List<String> emptyContent = Collections.emptyList();

        // When
        paginatedResponse = new PaginatedResponse<>(
            emptyContent, 0, 10, 0L, 0, true, true, false, false
        );

        // Then
        assertThat(paginatedResponse.getContent()).isEmpty();
        assertThat(paginatedResponse.getTotalElements()).isEqualTo(0);
        assertThat(paginatedResponse.getTotalPages()).isEqualTo(0);
        assertThat(paginatedResponse.isFirst()).isTrue();
        assertThat(paginatedResponse.isLast()).isTrue();
        assertThat(paginatedResponse.isHasNext()).isFalse();
        assertThat(paginatedResponse.isHasPrevious()).isFalse();
    }

    @Test
    void testPrimeiraPagina() {
        // Given
        int page = 0;
        int size = 10;
        long totalElements = 25L;
        int totalPages = 3;

        // When
        paginatedResponse = new PaginatedResponse<>(
            content, page, size, totalElements, totalPages,
            true, false, true, false
        );

        // Then
        assertThat(paginatedResponse.getPage()).isEqualTo(0);
        assertThat(paginatedResponse.isFirst()).isTrue();
        assertThat(paginatedResponse.isLast()).isFalse();
        assertThat(paginatedResponse.isHasNext()).isTrue();
        assertThat(paginatedResponse.isHasPrevious()).isFalse();
    }

    @Test
    void testUltimaPagina() {
        // Given
        int page = 2;
        int size = 10;
        long totalElements = 25L;
        int totalPages = 3;

        // When
        paginatedResponse = new PaginatedResponse<>(
            content, page, size, totalElements, totalPages,
            false, true, false, true
        );

        // Then
        assertThat(paginatedResponse.getPage()).isEqualTo(2);
        assertThat(paginatedResponse.isFirst()).isFalse();
        assertThat(paginatedResponse.isLast()).isTrue();
        assertThat(paginatedResponse.isHasNext()).isFalse();
        assertThat(paginatedResponse.isHasPrevious()).isTrue();
    }

    @Test
    void testPaginaIntermediaria() {
        // Given
        int page = 1;
        int size = 10;
        long totalElements = 25L;
        int totalPages = 3;

        // When
        paginatedResponse = new PaginatedResponse<>(
            content, page, size, totalElements, totalPages,
            false, false, true, true
        );

        // Then
        assertThat(paginatedResponse.getPage()).isEqualTo(1);
        assertThat(paginatedResponse.isFirst()).isFalse();
        assertThat(paginatedResponse.isLast()).isFalse();
        assertThat(paginatedResponse.isHasNext()).isTrue();
        assertThat(paginatedResponse.isHasPrevious()).isTrue();
    }

    @Test
    void testEquals() {
        // Given
        PaginatedResponse<String> response1 = new PaginatedResponse<>(
            content, 0, 10, 25L, 3, true, false, true, false
        );
        
        PaginatedResponse<String> response2 = new PaginatedResponse<>(
            content, 0, 10, 25L, 3, true, false, true, false
        );

        // When & Then
        assertThat(response1).isEqualTo(response2);
    }

    @Test
    void testHashCode() {
        // Given
        PaginatedResponse<String> response1 = new PaginatedResponse<>(
            content, 0, 10, 25L, 3, true, false, true, false
        );
        
        PaginatedResponse<String> response2 = new PaginatedResponse<>(
            content, 0, 10, 25L, 3, true, false, true, false
        );

        // When & Then
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        paginatedResponse = new PaginatedResponse<>(
            content, 0, 10, 25L, 3, true, false, true, false
        );

        // When
        String toString = paginatedResponse.toString();

        // Then
        assertThat(toString).isNotNull();
        assertThat(toString).contains("PaginatedResponse");
    }
}
