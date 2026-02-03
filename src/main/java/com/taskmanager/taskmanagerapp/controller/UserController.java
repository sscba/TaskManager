package com.taskmanager.taskmanagerapp.controller;

import com.taskmanager.taskmanagerapp.dto.request.TaskFilterDTO;
import com.taskmanager.taskmanagerapp.dto.response.PaginatedResponseDTO;
import com.taskmanager.taskmanagerapp.security.CustomUserDetails;
import com.taskmanager.taskmanagerapp.dto.response.ApiResponseDTO;
import com.taskmanager.taskmanagerapp.dto.request.TaskRequestDTO;
import com.taskmanager.taskmanagerapp.dto.response.TaskResponseDTO;
import com.taskmanager.taskmanagerapp.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
@Tag(name = "User - Task Management", description = "Endpoints for users to manage their personal tasks")
public class UserController {

    private final TaskService taskService;

    @GetMapping("/tasks")
    @Operation(summary = "Get all tasks with pagination and sorting")
    public ResponseEntity<PaginatedResponseDTO<TaskResponseDTO>> getAllTasks(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long userId = userDetails.getUser().getId();
        PaginatedResponseDTO<TaskResponseDTO> tasks = taskService.getAllTasksForUser(userId, pageable);
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
    @Operation(summary = "Get tasks by status with pagination")
    public ResponseEntity<PaginatedResponseDTO<TaskResponseDTO>> getTasksByStatus(
            @Parameter(description = "Status: PENDING, IN_PROGRESS, COMPLETED, CANCELLED") @PathVariable String status,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long userId = userDetails.getUser().getId();
        PaginatedResponseDTO<TaskResponseDTO> tasks = taskService.getTasksByStatus(userId, status, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks/priority/{priority}")
    @Operation(summary = "Get tasks by priority with pagination")
    public ResponseEntity<PaginatedResponseDTO<TaskResponseDTO>> getTasksByPriority(
            @Parameter(description = "Priority: LOW, MEDIUM, HIGH, URGENT") @PathVariable String priority,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long userId = userDetails.getUser().getId();
        PaginatedResponseDTO<TaskResponseDTO> tasks = taskService.getTasksByPriority(userId, priority,pageable
        );
        return ResponseEntity.ok(tasks);
    }

    // search tasks by keyword
    @GetMapping("/tasks/search")
    @Operation(summary = "Search tasks by keyword", description = "Searches in title and description")
    public ResponseEntity<PaginatedResponseDTO<TaskResponseDTO>> searchTasks(
            @Parameter(description = "Search keyword", example = "spring") @RequestParam String keyword,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long userId = userDetails.getUser().getId();
        PaginatedResponseDTO<TaskResponseDTO> tasks = taskService.searchTasks(userId, keyword, pageable);
        return ResponseEntity.ok(tasks);
    }

    // New endpoint: advanced filter combining keyword + status + priority
    @GetMapping("/tasks/filter")
    @Operation(summary = "Advanced filter tasks", description = "Combine keyword, status, and priority filters. All params are optional.")
    public ResponseEntity<PaginatedResponseDTO<TaskResponseDTO>> filterTasks(
            @Parameter(description = "Search keyword") @RequestParam(required = false) String keyword,
            @Parameter(description = "Task status") @RequestParam(required = false) String status,
            @Parameter(description = "Task priority") @RequestParam(required = false) String priority,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long userId = userDetails.getUser().getId();
        PaginatedResponseDTO<TaskResponseDTO> filteredTasks = taskService.filterTasks(userId,new TaskFilterDTO(keyword,status,priority),pageable);
        return ResponseEntity.ok(filteredTasks);
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
