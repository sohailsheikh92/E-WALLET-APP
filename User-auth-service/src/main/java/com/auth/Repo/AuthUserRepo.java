package com.auth.Repo;

import com.auth.model.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthUserRepo extends JpaRepository<AuthUser,Integer>
{
    Optional<AuthUser> findByUsername(String username);

}
