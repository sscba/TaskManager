package com.taskmanager.taskmanagerapp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskFilterDTO {
    private String keyword;   // Search across title & description
    private String status;    // PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    private String priority;  // LOW, MEDIUM, HIGH, URGENT
}
