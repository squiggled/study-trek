package vttp.proj2.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp.proj2.backend.models.ForumThread;
import vttp.proj2.backend.models.ThreadMessage;
import vttp.proj2.backend.repositories.ForumRepository;

@Service
public class ForumService {

    @Autowired
    ForumRepository forumRepo;

    public List<ForumThread> getAllThreads(){
        return forumRepo.getAllThreads();
    }

    public ForumThread postNewTopic(ForumThread thread){
        ForumThread newThread = forumRepo.createThread(thread);
        return newThread;
    }

    public ForumThread postNewReply(String threadId, ThreadMessage message){
        // System.out.println("Reached endpoint: ForumService");
        ForumThread updatedThread = forumRepo.postNewReply(threadId, message); 
        return updatedThread;
    }
    
}
