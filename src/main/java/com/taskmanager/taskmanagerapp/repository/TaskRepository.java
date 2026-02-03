package com.taskmanager.taskmanagerapp.repository;


import com.taskmanager.taskmanagerapp.entity.Priority;
import com.taskmanager.taskmanagerapp.entity.Task;
import com.taskmanager.taskmanagerapp.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // Paginated queries
    Page<Task> findByUserId(Long userId, Pageable pageable);
    Page<Task> findByUserIdAndStatus(Long userId, TaskStatus status, Pageable pageable);
    Page<Task> findByUserIdAndPriority(Long userId, Priority priority, Pageable pageable);

    // Combined filter: status AND priority together
    Page<Task> findByUserIdAndStatusAndPriority(Long userId, TaskStatus status, Priority priority, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.id = :taskId")
    Optional<Task> findByIdAndUserId(@Param("taskId") Long taskId, @Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId")
    Long countTasksByUserId(@Param("userId") Long userId);

    // Search with pagination
    // Searches across title and description fields
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND " +
            "(LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Task> searchTasksByUser(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);

    // Advanced filter: search + status + priority combined
    // Each parameter is optional using COALESCE
    // When parameter is null, condition becomes TRUE and does not filter
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:priority IS NULL OR t.priority = :priority) AND " +
            "(:keyword IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Task> findByUserIdWithFilters(
            @Param("userId") Long userId,
            @Param("status") TaskStatus status,
            @Param("priority") Priority priority,
            @Param("keyword") String keyword,
            Pageable pageable);
}
