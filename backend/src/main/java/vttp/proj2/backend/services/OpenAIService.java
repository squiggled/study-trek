package vttp.proj2.backend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import vttp.proj2.backend.models.OpenAIRequest;
import vttp.proj2.backend.models.OpenAIResponse;
import org.springframework.http.MediaType;


@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    String openAIApiKey;

    public final String OPENAI_CHAT_URL = "https://api.openai.com/v1/completions";

    public OpenAIResponse getResponse(OpenAIRequest request) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        System.out.println("openaikey " + openAIApiKey);
        headers.set("Authorization", "Bearer " + openAIApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        //wrap headers in entity obj
        HttpEntity<Object> entity = new HttpEntity<>(request, headers);
        //headers.setContentType(MediaType.APPLICATION_JSON);

        // Making the GET request to the external API
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
        // OpenAIResponse response = restTemplate.postForObject(OPENAI_CHAT_URL, entity, OpenAIResponse.class);

}
