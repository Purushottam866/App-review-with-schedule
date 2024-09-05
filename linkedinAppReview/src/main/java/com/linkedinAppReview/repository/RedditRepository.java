package com.linkedinAppReview.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.linkedinAppReview.dto.RedditDto;



@Repository
public interface RedditRepository extends JpaRepository<RedditDto, Integer>{


}
