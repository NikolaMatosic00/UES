package com.matosic.Facebook.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matosic.Facebook.model.Banned;

@Repository
public interface BannedRepository extends JpaRepository<Banned, Long>{

}
