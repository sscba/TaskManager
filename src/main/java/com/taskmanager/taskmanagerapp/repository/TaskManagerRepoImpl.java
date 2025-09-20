package com.taskmanager.taskmanagerapp.repository;

import com.taskmanager.taskmanagerapp.entity.Task;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TaskManagerRepoImpl implements TaskManagerRepository {

    List<Task> tasks;

    public TaskManagerRepoImpl() {
        this.tasks = new ArrayList<>();
    }

    //get all tasks
    public List<Task> getTasks(){
        return tasks;
    }

    //get task by id
    public Task getTask(long id){
        for(Task task : tasks){
            if(task.getId() == id){
                return task;
            }
        }
        return null;
    }

    //add task
    public String addTask(Task task){
        tasks.add(task);
        return "Task inserted successfully";
    }

    //update task
    public String updateTask(long id, Task task){
        for(Task t : tasks){
            if(t.getId() == id){
                t.setTitle(task.getTitle());
                return "Task updated successfully";
            }
        }
        return "Task not found";
    }

    //delete task
    public String deleteTask(long id){
        for(int i = 0; i<tasks.size(); i++ ){
            if(tasks.get(i).getId() == id){
                tasks.remove(i);
                return "Task deleted successfully";
            }
        }
        return "Task not found";
    }

    @Override
    public Task createTask() {
        return new Task(12345L, "Learn Spring Boot",
                "Build rest api using spring boot", false);
    }


}
