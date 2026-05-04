package desafio.review_jogos.service;

import desafio.review_jogos.dto.MediaNotasResponseDto;
import desafio.review_jogos.dto.ReviewRequestDto;
import desafio.review_jogos.dto.ReviewResponseDto;
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

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final JogoRepository jogoRepository;

    public ReviewService(ReviewRepository reviewRepository, JogoRepository jogoRepository) {
        this.reviewRepository = reviewRepository;
        this.jogoRepository = jogoRepository;
    }

    public ReviewResponseDto salvar(Long jogoId, ReviewRequestDto dto, Usuario usuarioAutenticado) {
        Jogo jogo = jogoRepository.findById(jogoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Jogo com id " + jogoId + " não encontrado."));

        Review review = ReviewMapper.toEntity(dto);
        review.setJogo(jogo);
        review.setUsuario(usuarioAutenticado); // ← associa o dono da review

        return ReviewMapper.toResponse(reviewRepository.save(review));
    }

    public Page<ReviewResponseDto> listar(Long jogoId, Pageable pageable) {
        if (!jogoRepository.existsById(jogoId)) {
            throw new RecursoNaoEncontradoException("Jogo com id " + jogoId + " não encontrado.");
        }

        return reviewRepository.findByJogoId(jogoId, pageable)
                .map(ReviewMapper::toResponse);
    }

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
}