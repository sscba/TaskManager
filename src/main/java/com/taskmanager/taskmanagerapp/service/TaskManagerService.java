package com.taskmanager.taskmanagerapp.service;


import com.taskmanager.taskmanagerapp.entity.Task;

import java.util.List;

public interface TaskManagerService {
    Task getTask();
    Task getTask(long id);
    List<Task> getTasks();
    String addTask(Task task);
    String updateTask(long id, Task task);
    String deleteTask(long id);
}
