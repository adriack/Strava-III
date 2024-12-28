
package com.cliente.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cliente.service.ChallengeServiceProxy;

@Controller
public class ChallengeController {

    private final ChallengeServiceProxy challengeServiceProxy;

    public ChallengeController(ChallengeServiceProxy challengeServiceProxy) {
        this.challengeServiceProxy = challengeServiceProxy;
    }

    @GetMapping("/discover")
    public String discoverChallenges(@RequestParam(required = false) String startDate,
                                     @RequestParam(required = false) String endDate, Model model) {
        var challenges = challengeServiceProxy.getActiveChallenges(startDate, endDate);
        model.addAttribute("challenges", challenges);
        return "views/discover";
    }

    @GetMapping("/accepted-challenges")
    public String getAcceptedChallenges(Model model) {
        var challenges = challengeServiceProxy.getAcceptedChallenges();
        model.addAttribute("challenges", challenges);
        return "views/accepted-challenges";
    }
}