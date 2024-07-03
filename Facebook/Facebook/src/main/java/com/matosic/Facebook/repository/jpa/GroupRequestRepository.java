package com.matosic.Facebook.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matosic.Facebook.model.GroupRequest;
import com.matosic.Facebook.model.User;
import com.matosic.Facebook.model.UserGroup;

@Repository
public interface GroupRequestRepository extends JpaRepository<GroupRequest, Long>{

	List<GroupRequest> findByGroupAndUser(UserGroup group, User user);

}
