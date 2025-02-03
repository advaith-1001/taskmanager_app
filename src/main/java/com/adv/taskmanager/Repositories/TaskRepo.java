package com.adv.taskmanager.Repositories;

import com.adv.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepo extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId AND t.status = 'COMPLETED' " +
            "AND t.createdAt BETWEEN :startDate AND :endDate")
    Long countCompletedTasks(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId " +
            "AND t.status = 'COMPLETED' " +
            "AND t.completedAt <= t.dueDateTime " +
            "AND t.completedAt BETWEEN :startDate AND :endDate")
    Long countOnTimeCompletedTasks(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId AND t.status = 'COMPLETED' " +
            "AND t.completedAt > t.dueDateTime " +
            "AND t.completedAt BETWEEN :startDate AND :endDate")
    Long countLateCompletedTasks(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

}
