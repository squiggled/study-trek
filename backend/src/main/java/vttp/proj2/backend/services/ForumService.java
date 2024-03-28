package vttp.proj2.backend.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp.proj2.backend.models.ForumThread;
import vttp.proj2.backend.repositories.ForumRepository;

@Service
public class ForumService {

    @Autowired
    ForumRepository forumRepo;

    public List<ForumThread> getAllThreads(){
        return forumRepo.getAllThreads();
    }

    public ForumThread postNew(ForumThread thread){
        ForumThread newThread = forumRepo.createThread(thread);
        return newThread;
    }
    
}
