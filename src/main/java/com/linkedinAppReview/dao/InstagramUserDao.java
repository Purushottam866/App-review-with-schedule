package com.linkedinAppReview.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.linkedinAppReview.dto.InstagramUser;
import com.linkedinAppReview.repository.InstagramRepository;

@Component
public class InstagramUserDao {
	
	@Autowired
	InstagramRepository instagramRepository;
	
	public String findLastUserId() {

		InstagramUser latestUser = instagramRepository.findTopByOrderByInstaIdDesc();
		if (latestUser != null) {
			return latestUser.getInstaId();
		}
		return null;
	}

	public void save(InstagramUser instagramUser) {
		instagramRepository.save(instagramUser);
		
	}

	public InstagramUser findById(String instaId) {
		return instagramRepository.findById(instaId).orElse(null);
	}
	
	public void deleteUser(InstagramUser user) {
		instagramRepository.delete(user);
	}

}
