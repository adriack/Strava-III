
package com.cliente.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cliente.service.UserServiceProxy;

@Controller
public class UserController {

    private final UserServiceProxy userServiceProxy;

    public UserController(UserServiceProxy userServiceProxy) {
        this.userServiceProxy = userServiceProxy;
    }

    @GetMapping("/me")
    public String getUserInfo(Model model) {
        var userInfo = userServiceProxy.getUserInfo();
        model.addAttribute("user", userInfo);
        return "views/me";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "views/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, Model model) {
        var response = userServiceProxy.login(email, password);
        if (response.isSuccessful()) {
            return "redirect:/me";
        } else {
            model.addAttribute("error", response.getError());
            return "views/login";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        userServiceProxy.logout();
        return "views/logout";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "views/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String email, @RequestParam String password, Model model) {
        var response = userServiceProxy.register(email, password);
        if (response.isSuccessful()) {
            return "redirect:/login";
        } else {
            model.addAttribute("error", response.getError());
            return "views/register";
        }
    }
}