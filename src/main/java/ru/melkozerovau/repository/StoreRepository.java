package ru.melkozerovau.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.melkozerovau.entity.Store;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
}
