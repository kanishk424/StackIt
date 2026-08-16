package com.stackit_db.mainpro.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
