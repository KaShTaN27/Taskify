package com.example.taskify;

import com.example.taskify.domain.AppUser;
import com.example.taskify.domain.Organization;
import com.example.taskify.domain.Role;
import com.example.taskify.domain.Task;
import com.example.taskify.service.OrganizationService;
import com.example.taskify.service.TaskService;
import com.example.taskify.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;

@SpringBootApplication
public class TaskifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskifyApplication.class, args);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
