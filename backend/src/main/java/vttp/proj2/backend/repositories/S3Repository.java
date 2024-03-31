package vttp.proj2.backend.repositories;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

@Repository
public class S3Repository {
    
    @Autowired
    private AmazonS3 s3;

    private final String BUCKET_NAME = "squiggled-csf";

    public String savePhotoToS3(InputStream is, String mediaType, long size, String userId) {
        ObjectMetadata metadata = new ObjectMetadata();
        Map<String, String> data = new HashMap<>();
        data.put("userId", userId);
        metadata.setContentType(mediaType);
        metadata.setContentLength(size);
        metadata.setUserMetadata(data);

        PutObjectRequest putReq = new PutObjectRequest(
            BUCKET_NAME, 
            userId, 
            is, metadata);
        putReq = putReq.withCannedAcl(CannedAccessControlList.PublicRead); //sets the access level to public read

        PutObjectResult result = s3.putObject(putReq); 
        // System.out.println("putobjectresult in s3 repo " + result);
        String urlToReturn= s3.getUrl(BUCKET_NAME, userId).toExternalForm();
        // System.out.println("url to return from s3 repo: " + urlToReturn);
        return urlToReturn;
    } 

}
