package vttp.proj2.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vttp.proj2.backend.models.ForumThread;
import vttp.proj2.backend.models.ThreadMessage;
import vttp.proj2.backend.services.ForumService;

@RestController
@RequestMapping("/api")
public class ForumController {

    @Autowired
    ForumService forumSvc;

    @GetMapping("/forum")
    public ResponseEntity<?> getAllThreads(){
        List<ForumThread> threads = forumSvc.getAllThreads();
        if (threads==null){
            return ResponseEntity.notFound().build(); 
        }
        return ResponseEntity.ok(threads);
    }

    @PostMapping("/forum/topic/new")
    public ResponseEntity<?> postNewTopic(@RequestBody ForumThread thread){
        ForumThread newThread = forumSvc.postNewTopic(thread);
        if (newThread==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(newThread);
    }

    @PostMapping("/forum/topic/{threadId}/reply")
    public ResponseEntity<?> postNewReply(@RequestBody ThreadMessage message, @PathVariable String threadId) {
        // System.out.println("ForumController message " + message);
        // System.out.println("ForumController threadId " + threadId);
        ForumThread updatedThread = forumSvc.postNewReply(threadId, message);
        if (null==updatedThread){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedThread); 
    }
    
}
