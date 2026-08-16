package com.stackit_db.mainpro.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionDTO {
    private String title;
    private String content;
    private Long userId;
    private List<String> tags;

    // UI sync field (optional on create, mainly useful for payload compatibility)
    private Integer answerCount;
}
