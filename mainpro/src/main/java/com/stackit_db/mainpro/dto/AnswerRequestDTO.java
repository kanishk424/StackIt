package com.stackit_db.mainpro.dto;

import lombok.Data;

@Data
public class AnswerRequestDTO {
    private Long questionId;
    private Long userId;
    private String content;
}
