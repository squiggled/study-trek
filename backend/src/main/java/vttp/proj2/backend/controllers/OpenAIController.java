package vttp.proj2.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

import vttp.proj2.backend.models.OpenAIRequest;
import vttp.proj2.backend.models.OpenAIResponse;
import vttp.proj2.backend.services.OpenAIService;

@RestController
@RequestMapping("/api")
public class OpenAIController {

    @Autowired
    OpenAIService openAISvc;

    public final String model = "gpt-3.5-turbo";

    @GetMapping("/bot")
    public String chat(@RequestParam("prompt") String prompt, @RequestParam("number") String number) {
        String formattedPrompt = " Recommend " + number + " course(s) on " + prompt + " from udemy and coursera only" ;
        System.out.println("formattedprompt " + formattedPrompt);
        OpenAIRequest req = new OpenAIRequest(model, formattedPrompt);
        OpenAIResponse response = openAISvc.getResponse(req);
        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            OpenAIResponse.Choice firstChoice = response.getChoices().get(0);
            if (firstChoice != null && firstChoice.getMessage() != null) {
                String role = firstChoice.getMessage().getRole();
                String content = firstChoice.getMessage().getContent();
                System.out.println("Role: " + role + ", Content: " + content);
                return content; 
            }
        }
        return "No courses found. Search again?"; 

    }

}
