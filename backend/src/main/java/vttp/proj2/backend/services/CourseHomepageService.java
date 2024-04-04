package vttp.proj2.backend.services;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
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
import jakarta.json.JsonValue;
import vttp.proj2.backend.models.CourseSearch;
import vttp.proj2.backend.repositories.CourseRepository;
import vttp.proj2.backend.utils.Utils;

@Service
public class CourseHomepageService {

    @Value("${udemy.client.id}")
    String udemyId;

    @Value("${udemy.client.secret}")
    String udemySecret;

    @Autowired
    CourseRepository courseRepo;

    private final String UDEMY_COURSE_SEARCH_URL = "https://www.udemy.com/api-2.0/courses/";
    RestTemplate template = new RestTemplate();
    private HttpHeaders headers;
    // https://www.udemy.com/api-2.0/courses/?page=1&page_size=15&search=business&ordering=relevance
    private final String[] categories = {"business", "design", "video", "marketing", "coding"};

    @PostConstruct
    private void init() {
        String encodedCredentials = Base64.getEncoder().encodeToString((udemyId + ":" + udemySecret).getBytes());
        headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials);
    }

    @Cacheable(value = "homePageCoursesCache")
    public List<CourseSearch> loadCoursesForHomepage(){
        List<CourseSearch> homepageCourses = new ArrayList<>();
        for (String category : categories){
            homepageCourses = getHomepageCourses(category, homepageCourses);
        }
        return homepageCourses;
    }

    public List<CourseSearch> getHomepageCourses(String category, List<CourseSearch> list){
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(UDEMY_COURSE_SEARCH_URL)
            .queryParam("page_size", "15")
            .queryParam("search", category)
            .queryParam("ordering", "relevance");

        String uri = builder.toUriString();
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = template.exchange(uri, HttpMethod.GET, request, String.class);
        String searchString = response.getBody();
        Reader reader = new StringReader(searchString);
        JsonReader jsonReader = Json.createReader(reader);
        JsonObject searchObj = jsonReader.readObject();
        JsonArray resultArray = searchObj.getJsonArray("results");
        for (JsonValue resultValue : resultArray) {
            JsonObject resultObject = resultValue.asJsonObject();
            int courseId = resultObject.getInt("id");
            Document doc = courseRepo.getUdemyCourseById(courseId);
            CourseSearch course = Utils.documentToCourseSearch(doc);
            if (course!=null){
                course.setCategory(category);
                list.add(course);
            } else {
                continue;
            }
            
        }
        return list;
    }
    
}
