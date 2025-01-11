package com.cliente.controller;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cliente.dto.ClientErrorResponseDTO;
import com.cliente.dto.FilterDTO;
import com.cliente.dto.LoginDTO;
import com.cliente.dto.RegistrationDTO;
import com.cliente.dto.SuccessResponseDTO;
import com.cliente.dto.TrainingSessionDTO;
import com.cliente.dto.UserPhysicalInfoDTO;
import com.cliente.entity.enumeration.SportType;
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
                    return "redirect:/strava/my_activity?startDate=" + LocalDate.now().withDayOfMonth(1);  // Redirigir al método GET que carga "my activity"
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

    @GetMapping("/my_activity")
    public String showMyActivity(@RequestParam(required = false) String sport,
                                @RequestParam(required = false) LocalDate startDate,
                                @RequestParam(required = false) LocalDate endDate,
                                @RequestParam(required = false) Boolean unexpectedError, // Recibe el parámetro de error
                                Model model, HttpSession session) {

        String token = (String) session.getAttribute("userToken");
        if (token == null) {
            return "redirect:/strava/login";
        }

        // Llamadas al servicio
        var userInfo = serviceProxy.getUserInfo(token);
        SportType sportType = (sport != null && !sport.isEmpty()) ? SportType.valueOf(sport) : null;  // Modificación aquí
        var filterDTO = new FilterDTO(sportType, startDate, endDate);
        var userSessions = serviceProxy.getUserSessions(token, filterDTO);

        // Verificar si userInfo es una respuesta exitosa
        if (userInfo instanceof SuccessResponseDTO successResponseDTO) {
            model.addAttribute("userInfo", successResponseDTO.getData());
        } else {
            model.addAttribute("userInfo", null);
        }

        // Verificar si userSessions es una respuesta exitosa
        if (userSessions instanceof SuccessResponseDTO successResponseDTO) {
            model.addAttribute("userSessions", successResponseDTO.getValue("sessions"));
        } else {
            model.addAttribute("userSessions", null);
        }

        // Añadir filtros al modelo
        model.addAttribute("sport", sport);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        // Si unexpectedError es true, añadir al modelo para mostrar en la vista
        if (unexpectedError != null && unexpectedError) {
            model.addAttribute("unexpectedError", true);
        }

        return "views/my_activity";
    }
    
    @PostMapping("/updateUserInfo")
    public String updateUserInfo(@Valid @ModelAttribute("userPhysicalInfoDTO") UserPhysicalInfoDTO userPhysicalInfoDTO, 
                                BindingResult bindingResult, HttpSession session, Model model, RedirectAttributes redirectAttributes) {

        String token = (String) session.getAttribute("userToken");
        if (token == null) {
            return "redirect:/strava/login";
        }

        // Verificar si hay errores de validación
        if (bindingResult.hasErrors()) {
            return "views/my_activity";
        }                         

        var response = serviceProxy.updateUserInfo(token, userPhysicalInfoDTO);
        if (response instanceof SuccessResponseDTO) {
            return "redirect:/strava/my_activity?startDate=" + LocalDate.now().withDayOfMonth(1);
        } else {
            redirectAttributes.addFlashAttribute("unexpectedError", true);
            return "redirect:/strava/my_activity?startDate=" + LocalDate.now().withDayOfMonth(1);
        }
    }

    @PostMapping("/deleteSession")
    public String deleteSession(@RequestParam UUID sessionId, HttpSession session, RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("userToken");
        if (token == null) {
            return "redirect:/strava/login";
        }
        var response = serviceProxy.deleteSession(token, sessionId);
        if (response instanceof SuccessResponseDTO) {
            return "redirect:/strava/my_activity?startDate=" + LocalDate.now().withDayOfMonth(1);
        } else {
            redirectAttributes.addFlashAttribute("unexpectedError", true);
        }
        return "redirect:/strava/my_activity?startDate=" + LocalDate.now().withDayOfMonth(1);
    }

    @GetMapping("/new_session")
    public String showNewSessionForm(Model model, HttpSession session) {

        String token = (String) session.getAttribute("userToken");
        if (token == null) {
            return "redirect:/strava/login";
        }

        model.addAttribute("sessionDTO", new TrainingSessionDTO());
        return "views/new_session";
    }

    @PostMapping("/createSession")
    public String createSession(@Valid @ModelAttribute("sessionDTO") TrainingSessionDTO sessionDTO, 
                                BindingResult bindingResult, HttpSession session, Model model,
                                RedirectAttributes redirectAttributes) {

        String token = (String) session.getAttribute("userToken");
        if (token == null) {
            return "redirect:/strava/login";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("sessionDTO", sessionDTO);
            return "views/new_session";
        }

        var response = serviceProxy.createSession(token, sessionDTO);
        if (response instanceof SuccessResponseDTO) {
            return "redirect:/strava/my_activity?startDate=" + LocalDate.now().withDayOfMonth(1);
        } else {
            redirectAttributes.addFlashAttribute("unexpectedError", true);
            return "redirect:/strava/my_activity?startDate=" + LocalDate.now().withDayOfMonth(1);
        }
    }

}
