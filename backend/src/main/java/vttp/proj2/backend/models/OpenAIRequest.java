package vttp.proj2.backend.models;

import lombok.Data;
import java.util.List;
import java.util.ArrayList;

@Data
public class OpenAIRequest {
    private String model;
    private List<OpenAIMessage> messages;
    private double temperature;

    public OpenAIRequest(String model, String prompt){
        this.model=model;
        this.messages = new ArrayList<>();
        this.messages.add(new OpenAIMessage("user", prompt));
        this.temperature=0.7;
    }
    
}
