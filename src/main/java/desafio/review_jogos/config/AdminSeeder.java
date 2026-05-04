package desafio.review_jogos.config;

import desafio.review_jogos.model.Usuario;
import desafio.review_jogos.model.enums.Role;
import desafio.review_jogos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email:admin@admin.com}")
    private String adminEmail;

    @Value("${admin.senha:admin123}")
    private String adminSenha;

    public AdminSeeder(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.findByEmail(adminEmail).isEmpty()) {
            Usuario admin = new Usuario();
            admin.setEmail(adminEmail);
            admin.setSenha(passwordEncoder.encode(adminSenha));
            admin.setRole(Role.ROLE_ADMIN);
            usuarioRepository.save(admin);
            System.out.println("Admin criado: " + adminEmail);
        }
    }
}