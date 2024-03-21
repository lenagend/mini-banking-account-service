package service;

import dto.User;
import entity.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import repository.AccountRepository;

import java.util.List;

@Service
public class AccountService {

    private final RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

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

    public User getUserById(Long userId) {
        ResponseEntity<User> response = restTemplate.getForEntity(userServiceUrl + "/users/" + userId, User.class);
        return response.getBody();
    }
}
