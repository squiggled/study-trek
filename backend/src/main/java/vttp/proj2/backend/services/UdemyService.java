package vttp.proj2.backend.services;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp.proj2.backend.models.CourseSearch;
import vttp.proj2.backend.models.Platform;
import vttp.proj2.backend.models.SearchResult;

@Service
public class UdemyService {

    @Value("${udemy.client.id}") 
    String udemyId;

    @Value("${udemy.client.secret}") 
    String udemySecret;

    private final String UDEMY_SEARCH_URL ="https://www.udemy.com/api-2.0/courses/";
    RestTemplate template = new RestTemplate();
    private HttpHeaders headers;
    //https://www.udemy.com/api-2.0/courses/?page=1&search=python&ratings=4

    @PostConstruct //called after dependency injection 
    private void init() {
        String encodedCredentials = Base64.getEncoder().encodeToString((udemyId + ":" + udemySecret).getBytes());
        headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials);
    }

    public SearchResult courseSearch(String query){
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = template.exchange(UDEMY_SEARCH_URL + "?search=" + query, HttpMethod.GET, request, String.class);
        String searchString = response.getBody();
        System.out.println("search string " + searchString);
        Reader reader = new StringReader(searchString);
        JsonReader jsonReader = Json.createReader(reader);
        JsonObject searchObj = jsonReader.readObject();

        SearchResult searchResult = new SearchResult();
        searchResult.setPrevPageUrl(searchObj.getString("previous", ""));
        searchResult.setNextPageUrl(searchObj.getString("next", ""));
        
        JsonArray resultArray = searchObj.getJsonArray("results");
        List<CourseSearch> foundCourses = new ArrayList<>();
        for (JsonObject courseObj : resultArray.getValuesAs(JsonObject.class)){
            CourseSearch cs = new CourseSearch();
            cs.setPlatform(Platform.UDEMY);
            cs.setHeadline(courseObj.getString("headline"));
            cs.setTitle(courseObj.getString("title"));
            cs.setId(courseObj.getInt("id"));
            cs.setPrice(courseObj.getString("price"));
            JsonArray instArray = courseObj.getJsonArray("visible_instructors");
            if (instArray != null && !instArray.isEmpty()) {
                JsonObject firstInstructor = instArray.getJsonObject(0);
                String displayName = firstInstructor.getString("display_name");
                cs.setInstructor(displayName);
            }
            foundCourses.add(cs);
        }
        searchResult.setFoundCourses(foundCourses);
        return searchResult;
    }
}
