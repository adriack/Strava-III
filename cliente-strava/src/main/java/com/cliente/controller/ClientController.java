package com.cliente.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cliente.dto.ClientErrorResponseDTO;
import com.cliente.dto.RegistrationDTO;
import com.cliente.dto.SuccessResponseDTO;
import com.cliente.service.ServiceProxy;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/strava")
public class ClientController {

    private final ServiceProxy serviceProxy;

    public ClientController(ServiceProxy serviceProxy) {
        this.serviceProxy = serviceProxy;
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registrationDTO") RegistrationDTO user, 
                               BindingResult bindingResult, Model model) {
        // Restablecer todos los atributos de error al inicio
        model.addAttribute("unexpectedError", false);
        model.addAttribute("invalidCredentials", false);
        model.addAttribute("alreadyRegistered", false);

        if (bindingResult.hasErrors()) {
            // Si hay errores en los campos del formulario, volver a mostrar el formulario con los errores
            model.addAttribute("registrationDTO", user);
            return "views/register";  // Esto renderiza la vista de registro con los mensajes de error
        } else {
            // Si no se encuentran errores de validación, procede al registro
            var response = serviceProxy.registerUser(user);
            
            // Verifica la respuesta del registro
            switch (response) {
                case null -> {
                    model.addAttribute("unexpectedError", true);
                    return "views/register";
                }
                case SuccessResponseDTO successResponse -> {
                    model.addAttribute("response", successResponse.getData());
                    return "views/register";  // Aquí puedes redirigir o mostrar una vista de éxito
                }
                case ClientErrorResponseDTO errorResponse -> {
                    var errors = errorResponse.getErrors();
                    if (errors.containsKey("error")) {
                        String errorMessage = errors.get("error");
                        if (errorMessage.contains("not registered") || errorMessage.contains("credentials")) {
                            model.addAttribute("invalidCredentials", true);
                        } else if (errorMessage.contains("already registered")) {
                            model.addAttribute("alreadyRegistered", true);
                        } else {
                            model.addAttribute("unexpectedError", true);
                        }
                    } else {
                        model.addAttribute("unexpectedError", true);
                    }
                    return "views/register";
                }
                default -> {
                    model.addAttribute("unexpectedError", true);
                    return "views/register";
                }
            }
        }
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registrationDTO", new RegistrationDTO());

        // Restablecer todos los atributos de error al inicio
        model.addAttribute("unexpectedError", false);
        model.addAttribute("invalidCredentials", false);
        model.addAttribute("alreadyRegistered", false);
        
        return "views/register";
    }
}
