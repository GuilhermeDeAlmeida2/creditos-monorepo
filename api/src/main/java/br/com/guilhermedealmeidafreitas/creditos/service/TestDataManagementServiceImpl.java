package br.com.guilhermedealmeidafreitas.creditos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementação específica para operações de gerenciamento de dados de teste.
 * Demonstra o Interface Segregation Principle (ISP) - esta implementação
 * só precisa implementar métodos de teste, não métodos de consulta.
 */
@Service
public class TestDataManagementServiceImpl implements TestDataManagementService {
    
    private final TestDataGeneratorService testDataGeneratorService;
    
    /**
     * Construtor para injeção de dependências seguindo o Dependency Inversion Principle (DIP).
     * Torna as dependências explícitas e facilita testes unitários.
     */
    public TestDataManagementServiceImpl(TestDataGeneratorService testDataGeneratorService) {
        this.testDataGeneratorService = testDataGeneratorService;
    }
    
    @Override
    public int gerarRegistrosTeste() {
        return testDataGeneratorService.gerarRegistrosTeste();
    }
    
    @Override
    public int deletarRegistrosTeste() {
        return testDataGeneratorService.deletarRegistrosTeste();
    }
}
