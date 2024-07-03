package com.matosic.Facebook.repository.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.matosic.Facebook.model.UserGroup;

@Repository
public interface ElasticUserGroupRepository extends ElasticsearchRepository<UserGroup, Long> {

}
