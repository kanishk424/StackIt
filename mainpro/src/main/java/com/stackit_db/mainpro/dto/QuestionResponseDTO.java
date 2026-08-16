package com.stackit_db.mainpro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponseDTO {
    private Long id;
    private String title;
    private String content;
    private List<String> tags;
    private Integer upvotes;
    private Long userId;
    private String username;
    private String displayName;
    private Integer reputation;
    private Integer answerCount;
    private List<AnswerResponseDTO> answers;
    private String fileUrl;
}
