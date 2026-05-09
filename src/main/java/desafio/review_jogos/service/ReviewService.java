package desafio.review_jogos.service;

import desafio.review_jogos.dto.ReviewRequestDto;
import desafio.review_jogos.dto.ReviewResponseDto;
import desafio.review_jogos.exception.RecursoJaExisteException;
import desafio.review_jogos.exception.RecursoNaoEncontradoException;
import desafio.review_jogos.mapper.ReviewMapper;
import desafio.review_jogos.model.Jogo;
import desafio.review_jogos.model.Review;
import desafio.review_jogos.model.Usuario;
import desafio.review_jogos.repository.JogoRepository;
import desafio.review_jogos.repository.ReviewRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final JogoRepository jogoRepository;

    public ReviewService(ReviewRepository reviewRepository, JogoRepository jogoRepository) {
        this.reviewRepository = reviewRepository;
        this.jogoRepository = jogoRepository;
    }

    @Transactional
    public ReviewResponseDto salvar(Long jogoId, ReviewRequestDto dto, Usuario usuarioAutenticado) {
        Jogo jogo = jogoRepository.findById(jogoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Jogo com id " + jogoId + " não encontrado."));

        if (reviewRepository.existsByJogoIdAndUsuarioId(jogoId, usuarioAutenticado.getId())) {
            throw new RecursoJaExisteException("Você já possui uma review para este jogo.");
        }

        Review review = ReviewMapper.toEntity(dto);
        review.setJogo(jogo);
        review.setUsuario(usuarioAutenticado);

        return ReviewMapper.toResponse(reviewRepository.save(review));
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> listar(Long jogoId, Pageable pageable) {
        if (!jogoRepository.existsById(jogoId)) {
            throw new RecursoNaoEncontradoException("Jogo com id " + jogoId + " não encontrado.");
        }

        return reviewRepository.findByJogoId(jogoId, pageable)
                .map(ReviewMapper::toResponse);
    }

    @Transactional
    public void deletar(Long id, Usuario usuarioAutenticado) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Review com id " + id + " não encontrada."));

        boolean ehDono = review.getUsuario().getId().equals(usuarioAutenticado.getId());
        boolean ehAdmin = usuarioAutenticado.getRole().name().equals("ROLE_ADMIN");

        if (!ehDono && !ehAdmin) {
            throw new AccessDeniedException("Você não tem permissão para deletar esta review.");
        }

        reviewRepository.deleteById(id);
    }

    @Transactional
    public ReviewResponseDto atualizar(Long id, ReviewRequestDto dto, Usuario usuarioAutenticado) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Review com id " + id + " não encontrada."));

        if (!review.getUsuario().getId().equals(usuarioAutenticado.getId())) {
            throw new AccessDeniedException("Você não tem permissão para editar esta review.");
        }

        if (dto.nota() != null) {
            review.setNota(dto.nota());
        }
        if (dto.comentario() != null && !dto.comentario().isBlank()) {
            review.setComentario(dto.comentario());
        }

        return ReviewMapper.toResponse(reviewRepository.save(review));
    }
}