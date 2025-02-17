package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String allUsers(Model model) {
        try {
            List<User> users = userService.findAll();
            model.addAttribute("users", users);
            return "user/user-list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to fetch users.");
            return "error"; // Убедитесь, что error.html существует
        }
    }

    @GetMapping("/new")
    public String createUserForm(@ModelAttribute("user") User user) {
        return "user/user-create";
    }

    @PostMapping
    public String createUser(@ModelAttribute("user") @Valid User user,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "user/user-create";
        }
        try {
            userService.save(user);
            return "redirect:/users";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to create user.");
            return "user/user-create";
        }
    }

    @GetMapping("/edit/{id}") // Изменен URL, чтобы не конфликтовать с POST /users/edit
    public String editUserForm(@PathVariable("id") Long id, Model model) { // Используем @PathVariable
        Optional<User> userById = userService.findById(id);
        if (userById.isPresent()) {
            model.addAttribute("user", userById.get());
            return "user/edit-user";
        } else {
            model.addAttribute("errorMessage", "User not found.");
            return "user/user-list"; // Или создайте отдельную страницу для "User not found"
        }
    }

    @PostMapping("/edit")
    public String editUser(@ModelAttribute("user") @Valid User user,
                           BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "user/edit-user";
        }
        try {
            userService.updateUser(user);
            return "redirect:/users";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to update user.");
            return "user/edit-user";
        }
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id, Model model) {
        try {
            userService.deleteById(id);
            return "redirect:/users";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to delete user.");
            return "user/user-list";
        }
    }
}
