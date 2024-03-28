package vttp.proj2.backend.repositories;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import vttp.proj2.backend.models.ForumThread;
import vttp.proj2.backend.models.ThreadMessage;

@Repository
public class ForumRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    // get all
    public List<ForumThread> getAllThreads() {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "createdDate"));
        List<Document> foundDocs = mongoTemplate.find(query, Document.class, "forum");
        List<ForumThread> threads = new ArrayList<>();
        for (Document doc : foundDocs) {
            threads.add(documentToThread(doc));
        }
        return threads;
    }

    private ForumThread documentToThread(Document doc) {
        ForumThread thread = new ForumThread();
        ObjectId objectId = doc.getObjectId("_id");
        if (objectId != null) {
            thread.setId(objectId.toHexString()); 
        }
        thread.setUserId(doc.getString("userId"));
        thread.setFirstName(doc.getString("firstName"));
        thread.setUserProfilePic(doc.getString("userProfilePic"));
        thread.setCreatedDate(doc.getDate("createdDate"));
        thread.setTitle(doc.getString("title"));
        thread.setContent(doc.getString("content"));

        List<Document> messagesDocs = doc.getList("messages", Document.class);
        List<ThreadMessage> messages = new ArrayList<>();
        if (messagesDocs != null) {
            for (Document messageDoc : messagesDocs) {
                messages.add(documentToThreadMessage(messageDoc));
            }
        }
        thread.setMessages(messages);
        return thread;
    }

    private ThreadMessage documentToThreadMessage(Document doc) {
        ThreadMessage message = new ThreadMessage();
        message.setUserId(doc.getString("userId"));
        message.setFirstName(doc.getString("firstName"));
        message.setUserProfilePic(doc.getString("userProfilePic"));
        message.setPostedDate(doc.getDate("postedDate"));
        message.setContent(doc.getString("content"));
        return message;
    }

    // post a new thread
    public ForumThread createThread(ForumThread thread) {
        ForumThread createdThread = mongoTemplate.insert(thread, "forum");   
        return createdThread;
    }

    // add message to thread
    public boolean addMessageToThread(String threadId, ThreadMessage message) {
        Query query = new Query(Criteria.where("_id").is(threadId));
        Update update = new Update().push("messages", message);
        UpdateResult result = mongoTemplate.updateFirst(query, update, Thread.class);
        if (result.getMatchedCount() == 1) {
            System.out.println("Add message operation successful");
            return true; 
        } else {
            System.out.println("No document matching the query was found to update");
            return false; 
        }
    }

    public ForumThread postNewReply(String threadId, ThreadMessage message) {
        Query query = new Query(Criteria.where("_id").is(threadId));
        Update update = new Update().push("messages", message);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);        
        ForumThread updatedThread = mongoTemplate.findAndModify(query, update, options, ForumThread.class, "forum");
        // System.out.println("updatedThread "+ updatedThread);
        return updatedThread; 
    }

}
