package com.taskmanager.taskmanagerapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TaskmanagerappApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskmanagerappApplication.class, args);
	}

}
