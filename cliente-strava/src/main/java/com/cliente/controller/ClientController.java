package com.cliente.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cliente.dto.UserDTO;
import com.cliente.service.ServiceProxy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/users")
public class ClientController {

    private final ServiceProxy serviceProxy;

    public ClientController(ServiceProxy serviceProxy) {
        this.serviceProxy = serviceProxy;
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserDTO user, Model model) {
        var response = serviceProxy.registerUser(user);
        if (response.getStatusCode().is2xxSuccessful()) {
            model.addAttribute("response", response.getBody());
        } else {
            Map<String, String> errorMap = (Map<String, String>) response.getBody();
            String status = errorMap.get("status");
            String body = errorMap.get("body");

            switch (status) {
                case "401 UNAUTHORIZED" -> model.addAttribute("passwordError", body);
                case "400 BAD_REQUEST" -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        Map<String, String> errors = mapper.readValue(body, Map.class);
                        errors.forEach((field, message) -> {
                            switch (field) {
                                case "email" -> model.addAttribute("emailError", message);
                                case "password" -> model.addAttribute("passwordError", message);
                                case "name" -> model.addAttribute("nameError", message);
                                case "weight" -> model.addAttribute("weightError", message);
                                case "height" -> model.addAttribute("heightError", message);
                                case "maxHeartRate" -> model.addAttribute("maxHeartRateError", message);
                                case "restingHeartRate" -> model.addAttribute("restingHeartRateError", message);
                                case "authProvider" -> model.addAttribute("authProviderError", message);
                            }
                        });
                    } catch (JsonProcessingException e) {
                        model.addAttribute("error", "Error parsing error response");
                    }
                }

                case "404 NOT_FOUND", "409 CONFLICT" -> model.addAttribute("emailError", body);
                case "500 INTERNAL_SERVER_ERROR" -> model.addAttribute("error", body);
                default -> model.addAttribute("error", body);
            }
        }
        return "views/register";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "views/register";
    }
}
