package vttp.proj2.backend.controllers;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp.proj2.backend.exceptions.UserAddCourseException;
import vttp.proj2.backend.exceptions.UserAddFriendException;
import vttp.proj2.backend.models.CourseDetails;
import vttp.proj2.backend.models.FriendInfo;
import vttp.proj2.backend.models.FriendRequest;
import vttp.proj2.backend.models.Notification;
import vttp.proj2.backend.services.UserService;
import vttp.proj2.backend.utils.Utils;

@RestController
// @CrossOrigin
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userSvc;

    //for adding user courses
    @PostMapping("/{userId}/courses")
    public ResponseEntity<?> addCourse(@PathVariable String userId, @RequestBody CourseDetails courseDetails) {
        try {
            CourseDetails addedCourseDetails = userSvc.addCourseAndInitializeProgress(userId, courseDetails,
                    courseDetails.getCurriculum());
                    System.out.println("course details added " + courseDetails);
            return ResponseEntity.ok(addedCourseDetails);
        } catch (UserAddCourseException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    //FRIENDS
    //finding friends
    @GetMapping("/friend-search")
    public ResponseEntity<?> findFriendsByEmail(@RequestParam("userId") String userId, @RequestParam("email") String friendEmail){
        FriendInfo foundFriend = userSvc.findFriendsByEmail(userId, friendEmail);
        if (null==foundFriend){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundFriend);
    }

    //make friend request
    @PostMapping("/addfriend")
    public ResponseEntity<?> addFriend(@RequestBody FriendRequest friendRequest){
        try {
            FriendInfo updatedFriendInfo = userSvc.makeFriendRequest(friendRequest);
            System.out.println("updatedfriendinfo " + updatedFriendInfo);
            return ResponseEntity.ok(updatedFriendInfo);
        } catch (UserAddFriendException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    //ACCEPT/REJECT friend request
    @PutMapping("/addfriend/amend")
    public ResponseEntity<?> amendFriendRequest(@RequestBody String payload){
        System.out.println(payload);

        Reader reader = new StringReader(payload);
        JsonReader jsonReader = Json.createReader(reader);
        JsonObject dataObj = jsonReader.readObject();
        

        JsonObject notificationJson = dataObj.getJsonObject("notification");
        JsonObject friendRequestJson = dataObj.getJsonObject("friendRequest");
        boolean status = dataObj.getBoolean("status");

        Notification notification = Utils.parseNotification(notificationJson);
        System.out.println("notif " + notification);
        FriendRequest friendRequest = Utils.parseFriendRequest(friendRequestJson);
        
        boolean isUpdated = userSvc.updateFriendRequest(friendRequest, notification, status);
        if (!status && isUpdated) return ResponseEntity.ok(null);
        
        //if friend req accepted, retrieve new list of friend list
        String userId = friendRequest.getSenderId();
        List<FriendInfo> friends = userSvc.getAllFriends(userId);
        return ResponseEntity.ok(friends);
    }
    
    //get friends
    @GetMapping("/{userId}/friends")
    public ResponseEntity<?> getFriends(@PathVariable String userId){
        List<FriendInfo> friends = userSvc.getAllFriends(userId);
        return ResponseEntity.ok(friends);
    }
    
    //get notifs
    @GetMapping("/notifications/{userId}")
    public ResponseEntity<?> loadNotifications(@PathVariable String userId){
        System.out.println("got here");
        List<Notification> notifs = userSvc.getNotifications(userId);
        if (notifs==null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(notifs);
    }



}
