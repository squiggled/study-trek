package vttp.proj2.backend.services;

import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.NonNull;
import vttp.proj2.backend.dtos.UserAmendDetailsDTO;
import vttp.proj2.backend.exceptions.UserAddCourseException;
import vttp.proj2.backend.exceptions.UserAddFriendException;
import vttp.proj2.backend.exceptions.UserAmendFriendRequestException;
import vttp.proj2.backend.exceptions.UserAmendDetailsException;
import vttp.proj2.backend.models.AccountInfo;
import vttp.proj2.backend.models.CourseDetails;
import vttp.proj2.backend.models.Curriculum;
import vttp.proj2.backend.models.FriendInfo;
import vttp.proj2.backend.models.FriendRequest;
import vttp.proj2.backend.models.Notification;
import vttp.proj2.backend.repositories.S3Repository;
import vttp.proj2.backend.repositories.UserRepository;

@Service
public class UserService {
    
    @Autowired
    UserRepository userRepo;

    @Autowired
    S3Repository s3Repo;

    //for login
    public AccountInfo getUserByEmail(String email){
        return userRepo.findUserByEmail(email);
    }

    public AccountInfo getUserById(String userId){
        return userRepo.findUserById(userId);
    }

    public CourseDetails addCourseAndInitializeProgress(String userId, CourseDetails courseDetails,
            List<Curriculum> curriculumList) {
        try {
            CourseDetails cd = userRepo.addCourseAndInitializeProgress(userId, courseDetails, curriculumList);
            return cd;
        } catch (UserAddCourseException e){
            e.printStackTrace();
            return null;
        }
    }

    public FriendInfo findFriendsByEmail(String userId , String friendEmail) {
        FriendInfo friend = userRepo.friendSearchByEmail(friendEmail, userId);
        if (null==friend){
            return null;
        }
        return friend;
    }

    public String getRolesById(String userId) {
        String role = userRepo.getUserRole(userId);
        return role;
    }

    @Transactional(rollbackFor = UserAddFriendException.class)
    public FriendInfo makeFriendRequest(FriendRequest friendRequest) throws UserAddFriendException{
        System.out.println("for usersvc makefriendrq " + friendRequest.getReceiverId() + " " + friendRequest.getSenderId());
        boolean isPending = userRepo.isPending(friendRequest.getSenderId(), friendRequest.getReceiverId());
        if (isPending){
            throw new UserAddFriendException("Request is already pending");
        }
        int requestInteger = userRepo.addFriendRequest(friendRequest);
        if (requestInteger == 0){
            throw new UserAddFriendException("Cannot create new friend request");
        }
        Notification notif = new Notification();
        notif.setUserId(friendRequest.getReceiverId());
        notif.setFriendRequest(friendRequest);
        // System.out.println("friend req in user svc create notif " + friendRequest);
        notif.setMessage("You have a new friend request from " + userRepo.findNameByUserId(friendRequest.getSenderId()));
        notif.setRead(false);
        notif.setType("FRIEND_REQUEST");
        notif.setRelatedId(Integer.toString(requestInteger));
        int notifInteger = userRepo.addNotification(notif);
        if (notifInteger==0){
            throw new UserAddFriendException("Cannot create new notification");
        }
        //return new friend object, with pending updated
        FriendInfo updatedFriend = userRepo.friendSearchById(friendRequest.getReceiverId(), friendRequest.getSenderId());
        return updatedFriend;
    }

    //get notifs
    public List<Notification> getNotifications(String userId){
        return userRepo.getUserNotifications(userId);
    }

    //get updated friend list
    public List<FriendInfo> getFriendListByUserId(String userId) {
        // Implementation to fetch the friend list from the database
        return null;
        // return userRepo.findFriendIdsByUserId(userId);
    }

    //update friend req
    @Transactional(rollbackFor = UserAmendFriendRequestException.class)
    public boolean updateFriendRequest(FriendRequest req, Notification notif, boolean isAccepted) throws UserAmendFriendRequestException{
        boolean isNotifDeleted = userRepo.deleteNotification(notif);
        if (!isNotifDeleted){
            throw new UserAmendFriendRequestException("Failed to delete notification");
        }

        String status = "ACCEPTED";
        if (!isAccepted) status="REJECTED";
        boolean isFriendReqAmended = userRepo.amendFriendRequest(req, status);
        if (!isFriendReqAmended){
            throw new UserAmendFriendRequestException("Failed to update friend request");
        }
        if (!isAccepted && isFriendReqAmended) return true;
        boolean addedToFriendList = userRepo.addToFriendsList(req);
        if (!addedToFriendList) {
            throw new UserAmendFriendRequestException("faled to add to friend list");
        }
        return true;
    }

    //get all friendinfo obj
    public List<FriendInfo> getAllFriends(String userId){
        List<String> friendIds = userRepo.getAllFriends(userId);
        List<FriendInfo> friends = userRepo.getAllFriendObjects(friendIds);
        return friends;
    }
    

    //upoad profile pic
    public String uploadProfilePicture(InputStream is, String mediaType, long size, String userId){
        //upload to s3 first
        String newUrl = s3Repo.savePhotoToS3(is, mediaType, size, userId);
        if (newUrl==null){
            return null;
        }
        //then update user url in SQL
        boolean isUpdated = userRepo.updateProfilePicture(userId, newUrl);
        if (isUpdated) return newUrl;
        return null;
    }

    @Transactional(rollbackFor = {UserAmendDetailsException.class})
    public UserAmendDetailsDTO amendProfileDetails(String userId, UserAmendDetailsDTO dto) throws UserAmendDetailsException {
        System.out.println("UserService amendProfileDetails: This endpoint was reached");
        boolean isNameUpdated = userRepo.amendProfileDetails(userId, dto);
        if (!isNameUpdated){
            System.out.println("Cannot amend user names");
            throw new UserAmendDetailsException();
        }
        boolean isInterestUpdated = userRepo.amendInterests(userId, dto);
        if (!isInterestUpdated){
            System.out.println("Cannot amend user interests");
            throw new UserAmendDetailsException();
        }
        return dto;
    }


}
