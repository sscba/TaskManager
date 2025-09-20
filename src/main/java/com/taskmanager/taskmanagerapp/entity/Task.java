package com.taskmanager.taskmanagerapp.entity;



import jakarta.persistence.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    // lifecycle hook runs before INSERT
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
