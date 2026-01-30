package com.taskmanager.taskmanagerapp.service;

import com.taskmanager.taskmanagerapp.dto.request.TaskRequestDTO;
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
    public List<TaskResponseDTO> getAllTasksForUser(Long userId) {
        log.info("Fetching all tasks for user: {}", userId);
        return taskRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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
    public List<TaskResponseDTO> getTasksByStatus(Long userId, String status) {
        log.info("Fetching tasks by status {} for user {}", status, userId);

        try {
            TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
            return taskRepository.findByUserIdAndStatus(userId, taskStatus).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + status);
        }
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getTasksByPriority(Long userId, String priority) {
        log.info("Fetching tasks by priority {} for user {}", priority, userId);

        try {
            Priority taskPriority = Priority.valueOf(priority.toUpperCase());
            return taskRepository.findByUserIdAndPriority(userId, taskPriority).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid priority: " + priority);
        }
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
