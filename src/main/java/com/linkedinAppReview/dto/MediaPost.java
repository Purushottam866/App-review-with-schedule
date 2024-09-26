package com.linkedinAppReview.dto;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class MediaPost {
	private String mediaPlatform;
	private String caption;
	 private LocalDateTime scheduledTime;
}
