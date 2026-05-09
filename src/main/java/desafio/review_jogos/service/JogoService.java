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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JogoService {

    private final JogoRepository jogoRepository;
    private final ReviewRepository reviewRepository;

    public JogoService(JogoRepository jogoRepository, ReviewRepository reviewRepository) {
        this.jogoRepository = jogoRepository;
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    public Jogo salvar(JogoRequestDto dto) {
        //o trim para tirar espacos extras
        String nomeLimpo = dto.nome().trim();

        if (jogoRepository.existsByNomeIgnoreCase(nomeLimpo)) {
            throw new RecursoJaExisteException("O jogo '" + nomeLimpo + "' já existe.");
        }

        Jogo jogo = JogoMapper.toEntity(dto);
        jogo.setNome(nomeLimpo);
        return jogoRepository.save(jogo);
    }

    @Transactional(readOnly = true)
    public Page<JogoResponseDto> buscarTodos(Genero genero, Plataforma plataforma, Pageable pageable) {

        Specification<Jogo> spec = Specification
                .where(JogoSpecification.porGenero(genero))
                .and(JogoSpecification.porPlataforma(plataforma));

        return jogoRepository.findAll(spec, pageable)
                .map(JogoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public JogoResponseDto buscarPorId(Long id) {
        return jogoRepository.findById(id)
                .map(JogoMapper::toResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Jogo com id " + id + " não encontrado."));
    }

    @Transactional
    public void excluir(Long id) {
        Jogo jogoExiste = jogoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Jogo com id " + id + " não encontrado."));

        jogoRepository.delete(jogoExiste);
    }

    @Transactional
    public JogoResponseDto atualizar(Long id, JogoRequestDto dto) {
        Jogo jogo = jogoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Jogo com id " + id + " não encontrado."));

        String novoNome = dto.nome().trim();
        if (!jogo.getNome().equalsIgnoreCase(novoNome)
                && jogoRepository.existsByNomeIgnoreCase(novoNome)) {
            throw new RecursoJaExisteException("O jogo '" + novoNome + "' já existe.");
        }

        jogo.setNome(novoNome);
        jogo.setGenero(dto.genero());
        jogo.setPlataforma(dto.plataforma());
        if (dto.imageUrl() != null) {
            jogo.setImageUrl(dto.imageUrl());
        }

        return JogoMapper.toResponse(jogoRepository.save(jogo));
    }

    @Transactional(readOnly = true)
    public MediaNotasResponseDto buscarMediaDoJogo(Long id) {

        Jogo jogo = jogoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Jogo com id " + id + " não encontrado."
                        )
                );

        Double media = reviewRepository.calcularMediaPorJogoId(id);

        return new MediaNotasResponseDto(
                jogo.getId(),
                jogo.getNome(),
                media != null ? media : 0.0
        );
    }


}
