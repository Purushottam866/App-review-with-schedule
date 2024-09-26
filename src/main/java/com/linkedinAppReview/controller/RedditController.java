package com.linkedinAppReview.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.databind.JsonNode;
import com.linkedinAppReview.configure.JwtUtilConfig;
import com.linkedinAppReview.dao.QuantumShareUserDao;
import com.linkedinAppReview.dto.MediaPost;
import com.linkedinAppReview.dto.QuantumShareUser;
import com.linkedinAppReview.response.ResponseStructure;
import com.linkedinAppReview.service.PostService;
import com.linkedinAppReview.service.RedditService;

import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/quantum-share")
public class RedditController {

	@Autowired
	HttpServletRequest request;
	
	@Autowired
	ResponseStructure<String> structure;
	
	@Autowired
	JwtUtilConfig jwtUtilConfig;
	
	@Autowired
	QuantumShareUserDao userDao;
	
	@Autowired
	RedditService redditService;
	
	@Value("${reddit.client_id}")
    private String clientId;

    @Value("${reddit.redirect_uri}")
    private String redirectUri;

    @Value("${reddit.scope}")
    private String scope;
	
    @Autowired
    PostService postService;   
    
    @Autowired
    MediaPost mediaPost;
    
    @GetMapping("/connect-reddit")
    public RedirectView authorize() {
        String authorizationUrl = redditService.getAuthorizationUrl();
        return new RedirectView(authorizationUrl);
    }
    
    
    @GetMapping("/connect/reddit")
    public ResponseEntity<Map<String, String>> getRedditAuthUrl(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Map<String, String> authUrlParams = new HashMap<>();
        
        if (token == null || !token.startsWith("Bearer ")) {
            authUrlParams.put("status", "error");
            authUrlParams.put("code", "115");
            authUrlParams.put("message", "Missing or invalid authorization token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authUrlParams);
        }

        String jwtToken = token.substring(7); // remove "Bearer " prefix
        String userId = jwtUtilConfig.extractUserId(jwtToken);
        QuantumShareUser user = userDao.fetchUser(userId);

        if (user == null) {
            authUrlParams.put("status", "error");
            authUrlParams.put("code", String.valueOf(HttpStatus.NOT_FOUND.value()));
            authUrlParams.put("message", "User doesn't exist, please sign up");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(authUrlParams);
        }
        authUrlParams.put("client_id", clientId);
        authUrlParams.put("redirect_uri", redirectUri);
        authUrlParams.put("scope", scope);
        authUrlParams.put("status", "success");

        return ResponseEntity.ok(authUrlParams);
	    }

	    public ResponseEntity<Map<String, String>> getRedditAuth() {
	        Map<String, String> authUrlParams = new HashMap<>();
	        authUrlParams.put("client_id", clientId);
	        authUrlParams.put("response_type", "code");
	        authUrlParams.put("state", "string");
	        authUrlParams.put("redirect_uri", redirectUri);
	        authUrlParams.put("duration", "permanent");
	        authUrlParams.put("scope", scope);
	        return ResponseEntity.ok(authUrlParams);
	    }
	 
	 @GetMapping("/callback-redirect")
	 public ResponseEntity<ResponseStructure<Map<String, String>>> handleRedirect(@RequestParam("code") String code) {
		 String token = request.getHeader("Authorization");
	        ResponseStructure<Map<String, String>> responseStructure = new ResponseStructure<>();

	        if (token == null || !token.startsWith("Bearer ")) {
	            responseStructure.setMessage("Missing or invalid authorization token");
	            responseStructure.setStatus("error");
	            responseStructure.setCode(HttpStatus.UNAUTHORIZED.value());
	            responseStructure.setPlatform("Reddit");
	            responseStructure.setData(null);
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseStructure);
	        }

	        String jwtToken = token.substring(7); // remove "Bearer " prefix
	        String userId = jwtUtilConfig.extractUserId(jwtToken);
	        QuantumShareUser user = userDao.fetchUser(userId);

	        if (user == null) {
	            responseStructure.setMessage("User doesn't exist, please sign up");
	            responseStructure.setStatus("error");
	            responseStructure.setCode(HttpStatus.NOT_FOUND.value());
	            responseStructure.setPlatform("Reddit");
	            responseStructure.setData(null);
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseStructure);
	        }

	        responseStructure = redditService.getAccessToken(code,user);

	        // Customize the response structure
	        if (responseStructure.getStatus().equals("success")) {
	            responseStructure.setMessage("Reddit connected successfully");
	            responseStructure.setCode(HttpStatus.OK.value());
	            responseStructure.setPlatform("Reddit");
	        }

	        return ResponseEntity.status(responseStructure.getCode()).body(responseStructure);
	    
	    }
	 
	 
	 @PostMapping("/callback/reddit")
	 public ResponseEntity<ResponseStructure<Map<String, String>>> handleRedirectUrl(@RequestParam("code") String code) {
		 String token = request.getHeader("Authorization");
	        ResponseStructure<Map<String, String>> responseStructure = new ResponseStructure<>();

	      
	        
	        if (token == null || !token.startsWith("Bearer ")) {
	            responseStructure.setMessage("Missing or invalid authorization token");
	            responseStructure.setStatus("error");
	            responseStructure.setCode(HttpStatus.UNAUTHORIZED.value());
	            responseStructure.setPlatform("Reddit");
	            responseStructure.setData(null);
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseStructure);
	        }

	        String jwtToken = token.substring(7); // remove "Bearer " prefix
	        
	        
	        
	        String userId = jwtUtilConfig.extractUserId(jwtToken);
	        QuantumShareUser user = userDao.fetchUser(userId);
	       
	        if (user == null) {
	        	
	            responseStructure.setMessage("User doesn't exist, please sign up");
	            responseStructure.setStatus("error");
	            responseStructure.setCode(HttpStatus.NOT_FOUND.value());
	            responseStructure.setPlatform("Reddit");
	            responseStructure.setData(null);
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseStructure);
	        }
	       
	        responseStructure = redditService.getAccessToken(code,user);

	        // Customize the response structure
	        if (responseStructure.getStatus().equals("success")) {
	            responseStructure.setMessage("Reddit connected successfully");
	            responseStructure.setCode(HttpStatus.OK.value());
	            responseStructure.setPlatform("Reddit");
	        }

	        return ResponseEntity.status(responseStructure.getCode()).body(responseStructure);
	    
	    } 
	 
	 
	 @GetMapping("/refreshToken")
	    public ResponseEntity<ResponseStructure<Map<String, String>>> handleRefreshToken(@RequestParam("refreshToken") String refreshToken) {
	        // Use the authorization code to get an access token and refresh token
	        ResponseStructure<Map<String, String>> responseStructure = redditService.refreshAccessToken(refreshToken);
	        return ResponseEntity.status(responseStructure.getCode()).body(responseStructure);
	    }
	 
	 
	
}
