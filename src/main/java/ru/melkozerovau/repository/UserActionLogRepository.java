package ru.melkozerovau.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.melkozerovau.entity.UserActionLog;

public interface UserActionLogRepository extends JpaRepository<UserActionLog, Long> {
}
