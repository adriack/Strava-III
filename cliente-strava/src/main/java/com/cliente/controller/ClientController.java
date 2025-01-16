package com.cliente.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

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

    @GetMapping("/logout")
    public String logoutUser(HttpSession session) {
        String token = (String) session.getAttribute("userToken");
        if (token != null) {
            serviceProxy.logoutUser(token);  // Llamar al método de logout del serviceProxy
        }
        session.invalidate();  // Invalidar la sesión actual
        return "redirect:/strava/login";  // Redirigir a la página de login
    }
    
    @GetMapping("/my_activity")
    public String showMyActivity(@RequestParam(required = false) String sport,
                                @RequestParam(required = false) LocalDate startDate,
                                @RequestParam(required = false) LocalDate endDate,
                                @RequestParam(required = false) Boolean unexpectedError,
                                Model model, HttpSession session) {

        String token = (String) session.getAttribute("userToken");
        if (token == null) {
            return "redirect:/strava/login";
        }

        // Obtener la información necesaria
        getUserInfo(model, session);
        getTrainingSessions(sport, startDate, endDate, model, session);

        // Si unexpectedError es true, añadir al modelo para mostrar en la vista
        if (unexpectedError != null && unexpectedError) {
            model.addAttribute("unexpectedError", true);
        }

        return "views/my_activity";
    }

    // Función auxiliar para añadir la información de usuario al modelo
    public void getUserInfo(Model model, HttpSession session) {
        String token = (String) session.getAttribute("userToken");
        if (token == null) {
            model.addAttribute("userInfo", null);
        }
    
        var userInfo = serviceProxy.getUserInfo(token);
    
        if (userInfo instanceof SuccessResponseDTO successResponseDTO) {
            model.addAttribute("userInfo", successResponseDTO.getData());
        } else {
            model.addAttribute("userInfo", null);
        }
    }
    
    // Función auxiliar para añadir las sesiones de entrenamiento al modelo
    public void getTrainingSessions(String sport,LocalDate startDate, LocalDate endDate,
                                    Model model, HttpSession session) {
        String token = (String) session.getAttribute("userToken");
        if (token == null) {
            model.addAttribute("userSessions", null);
        }
    
        SportType sportType = (sport != null && !sport.isEmpty()) ? SportType.valueOf(sport) : null;
        var filterDTO = new FilterDTO(sportType, startDate, endDate);
        var userSessions = serviceProxy.getUserSessions(token, filterDTO);
    
        if (userSessions instanceof SuccessResponseDTO successResponseDTO) {
            model.addAttribute("userSessions", successResponseDTO.getValue("sessions"));
        } else {
            model.addAttribute("userSessions", null);
        }
    
        // Actualizar los atributos de deporte, fecha de inicio y fecha de fin en el modelo
        model.addAttribute("sport", sport);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
    
    }
    
    @PostMapping("/updateUserInfo")
    public String updateUserInfo(@Valid @ModelAttribute("userPhysicalInfoDTO") UserPhysicalInfoDTO userPhysicalInfoDTO, 
                                BindingResult bindingResult, HttpSession session, Model model) {

        String token = (String) session.getAttribute("userToken");
        if (token != null) {
            if (!(bindingResult.hasErrors())) {
                var response = serviceProxy.updateUserInfo(token, userPhysicalInfoDTO);
                if (!(response instanceof SuccessResponseDTO)) {
                    model.addAttribute("userInfoUnexpectedError", true);
                } else {
                    // Recargar la información de usuario
                    getUserInfo(model, session);
                }
            }
        }
        return "fragments/user_info :: userInfo";
    }

    @PostMapping("/filterSessions")
    public String filterSessions(@RequestParam(required = false) String sport,
                                 @RequestParam(required = false) LocalDate startDate,
                                 @RequestParam(required = false) LocalDate endDate,
                                 Model model, HttpSession session) {

        getTrainingSessions(sport, startDate, endDate, model, session);

        return "fragments/training_sessions :: trainingSessions";
    }

    @PostMapping("/deleteSession")
    public String deleteSession(@RequestParam UUID sessionId, 
                                @RequestParam(required = false) String sport,
                                @RequestParam(required = false) LocalDate startDate, 
                                @RequestParam(required = false) LocalDate endDate,
                                HttpSession session, 
                                Model model) {
        // Obtener el token de la sesión
        String token = (String) session.getAttribute("userToken");
        
        // Verificar si el token es válido
        if (token != null) {
            // Llamar al servicio para eliminar la sesión
            var response = serviceProxy.deleteSession(token, sessionId);
            
            // Verificar si la eliminación fue exitosa
            if (!(response instanceof SuccessResponseDTO)) {
                model.addAttribute("sessionUnexpectedError", true);
            } else {
                // Si la eliminación fue exitosa, recargar las sesiones de entrenamiento
                getTrainingSessions(sport, startDate, endDate, model, session);
            }
        }
        
        // Devolver un fragmento que actualice las sesiones de entrenamiento en la vista
        return "fragments/training_sessions :: trainingSessions";  // Esto actualizará solo el fragmento de las sesiones
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

    @GetMapping("/my_challenges")
    public String showMyChallenges(Model model, HttpSession session) {
        String token = (String) session.getAttribute("userToken");
        if (token == null) {
            return "redirect:/strava/login";
        }

        var response = serviceProxy.getAcceptedChallenges(token, true);
        if (response instanceof SuccessResponseDTO successResponseDTO) {
            var challenges = successResponseDTO.getValue("challenges");
            List<Map<String, Object>> activeChallenges = new ArrayList<>();
            List<Map<String, Object>> completedChallenges = new ArrayList<>();
            List<Map<String, Object>> futureChallenges = new ArrayList<>();

            LocalDate today = LocalDate.now();
            for (Map<String, Object> challenge : (List<Map<String, Object>>) challenges) {
                LocalDate startDate = LocalDate.parse((String) challenge.get("startDate"));
                LocalDate endDate = LocalDate.parse((String) challenge.get("endDate"));

                if (endDate.isBefore(today)) {
                    completedChallenges.add(challenge);
                } else if (startDate.isAfter(today)) {
                    futureChallenges.add(challenge);
                } else {
                    activeChallenges.add(challenge);
                }
            }

            model.addAttribute("activeChallenges", activeChallenges);
            model.addAttribute("completedChallenges", completedChallenges);
            model.addAttribute("futureChallenges", futureChallenges);
            model.addAttribute("fetchChallengesError", false);
        } else {
            model.addAttribute("fetchChallengesError", true);
        }

        return "views/my_challenges";
    }

}
