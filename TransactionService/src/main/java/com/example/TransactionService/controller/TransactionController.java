package com.example.TransactionService.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @GetMapping("/{username}")
    public String getTransactions(@PathVariable String username) {
        return "Welcome " + username;
    }

}
