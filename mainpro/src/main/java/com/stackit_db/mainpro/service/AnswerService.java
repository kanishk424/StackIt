package com.stackit_db.mainpro.service;

import com.stackit_db.mainpro.dto.AnswerRequestDTO;
import com.stackit_db.mainpro.dto.AnswerResponseDTO;
import com.stackit_db.mainpro.entity.Answer;
import com.stackit_db.mainpro.entity.Question;
import com.stackit_db.mainpro.entity.User;
import com.stackit_db.mainpro.repository.AnswerRepository;
import com.stackit_db.mainpro.repository.QuestionRepository;
import com.stackit_db.mainpro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    @Transactional
    public AnswerResponseDTO postAnswer(AnswerRequestDTO dto) {
        if (dto == null
                || dto.getQuestionId() == null
                || dto.getUserId() == null
                || !StringUtils.hasText(dto.getContent())) {
            throw new IllegalArgumentException("questionId, userId, and content are required.");
        }

        Question question = questionRepository.findById(dto.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("Question not found with id: " + dto.getQuestionId()));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + dto.getUserId()));

        Answer answer = new Answer();
        answer.setContent(dto.getContent().trim());
        answer.setQuestion(question);
        answer.setUser(user);
        answer.setUpvotes(0);
        answer.setBestAnswer(false);

        Answer savedAnswer = answerRepository.save(answer);
        return toResponseDTO(savedAnswer);
    }

    @Transactional(readOnly = true)
    public List<AnswerResponseDTO> getAnswersForQuestion(Long questionId) {
        return answerRepository.findByQuestionId(questionId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional
    public AnswerResponseDTO markBestAnswer(Long answerId, Long actorUserId) {
        if (answerId == null || actorUserId == null) {
            throw new IllegalArgumentException("answerId and userId are required.");
        }

        Answer target = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("Answer not found with id: " + answerId));

        Question question = target.getQuestion();
        if (question == null || question.getUser() == null) {
            throw new IllegalArgumentException("Question owner not found.");
        }

        if (!question.getUser().getId().equals(actorUserId)) {
            throw new IllegalArgumentException("Only question owner can mark best answer.");
        }

        List<Answer> answers = answerRepository.findByQuestionId(question.getId());
        for (Answer a : answers) {
            a.setBestAnswer(a.getId().equals(answerId));
        }
        answerRepository.saveAll(answers);

        return toResponseDTO(target);
    }

    @Transactional
    public void deleteAnswer(Long answerId) {
        if (answerId == null) {
            throw new IllegalArgumentException("answerId is required.");
        }
        if (!answerRepository.existsById(answerId)) {
            throw new IllegalArgumentException("Answer not found with id: " + answerId);
        }
        answerRepository.deleteById(answerId);
    }

    private AnswerResponseDTO toResponseDTO(Answer answer) {
        return new AnswerResponseDTO(
                answer.getId(),
                answer.getContent(),
                answer.getUpvotes(),
                answer.isBestAnswer(),
                answer.getUser() != null ? answer.getUser().getUsername() : null,
                answer.getQuestion() != null ? answer.getQuestion().getId() : null,
                answer.getUser() != null ? answer.getUser().getId() : null
        );
    }
}
