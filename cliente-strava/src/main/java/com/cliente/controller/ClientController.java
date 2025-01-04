package com.cliente.controller;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cliente.dto.ClientErrorResponseDTO;
import com.cliente.dto.FilterDTO;
import com.cliente.dto.LoginDTO;
import com.cliente.dto.RegistrationDTO;
import com.cliente.dto.SuccessResponseDTO;
import com.cliente.service.ServiceProxy;

import jakarta.servlet.http.HttpSession;
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

    @PostMapping("/login")
    public String loginUser(@Valid @ModelAttribute("loginDTO") LoginDTO login, 
                            BindingResult bindingResult, Model model, HttpSession session) {
        model.addAttribute("unexpectedError", false);
        model.addAttribute("invalidCredentials", false);
        model.addAttribute("notRegistered", false);

        if (bindingResult.hasErrors()) {
            model.addAttribute("loginDTO", login);
            return "views/login";
        } else {
            var response = serviceProxy.loginUser(login);

            switch (response) {
                case null -> {
                    model.addAttribute("unexpectedError", true);
                    return "views/login";
                }
                case SuccessResponseDTO successResponse -> {
                    model.addAttribute("response", successResponse.getData());
                    session.setAttribute("userToken", successResponse.getValue("token"));  // Guardar el token en la sesión
                    return "redirect:/strava/my_activity";  // Redirigir al método GET que carga "my activity"
                }
                case ClientErrorResponseDTO errorResponse -> {
                    var errors = errorResponse.getErrors();
                    if (errors.containsKey("error")) {
                        String errorMessage = errors.get("error");
                        if (errorMessage.contains("credentials")) {
                            model.addAttribute("invalidCredentials", true);
                        } else if (errorMessage.contains("must be registered")) {
                            model.addAttribute("notRegistered", true);
                        } else {
                            model.addAttribute("unexpectedError", true);
                        }
                    } else {
                        model.addAttribute("unexpectedError", true);
                    }
                    return "views/login";
                }
                default -> {
                    model.addAttribute("unexpectedError", true);
                    return "views/login";
                }
            }
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());
        model.addAttribute("unexpectedError", false);
        model.addAttribute("invalidCredentials", false);
        model.addAttribute("notRegistered", false);
        return "views/login";
    }

    @PostMapping("/deleteSession")
    public String deleteSession(@RequestParam UUID sessionId, @RequestParam String token, Model model) {
        var response = serviceProxy.deleteSession(token, sessionId);
        if (response instanceof SuccessResponseDTO) {
            model.addAttribute("response", ((SuccessResponseDTO) response).getData());
        } else {
            model.addAttribute("unexpectedError", true);
        }
        return "views/my_activity";
    }

    @GetMapping("/my_activity")
    public String showMyActivity(Model model, HttpSession session) {
        String token = (String) session.getAttribute("userToken");
        if (token == null) {
            return "redirect:/strava/login";
        }
        var userInfo = serviceProxy.getUserInfo(token);
        var userSessions = serviceProxy.getUserSessions(token, new FilterDTO());
        model.addAttribute("userInfo", ((SuccessResponseDTO) userInfo).getData());
        model.addAttribute("userSessions", ((SuccessResponseDTO) userSessions).getValue("sessions"));
        return "views/my_activity";
    }

}
