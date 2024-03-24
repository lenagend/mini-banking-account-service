package com.example.minibankingaccountservice.controller;

import com.example.minibankingaccountservice.dto.User;
import com.example.minibankingaccountservice.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.minibankingaccountservice.service.AccountService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/accounts")
@CrossOrigin
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        Account createdAccount = accountService.createAccount(account);
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Account>> getAccountsByUserId(@PathVariable Long userId) {
        List<Account> accounts = accountService.getAccountsByUserId(userId);
        return ResponseEntity.ok(accounts);
    }

    // 사용자 정보 조회를 위한 컨트롤러 메소드 추가
    @GetMapping("/{accountId}/user")
    public ResponseEntity<User> getUserByAccountId(@PathVariable Long accountId) {
        User user = accountService.getUserByAccountId(accountId);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{accountId}/enroll-product")
    public ResponseEntity<Account> enrollProductToAccount(@PathVariable Long accountId, @RequestBody Map<String, Long> payload) {
        Long productId = payload.get("productId");
        Account updatedAccount = accountService.enrollProductToAccount(accountId, productId);
        return ResponseEntity.ok(updatedAccount);
    }
}
