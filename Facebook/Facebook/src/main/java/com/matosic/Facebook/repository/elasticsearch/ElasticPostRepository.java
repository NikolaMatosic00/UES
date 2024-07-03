package com.matosic.Facebook.repository.elasticsearch;

import com.matosic.Facebook.model.Post;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticPostRepository extends ElasticsearchRepository<Post, Long> {

}
