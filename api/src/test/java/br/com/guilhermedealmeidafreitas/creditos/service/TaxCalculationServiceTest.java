package br.com.guilhermedealmeidafreitas.creditos.service;

import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para TaxCalculationService.
 * 
 * Este teste cobre todos os cenários do serviço de cálculo de impostos,
 * incluindo cálculos de ISS, base de cálculo, validações e criação de créditos.
 */
@ExtendWith(MockitoExtension.class)
class TaxCalculationServiceTest {

    private TaxCalculationService taxCalculationService;

    @BeforeEach
    void setUp() {
        taxCalculationService = new TaxCalculationService();
    }

    @Test
    @DisplayName("Deve calcular valor do ISS corretamente")
    void deveCalcularValorDoIssCorretamente() {
        // Given
        BigDecimal baseCalculo = new BigDecimal("1000.00");
        BigDecimal aliquota = new BigDecimal("5.00");

        // When
        BigDecimal result = taxCalculationService.calcularValorIssqn(baseCalculo, aliquota);

        // Then
        assertEquals(new BigDecimal("50.00"), result);
    }

    @Test
    @DisplayName("Deve calcular valor do ISS com alíquota decimal")
    void deveCalcularValorDoIssComAliquotaDecimal() {
        // Given
        BigDecimal baseCalculo = new BigDecimal("1000.00");
        BigDecimal aliquota = new BigDecimal("2.5");

        // When
        BigDecimal result = taxCalculationService.calcularValorIssqn(baseCalculo, aliquota);

        // Then
        assertEquals(new BigDecimal("25.00"), result);
    }

    @Test
    @DisplayName("Deve retornar zero quando base de cálculo é nula")
    void deveRetornarZeroQuandoBaseDeCalculoENula() {
        // Given
        BigDecimal baseCalculo = null;
        BigDecimal aliquota = new BigDecimal("5.00");

        // When
        BigDecimal result = taxCalculationService.calcularValorIssqn(baseCalculo, aliquota);

        // Then
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("Deve retornar zero quando alíquota é nula")
    void deveRetornarZeroQuandoAliquotaENula() {
        // Given
        BigDecimal baseCalculo = new BigDecimal("1000.00");
        BigDecimal aliquota = null;

        // When
        BigDecimal result = taxCalculationService.calcularValorIssqn(baseCalculo, aliquota);

        // Then
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("Deve lançar exceção quando base de cálculo é negativa")
    void deveLancarExcecaoQuandoBaseDeCalculoENegativa() {
        // Given
        BigDecimal baseCalculo = new BigDecimal("-100.00");
        BigDecimal aliquota = new BigDecimal("5.00");

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> taxCalculationService.calcularValorIssqn(baseCalculo, aliquota)
        );
        
        assertTrue(exception.getMessage().contains("Base de cálculo não pode ser negativa"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando alíquota é negativa")
    void deveLancarExcecaoQuandoAliquotaENegativa() {
        // Given
        BigDecimal baseCalculo = new BigDecimal("1000.00");
        BigDecimal aliquota = new BigDecimal("-5.00");

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> taxCalculationService.calcularValorIssqn(baseCalculo, aliquota)
        );
        
        assertTrue(exception.getMessage().contains("Alíquota não pode ser negativa"));
    }

    @Test
    @DisplayName("Deve calcular base de cálculo corretamente")
    void deveCalcularBaseDeCalculoCorretamente() {
        // Given
        BigDecimal valorFaturado = new BigDecimal("1000.00");
        BigDecimal valorDeducao = new BigDecimal("200.00");

        // When
        BigDecimal result = taxCalculationService.calcularBaseCalculo(valorFaturado, valorDeducao);

        // Then
        assertEquals(new BigDecimal("800.00"), result);
    }

    @Test
    @DisplayName("Deve calcular base de cálculo sem dedução")
    void deveCalcularBaseDeCalculoSemDeducao() {
        // Given
        BigDecimal valorFaturado = new BigDecimal("1000.00");
        BigDecimal valorDeducao = BigDecimal.ZERO;

        // When
        BigDecimal result = taxCalculationService.calcularBaseCalculo(valorFaturado, valorDeducao);

        // Then
        assertEquals(new BigDecimal("1000.00"), result);
    }

    @Test
    @DisplayName("Deve retornar zero quando valor faturado é nulo")
    void deveRetornarZeroQuandoValorFaturadoENulo() {
        // Given
        BigDecimal valorFaturado = null;
        BigDecimal valorDeducao = new BigDecimal("200.00");

        // When
        BigDecimal result = taxCalculationService.calcularBaseCalculo(valorFaturado, valorDeducao);

        // Then
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("Deve retornar zero quando valor dedução é nulo")
    void deveRetornarZeroQuandoValorDeducaoENulo() {
        // Given
        BigDecimal valorFaturado = new BigDecimal("1000.00");
        BigDecimal valorDeducao = null;

        // When
        BigDecimal result = taxCalculationService.calcularBaseCalculo(valorFaturado, valorDeducao);

        // Then
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("Deve lançar exceção quando valor faturado é negativo")
    void deveLancarExcecaoQuandoValorFaturadoENegativo() {
        // Given
        BigDecimal valorFaturado = new BigDecimal("-1000.00");
        BigDecimal valorDeducao = new BigDecimal("200.00");

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> taxCalculationService.calcularBaseCalculo(valorFaturado, valorDeducao)
        );
        
        assertTrue(exception.getMessage().contains("Valor faturado não pode ser negativo"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando valor dedução é negativo")
    void deveLancarExcecaoQuandoValorDeducaoENegativo() {
        // Given
        BigDecimal valorFaturado = new BigDecimal("1000.00");
        BigDecimal valorDeducao = new BigDecimal("-200.00");

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> taxCalculationService.calcularBaseCalculo(valorFaturado, valorDeducao)
        );
        
        assertTrue(exception.getMessage().contains("Valor dedução não pode ser negativo"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando valor dedução é maior que valor faturado")
    void deveLancarExcecaoQuandoValorDeducaoEMaiorQueValorFaturado() {
        // Given
        BigDecimal valorFaturado = new BigDecimal("1000.00");
        BigDecimal valorDeducao = new BigDecimal("1500.00");

        // When & Then
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> taxCalculationService.calcularBaseCalculo(valorFaturado, valorDeducao)
        );
        
        assertTrue(exception.getMessage().contains("Valor dedução não pode ser maior que valor faturado"));
    }

    @Test
    @DisplayName("Deve validar alíquota dentro dos limites")
    void deveValidarAliquotaDentroDosLimites() {
        // Given
        BigDecimal aliquota = new BigDecimal("5.00");
        BigDecimal limiteMinimo = new BigDecimal("0.00");
        BigDecimal limiteMaximo = new BigDecimal("100.00");

        // When
        boolean result = taxCalculationService.validarAliquota(aliquota, limiteMinimo, limiteMaximo);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Deve validar alíquota no limite mínimo")
    void deveValidarAliquotaNoLimiteMinimo() {
        // Given
        BigDecimal aliquota = new BigDecimal("0.00");
        BigDecimal limiteMinimo = new BigDecimal("0.00");
        BigDecimal limiteMaximo = new BigDecimal("100.00");

        // When
        boolean result = taxCalculationService.validarAliquota(aliquota, limiteMinimo, limiteMaximo);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Deve validar alíquota no limite máximo")
    void deveValidarAliquotaNoLimiteMaximo() {
        // Given
        BigDecimal aliquota = new BigDecimal("100.00");
        BigDecimal limiteMinimo = new BigDecimal("0.00");
        BigDecimal limiteMaximo = new BigDecimal("100.00");

        // When
        boolean result = taxCalculationService.validarAliquota(aliquota, limiteMinimo, limiteMaximo);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Deve rejeitar alíquota abaixo do limite mínimo")
    void deveRejeitarAliquotaAbaixoDoLimiteMinimo() {
        // Given
        BigDecimal aliquota = new BigDecimal("-1.00");
        BigDecimal limiteMinimo = new BigDecimal("0.00");
        BigDecimal limiteMaximo = new BigDecimal("100.00");

        // When
        boolean result = taxCalculationService.validarAliquota(aliquota, limiteMinimo, limiteMaximo);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Deve rejeitar alíquota acima do limite máximo")
    void deveRejeitarAliquotaAcimaDoLimiteMaximo() {
        // Given
        BigDecimal aliquota = new BigDecimal("101.00");
        BigDecimal limiteMinimo = new BigDecimal("0.00");
        BigDecimal limiteMaximo = new BigDecimal("100.00");

        // When
        boolean result = taxCalculationService.validarAliquota(aliquota, limiteMinimo, limiteMaximo);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Deve rejeitar alíquota nula")
    void deveRejeitarAliquotaNula() {
        // Given
        BigDecimal aliquota = null;
        BigDecimal limiteMinimo = new BigDecimal("0.00");
        BigDecimal limiteMaximo = new BigDecimal("100.00");

        // When
        boolean result = taxCalculationService.validarAliquota(aliquota, limiteMinimo, limiteMaximo);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Deve validar alíquota com limites padrão")
    void deveValidarAliquotaComLimitesPadrao() {
        // Given
        BigDecimal aliquota = new BigDecimal("5.00");

        // When
        boolean result = taxCalculationService.validarAliquota(aliquota);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Deve rejeitar alíquota nula com limites padrão")
    void deveRejeitarAliquotaNulaComLimitesPadrao() {
        // Given
        BigDecimal aliquota = null;

        // When
        boolean result = taxCalculationService.validarAliquota(aliquota);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Deve criar crédito com cálculos automáticos")
    void deveCriarCreditoComCalculosAutomaticos() {
        // Given
        String numeroCredito = "CREDITO001";
        String numeroNfse = "NFSE001";
        LocalDate dataConstituicao = LocalDate.now();
        String tipoCredito = "ISS";
        Boolean simplesNacional = false;
        BigDecimal aliquota = new BigDecimal("5.00");
        BigDecimal valorFaturado = new BigDecimal("1000.00");
        BigDecimal valorDeducao = new BigDecimal("200.00");

        // When
        Credito result = taxCalculationService.criarCreditoComCalculos(
            numeroCredito, numeroNfse, dataConstituicao, tipoCredito, 
            simplesNacional, aliquota, valorFaturado, valorDeducao
        );

        // Then
        assertNotNull(result);
        assertEquals(numeroCredito, result.getNumeroCredito());
        assertEquals(numeroNfse, result.getNumeroNfse());
        assertEquals(dataConstituicao, result.getDataConstituicao());
        assertEquals(tipoCredito, result.getTipoCredito());
        assertEquals(simplesNacional, result.getSimplesNacional());
        assertEquals(aliquota, result.getAliquota());
        assertEquals(valorFaturado, result.getValorFaturado());
        assertEquals(valorDeducao, result.getValorDeducao());
        
        // Verificar cálculos automáticos
        BigDecimal expectedBaseCalculo = new BigDecimal("800.00"); // 1000 - 200
        BigDecimal expectedValorIssqn = new BigDecimal("40.00"); // 800 * 5%
        
        assertEquals(expectedBaseCalculo, result.getBaseCalculo());
        assertEquals(expectedValorIssqn, result.getValorIssqn());
    }

    @Test
    @DisplayName("Deve criar crédito sem dedução")
    void deveCriarCreditoSemDeducao() {
        // Given
        String numeroCredito = "CREDITO002";
        String numeroNfse = "NFSE002";
        LocalDate dataConstituicao = LocalDate.now();
        String tipoCredito = "ISS";
        Boolean simplesNacional = true;
        BigDecimal aliquota = new BigDecimal("3.00");
        BigDecimal valorFaturado = new BigDecimal("1000.00");
        BigDecimal valorDeducao = BigDecimal.ZERO;

        // When
        Credito result = taxCalculationService.criarCreditoComCalculos(
            numeroCredito, numeroNfse, dataConstituicao, tipoCredito, 
            simplesNacional, aliquota, valorFaturado, valorDeducao
        );

        // Then
        assertNotNull(result);
        assertEquals(numeroCredito, result.getNumeroCredito());
        assertEquals(simplesNacional, result.getSimplesNacional());
        
        // Verificar cálculos automáticos
        BigDecimal expectedBaseCalculo = new BigDecimal("1000.00"); // 1000 - 0
        BigDecimal expectedValorIssqn = new BigDecimal("30.00"); // 1000 * 3%
        
        assertEquals(expectedBaseCalculo, result.getBaseCalculo());
        assertEquals(expectedValorIssqn, result.getValorIssqn());
    }

    @Test
    @DisplayName("Deve calcular ISS com precisão correta")
    void deveCalcularIssComPrecisaoCorreta() {
        // Given
        BigDecimal baseCalculo = new BigDecimal("1000.33");
        BigDecimal aliquota = new BigDecimal("2.33");

        // When
        BigDecimal result = taxCalculationService.calcularValorIssqn(baseCalculo, aliquota);

        // Then
        // 1000.33 * (2.33 / 100) = 1000.33 * 0.0233 = 23.307689
        // Arredondado para 2 casas decimais: 23.31
        assertEquals(new BigDecimal("23.31"), result);
    }

    @Test
    @DisplayName("Deve calcular base de cálculo com precisão correta")
    void deveCalcularBaseDeCalculoComPrecisaoCorreta() {
        // Given
        BigDecimal valorFaturado = new BigDecimal("1000.99");
        BigDecimal valorDeducao = new BigDecimal("200.11");

        // When
        BigDecimal result = taxCalculationService.calcularBaseCalculo(valorFaturado, valorDeducao);

        // Then
        assertEquals(new BigDecimal("800.88"), result);
    }

    @Test
    @DisplayName("Deve validar alíquota com limites customizados")
    void deveValidarAliquotaComLimitesCustomizados() {
        // Given
        BigDecimal aliquota = new BigDecimal("15.50");
        BigDecimal limiteMinimo = new BigDecimal("10.00");
        BigDecimal limiteMaximo = new BigDecimal("20.00");

        // When
        boolean result = taxCalculationService.validarAliquota(aliquota, limiteMinimo, limiteMaximo);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Deve rejeitar alíquota com limites customizados")
    void deveRejeitarAliquotaComLimitesCustomizados() {
        // Given
        BigDecimal aliquota = new BigDecimal("25.00");
        BigDecimal limiteMinimo = new BigDecimal("10.00");
        BigDecimal limiteMaximo = new BigDecimal("20.00");

        // When
        boolean result = taxCalculationService.validarAliquota(aliquota, limiteMinimo, limiteMaximo);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Deve criar crédito com todos os campos preenchidos")
    void deveCriarCreditoComTodosOsCamposPreenchidos() {
        // Given
        String numeroCredito = "CREDITO003";
        String numeroNfse = "NFSE003";
        LocalDate dataConstituicao = LocalDate.of(2023, 12, 25);
        String tipoCredito = "ISSQN";
        Boolean simplesNacional = false;
        BigDecimal aliquota = new BigDecimal("7.5");
        BigDecimal valorFaturado = new BigDecimal("2000.50");
        BigDecimal valorDeducao = new BigDecimal("300.25");

        // When
        Credito result = taxCalculationService.criarCreditoComCalculos(
            numeroCredito, numeroNfse, dataConstituicao, tipoCredito, 
            simplesNacional, aliquota, valorFaturado, valorDeducao
        );

        // Then
        assertNotNull(result);
        assertEquals(numeroCredito, result.getNumeroCredito());
        assertEquals(numeroNfse, result.getNumeroNfse());
        assertEquals(dataConstituicao, result.getDataConstituicao());
        assertEquals(tipoCredito, result.getTipoCredito());
        assertEquals(simplesNacional, result.getSimplesNacional());
        assertEquals(aliquota, result.getAliquota());
        assertEquals(valorFaturado, result.getValorFaturado());
        assertEquals(valorDeducao, result.getValorDeducao());
        
        // Verificar cálculos automáticos
        BigDecimal expectedBaseCalculo = new BigDecimal("1700.25"); // 2000.50 - 300.25
        BigDecimal expectedValorIssqn = new BigDecimal("127.52"); // 1700.25 * 7.5%
        
        assertEquals(expectedBaseCalculo, result.getBaseCalculo());
        assertEquals(expectedValorIssqn, result.getValorIssqn());
    }
}
