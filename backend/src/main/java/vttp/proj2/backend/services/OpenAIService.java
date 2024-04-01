package vttp.proj2.backend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import vttp.proj2.backend.models.OpenAIMessage;
import vttp.proj2.backend.models.OpenAIRequest;
import vttp.proj2.backend.models.OpenAIResponse;
import org.springframework.http.MediaType;


@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    String openAIApiKey;

    public final String OPENAI_CHAT_URL = "https://api.openai.com/v1/chat/completions";

    public OpenAIResponse getResponse(OpenAIRequest request) {

        //format req body
        String formattedBody = buildJsonPayload(request);

        //set headers
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + openAIApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        //wrap headers in entity obj
        System.out.println(formattedBody);
        HttpEntity<Object> entity = new HttpEntity<>(formattedBody, headers);

        //post request to API
        try {
            ResponseEntity<OpenAIResponse> responseEntity = restTemplate.postForEntity(OPENAI_CHAT_URL, entity, OpenAIResponse.class);
            
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                System.out.println("Error: Non-successful response received. Status code: " + responseEntity.getStatusCode());
                return null; 
            }
            return responseEntity.getBody();
        } catch (HttpClientErrorException ex) {
            System.out.println("Client error occurred: " + ex.getStatusCode() + " " + ex.getResponseBodyAsString());
        } catch (RestClientException ex) {
            System.out.println("Error occurred while calling the OpenAI API: " + ex.getMessage());
        }
        return null; 
    }
     
    public static String buildJsonPayload(OpenAIRequest request) {
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("model", request.getModel());
        job.add("temperature", request.getTemperature());

        JsonArrayBuilder jab = Json.createArrayBuilder();
        for (OpenAIMessage message : request.getMessages()) {
            jab.add(Json.createObjectBuilder()
                        .add("role", message.getRole())
                        .add("content", message.getContent()));
        }

        job.add("messages", jab);

        JsonObject jsonObject = job.build();
        return jsonObject.toString();
    }

}
