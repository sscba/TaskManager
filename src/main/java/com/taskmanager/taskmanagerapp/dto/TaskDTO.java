package com.taskmanager.taskmanagerapp.dto;

import lombok.Data;

@Data
public class TaskDTO {
    private Long id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private Long userId;
    private String username;
}
