package br.com.guilhermedealmeidafreitas.creditos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Créditos")
                        .description("API para gerenciamento de créditos constituídos")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe de Desenvolvimento")
                                .email("dev@empresa.com")));
    }
}

