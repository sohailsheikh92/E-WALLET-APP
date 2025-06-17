package com.wallet.Controller;

import com.wallet.Repo.WalletRepo;
import com.wallet.model.Wallet;
import com.wallet.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
//java.util.Collections
import java.util.Collections;
@CrossOrigin(origins = { "http://localhost:5500", "http://127.0.0.1:5500" })
@RestController
@RequestMapping("/wallet")
public class WalletController {
    @Autowired
    private WalletRepo walletRepo;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping("/topup")
    public ResponseEntity<?> topupWallet(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam Double amt,
            @RequestParam(required = false) String usernameOverride  // optional
    ) {
        System.out.println("🔐 AUTH HEADER RECEIVED: " + authHeader);
        String jwt = authHeader.substring(7);
        String actualUser = jwtUtil.extractUsername(jwt);

        String username = (usernameOverride != null) ? usernameOverride : actualUser;

        Wallet wallet = walletRepo.findByUsername(username)
                .orElse(new Wallet(0, username, 0.0));  // if not exists, create new

        wallet.setAmount(wallet.getAmount() + amt);

        Wallet updatedWallet = walletRepo.save(wallet); // ✅ SAVE TO DB

        return ResponseEntity.ok(Collections.singletonMap("amount", updatedWallet.getAmount()));
        // ✅ return wallet as JSON
    }



    @GetMapping("/getbalance")
    public ResponseEntity<?>getBalance(@RequestHeader("Authorization")String authHeader){
        String jwt=authHeader.substring(7);
        String username =jwtUtil.extractUsername(jwt);

        Wallet wallet=walletRepo.findByUsername(username).orElse(new Wallet(0, username,0.0));

        return ResponseEntity.ok(Collections.singletonMap("Balance",wallet.getAmount()));
    }
}
