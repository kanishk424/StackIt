package com.stackit_db.mainpro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponseDTO {
    private Long id;
    private String content;
    private Integer upvotes;
    private boolean bestAnswer;
    private String username;
    private Long questionId;
    private Long userId; // needed for owner-check in UI
}
