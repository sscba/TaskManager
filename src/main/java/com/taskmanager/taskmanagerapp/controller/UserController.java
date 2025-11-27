package com.taskmanager.taskmanagerapp.controller;

import com.taskmanager.taskmanagerapp.auth.security.CustomUserDetails;
import com.taskmanager.taskmanagerapp.dto.TaskDTO;
import com.taskmanager.taskmanagerapp.entity.Task;
import com.taskmanager.taskmanagerapp.entity.UserDetails;
import com.taskmanager.taskmanagerapp.repository.TaskManagerRepository;
import com.taskmanager.taskmanagerapp.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@PreAuthorize("hasRole('USER')")
public class UserController {
    private UserRepository userRepository;
    private TaskManagerRepository taskManagerRepository;

    public UserController(UserRepository userRepository, TaskManagerRepository taskManagerRepository) {
        this.userRepository = userRepository;
        this.taskManagerRepository = taskManagerRepository;
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskDTO>> getMyTasks(Authentication auth){
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long userId = userDetails.getUserDetails().getId();

        List<Task> tasks = taskManagerRepository.findByAssignedUserId(userId);
        List<TaskDTO> taskDTOs = tasks.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity
                .ok(taskDTOs);
    }

    @PostMapping("/tasks")
    public ResponseEntity<?> createTask(@RequestBody TaskDTO dto, Authentication auth){
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        UserDetails user = userDetails.getUserDetails();

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus() != null ? dto.getStatus() : "PENDING");
        task.setPriority(dto.getPriority()!= null ? dto.getPriority() : "MEDIUM" );
        task.setAssignedUser(user);

        taskManagerRepository.save(task);
        return ResponseEntity.ok("Task created successfully");
    }

    @PutMapping("/task/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody TaskDTO dto, Authentication auth){
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long userId = userDetails.getUserDetails().getId();

        return taskManagerRepository.findById(id)
                .map(task -> {
                    if(!task.getAssignedUser().getId().equals(userId)){
                        return ResponseEntity.status(403).body("Access dennied");
                    }

                    if(dto.getTitle() != null) task.setTitle(dto.getTitle());
                    if(dto.getDescription() != null) task.setDescription(dto.getDescription());
                    if(dto.getStatus() != null) task.setStatus(dto.getStatus());
                    if(dto.getPriority() != null) task.setPriority(dto.getPriority());

                    taskManagerRepository.save(task);
                    return ResponseEntity.ok("Task updated successfully");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setUserId(task.getAssignedUser().getId());
        dto.setUsername(task.getAssignedUser().getUsername());
        return dto;
    }
}
