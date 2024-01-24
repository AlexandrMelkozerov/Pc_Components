package ru.melkozerovau.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.melkozerovau.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}