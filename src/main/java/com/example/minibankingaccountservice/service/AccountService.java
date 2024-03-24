package com.example.minibankingaccountservice.service;

import com.example.minibankingaccountservice.dto.BankProduct;
import com.example.minibankingaccountservice.dto.User;
import com.example.minibankingaccountservice.entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.minibankingaccountservice.repository.AccountRepository;

import java.util.List;

@Service
public class AccountService {

    private final RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${bank.product.service.url}")
    private String bankProductServiceUrl;

    public AccountService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    private AccountRepository accountRepository;

    public Account createAccount(Account account) {
        // 계좌 생성 로직
        return accountRepository.save(account);
    }

    public List<Account> getAccountsByUserId(Long userId) {
        // 사용자 ID로 계좌 목록 조회 로직
        return accountRepository.findByUserId(userId);
    }

    public User getUserByAccountId(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account not found"));
        Long userId = account.getUserId();
        ResponseEntity<User> response = restTemplate.getForEntity(userServiceUrl + "/users/" + userId, User.class);
        return response.getBody();
    }

    public Account enrollProductToAccount(Long accountId, Long productId) {
        // 계좌 ID로 계좌 정보 조회
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));

        // 은행 상품 ID 설정
        account.setBankProductId(productId);

        // 계좌 정보 업데이트
        return accountRepository.save(account);
    }

    public BankProduct getBankProductById(Long productId) {
        ResponseEntity<BankProduct> response = restTemplate.getForEntity(bankProductServiceUrl + "/bank-products/" + productId, BankProduct.class);
        return response.getBody();
    }

    @Scheduled(fixedRate = 30000)  // 30초마다 실행
    public void applyInterestRates() {
        List<Account> accounts = accountRepository.findAll();

        for (Account account : accounts) {
            if(account.getBankProductId() != null) {
                BankProduct product = getBankProductById(account.getBankProductId());
                if(product != null) {
                    double interest = account.getBalance() * product.getInterestRate() / 100;  // 가정: 이자율이 백분율로 제공됨
                    account.setBalance(account.getBalance() + interest);
                    accountRepository.save(account);
                }
            }
        }
    }
}
