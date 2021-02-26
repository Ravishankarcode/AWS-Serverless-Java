package com.amazonaws;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Handler implements RequestHandler<S3Event, String>{
		
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
	    
	    final String FROM = "ravi0488@gmail.com";
        final String TO = "ravi0488@gmail.com";
        final String SUBJECT = "Upload Successful";
        final String HTMLBODY = srckey+" has been successfully uploaded to "+srcBucket;
        final String TEXTBODY = "This email was sent through Amazon SES " + "using the AWS SDK for Java.";
        
        try {
            AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
            	.withRegion(Regions.AP_SOUTH_1).build();
            SendEmailRequest request = new SendEmailRequest()
                .withDestination(
                    new Destination().withToAddresses(TO))
                .withMessage(new Message()
                    .withBody(new Body()
                        .withHtml(new Content()
                            .withCharset("UTF-8").withData(HTMLBODY))
                        .withText(new Content()
                            .withCharset("UTF-8").withData(TEXTBODY)))
                    .withSubject(new Content()
                        .withCharset("UTF-8").withData(SUBJECT)))
                .withSource(FROM);

            client.sendEmail(request);
            logger.log("Email Sent!!");
          } catch (Exception ex) {
            System.out.println("The email was not sent. Error message: " + ex.getMessage());
          }
	    
	    return "Ok";
	}


}
