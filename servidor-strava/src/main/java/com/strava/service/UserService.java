package com.strava.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.strava.dao.TokenDAO;
import com.strava.dao.UserDAO;
import com.strava.dto.LoginDTO;
import com.strava.dto.RegistrationDTO;
import com.strava.dto.ResponseWrapper;
import com.strava.dto.TokenDTO;
import com.strava.dto.UserDTO;
import com.strava.dto.UserPhysicalInfoDTO;
import com.strava.entity.User;
import com.strava.entity.UserToken;
import com.strava.external.AuthGateway;
import com.strava.external.FactoriaGateway;

@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private TokenDAO tokenDAO;

    private final FactoriaGateway factoriaGateway;
    private final TokenService tokenService;

    public UserService(TokenService tokenService) {
        this.factoriaGateway = new FactoriaGateway();
        this.tokenService = tokenService;
    }

    // Registrar un nuevo usuario
    public ResponseWrapper registerUser(RegistrationDTO userDTO) {
        if (userDAO.findByEmail(userDTO.getEmail()).isPresent()) {
            return new ResponseWrapper(409, "error", "This email is already registered.");
        }

        AuthGateway authGateway = factoriaGateway.createGateway(userDTO.getAuthProvider());

        Optional<Boolean> emailValidOptional = authGateway.validateEmail(userDTO.getEmail());
        if (emailValidOptional.isEmpty()) {
            return new ResponseWrapper(500, "error", "Error communicating with authentication provider for email validation.");
        }
        boolean emailValid = emailValidOptional.get();
        if (!emailValid) {
            return new ResponseWrapper(404, "error", "Email is not registered with the specified provider.");
        }

        Optional<Boolean> passwordValidOptional = authGateway.validatePassword(userDTO.getEmail(), userDTO.getPassword());
        if (passwordValidOptional.isEmpty()) {
            return new ResponseWrapper(500, "error", "Error communicating with authentication provider for password validation.");
        }
        boolean passwordValid = passwordValidOptional.get();
        if (!passwordValid) {
            return new ResponseWrapper(401, "error", "Invalid credentials");
        }

        User user = new User(userDTO);
        userDAO.save(user);

        return new ResponseWrapper(201, "user-id", user.getId());
    }

    // Iniciar sesión de usuario
    public ResponseWrapper loginUser(LoginDTO loginDTO) {
        String email = loginDTO.getEmail();
        String password = loginDTO.getPassword();

        // Obtiene el usuario de la base de datos por su email
        Optional<User> optionalUser = userDAO.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return new ResponseWrapper(400, "error", "User must be registered first.");
        }

        User user = optionalUser.get();

        // Valida las credenciales de usuario con el proveedor correspondiente
        AuthGateway authGateway = factoriaGateway.createGateway(user.getAuthProvider());

        Optional<Boolean> credentialsValidOptional = authGateway.validatePassword(email, password);
        if (credentialsValidOptional.isEmpty()) {
            return new ResponseWrapper(500, "error", "Error communicating with authentication provider for password validation.");
        }
        boolean credentialsValid = credentialsValidOptional.get();
        if (!credentialsValid) {
            return new ResponseWrapper(401, "error", "Invalid credentials.");
        }

        // Genera un token de usuario y lo almacena en la base de datos
        String token = tokenService.generateToken();
        UserToken userToken = new UserToken(user, token);
        tokenDAO.save(userToken);

        return new ResponseWrapper(200, "token", token);
    }

    // Cerrar sesión de usuario
    public ResponseWrapper logoutUser(TokenDTO tokenDTO) {
        String token = tokenDTO.getToken();
        Optional<UserToken> optionalToken = tokenDAO.findByToken(token);

        if (optionalToken.isEmpty()) {
            return new ResponseWrapper(400, "error", "Invalid token.");
        }

        UserToken userToken = optionalToken.get();
        userToken.setRevoked(true);
        tokenDAO.save(userToken);

        return new ResponseWrapper(200, "message", "User logged out successfully.");
    }

    // Método para obtener información del usuario basado en un token
    public ResponseWrapper getUserInfoFromToken(TokenDTO tokenDTO) {
        User user = tokenService.getUserFromToken(tokenDTO);
        UserDTO userInfo = new UserDTO(user);
        return new ResponseWrapper(200, userInfo.toMap());
    }

    // Método para actualizar la información física del usuario
    public ResponseWrapper updateUserPhysicalInfo(TokenDTO tokenDTO, UserPhysicalInfoDTO userPhysicalInfoDTO) {
        User user = tokenService.getUserFromToken(tokenDTO);

        if (userPhysicalInfoDTO.getName() != null) {
            user.setName(userPhysicalInfoDTO.getName());
        }
        if (userPhysicalInfoDTO.getDateOfBirth() != null) {
            user.setDateOfBirth(userPhysicalInfoDTO.getDateOfBirth());
        }
        if (userPhysicalInfoDTO.getWeight() != null) {
            user.setWeight(userPhysicalInfoDTO.getWeight());
        }
        if (userPhysicalInfoDTO.getHeight() != null) {
            user.setHeight(userPhysicalInfoDTO.getHeight());
        }
        if (userPhysicalInfoDTO.getMaxHeartRate() != null) {
            user.setMaxHeartRate(userPhysicalInfoDTO.getMaxHeartRate());
        }
        if (userPhysicalInfoDTO.getRestingHeartRate() != null) {
            user.setRestingHeartRate(userPhysicalInfoDTO.getRestingHeartRate());
        }

        userDAO.save(user);
        return new ResponseWrapper(200, "message", "User info updated successfully.");
    }

    public User getUserById(UUID userId) {
        return userDAO.findById(userId).orElse(null);
    }

}
