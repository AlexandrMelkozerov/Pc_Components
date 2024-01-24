package ru.melkozerovau.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.melkozerovau.entity.Component;


@Repository
public interface ComponentRepository extends JpaRepository<Component, Long> {
}
