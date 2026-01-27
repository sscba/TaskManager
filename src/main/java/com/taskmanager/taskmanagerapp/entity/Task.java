package com.taskmanager.taskmanagerapp.entity;



import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue( strategy = GenerationType.SEQUENCE,generator = "seq_task_id")
    @SequenceGenerator(name="seq_task_id", sequenceName = "SEQ_TASK_ID", initialValue = 100,  allocationSize = 2)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, IN_PROGRESS, COMPLETED

    @Column(nullable = false)
    private String priority = "MEDIUM"; // LOW, MEDIUM, HIGH

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserDetails assignedUser;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    // lifecycle hook runs before INSERT
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
    }
}
