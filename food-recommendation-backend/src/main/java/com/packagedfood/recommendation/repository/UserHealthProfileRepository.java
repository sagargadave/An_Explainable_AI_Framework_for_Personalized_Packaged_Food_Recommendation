package com.packagedfood.recommendation.repository;

import com.packagedfood.recommendation.entity.UserHealthProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserHealthProfileRepository
        extends JpaRepository<UserHealthProfile, Long> {
}