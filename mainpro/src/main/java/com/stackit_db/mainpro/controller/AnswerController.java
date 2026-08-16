package com.stackit_db.mainpro.controller;

import com.stackit_db.mainpro.dto.AnswerRequestDTO;
import com.stackit_db.mainpro.dto.AnswerResponseDTO;
import com.stackit_db.mainpro.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping
    @Transactional
    public ResponseEntity<?> createAnswer(@RequestBody AnswerRequestDTO request) {
        try {
            AnswerResponseDTO savedAnswer = answerService.postAnswer(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAnswer);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping("/post")
    @Transactional
    public ResponseEntity<?> postAnswer(@RequestBody AnswerRequestDTO request) {
        return createAnswer(request);
    }

    @PatchMapping("/{answerId}/mark-best")
    @Transactional
    public ResponseEntity<?> markBestAnswer(@PathVariable Long answerId, @RequestParam Long userId) {
        try {
            AnswerResponseDTO updated = answerService.markBestAnswer(answerId, userId);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
        }
    }

    @DeleteMapping("/{answerId}")
    @Transactional
    public ResponseEntity<?> deleteAnswer(@PathVariable Long answerId) {
        try {
            answerService.deleteAnswer(answerId);
            return ResponseEntity.ok(Map.of("message", "Answer deleted"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
        }
    }
}
