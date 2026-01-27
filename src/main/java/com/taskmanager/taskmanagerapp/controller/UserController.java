package com.taskmanager.taskmanagerapp.controller;

import com.taskmanager.taskmanagerapp.auth.security.CustomUserDetails;
import com.taskmanager.taskmanagerapp.dto.ApiResponseDTO;
import com.taskmanager.taskmanagerapp.dto.TaskRequestDTO;
import com.taskmanager.taskmanagerapp.dto.TaskResponseDTO;
import com.taskmanager.taskmanagerapp.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/user")
@PreAuthorize("hasRole('USER')")
@RequiredArgsConstructor
public class UserController {

    private final TaskService taskService;

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        List<TaskResponseDTO> tasks = taskService.getAllTasksForUser(userId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        TaskResponseDTO task = taskService.getTaskById(id, userId);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/tasks/status/{status}")
    public ResponseEntity<List<TaskResponseDTO>> getTasksByStatus(
            @PathVariable String status,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        List<TaskResponseDTO> tasks = taskService.getTasksByStatus(userId, status);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks/priority/{priority}")
    public ResponseEntity<List<TaskResponseDTO>> getTasksByPriority(
            @PathVariable String priority,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        List<TaskResponseDTO> tasks = taskService.getTasksByPriority(userId, priority);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks/count")
    public ResponseEntity<Long> getTaskCount(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        Long count = taskService.getTaskCount(userId);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskResponseDTO> createTask(
            @Valid @RequestBody TaskRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        TaskResponseDTO task = taskService.createTask(request, userId);
        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        TaskResponseDTO task = taskService.updateTask(id, request, userId);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<ApiResponseDTO> deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        taskService.deleteTask(id, userId);
        return ResponseEntity.ok(ApiResponseDTO.success("Task deleted successfully"));
    }
}
