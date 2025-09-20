package com.taskmanager.taskmanagerapp.repository;


import com.taskmanager.taskmanagerapp.entity.Task;

import java.util.List;

public interface TaskManagerRepository {
    Task createTask();
    List<Task> getTasks();
    Task getTask(long id);
    String addTask(Task task);
    String updateTask(long id, Task task);
    String deleteTask(long id);
}
