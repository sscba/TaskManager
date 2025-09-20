package com.taskmanager.taskmanagerapp.controller;


import com.taskmanager.taskmanagerapp.entity.Task;
import com.taskmanager.taskmanagerapp.service.TaskManagerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AppController {

    TaskManagerService managerService;

    public AppController(TaskManagerService managerService) {
        this.managerService = managerService;
    }

    @GetMapping("/hello")
    public Task printHello(){
        return managerService.getTask();
    }

    @GetMapping("/task")
    public List<Task> getTask(){
        return managerService.getTasks();
    }

    @GetMapping("/task/{id}")
    public Task getTask(@PathVariable long id){
        return managerService.getTask(id);
    }

    @PostMapping("/task")
    public String addTask(@RequestBody Task task){
        return managerService.addTask(task);
    }

    @PutMapping("/task/{id}")
    public String updateTask(@PathVariable long id, @RequestBody Task task){
        return managerService.updateTask(id,task);
    }

    @DeleteMapping("/task/{id}")
    public String deleteTask(@PathVariable long id){
        return managerService.deleteTask(id);
    }
}
