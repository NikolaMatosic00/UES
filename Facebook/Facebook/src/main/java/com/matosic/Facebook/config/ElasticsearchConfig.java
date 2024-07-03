package com.matosic.Facebook.config;

import java.util.List;
import java.util.Map;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.erhlc.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.client.erhlc.RestClients;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.matosic.Facebook.repository.elasticsearch")
public class ElasticsearchConfig {
	 @Bean
	    public ElasticsearchRestTemplate elasticsearchTemplate() {
	        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
	                .connectedTo("localhost:9200")
	                .build();

	        return new ElasticsearchRestTemplate(RestClients.create(clientConfiguration).rest(), elasticsearchConverter());
	    }
	 
	 @Bean
	    public RestHighLevelClient restHighLevelClient() {
	        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
	                .connectedTo("localhost:9200")
	                .build();

	        return RestClients.create(clientConfiguration).rest();
	    }

	    @Bean
	    public ElasticsearchConverter elasticsearchConverter() {
	        return new MappingElasticsearchConverter(new SimpleElasticsearchMappingContext());
	    }

	    @Bean
	    public void createIndex() {
	        ElasticsearchRestTemplate template = elasticsearchTemplate();
	        IndexOperations indexOps = template.indexOps(IndexCoordinates.of("facebook_data"));

	        if (!indexOps.exists()) {
	            // Define the settings and mappings
	            Map<String, Object> settings = Map.of(
	                "analysis", Map.of(
	                    "tokenizer", Map.of(
	                        "standard_tokenizer", Map.of(
	                            "type", "standard"
	                        )
	                    ),
	                    "filter", Map.of(
	                        "serbian_stop", Map.of(
	                            "type", "stop",
	                            "stopwords", "_serbian_"
	                        ),
	                        "serbian_keywords", Map.of(
	                            "type", "keyword_marker",
	                            "keywords", List.of("example")
	                        ),
	                        "serbian_stemmer", Map.of(
	                            "type", "stemmer",
	                            "language", "serbian"
	                        )
	                    ),
	                    "analyzer", Map.of(
	                        "serbian", Map.of(
	                            "type", "custom",
	                            "tokenizer", "standard_tokenizer",
	                            "filter", List.of("lowercase", "serbian_stop", "serbian_keywords", "serbian_stemmer")
	                        )
	                    )
	                )
	            );

	            indexOps.create(settings);  // Create the index with settings
	            indexOps.putMapping();  // Apply the mappings
	        } else {
	            // Handle case when index already exists
	            System.out.println("Index 'facebook_data' already exists.");
	        }
	    }

	}