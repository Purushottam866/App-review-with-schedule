package com.linkedinAppReview.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.linkedinAppReview.dto.MediaPost;
import com.linkedinAppReview.dto.QuantumShareUser;
import com.linkedinAppReview.helper.CustomMultipartFile;


@Service
public class PostSchedulingService {

	@Autowired
	PostService postServices;
	
	@Autowired
    private TaskScheduler taskScheduler;

    @SuppressWarnings("deprecation")
	public void schedulePost(String subreddit, String title, MediaPost mediaPost, QuantumShareUser user) {
        Runnable task = () -> postServices.submitPost(subreddit, title,user.getSocialAccounts(), mediaPost);
        System.out.println("scheduled success");
        LocalDateTime scheduledTime = mediaPost.getScheduledTime();
        taskScheduler.schedule(task, Date.from(scheduledTime.atZone(ZoneId.systemDefault()).toInstant()));
    }
    
//    // Method to schedule LinkedIn post
//    @SuppressWarnings("deprecation")
//	public void scheduleLinkedInPost(MediaPost mediaPost, MultipartFile mediaFile, QuantumShareUser user) {
//    	 System.out.println("Pic Size = " + mediaFile.getSize());
//        Runnable task = () -> postServices.postOnLinkedInSchedule(mediaPost, mediaFile, user.getSocialAccounts());
//        System.out.println("LinkedIn post scheduled successfully");
//        LocalDateTime scheduledTime = mediaPost.getScheduledTime();
//        taskScheduler.schedule(task, Date.from(scheduledTime.atZone(ZoneId.systemDefault()).toInstant()));
//    }
    
    @SuppressWarnings("deprecation")
    public void scheduleLinkedInPost(MediaPost mediaPost, MultipartFile mediaFile, QuantumShareUser user) throws IOException {
        File tempFile = File.createTempFile("temp", mediaFile.getOriginalFilename());
        mediaFile.transferTo(tempFile); // This line can throw IOException

        Runnable task = () -> {
            try {
                // Create an instance of CustomMultipartFile
                MultipartFile savedMediaFile = new CustomMultipartFile(tempFile);
                postServices.postOnLinkedInSchedule(mediaPost, savedMediaFile, user.getSocialAccounts());
            } finally {
                // Clean up the temporary file after use
                if (tempFile != null && tempFile.exists()) {
                    tempFile.delete();
                }
            }
        };

        LocalDateTime scheduledTime = mediaPost.getScheduledTime();
        taskScheduler.schedule(task, Date.from(scheduledTime.atZone(ZoneId.systemDefault()).toInstant()));
    }

}
