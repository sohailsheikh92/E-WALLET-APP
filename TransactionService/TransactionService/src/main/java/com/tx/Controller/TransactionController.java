package com.tx.Controller;

import com.tx.Model.Transaction;
import com.tx.Repo.TransactionRepo;
import com.tx.Util.Jwtutil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private Jwtutil jwtutil;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/test")
    public String test() {
        return "Server is running";
    }




    private String getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        return authentication.getName();
    }


    @PostMapping("/send")
    public ResponseEntity<?> sendMoney(HttpServletRequest request,
                                       @RequestParam String to,
                                       @RequestParam Double amount) {

        // ⛔ Step 0: Validate amount
        if (amount == null || amount <= 0) {
            return ResponseEntity.badRequest().body("❌ Invalid amount. Must be greater than 0.");
        }

        HttpHeaders headers = getHeaders(request);
        String from = getUser(); // sender

        // ✅ 1. Get sender balance
        ResponseEntity<Map> balanceResponse = restTemplate.exchange(
                "http://localhost:8083/wallet/getbalance",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        );

        Object balanceObj = balanceResponse.getBody().get("Balance");
        if (balanceObj == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Failed to retrieve balance from Wallet Service.");
        }

        Double balance = Double.parseDouble(balanceObj.toString());

        // ❌ 2. If insufficient balance
        if (balance < amount) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("❌ Transaction failed: Insufficient balance.");
        }

        // ✅ 3. Deduct from sender
        restTemplate.postForEntity(
                "http://localhost:8083/wallet/topup?amt=" + (-amount),
                new HttpEntity<>(null, headers),
                Map.class
        );

        // ✅ 4. Add to receiver
        restTemplate.postForEntity(
                "http://localhost:8083/wallet/topup?amt=" + amount + "&usernameOverride=" + to,
                new HttpEntity<>(null, headers),
                Map.class
        );

        // ✅ 5. Save transaction
        Transaction txn = new Transaction(0, from, to, amount, LocalDateTime.now());
        transactionRepo.save(txn);

        return ResponseEntity.ok("✅ Transfer successful from " + from + " to " + to);
    }




    @GetMapping("/history")
    public ResponseEntity<?> getMyTransactions(HttpServletRequest request) {
        String user = getUser();
        List<Transaction> txns = transactionRepo.findBySenderOrReceiver(user, user);
        return ResponseEntity.ok(txns);
    }


    private HttpHeaders getHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", request.getHeader("Authorization"));
        return headers;
    }

    private HttpServletRequest requestWithUser(String user, HttpServletRequest request) {
        // Ideally call auth-service to impersonate `to` user via internal logic.
        // For now, assume the Authorization token allows both actions (simulate).
        return request;
    }
}