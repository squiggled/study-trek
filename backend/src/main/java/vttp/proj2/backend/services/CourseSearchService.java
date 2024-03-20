package vttp.proj2.backend.services;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.annotation.PostConstruct;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import vttp.proj2.backend.models.CourseDetails;
import vttp.proj2.backend.models.CourseSearch;
import vttp.proj2.backend.models.Curriculum;
import vttp.proj2.backend.models.Platform;
import vttp.proj2.backend.repositories.CourseRepository;

@Service
public class CourseSearchService {

    @Value("${udemy.client.id}")
    String udemyId;

    @Value("${udemy.client.secret}")
    String udemySecret;

    @Autowired
    CourseRepository courseRepo;

    private final String UDEMY_COURSE_SEARCH_URL = "https://www.udemy.com/api-2.0/courses/";
    RestTemplate template = new RestTemplate();
    private HttpHeaders headers;
    // https://www.udemy.com/api-2.0/courses/?page=1&search=python&ratings=4

    @PostConstruct // called after dependency injection
    private void init() {
        String encodedCredentials = Base64.getEncoder().encodeToString((udemyId + ":" + udemySecret).getBytes());
        headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials);
    }

    public List<CourseSearch> courseSearch(Map<String, String> paramMap) {
        return courseRepo.courseSearch(paramMap);
        // String query = paramMap.get("query");

        // UriComponentsBuilder builder =
        // UriComponentsBuilder.fromHttpUrl(UDEMY_COURSE_SEARCH_URL)
        // .queryParam("search", query)
        // .queryParam("page_size", "20");

        // paramMap.forEach((key, value) -> {
        // if (!key.equals("query")) {
        // builder.queryParam(key, value);
        // }
        // });

        // String uri = builder.toUriString();
        // HttpEntity<String> request = new HttpEntity<>(headers);
        // ResponseEntity<String> response = template.exchange(uri, HttpMethod.GET,
        // request, String.class);
        // String searchString = response.getBody();

        // Reader reader = new StringReader(searchString);
        // JsonReader jsonReader = Json.createReader(reader);
        // JsonObject searchObj = jsonReader.readObject();
        // JsonArray resultArray = searchObj.getJsonArray("results");
        // List<CourseSearch> foundCourses = new ArrayList<>();
        // for (JsonObject courseObj : resultArray.getValuesAs(JsonObject.class)){
        // CourseSearch cs = new CourseSearch();
        // cs.setPlatform(Platform.UDEMY);
        // cs.setHeadline(courseObj.getString("headline", ""));
        // cs.setTitle(courseObj.getString("title"));
        // cs.setUdemyId(courseObj.getInt("id"));
        // cs.setPrice(courseObj.getString("price"));
        // cs.setImageUrl(courseObj.getString("image_480x270"));
        // JsonArray instArray = courseObj.getJsonArray("visible_instructors");
        // if (instArray != null && !instArray.isEmpty()) {
        // JsonObject firstInstructor = instArray.getJsonObject(0);
        // String displayName = firstInstructor.getString("display_name", "");
        // cs.setInstructor(displayName);
        // }
        // foundCourses.add(cs);
        // }
        // return foundCourses;
    }

    public List<Document> fetchDataFromApi() {
        List<Document> allDocuments = new ArrayList<>();
        int pageSize = 100;
        int totalPages = 100;
        int callCount = 0;
        List<String> fixedQueries = List.of("business", "coding", "design", "finance", "health", "lifestyle",
                "marketing", "music", "media");
        // List<String> fixedQueries = List.of("coding", "marketing");
        for (String query : fixedQueries) {
            System.out.println("searching for " + query);
            for (int currentPage = 1; currentPage <= totalPages; currentPage++) {
                System.out.println("current page " + currentPage);
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(UDEMY_COURSE_SEARCH_URL)
                        .queryParam("search", query)
                        .queryParam("page_size", pageSize)
                        .queryParam("page", currentPage);

                String uri = builder.toUriString();
                HttpEntity<String> request = new HttpEntity<>(headers);
                ResponseEntity<String> response = template.exchange(uri, HttpMethod.GET, request, String.class);
                String searchString = response.getBody();

                Reader reader = new StringReader(searchString);
                JsonReader jsonReader = Json.createReader(reader);
                JsonObject searchObj = jsonReader.readObject();
                JsonArray resultArray = searchObj.getJsonArray("results");

                for (JsonObject courseObj : resultArray.getValuesAs(JsonObject.class)) {
                    Document document = parseCourseSearchFromJson(courseObj);
                    allDocuments.add(document);
                }

                if (resultArray.size() < pageSize) {
                    break;
                }
                callCount++;
                if (callCount % 100 == 0) {
                    try {
                        Thread.sleep(12000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        // System.out.println("alldocs.size"+ allDocuments.size());
        return allDocuments;
    }

    // @Scheduled(fixedRate = 604800000)
    public void updateUdemyCoursesOnMongo() {
        System.out.println("got here");
        List<Document> allCourses = fetchDataFromApi();

        // courseRepo.deleteAll();

        courseRepo.saveAll(allCourses);
    }

    private Document parseCourseSearchFromJson(JsonObject courseObj) {
        Document document = new Document();
        document.put("platform", Platform.UDEMY);
        document.put("title", courseObj.getString("title"));
        document.put("platformId", courseObj.getInt("platformId"));
        document.put("headline", courseObj.getString("headline"));
        document.put("imageUrl", courseObj.getString("image_480x270"));
        document.put("price", courseObj.getString("price"));

        JsonArray instArray = courseObj.getJsonArray("visible_instructors");
        if (instArray != null && !instArray.isEmpty()) {
            JsonObject firstInstructor = instArray.getJsonObject(0);
            String displayName = firstInstructor.getString("display_name", "");
            document.put("instructor", displayName);
        }
        return document;
    }

    public List<Curriculum> getCurriculumByCourseId(String courseId, boolean isEnrolled) {
        String url = UDEMY_COURSE_SEARCH_URL + courseId + "/public-curriculum-items/?page_size=50";
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, request, String.class);
        String searchString = response.getBody();

        Reader reader = new StringReader(searchString);
        JsonReader jsonReader = Json.createReader(reader);
        JsonObject currObj = jsonReader.readObject();

        List<Curriculum> curriculumList = new ArrayList<>();
        JsonArray results = currObj.getJsonArray("results");
        int lectureNum = 1;
        for (JsonValue value : results) {
            JsonObject courseObject = value.asJsonObject();
            Curriculum curriculum = new Curriculum();

            curriculum.setLectureNumber(lectureNum);
            lectureNum++;
            curriculum.setTitle(courseObject.getString("title"));
            curriculumList.add(curriculum);

            if (curriculumList.size() == 50) {
                break;
            }
        }
        return curriculumList;
    }

    public CourseDetails getUdemyCourseById(String courseId) {
        String url = UDEMY_COURSE_SEARCH_URL + courseId;
        System.out.println(url);
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, request, String.class);
        String searchString = response.getBody();
        Reader reader = new StringReader(searchString);
        JsonReader jsonReader = Json.createReader(reader);
        JsonObject searchObj = jsonReader.readObject();

        CourseDetails cd = new CourseDetails();
        cd.setPlatform(Platform.UDEMY);
        cd.setHeadline(searchObj.getString("headline", ""));
        if (searchObj.containsKey("id") && !searchObj.isNull("id")) {
            int platformId = searchObj.getInt("id");
            cd.setPlatformId(String.valueOf(platformId));
        }
        cd.setTitle(searchObj.getString("title"));
        cd.setImageUrl(searchObj.getString("image_480x270"));
        cd.setUrlToCourse("https://www.udemy.com" + searchObj.getString("url"));
        cd.setPaid(searchObj.getBoolean("is_paid"));
        cd.setPrice(searchObj.getString("price"));
        JsonArray instArray = searchObj.getJsonArray("visible_instructors");
        if (instArray != null && !instArray.isEmpty()) {
            JsonObject firstInstructor = instArray.getJsonObject(0);
            String displayName = firstInstructor.getString("display_name", "");
            cd.setInstructor(displayName);
        }
        cd.setCurriculum(getCurriculumByCourseId(courseId, cd.isEnrolled()));

        return cd;
    }

    public CourseDetails getCourseraCourseById(String courseId) {
        Document foundDoc = courseRepo.getCourseraCourseById(courseId);
        if (null == foundDoc)
            return null;
        CourseDetails foundCourse = new CourseDetails();
        foundCourse.setPlatform(Platform.COURSERA);
        Object platformIdObj = foundDoc.get("platformId");
        if (platformIdObj instanceof ObjectId) {
            String platformIdStr = ((ObjectId) platformIdObj).toHexString();
            foundCourse.setPlatformId(platformIdStr);
            System.out.println("platform id in course serach svc " + platformIdStr);
        }
        foundCourse.setTitle(foundDoc.getString("title"));
        String headline = foundDoc.getString("headline");
        foundCourse.setHeadline(headline.substring(1, headline.length() - 1).replace("'", "").trim());
        // System.out.println("headline in coursesvc line 249 " + foundCourse.getHeadline());
        foundCourse.setImageUrl(foundDoc.getString("imageUrl"));
        foundCourse.setUrlToCourse(foundDoc.getString("course_URL"));
        foundCourse.setPaid(false);
        foundCourse.setPrice("Free trial");
        foundCourse.setInstructor(foundDoc.getString("instructor"));
        String curriculumString = foundDoc.getString("sub_course_list");
        String validJsonArrayString = curriculumString.replace("'", "\"");

        JsonReader jsonReader = Json.createReader(new StringReader(validJsonArrayString));
        JsonArray jsonArray = jsonReader.readArray();
        jsonReader.close();

        List<Curriculum> resultList = new ArrayList<>();

        int lectureNumber = 1;
        for (int i = 0; i < jsonArray.size(); i++) {
            String currentItem = jsonArray.getString(i);
            if (!isNumeric(currentItem)) {
                Curriculum curr = new Curriculum();
                curr.setLectureNumber(lectureNumber);
                curr.setTitle(currentItem);
                lectureNumber++;
                resultList.add(curr);
            }
        }
        System.out.println(resultList);
        foundCourse.setCurriculum(resultList);
        foundCourse.setEnrolled(false);
        return foundCourse;

    }

    private static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c))
                return false;
        }
        return true;
    }

}
