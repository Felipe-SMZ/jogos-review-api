package desafio.review_jogos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Review de Jogos")
                        .version("1.0.0")
                        .description("API REST para gerenciamento de jogos e avaliações (reviews)")
                        .contact(new Contact()
                                .name("Seu Nome")
                                .email("seuemail@email.com")))
                // Registra o esquema de segurança Bearer JWT
                .components(new Components()
                        .addSecuritySchemes("bearer-key", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                // Aplica o esquema globalmente em todos os endpoints
                .addSecurityItem(new SecurityRequirement().addList("bearer-key"));
    }
}