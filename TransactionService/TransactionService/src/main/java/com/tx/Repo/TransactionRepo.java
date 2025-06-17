package com.tx.Repo;


import com.tx.Model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction,Integer> {
    Optional<Transaction> findBySender(String sender);
    List<Transaction> findBySenderOrReceiver(String sender, String receiver);


}