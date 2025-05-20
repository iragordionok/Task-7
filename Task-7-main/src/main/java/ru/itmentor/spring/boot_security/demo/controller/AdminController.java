package ru.itmentor.spring.boot_security.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.service.RoleService;
import ru.itmentor.spring.boot_security.demo.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController{
    private final UserService userService;
    private final RoleService roleService;
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }


    @GetMapping
    public String showAllUsers(Model model) {
        try {
            model.addAttribute("users", userService.getAllUsers());
            return "admin/users";
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error loading users", e);
        }
    }

    @GetMapping("/add")
    public String addUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "admin/edit";
    }

    @PostMapping("/edit")
    public String saveUser(@ModelAttribute User user, @RequestParam ("roles") Set<Long> roleIds) {
        Set<Role> roles = new HashSet<>();
        for (Long id : roleIds) {
            roles.add(roleService.getRoleById(id));
        }
        user.setRoles(roles);
        userService.saveUser(user);
        return "redirect:/admin";

    }

    @GetMapping("/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.getById(id);
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "admin/edit";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }
    @ControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(Exception.class)
        public ResponseEntity<Map<String, String>> handleAllExceptions(Exception ex) {
            Map<String, String> response = new HashMap<>();
            response.put("message", ex.getMessage());
            response.put("timestamp", LocalDateTime.now().toString());

            HttpStatus status = ex instanceof ResponseStatusException
                    ? ((ResponseStatusException) ex).getStatus()
                    : HttpStatus.INTERNAL_SERVER_ERROR;

            return new ResponseEntity<>(response, status);
        }
    }

    @ModelAttribute("hasRole")
    public boolean hasRole(User user, Role role) {
        return user.getRoles().stream().anyMatch(r -> r.getId().equals(role.getId()));
    }

}
