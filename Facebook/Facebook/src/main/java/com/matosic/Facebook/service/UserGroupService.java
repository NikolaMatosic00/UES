package com.matosic.Facebook.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.matosic.Facebook.dto.UserGroupDTO;
import com.matosic.Facebook.model.GroupRequest;
import com.matosic.Facebook.model.User;
import com.matosic.Facebook.model.UserGroup;
import com.matosic.Facebook.repository.elasticsearch.ElasticUserGroupRepository;
import com.matosic.Facebook.repository.jpa.GroupRequestRepository;
import com.matosic.Facebook.repository.jpa.UserGroupRepository;
import com.matosic.Facebook.repository.jpa.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserGroupService {

    @Autowired
    private UserGroupRepository jpaUserGroupRepository;

    @Autowired
    private ElasticUserGroupRepository elasticUserGroupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRequestRepository groupRequestRepository;

    @Autowired
    private MinioService minioService;

    public List<UserGroup> getAllGroups() {
        return (List<UserGroup>) jpaUserGroupRepository.findAll();
    }

    @Transactional
    public String createUserGroup(UserGroupDTO userGroupDTO, User admin) throws IOException {
        String fileName = "group-" + System.currentTimeMillis() + ".pdf";
        String descriptionUrl = minioService.uploadFile(userGroupDTO.getDescriptionPdf(), fileName);

        UserGroup group = new UserGroup();
        group.setName(userGroupDTO.getName());
        group.setDescription(userGroupDTO.getDescription());
        group.setPdfUrl(descriptionUrl);
        elasticUserGroupRepository.save(group);
        group.setCreationDate(LocalDateTime.now());
        group.setAdmin(admin);
        group.setSuspended(false); // Nova grupa nije suspendovana po defaultu
        group.setUsers(new HashSet<>()); // Inicijalizujemo prazan skup korisnika

        // Dodajemo kreatora grupe kao člana
        group.getUsers().add(admin);

        jpaUserGroupRepository.save(group);
        log.info("Created group: {}", group);

        return "created";
    }


    @Transactional
    public boolean suspendGroup(Long groupId, String reason) {
        Optional<UserGroup> optionalGroup = jpaUserGroupRepository.findById(groupId);
        if (optionalGroup.isPresent()) {
            UserGroup group = optionalGroup.get();
            group.setSuspended(true);
            group.setSuspendedReason(reason);

            jpaUserGroupRepository.save(group);
            
            group.setUsers(null);
            group.setAdmin(null);
            elasticUserGroupRepository.save(group);
            log.info("Suspended group: {} - Reason: {}", group.getName(), reason);

            return true;
        }
        log.warn("Group with ID {} not found", groupId);
        return false;
    }


    @Transactional
    public boolean sendGroupJoinRequest(Long groupId, Long userId) {
        Optional<UserGroup> optionalGroup = jpaUserGroupRepository.findById(groupId);
        if (optionalGroup.isPresent()) {
            UserGroup group = optionalGroup.get();
            User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

            // Provera da li korisnik već ima zahtev za pridruživanje ovoj grupi
            List<GroupRequest> existingRequests = groupRequestRepository.findByGroupAndUser(group, user);
            if (!existingRequests.isEmpty()) {
                log.warn("User {} already sent a request to join group {}", user.getUsername(), group.getName());
                return false;
            }

            // Kreiranje i čuvanje zahteva za pridruživanje
            GroupRequest request = new GroupRequest(false, LocalDateTime.now(), group, user);
            groupRequestRepository.save(request);

            log.info("User {} sent a request to join group {}", user.getUsername(), group.getName());
            return true;
        }
        log.warn("Group with ID {} not found", groupId);
        return false;
    }
//
//    @Transactional
//    public boolean approveJoinRequest(Long requestId) {
//        Optional<GroupRequest> optionalRequest = groupRequestRepository.findById(requestId);
//        if (optionalRequest.isPresent()) {
//            GroupRequest request = optionalRequest.get();
//            request.setApproved(true);
//            groupRequestRepository.save(request);
//            
//            UserGroup group = request.getGroup();
//            group.getUsers().add(request.getUser());
//            
//            jpaUserGroupRepository.save(group);
//            elasticUserGroupRepository.save(group);
//
//            log.info("Join request with ID {} approved", requestId);
//            return true;
//        }
//        log.warn("Join request with ID {} not found", requestId);
//        return false;
//    }
//    @Transactional
//    public boolean addUserToGroup(Long groupId, Long userId) {
//    	Optional<UserGroup> optionalGroup = jpaUserGroupRepository.findById(groupId);
//    	if (optionalGroup.isPresent()) {
//    		UserGroup group = optionalGroup.get();
//    		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
//    		
//    		group.getUsers().add(user);
//    		jpaUserGroupRepository.save(group);
////            elasticUserGroupRepository.save(group);
//    		log.info("Added user {} to group {}", user.getUsername(), group.getName());
//    		
//    		return true;
//    	}
//    	log.warn("Group with ID {} not found", groupId);
//    	return false;
//    }

    @Transactional
    public boolean approveJoinRequestAndAddUserToGroup(Long requestId) {
        Optional<GroupRequest> optionalRequest = groupRequestRepository.findById(requestId);
        if (optionalRequest.isPresent()) {
            GroupRequest request = optionalRequest.get();
            request.setApproved(true);
            groupRequestRepository.save(request);

            UserGroup group = request.getGroup();
            User user = request.getUser();

            // Add user to the group
            group.getUsers().add(user);
            user.getGroups().add(group);
            userRepository.save(user);
            jpaUserGroupRepository.save(group);
//            elasticUserGroupRepository.save(group); // Ovde se dodaje u elasticsearch

            log.info("Join request with ID {} approved and user {} added to group {}", requestId, user.getUsername(), group.getName());
            return true;
        }
        log.warn("Join request with ID {} not found", requestId);
        return false;
    }

    
    @Transactional
    public boolean rejectJoinRequest(Long requestId) {
        Optional<GroupRequest> optionalRequest = groupRequestRepository.findById(requestId);
        if (optionalRequest.isPresent()) {
            groupRequestRepository.delete(optionalRequest.get());

            log.info("Join request with ID {} rejected", requestId);
            return true;
        }
        log.warn("Join request with ID {} not found", requestId);
        return false;
    }
}
