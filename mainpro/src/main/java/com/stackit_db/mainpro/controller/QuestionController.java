package com.stackit_db.mainpro.controller;

import com.stackit_db.mainpro.dto.QuestionDTO;
import com.stackit_db.mainpro.dto.QuestionResponseDTO;
import com.stackit_db.mainpro.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<?> createQuestion(@RequestBody QuestionDTO questionDTO) {
        try {
            QuestionResponseDTO savedQuestion = questionService.createQuestion(questionDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedQuestion);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<?> createQuestionMultipart(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam Long userId,
            @RequestParam(required = false) String tags,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            List<String> tagList = StringUtils.hasText(tags)
                    ? Arrays.stream(tags.split(","))
                      .map(String::trim)
                      .filter(StringUtils::hasText)
                      .toList()
                    : List.of("general");

            QuestionResponseDTO savedQuestion = questionService.createQuestionWithFile(
                    title, content, userId, tagList, file
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(savedQuestion);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping(value = "/ask", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<?> askQuestion(@RequestBody QuestionDTO questionDTO) {
        return createQuestion(questionDTO);
    }

    @GetMapping
    public ResponseEntity<List<QuestionResponseDTO>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteQuestion(@PathVariable Long id) {
        try {
            questionService.deleteQuestion(id);
            // keep 200 so your current frontend status check passes
            return ResponseEntity.ok(Map.of("message", "Question deleted"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
        }
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<QuestionResponseDTO>> getQuestionsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(questionService.getQuestionsByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(questionService.getQuestionById(id));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
        }
    }
}
