package com.matosic.Facebook.repository.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matosic.Facebook.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

	User findByUsernameAndPassword(String username, String password);
	
	Optional<User> findByEmail(String email);
}
