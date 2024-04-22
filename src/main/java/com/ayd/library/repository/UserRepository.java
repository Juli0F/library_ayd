package com.ayd.library.repository;

import com.ayd.library.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    @Override
    List<User> findAll();
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameAndUserIdNot(String username, Long userId);
    Optional<User> findByEmailAndUserIdNot(String email, Long userId);
}
