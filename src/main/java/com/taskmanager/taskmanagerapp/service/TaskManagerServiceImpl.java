package com.taskmanager.taskmanagerapp.service;


import com.taskmanager.taskmanagerapp.entity.Task;
import com.taskmanager.taskmanagerapp.repository.TaskManagerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskManagerServiceImpl implements  TaskManagerService{
    TaskManagerRepository managerRepository;

    public TaskManagerServiceImpl(TaskManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
//        System.out.println(":::::::: SERVICE IMPL BEAN INJECTED ::::::::");
    }

    public Task getTask() {
        return managerRepository.createTask();
    }

    @Override
    public Task getTask(long id) {
        return managerRepository.getTask(id);
    }

    @Override
    public List<Task> getTasks() {
        return managerRepository.getTasks();
    }

    @Override
    public String addTask(Task task) {
        return managerRepository.addTask(task);
    }

    @Override
    public String updateTask(long id, Task task) {
        return managerRepository.updateTask(id,task);
    }

    @Override
    public String deleteTask(long id) {
        return managerRepository.deleteTask(id);
    }
}
