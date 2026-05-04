package desafio.review_jogos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final FiltroSeguranca filtroSeguranca;

    public SecurityConfig(FiltroSeguranca filtroSeguranca) {
        this.filtroSeguranca = filtroSeguranca;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // desabilita CSRF — APIs REST são stateless, não usam cookies de sessão
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // sem sessão no servidor
                .authorizeHttpRequests(auth -> auth
                        // Públicos — qualquer um acessa
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/registrar").permitAll()
                        .requestMatchers(HttpMethod.GET, "/jogos/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()

                        // Apenas ADMIN
                        .requestMatchers(HttpMethod.POST, "/jogos").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/jogos/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/jogos/**").hasAuthority("ROLE_ADMIN")

                        // USER ou ADMIN autenticado
                        .requestMatchers(HttpMethod.POST, "/jogos/*/reviews").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/reviews/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/reviews/**").authenticated()

                        .anyRequest().authenticated()
                )
                // Adiciona o filtro JWT antes do filtro padrão de autenticação do Spring
                .addFilterBefore(filtroSeguranca, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


