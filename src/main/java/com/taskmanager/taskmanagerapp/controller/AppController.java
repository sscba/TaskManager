package com.taskmanager.taskmanagerapp.controller;


import com.taskmanager.taskmanagerapp.entity.Task;
import com.taskmanager.taskmanagerapp.service.TaskManagerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AppController {

    TaskManagerService managerService;

    public AppController(TaskManagerService managerService) {
        this.managerService = managerService;
    }

    @GetMapping("/task")
    public List<Task> getTask(){
        return managerService.getTask();
    }

    @GetMapping("/task/{id}")
    public Optional<Task> getTask(@PathVariable long id){
        return managerService.getTask(id);
    }

    @PostMapping("/task")
    public Task addTask(@RequestBody Task task){
        return managerService.addTask(task);
    }

    @PutMapping("/task/{id}")
    public Task updateTask(@PathVariable long id, @RequestBody Task task){
        return managerService.updateTask(id,task);
    }

    @PatchMapping("/task/{id}")
    public Task patchUpdateTask(@PathVariable long id, @RequestBody Task task){
        return managerService.patchUpdateTask(id,task);
    }

    @DeleteMapping("/task/{id}")
    public void deleteTask(@PathVariable long id){
        managerService.deleteTask(id);
    }
}
