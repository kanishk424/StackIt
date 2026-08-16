package com.stackit_db.mainpro.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
}
