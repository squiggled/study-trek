package vttp.proj2.backend.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.DeleteResult;

import vttp.proj2.backend.models.CourseSearch;
import vttp.proj2.backend.models.Platform;

@Repository
public class CourseRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    public boolean deleteAll() {
        DeleteResult deleted = mongoTemplate.remove(new Query(), "udemy");
        return deleted.getDeletedCount() > 0;
    }

    public void saveAll(List<Document> allCourses) {
        mongoTemplate.insert(allCourses, "udemy");
    }

    //GET all courses fitting a criteria
    public List<CourseSearch> courseSearch(Map<String, String> paramMap) {
        String query = paramMap.get("query");
        int page = Integer.parseInt(paramMap.getOrDefault("page", "0"));
        int pageSize = Integer.parseInt(paramMap.getOrDefault("size", "20"));
        String platform = paramMap.get("platform");
        String byRating = paramMap.get("byRating");
        Query mongoQuery = new Query();
        List<Criteria> criteria = new ArrayList<>();

        if (query != null && !query.isEmpty()) {
            Criteria criteriaOr = new Criteria().orOperator(
                Criteria.where("title").regex(query, "i"),
                Criteria.where("headline").regex(query, "i"),
                Criteria.where("instructor").regex(query, "i")
            );
            criteria.add(criteriaOr);
        }

        if (platform != null && !platform.isEmpty()) {
            System.out.println("platform value " + platform);
            criteria.add(Criteria.where("platform").is(platform.toUpperCase()));
        }

        if (byRating != null && byRating.equals("byRating")) {
            criteria.add(Criteria.where("course_rating").exists(true));
            mongoQuery.with(Sort.by(Sort.Order.desc("course_rating"), Sort.Order.asc("_id")));
        } else {
            mongoQuery.with(Sort.by(Sort.Direction.ASC, "_id"));
        }

        if (!criteria.isEmpty()) {
            mongoQuery.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }
        Pageable pageable = PageRequest.of(page, pageSize);
        mongoQuery.with(pageable);
        System.out.println("query " + mongoQuery);

        List<Document> documents = mongoTemplate.find(mongoQuery, Document.class, "allCoursesListing");
        System.out.println("documents.size() " + documents.size());
        return convertDocumentsToCourseSearch(documents);
    }

    private List<CourseSearch> convertDocumentsToCourseSearch(List<Document> documents) {
        List<CourseSearch> courseSearches = new ArrayList<>();
        for (Document document : documents) {
            CourseSearch courseSearch = new CourseSearch();
            courseSearch.setPlatform(Platform.valueOf(document.getString("platform")));
            Object platformIdObj = document.get("platformId"); 
            String platformIdStr = null; 

            if (platformIdObj != null) {
                if (courseSearch.getPlatform().equals(Platform.COURSERA) && platformIdObj instanceof ObjectId) {
                    platformIdStr = ((ObjectId) platformIdObj).toHexString();
                } else if (courseSearch.getPlatform().equals(Platform.UDEMY) && platformIdObj instanceof Integer) {
                    platformIdStr = String.valueOf(platformIdObj);
                }
            
            courseSearch.setPlatformId(platformIdStr);
            // System.out.println(courseSearch.getPlatformId());
            courseSearch.setTitle(document.getString("title"));
            courseSearch.setHeadline(document.getString("headline"));
            courseSearch.setImageUrl(document.getString("imageUrl"));
            String price = document.containsKey("price") ? document.getString("price") : "Free trial";
            courseSearch.setPrice(price);
            Double rating = document.containsKey("course_rating") ? document.getDouble("course_rating"): null;
            courseSearch.setRating(rating);
            courseSearch.setInstructor(document.getString("instructor"));
            courseSearches.add(courseSearch);
            }
            
        }
        return courseSearches;
    }

    //GET one coursera coursedetails
    public Document getCourseraCourseById(String courseId) {
       
        ObjectId objectId = new ObjectId(courseId);
        Query query = new Query(Criteria.where("platformId").is(objectId));

        return mongoTemplate.findOne(query, Document.class, "allCoursesListing");

    }
}
