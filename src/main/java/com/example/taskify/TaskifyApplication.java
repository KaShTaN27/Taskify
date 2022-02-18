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

	@Bean
	CommandLineRunner commandLineRunner(UserService userService,
										TaskService taskService,
										OrganizationService organizationService) {
		return args -> {
			userService.saveRole(new Role(null, "ROLE_ADMIN"));
			userService.saveRole(new Role(null, "ROLE_USER"));
			Organization organization = new Organization(
					"Viva",
					"632233",
					"Generalov st.");
			organizationService.saveOrganization(organization);
			AppUser user = new AppUser("Boris","Johnson","korzundanik@gmail.com","123456");
			user.setOrganizationName(organization.getName());
			userService.saveUser(user);
			organizationService.addAdminToOrganization("Viva", "korzundanik@gmail.com");
			AppUser user1 = new AppUser("Lena","Kerson","Klerson@gmail.com","123456");
			user1.setOrganizationName(organization.getName());
			userService.saveUser(user1);
			userService.addRoleToUser("Klerson@gmail.com", "ROLE_USER");
			organizationService.addUserToOrganization("Viva", "Klerson@gmail.com");
			taskService.saveTask(new Task(null, "Work", "smthng about work", "2022-01-13", false));
			taskService.addTaskToUsers(new ArrayList<>(Arrays.asList("korzundanik@gmail.com", "Klerson@gmail.com")), "Work");
		};
	}
}
