package com.stackit_db.mainpro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping({"/dashboard", "/dashboard.html"})
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping({"/auth", "/auth.html"})
    public String auth() {
        return "auth";
    }

    @GetMapping({"/explore", "/explore.html"})
    public String explore() {
        return "explore";
    }

    @GetMapping({"/question-detail", "/question-detail.html"})
    public String questionDetail() {
        return "question-detail";
    }
}
