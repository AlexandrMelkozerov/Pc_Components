package ru.melkozerovau.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.melkozerovau.entity.Role;


public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);

}