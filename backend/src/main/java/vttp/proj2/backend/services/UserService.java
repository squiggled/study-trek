package vttp.proj2.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp.proj2.backend.exceptions.UserAddCourseException;
import vttp.proj2.backend.models.AccountInfo;
import vttp.proj2.backend.models.CourseDetails;
import vttp.proj2.backend.models.Curriculum;
import vttp.proj2.backend.repositories.UserRepository;

@Service
public class UserService {
    
    @Autowired
    UserRepository userRepo;

    public AccountInfo getUserByEmail(String email){
        return userRepo.findUserByEmail(email);
    }

    public String getFirstNameByEmail(String email){
        return userRepo.getFirstNameByEmail(email);
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
}
