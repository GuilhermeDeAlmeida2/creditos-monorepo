package br.com.guilhermedealmeidafreitas.creditos.service;

/**
 * Interface específica para operações de gerenciamento de dados de teste.
 * Segue o Interface Segregation Principle (ISP) - clientes que só precisam
 * gerenciar dados de teste não são forçados a implementar métodos de consulta.
 */
public interface TestDataManagementService {
    
    /**
     * Gera 300 registros aleatórios de teste
     * @return Número de registros gerados
     */
    int gerarRegistrosTeste();
    
    /**
     * Remove todos os registros de teste
     * @return Número de registros removidos
     */
    int deletarRegistrosTeste();
}
