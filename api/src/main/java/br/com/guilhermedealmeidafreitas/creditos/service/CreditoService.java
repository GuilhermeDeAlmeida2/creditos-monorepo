package br.com.guilhermedealmeidafreitas.creditos.service;

import br.com.guilhermedealmeidafreitas.creditos.dto.PaginatedCreditoResponse;
import br.com.guilhermedealmeidafreitas.creditos.entity.Credito;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interface principal para serviços relacionados a créditos.
 * Agrega as interfaces específicas seguindo o Interface Segregation Principle (ISP).
 * 
 * Esta interface serve como um "facade" que combina as responsabilidades específicas,
 * mas os clientes podem optar por depender apenas das interfaces específicas.
 */
public interface CreditoService extends CreditoQueryService, TestDataManagementService {
    
    // Todos os métodos são herdados das interfaces específicas.
    // Isso permite que implementações específicas sejam criadas se necessário,
    // mas mantém compatibilidade com código existente.
}
