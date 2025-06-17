package com.wallet.Repo;

import com.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepo extends JpaRepository<Wallet,Integer> {
    Optional<Wallet> findByUsername(String username);
}
