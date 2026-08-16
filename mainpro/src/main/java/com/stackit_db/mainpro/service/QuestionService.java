package com.stackit_db.mainpro.service;

import com.stackit_db.mainpro.dto.AnswerResponseDTO;
import com.stackit_db.mainpro.dto.QuestionDTO;
import com.stackit_db.mainpro.dto.QuestionResponseDTO;
import com.stackit_db.mainpro.entity.Answer;
import com.stackit_db.mainpro.entity.Question;
import com.stackit_db.mainpro.entity.User;
import com.stackit_db.mainpro.repository.QuestionRepository;
import com.stackit_db.mainpro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    @Value("${app.upload.dir:uploads/questions}")
    private String uploadDir;

    @Transactional
    public QuestionResponseDTO createQuestion(QuestionDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Title, content, and userId are required.");
        }
        return createQuestionWithFile(
                dto.getTitle(),
                dto.getContent(),
                dto.getUserId(),
                dto.getTags(),
                null
        );
    }

    @Transactional
    public QuestionResponseDTO createQuestionWithFile(
            String title,
            String content,
            Long userId,
            List<String> tags,
            MultipartFile file
    ) {
        if (!StringUtils.hasText(title) || !StringUtils.hasText(content) || userId == null) {
            throw new IllegalArgumentException("Title, content, and userId are required.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Question question = new Question();
        question.setTitle(title.trim());
        question.setContent(content.trim());
        question.setUser(user);
        question.setUpvotes(0);
        question.setTags(tags == null ? new ArrayList<>() : new ArrayList<>(tags));

        if (file != null && !file.isEmpty()) {
            question.setFileUrl(storeFile(file));
        }

        return toResponseDTO(questionRepository.save(question));
    }

    @Transactional
    public void deleteQuestion(Long questionId) {
        if (questionId == null) {
            throw new IllegalArgumentException("Question id is required.");
        }
        if (!questionRepository.existsById(questionId)) {
            throw new IllegalArgumentException("Question not found with id: " + questionId);
        }
        questionRepository.deleteById(questionId);
    }

    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getAllQuestions() {
        return questionRepository.findAll().stream().map(this::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getQuestionsByUserId(Long userId) {
        if (userId == null) throw new IllegalArgumentException("userId is required.");
        return questionRepository.findQuestionsByUserId(userId).stream().map(this::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public QuestionResponseDTO getQuestionById(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found with id: " + questionId));
        return toResponseDTO(question);
    }


    private String storeFile(MultipartFile file) {
        try {
            String originalName = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
            String safeOriginal = originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
            String storedName = UUID.randomUUID() + "_" + safeOriginal;

            Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);

            Path target = dir.resolve(storedName).normalize();
            if (!target.startsWith(dir)) {
                throw new IllegalArgumentException("Invalid file path.");
            }

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + storedName;
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to store file.");
        }
    }

    private QuestionResponseDTO toResponseDTO(Question question) {
        List<AnswerResponseDTO> answerDTOs = question.getAnswers() == null
                ? List.of()
                : question.getAnswers().stream().map(this::toAnswerResponseDTO).toList();

        int answerCount = answerDTOs.size();

        User u = question.getUser();
        String displayName = (u != null && u.getDisplayName() != null && !u.getDisplayName().isBlank())
                ? u.getDisplayName()
                : (u != null ? u.getUsername() : null);

        return new QuestionResponseDTO(
                question.getId(),
                question.getTitle(),
                question.getContent(),
                question.getTags() == null ? List.of() : new ArrayList<>(question.getTags()),
                question.getUpvotes(),
                u != null ? u.getId() : null,
                u != null ? u.getUsername() : null,
                displayName,
                u != null ? u.getReputation() : null,
                answerCount,
                answerDTOs,
                question.getFileUrl()
        );
    }

    private AnswerResponseDTO toAnswerResponseDTO(Answer answer) {
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
