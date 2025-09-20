package com.taskmanager.taskmanagerapp.repository;


import com.taskmanager.taskmanagerapp.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TaskManagerRepository extends JpaRepository<Task, Long> {

}
