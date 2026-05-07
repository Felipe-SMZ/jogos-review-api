package desafio.review_jogos.service;

import desafio.review_jogos.dto.JogoRequestDto;
import desafio.review_jogos.dto.JogoResponseDto;
import desafio.review_jogos.dto.MediaNotasResponseDto;
import desafio.review_jogos.exception.RecursoJaExisteException;
import desafio.review_jogos.exception.RecursoNaoEncontradoException;
import desafio.review_jogos.mapper.JogoMapper;
import desafio.review_jogos.model.Jogo;
import desafio.review_jogos.model.enums.Genero;
import desafio.review_jogos.model.enums.Plataforma;
import desafio.review_jogos.repository.JogoRepository;
import desafio.review_jogos.repository.ReviewRepository;
import desafio.review_jogos.specification.JogoSpecification;
import org.springframework.data.domain.Page;       // ✅ import correto
import org.springframework.data.domain.Pageable;   // ✅ import correto
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class JogoService {

    private final JogoRepository jogoRepository;
    private final ReviewRepository reviewRepository;

    public JogoService(JogoRepository jogoRepository, ReviewRepository reviewRepository) {
        this.jogoRepository = jogoRepository;
        this.reviewRepository = reviewRepository;
    }

    public Jogo salvar(Jogo jogo) {
        //o trim para tirar espacos extras
        String nomeLimpo = jogo.getNome().trim();

        if (jogoRepository.existsByNomeIgnoreCase(nomeLimpo)) {
            throw new RecursoJaExisteException("O jogo '" + nomeLimpo + "' já existe.");
        }

        jogo.setNome(nomeLimpo);
        return jogoRepository.save(jogo);
    }

    public Page<JogoResponseDto> buscarTodos(Genero genero, Plataforma plataforma, Pageable pageable) {

        Specification<Jogo> spec = Specification
                .where(JogoSpecification.porGenero(genero))
                .and(JogoSpecification.porPlataforma(plataforma));

        return jogoRepository.findAll(spec, pageable)
                .map(JogoMapper::toResponse);
    }

    public JogoResponseDto buscarPorId(Long id) {
        return jogoRepository.findById(id)
                .map(JogoMapper::toResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Jogo com id " + id + " não encontrado."));
    }

    public void excluir(Long id) {
        Jogo jogoExiste = jogoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Jogo com id " + id + " não encontrado."));

        jogoRepository.delete(jogoExiste);
    }

    public JogoResponseDto atualizar(Long id, JogoRequestDto dto) {
        Jogo jogo = jogoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Jogo com id " + id + " não encontrado."));

        jogo.setNome(dto.nome());
        jogo.setGenero(dto.genero());
        jogo.setPlataforma(dto.plataforma());

        return JogoMapper.toResponse(jogoRepository.save(jogo));
    }

    public MediaNotasResponseDto buscarMediaDoJogo(Long id) {

        Jogo jogo = jogoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Jogo com id " + id + " não encontrado."
                        )
                );

        Double media = reviewRepository.calcularMediaPorJogoId(id);

        if (media == null) {
            media = 0.0;
        }

        return new MediaNotasResponseDto(
                jogo.getId(),
                jogo.getNome(),
                media
        );
    }


}
