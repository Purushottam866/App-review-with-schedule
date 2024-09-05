package com.linkedinAppReview.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import com.linkedinAppReview.dto.MediaPost;
import com.linkedinAppReview.dto.QuantumShareUser;

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
}
