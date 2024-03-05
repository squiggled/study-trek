package vttp.proj2.backend.services;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.annotation.PostConstruct;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp.proj2.backend.models.CourseSearch;
import vttp.proj2.backend.models.Platform;

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

    public List<CourseSearch> courseSearch(Map<String, String> paramMap){
        String query = paramMap.get("query");
    
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(UDEMY_SEARCH_URL)
                                    .queryParam("search", query)
                                    .queryParam("page_size", "20");
        
        paramMap.forEach((key, value) -> {
            if (!key.equals("query")) { // Exclude the mandatory 'query' parameter
                builder.queryParam(key, value);
            }
        });
    
        String uri = builder.toUriString();
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        ResponseEntity<String> response = template.exchange(uri, HttpMethod.GET, request, String.class);
        String searchString = response.getBody();
        System.out.println("url string " + uri);
        Reader reader = new StringReader(searchString);
        JsonReader jsonReader = Json.createReader(reader);
        JsonObject searchObj = jsonReader.readObject();

        JsonArray resultArray = searchObj.getJsonArray("results");
        List<CourseSearch> foundCourses = new ArrayList<>();
        for (JsonObject courseObj : resultArray.getValuesAs(JsonObject.class)){
            CourseSearch cs = new CourseSearch();
            cs.setPlatform(Platform.UDEMY);
            cs.setHeadline(courseObj.getString("headline"));
            cs.setTitle(courseObj.getString("title"));
            cs.setId(courseObj.getInt("id"));
            cs.setPrice(courseObj.getString("price"));
            cs.setImageUrl(courseObj.getString("image_480x270"));
            JsonArray instArray = courseObj.getJsonArray("visible_instructors");
            if (instArray != null && !instArray.isEmpty()) {
                JsonObject firstInstructor = instArray.getJsonObject(0);
                String displayName = firstInstructor.getString("display_name");
                cs.setInstructor(displayName);
            }
            foundCourses.add(cs);
        }
        return foundCourses;
    }
}
