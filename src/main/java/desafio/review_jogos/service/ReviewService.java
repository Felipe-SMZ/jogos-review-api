package desafio.review_jogos.service;

import desafio.review_jogos.dto.MediaNotasResponseDto;
import desafio.review_jogos.dto.ReviewRequestDto;
import desafio.review_jogos.dto.ReviewResponseDto;
import desafio.review_jogos.mapper.ReviewMapper;
import desafio.review_jogos.model.Jogo;
import desafio.review_jogos.model.Review;
import desafio.review_jogos.repository.JogoRepository;
import desafio.review_jogos.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final JogoRepository jogoRepository;

    public ReviewService(ReviewRepository reviewRepository, JogoRepository jogoRepository) {
        this.reviewRepository = reviewRepository;
        this.jogoRepository = jogoRepository;
    }

    public ReviewResponseDto salvar(Long jogoId, ReviewRequestDto dto) {
        Jogo jogo = jogoRepository.findById(jogoId)
                .orElseThrow(() -> new RuntimeException("Jogo não encontrado"));

        Review review = ReviewMapper.toEntity(dto);
        review.setJogo(jogo); // Associa o jogo encontrado

        Review reviewSalva = reviewRepository.save(review);

        return ReviewMapper.toResponse(reviewSalva);
    }

    public Page<ReviewResponseDto> listar(Long jogoId, Pageable pageable) {
        if (!jogoRepository.existsById(jogoId)) {
            throw new RuntimeException("Não foi possível listar reviews: Jogo não encontrado.");
        }

        return reviewRepository.findByJogoId(jogoId, pageable)
                .map(ReviewMapper::toResponse);
    }

    public void deletar(Long id) {
        Review reviewExiste = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Não foi possível excluir: Review com id " + id + " não encontrado."));
        reviewRepository.deleteById(id);
    }

}
