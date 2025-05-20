package ru.itmentor.spring.boot_security.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.service.RoleService;
import ru.itmentor.spring.boot_security.demo.service.UserService;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class SpringBootSecurityDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootSecurityDemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner init(UserService userService,
								  RoleService roleService,
								  PasswordEncoder passwordEncoder) {
		return args -> {
			// Создаем роли, если их нет
			Role adminRole = roleService.findByName("ROLE_ADMIN");
			if (adminRole == null) {
				adminRole = new Role("ROLE_ADMIN");
				roleService.save(adminRole);
			}

			Role userRole = roleService.findByName("ROLE_USER");
			if (userRole == null) {
				userRole = new Role("ROLE_USER");
				roleService.save(userRole);
			}

			// Создаем администратора, если его нет
			if (userService.findByUsername("admin") == null) {
				User admin = new User();
				admin.setUsername("admin");
				admin.setPassword(passwordEncoder.encode("admin")); // Важно: хешируем пароль!
				admin.setRoles(new HashSet<>(Set.of(adminRole)));
				userService.saveUser(admin);
				System.out.println("Admin created with password: admin");
			}

			// Создаем обычного пользователя
			if (userService.findByUsername("user") == null) {
				User user = new User();
				user.setUsername("user");
				user.setPassword(passwordEncoder.encode("user")); // Важно: хешируем пароль!
				user.setRoles(new HashSet<>(Set.of(userRole)));
				userService.saveUser(user);
				System.out.println("User created with password: user");
			}
		};
	}
}
