
package com.cliente.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.cliente.service.TrainingSessionServiceProxy;

@Controller
public class TrainingSessionController {

    private final TrainingSessionServiceProxy trainingSessionServiceProxy;

    public TrainingSessionController(TrainingSessionServiceProxy trainingSessionServiceProxy) {
        this.trainingSessionServiceProxy = trainingSessionServiceProxy;
    }

    @GetMapping("/sessions")
    public String getUserSessions(Model model) {
        var sessions = trainingSessionServiceProxy.getUserSessions();
        model.addAttribute("sessions", sessions);
        return "views/sessions";
    }
}