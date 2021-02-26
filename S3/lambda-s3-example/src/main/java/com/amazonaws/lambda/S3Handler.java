package com.amazonaws.lambda;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class S3Handler implements RequestHandler<S3Event, String>{
	
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	@Override
	  public String handleRequest(S3Event s3event, Context context){
		
		LambdaLogger logger = context.getLogger();
		logger.log("EVENT: " + gson.toJson(s3event));
		
		S3EventNotificationRecord record = s3event.getRecords().get(0);
	      
	    String srcBucket = record.getS3().getBucket().getName();	    
	    String srckey = record.getS3().getObject().getKey();
	    
	    logger.log("Bucket Name ::" + srcBucket);	    
	    logger.log("Key Name ::" + srckey);
	    
	 // Object key may have spaces or unicode non-ASCII characters.
        String srcKey = record.getS3().getObject().getUrlDecodedKey();
	    
     // Download the image from S3 into a stream
        AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
        S3Object s3Object = s3Client.getObject(new GetObjectRequest(srcBucket, srcKey));
        InputStream objectData = s3Object.getObjectContent();
        
     // Read the source image
        BufferedImage srcImage;
		try {
			
			srcImage = ImageIO.read(objectData);
			
	        int srcHeight = srcImage.getHeight();
	        int srcWidth = srcImage.getWidth();
	        
	        logger.log("Src Image Height ::" + srcHeight);	    
		    logger.log("Src Image Width ::" + srcWidth);
		    
		} catch (IOException e) {
			
			e.printStackTrace();
		}

	    
	    return "Ok";
	}
}
