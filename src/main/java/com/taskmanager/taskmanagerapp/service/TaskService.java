package com.taskmanager.taskmanagerapp.service;

import com.taskmanager.taskmanagerapp.dto.request.TaskFilterDTO;
import com.taskmanager.taskmanagerapp.dto.request.TaskRequestDTO;
import com.taskmanager.taskmanagerapp.dto.response.PaginatedResponseDTO;
import com.taskmanager.taskmanagerapp.dto.response.TaskResponseDTO;
import com.taskmanager.taskmanagerapp.entity.Priority;
import com.taskmanager.taskmanagerapp.entity.Task;
import com.taskmanager.taskmanagerapp.entity.TaskStatus;
import com.taskmanager.taskmanagerapp.entity.User;
import com.taskmanager.taskmanagerapp.exception.BadRequestException;
import com.taskmanager.taskmanagerapp.exception.ResourceNotFoundException;
import com.taskmanager.taskmanagerapp.repository.TaskRepository;
import com.taskmanager.taskmanagerapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PaginatedResponseDTO<TaskResponseDTO> getAllTasksForUser(Long userId, Pageable pageable) {
        log.info("Fetching tasks for user: {}, page: {}, size: {}", userId, pageable.getPageNumber(), pageable.getPageSize());
        Page<Task> tasksPage = taskRepository.findByUserId(userId, pageable);
        Page<TaskResponseDTO> dtosPage = tasksPage.map(this::convertToDTO);
        return PaginatedResponseDTO.of(dtosPage);
    }

    @Transactional(readOnly = true)
    public TaskResponseDTO getTaskById(Long taskId, Long userId) {
        log.info("Fetching task {} for user {}", taskId, userId);

        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found with id: " + taskId + " for user: " + userId
                ));

        return convertToDTO(task);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDTO<TaskResponseDTO> getTasksByStatus(Long userId, String status, Pageable pageable) {
        log.info("Fetching tasks by status {} for user {}", status, userId);
        try {
            TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
            Page<Task> tasksPage = taskRepository.findByUserIdAndStatus(userId, taskStatus, pageable);
            Page<TaskResponseDTO> dtosPage = tasksPage.map(this::convertToDTO);
            return PaginatedResponseDTO.of(dtosPage);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + status);
        }
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDTO<TaskResponseDTO> getTasksByPriority(Long userId, String priority, Pageable pageable) {
        log.info("Fetching tasks by priority {} for user {}", priority, userId);
        try {
            Priority taskPriority = Priority.valueOf(priority.toUpperCase());
            Page<Task> tasksPage = taskRepository.findByUserIdAndPriority(userId, taskPriority, pageable);
            Page<TaskResponseDTO> dtosPage = tasksPage.map(this::convertToDTO);
            return PaginatedResponseDTO.of(dtosPage);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid priority: " + priority);
        }
    }

    // Advanced filter method: handles keyword + status + priority + pagination all at once
    @Transactional(readOnly = true)
    public PaginatedResponseDTO<TaskResponseDTO> filterTasks(Long userId, TaskFilterDTO filter, Pageable pageable) {
        log.info("Filtering tasks for user: {}, filters: {}", userId, filter);

        TaskStatus status = parseStatus(filter.getStatus());
        Priority priority = parsePriority(filter.getPriority());

        Page<Task> tasksPage = taskRepository.findByUserIdWithFilters(
                userId, status, priority, filter.getKeyword(), pageable);
        Page<TaskResponseDTO> dtosPage = tasksPage.map(this::convertToDTO);
        return PaginatedResponseDTO.of(dtosPage);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDTO<TaskResponseDTO> searchTasks(Long userId, String keyword, Pageable pageable) {
        log.info("Searching tasks with keyword: {} for user: {}", keyword, userId);
        Page<Task> tasksPage = taskRepository.searchTasksByUser(userId, keyword, pageable);
        Page<TaskResponseDTO> dtosPage = tasksPage.map(this::convertToDTO);
        return PaginatedResponseDTO.of(dtosPage);
    }



    @Transactional
    public TaskResponseDTO createTask(TaskRequestDTO request, Long userId) {
        log.info("Creating task for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(parseStatus(request.getStatus()))
                .priority(parsePriority(request.getPriority()))
                .user(user)
                .build();

        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully: {}", savedTask.getId());

        return convertToDTO(savedTask);
    }

    @Transactional
    public TaskResponseDTO updateTask(Long taskId, TaskRequestDTO request, Long userId) {
        log.info("Updating task {} for user {}", taskId, userId);

        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found with id: " + taskId + " for user: " + userId
                ));

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.setStatus(parseStatus(request.getStatus()));
        }
        if (request.getPriority() != null) {
            task.setPriority(parsePriority(request.getPriority()));
        }

        Task updatedTask = taskRepository.save(task);
        log.info("Task updated successfully: {}", updatedTask.getId());

        return convertToDTO(updatedTask);
    }

    @Transactional
    public void deleteTask(Long taskId, Long userId) {
        log.info("Deleting task {} for user {}", taskId, userId);

        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Task not found with id: " + taskId + " for user: " + userId
                ));

        taskRepository.delete(task);
        log.info("Task deleted successfully: {}", taskId);
    }

    @Transactional(readOnly = true)
    public Long getTaskCount(Long userId) {
        return taskRepository.countTasksByUserId(userId);
    }

    private TaskStatus parseStatus(String status) {
        if (status == null) return TaskStatus.PENDING;
        try {
            return TaskStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + status +
                    ". Valid values are: PENDING, IN_PROGRESS, COMPLETED, CANCELLED");
        }
    }

    private Priority parsePriority(String priority) {
        if (priority == null) return Priority.MEDIUM;
        try {
            return Priority.valueOf(priority.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid priority: " + priority +
                    ". Valid values are: LOW, MEDIUM, HIGH, URGENT");
        }
    }

    private TaskResponseDTO convertToDTO(Task task) {
        return TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus().name())
                .priority(task.getPriority().name())
                .userId(task.getUser().getId())
                .username(task.getUser().getUsername())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
