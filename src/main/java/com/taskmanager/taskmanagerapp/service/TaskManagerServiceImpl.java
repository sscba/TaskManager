package com.taskmanager.taskmanagerapp.service;

import com.taskmanager.taskmanagerapp.entity.Task;
import com.taskmanager.taskmanagerapp.repository.TaskManagerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TaskManagerServiceImpl implements  TaskManagerService{
    TaskManagerRepository managerRepository;

    public TaskManagerServiceImpl(TaskManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
//        System.out.println(":::::::: SERVICE IMPL BEAN INJECTED ::::::::");
    }

    public List<Task> getTask(){
        return managerRepository.findAll();
    }

    public Optional<Task> getTask(Long id){
        return managerRepository.findById(id);
    }

    public Task addTask(Task task){
        return managerRepository.save(task);
    }

    public Task updateTask(Long id,Task newTask){
        return managerRepository.findById(id)
                .map(task ->{
                    task.setTitle(newTask.getTitle());
                    task.setDescription(newTask.getDescription());
                    task.setCompleted( newTask.isCompleted());
                    return managerRepository.save(task);
                })
        .orElseThrow(() -> new RuntimeException("Task not found with id " + id));
    }

    public Task patchUpdateTask(Long id, Task newTask){
        return managerRepository.findById(id)
                .map(task -> {
                    if(newTask.getTitle() != null) task.setTitle(newTask.getTitle());
                    if(newTask.getDescription() != null) task.setDescription(newTask.getDescription());
                    return managerRepository.save(task);
                })
                .orElseThrow(() -> new RuntimeException("Task not found with id "+ id));
    }

    public void deleteTask(Long id){
        managerRepository.deleteById(id);
    }
}
