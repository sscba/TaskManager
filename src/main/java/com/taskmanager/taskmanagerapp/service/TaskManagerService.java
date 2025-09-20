package com.taskmanager.taskmanagerapp.service;


import com.taskmanager.taskmanagerapp.entity.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskManagerService {
    List<Task> getTask();
    Optional<Task> getTask(Long id);
    Task addTask(Task task);
    Task updateTask(Long id,Task newTask);
    Task patchUpdateTask(Long id, Task newTask);
    void deleteTask(Long id);
}
