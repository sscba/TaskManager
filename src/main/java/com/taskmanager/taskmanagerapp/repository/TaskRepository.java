package com.taskmanager.taskmanagerapp.repository;


import com.taskmanager.taskmanagerapp.entity.Priority;
import com.taskmanager.taskmanagerapp.entity.Task;
import com.taskmanager.taskmanagerapp.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
    List<Task> findByUserIdAndStatus(Long userId, TaskStatus status);
    List<Task> findByUserIdAndPriority(Long userId, Priority priority);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.id = :taskId")
    Optional<Task> findByIdAndUserId(@Param("taskId") Long taskId, @Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId")
    Long countTasksByUserId(@Param("userId") Long userId);
}
