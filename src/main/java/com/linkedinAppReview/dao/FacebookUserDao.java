package com.linkedinAppReview.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.linkedinAppReview.dto.FaceBookUser;
import com.linkedinAppReview.repository.FacebookUserRepository;

@Component
public class FacebookUserDao {
	@Autowired
	FacebookUserRepository repository;

	public String findLastUserId() {

		FaceBookUser latestUser = repository.findTopByOrderByFbIdDesc();
		if (latestUser != null) {
			return latestUser.getFbId();
		}
		return null;
	}

	public void saveUser(FaceBookUser user) {
		repository.save(user);
	}

	public FaceBookUser findById(String id) {
		return repository.findById(id).orElse(null);
	}

	public void deleteFbUser(FaceBookUser faceBookUser) {
		repository.delete(faceBookUser);
	}

}
