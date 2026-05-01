package desafio.review_jogos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
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
                                .email("seuemail@email.com")));
    }
}