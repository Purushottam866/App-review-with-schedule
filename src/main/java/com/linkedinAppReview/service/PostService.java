package com.linkedinAppReview.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.linkedinAppReview.configure.ConfigurationClass;
import com.linkedinAppReview.dao.FacebookUserDao;
import com.linkedinAppReview.dao.InstagramUserDao;
import com.linkedinAppReview.dao.LinkedInProfileDao;
import com.linkedinAppReview.dao.QuantumShareUserDao;
import com.linkedinAppReview.dto.LinkedInProfileDto;
import com.linkedinAppReview.dto.MediaPost;
import com.linkedinAppReview.dto.RedditDto;
import com.linkedinAppReview.dto.SocialAccounts;
import com.linkedinAppReview.response.ErrorResponse;
import com.linkedinAppReview.response.ResponseStructure;
import com.linkedinAppReview.response.ResponseWrapper;


@Service
public class PostService {

	@Autowired
	ResponseStructure<String> structure;

	@Autowired
	FacebookPostService facebookPostService;

	@Autowired
	InstagramService instagramService;

	@Autowired
	FacebookUserDao facebookUserDao;

	@Autowired
	QuantumShareUserDao userDao;

	@Autowired
	ErrorResponse errorResponse;

	@Autowired
	ConfigurationClass config;

	@Autowired
	InstagramUserDao instagramUserDao;
	
	@Autowired
	LinkedInProfilePostService linkedInProfilePostService;
	
	@Autowired
	LinkedInProfileDao linkedInProfileDao;
	
	@Autowired
	ResponseStructure<JsonNode> responseStructure;
	
	@Autowired	
	RedditDto redditDto;
	
	@Autowired	
	RedditService redditService;

	public ResponseEntity<List<Object>> postOnFb(MediaPost mediaPost, MultipartFile mediaFile, SocialAccounts socialAccounts) {
		List<Object> response = config.getList();
		if (mediaPost.getMediaPlatform().contains("facebook")) {
			if (socialAccounts == null || socialAccounts.getFacebookUser() == null) {
				structure.setMessage("Please connect your facebook account");
				structure.setCode(HttpStatus.NOT_FOUND.value());
				structure.setPlatform("facebook");
				structure.setStatus("error");
				structure.setData(null);
				response.add(structure);
				return new ResponseEntity<List<Object>>(response,HttpStatus.NOT_FOUND);
			}
			if (socialAccounts.getFacebookUser() != null)
				return facebookPostService.postMediaToPage(mediaPost, mediaFile,
						facebookUserDao.findById(socialAccounts.getFacebookUser().getFbId()));
			else {
				structure.setMessage("Please connect your facebook account");
				structure.setCode(HttpStatus.NOT_FOUND.value());
				structure.setPlatform("facebook");
				structure.setStatus("error");
				structure.setData(null);
				response.add(structure);
				return new ResponseEntity<List<Object>>(response,HttpStatus.NOT_FOUND);
			}
		}
		return null;
	}

	public ResponseEntity<ResponseWrapper> postOnInsta(MediaPost mediaPost, MultipartFile mediaFile,
			SocialAccounts socialAccounts) {
		System.out.println("main service");
		if (mediaPost.getMediaPlatform().contains("instagram")) {
			if (socialAccounts == null || socialAccounts.getInstagramUser() == null) {
				structure.setMessage("Please connect your Instagram account");
				structure.setCode(HttpStatus.NOT_FOUND.value());
				structure.setPlatform("instagram");
				structure.setStatus("error");
				structure.setData(null);
				return new ResponseEntity<ResponseWrapper>(config.getResponseWrapper(structure), HttpStatus.NOT_FOUND);
			}
			if (socialAccounts.getInstagramUser() != null)
				return instagramService.postMediaToPage(mediaPost, mediaFile,
						instagramUserDao.findById(socialAccounts.getInstagramUser().getInstaId()));
			else {
				structure.setMessage("Please connect your Instagram account");
				structure.setCode(HttpStatus.NOT_FOUND.value());
				structure.setPlatform("facebook");
				structure.setStatus("error");
				structure.setData(null);
				return new ResponseEntity<ResponseWrapper>(config.getResponseWrapper(structure), HttpStatus.NOT_FOUND);
			}
		}
		return null;
	}

	// POSTING ON LINKEDIN PROFILE
	public ResponseEntity<ResponseWrapper> postOnLinkedIn(MediaPost mediaPost, MultipartFile mediaFile,
	        SocialAccounts socialAccounts) {
	    
	    if (mediaPost.getMediaPlatform().contains("LinkedIn")) {
	        if (socialAccounts == null || socialAccounts.getLinkedInProfileDto() == null) {
	            structure.setMessage("Please connect your LinkedIn account");
	            structure.setCode(HttpStatus.NOT_FOUND.value());
	            structure.setPlatform("LinkedIn");
	            structure.setStatus("error");
	            structure.setData(null);
	            return new ResponseEntity<ResponseWrapper>(config.getResponseWrapper(structure), HttpStatus.NOT_FOUND);
	        }
	        
	        LinkedInProfileDto linkedInProfileUser = socialAccounts.getLinkedInProfileDto();
	        ResponseStructure<String> response;
	        
	        if (mediaFile != null && !mediaFile.isEmpty() && mediaPost.getCaption() != null && !mediaPost.getCaption().isEmpty()) {
	            // Both file and caption are present
	            response = linkedInProfilePostService.uploadImageToLinkedIn(mediaFile, mediaPost.getCaption(), linkedInProfileUser);
	        } else if (mediaPost.getCaption() != null && !mediaPost.getCaption().isEmpty()) {
	            // Only caption is present
	            response = linkedInProfilePostService.createPostProfile(mediaPost.getCaption(), linkedInProfileUser);
	        } else if (mediaFile != null && !mediaFile.isEmpty()) {
	            // Only file is present
	            response = linkedInProfilePostService.uploadImageToLinkedIn(mediaFile, "", linkedInProfileUser);
	        } else {
	            // Neither file nor caption are present
	            structure.setStatus("Failure");
	            structure.setMessage("Please connect your LinkedIn account");
	            structure.setCode(HttpStatus.BAD_REQUEST.value());
	            return new ResponseEntity<ResponseWrapper>(config.getResponseWrapper(structure), HttpStatus.BAD_REQUEST);
	        }
	        
	        // Map the response from ResponseStructure to ResponseWrapper
	        structure.setStatus(response.getStatus());
	        structure.setMessage(response.getMessage());
	        structure.setCode(response.getCode());
	        structure.setData(response.getData());
	        return new ResponseEntity<ResponseWrapper>(config.getResponseWrapper(structure), HttpStatus.valueOf(response.getCode()));
	    }
	    
	    structure.setMessage("Please connect your LinkedIn account");
	    structure.setCode(HttpStatus.BAD_REQUEST.value());
	    structure.setPlatform("LinkedIn");
	    structure.setStatus("error");
	    structure.setData(null);
	    return new ResponseEntity<ResponseWrapper>(config.getResponseWrapper(structure), HttpStatus.BAD_REQUEST);
	}

	
	// POSTING ON LINKEDIN PAGE
	public ResponseEntity<ResponseWrapper> postOnLinkedInPage(MediaPost mediaPost, MultipartFile mediaFile,
	        SocialAccounts socialAccounts) {

	    ResponseStructure<String> response;

	    if (mediaPost.getMediaPlatform().contains("LinkedIn")) {
	        if (socialAccounts == null || socialAccounts.getLinkedInProfileDto() == null) {
	            structure.setMessage("Please connect your LinkedIn account");
	            structure.setCode(HttpStatus.NOT_FOUND.value());
	            structure.setPlatform("LinkedIn");
	            structure.setStatus("error");
	            structure.setData(null);
	            return new ResponseEntity<ResponseWrapper>(config.getResponseWrapper(structure), HttpStatus.NOT_FOUND);
	        }

	        LinkedInProfileDto linkedInProfileUser = socialAccounts.getLinkedInProfileDto();

	        if (mediaFile != null && !mediaFile.isEmpty() && mediaPost.getCaption() != null && !mediaPost.getCaption().isEmpty()) {
	            // Both file and caption are present
	            response = linkedInProfilePostService.uploadImageToLinkedInPage(mediaFile, mediaPost.getCaption(), linkedInProfileUser);
	        } else if (mediaPost.getCaption() != null && !mediaPost.getCaption().isEmpty()) {
	            // Only caption is present
	            response = linkedInProfilePostService.createPostPage(mediaPost.getCaption(), linkedInProfileUser);
	        } else if (mediaFile != null && !mediaFile.isEmpty()) {
	            // Only file is present
	            response = linkedInProfilePostService.uploadImageToLinkedInPage(mediaFile, "", linkedInProfileUser);
	        } else {
	            // Neither file nor caption are present
	            structure.setStatus("Failure");
	            structure.setMessage("Please connect your LinkedIn account");
	            structure.setCode(HttpStatus.BAD_REQUEST.value());
	            return new ResponseEntity<ResponseWrapper>(config.getResponseWrapper(structure), HttpStatus.BAD_REQUEST);
	        }

	        // Map the response from ResponseStructure to ResponseWrapper
	        structure.setStatus(response.getStatus());
	        structure.setMessage(response.getMessage());
	        structure.setCode(response.getCode());
	        structure.setData(response.getData());
	        return new ResponseEntity<ResponseWrapper>(config.getResponseWrapper(structure), HttpStatus.valueOf(response.getCode()));
	    }

	    structure.setMessage("Please connect your LinkedIn account");
	    structure.setCode(HttpStatus.BAD_REQUEST.value());
	    structure.setPlatform("LinkedIn");
	    structure.setStatus("error");
	    structure.setData(null);
	    return new ResponseEntity<ResponseWrapper>(config.getResponseWrapper(structure), HttpStatus.BAD_REQUEST);
	}
	
	//TEXT POSTING TO REDDIT
	public ResponseStructure<JsonNode> submitPost(
	        String subreddit,
	        String title,
	        SocialAccounts socialAccounts,
	        MediaPost mediaPost) {

		String text = mediaPost.getCaption();
	    ResponseStructure<JsonNode> responseStructure = new ResponseStructure<>();

	    // Check if mediaPlatform is null or empty
	    if (mediaPost.getMediaPlatform() == null || mediaPost.getMediaPlatform().isEmpty()) {
	        responseStructure.setMessage("Please select the media platform");
	        responseStructure.setStatus("error");
	        responseStructure.setCode(HttpStatus.BAD_REQUEST.value());
	        responseStructure.setPlatform("Reddit");
	        responseStructure.setData(null);
	        return responseStructure;
	    }

	    if (mediaPost.getMediaPlatform().contains("Reddit")) {
	        if (socialAccounts == null || socialAccounts.getRedditDto() == null) {
	            responseStructure.setMessage("Please connect your Reddit account");
	            responseStructure.setStatus("error");
	            responseStructure.setCode(HttpStatus.NOT_FOUND.value());
	            responseStructure.setPlatform("Reddit");
	            responseStructure.setData(null);
	            return responseStructure;
	        }

	        RedditDto redditUser = socialAccounts.getRedditDto();

	        // Check if subreddit, title, or text are missing or empty
	        if (subreddit == null || subreddit.trim().isEmpty()) {
	            responseStructure.setMessage("Subreddit is required");
	            responseStructure.setStatus("error");
	            responseStructure.setCode(HttpStatus.BAD_REQUEST.value());
	            responseStructure.setPlatform("Reddit");
	            responseStructure.setData(null);
	            return responseStructure;
	        }

	        if (title == null || title.trim().isEmpty()) {
	            responseStructure.setMessage("Title is required");
	            responseStructure.setStatus("error");
	            responseStructure.setCode(HttpStatus.BAD_REQUEST.value());
	            responseStructure.setPlatform("Reddit");
	            responseStructure.setData(null);
	            return responseStructure;
	        }

	        if (text == null || text.trim().isEmpty()) {
	            responseStructure.setMessage("Text is required");
	            responseStructure.setStatus("error");
	            responseStructure.setCode(HttpStatus.BAD_REQUEST.value());
	            responseStructure.setPlatform("Reddit");
	            responseStructure.setData(null);
	            return responseStructure;
	        }
	        System.out.println(subreddit + " " + title + " " + text + " " + redditUser);
	        // If all parameters are present and not empty, proceed to submit the post
	        responseStructure = redditService.submitPost(subreddit, title, text, redditUser);

	        // Customize the response structure
	        if (responseStructure.getStatus().equals("success")) {
	            responseStructure.setMessage("Text post submitted successfully");
	            responseStructure.setCode(HttpStatus.OK.value());
	            responseStructure.setPlatform("Reddit");
	        }

	        return responseStructure;
	    } else {
	        responseStructure.setMessage("Please connect your Reddit account");
	        responseStructure.setStatus("error");
	        responseStructure.setCode(HttpStatus.NOT_FOUND.value());
	        responseStructure.setPlatform("Reddit");
	        responseStructure.setData(null);
	        return responseStructure;
	    }
	}



		public ResponseEntity<ResponseStructure<JsonNode>> submitLinkPost(String subreddit, String title, String url,
				SocialAccounts socialAccounts, MediaPost mediaPost) {
			 ResponseStructure<JsonNode> responseStructure = new ResponseStructure<>();

			    // Check if mediaPlatform is null or empty
			    if (mediaPost.getMediaPlatform() == null || mediaPost.getMediaPlatform().isEmpty()) {
			        responseStructure.setMessage("Please select the media platform");
			        responseStructure.setStatus("error");
			        responseStructure.setCode(HttpStatus.BAD_REQUEST.value());
			        responseStructure.setPlatform("Reddit");
			        responseStructure.setData(null);
			        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseStructure);
			    }

			    if (mediaPost.getMediaPlatform().contains("Reddit")) {
			        if (socialAccounts == null || socialAccounts.getRedditDto() == null) {
			            responseStructure.setMessage("Please connect your Reddit account");
			            responseStructure.setStatus("error");
			            responseStructure.setCode(HttpStatus.NOT_FOUND.value());
			            responseStructure.setPlatform("Reddit");
			            responseStructure.setData(null);
			            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseStructure);
			        }

			        RedditDto redditUser = socialAccounts.getRedditDto();

			        // Check if subreddit, title, or url are missing or empty
			        if (subreddit == null || subreddit.trim().isEmpty()) {
			            responseStructure.setMessage("Subreddit is required");
			            responseStructure.setStatus("error");
			            responseStructure.setCode(HttpStatus.BAD_REQUEST.value());
			            responseStructure.setPlatform("Reddit");
			            responseStructure.setData(null);
			            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseStructure);
			        }

			        if (title == null || title.trim().isEmpty()) {
			            responseStructure.setMessage("Title is required");
			            responseStructure.setStatus("error");
			            responseStructure.setCode(HttpStatus.BAD_REQUEST.value());
			            responseStructure.setPlatform("Reddit");
			            responseStructure.setData(null);
			            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseStructure);
			        }

			        if (url == null || url.trim().isEmpty()) {
			            responseStructure.setMessage("URL is required");
			            responseStructure.setStatus("error");
			            responseStructure.setCode(HttpStatus.BAD_REQUEST.value());
			            responseStructure.setPlatform("Reddit");
			            responseStructure.setData(null);
			            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseStructure);
			        }

			        // If all parameters are present and not empty, proceed to submit the post
			        responseStructure = redditService.submitLinkPost(subreddit, title, url, redditUser);

			        // Customize the response structure
			        if (responseStructure.getStatus().equals("success")) {
			            responseStructure.setMessage("Link post submitted successfully");
			            responseStructure.setCode(HttpStatus.OK.value());
			            responseStructure.setPlatform("Reddit");
			        } 

			        return ResponseEntity.status(responseStructure.getCode()).body(responseStructure);
			    } else {
			        responseStructure.setMessage("Please connect your Reddit account");
			        responseStructure.setStatus("error");
			        responseStructure.setCode(HttpStatus.NOT_FOUND.value());
			        responseStructure.setPlatform("Reddit");
			        responseStructure.setData(null);
			        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseStructure);
			    }
			}
}
