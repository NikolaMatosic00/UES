package com.matosic.Facebook.service;

import com.ibm.icu.text.Transliterator;
import com.matosic.Facebook.model.UserGroup;
import com.matosic.Facebook.repository.jpa.UserGroupRepository;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserGroupSearchService {

    @Autowired
    private RestHighLevelClient elasticsearchClient;

    @Autowired
    private UserGroupRepository userGroupRepository;

    private static final Transliterator toLatin = Transliterator.getInstance("Cyrillic-Latin");
    private static final Transliterator toLowerCase = Transliterator.getInstance("Lower");

    private String preprocessQuery(String query) {
        if (query == null) return null;
        return toLowerCase.transliterate(toLatin.transliterate(query));
    }

    public List<UserGroup> searchUserGroups(String name, String descriptionQuery, String pdfContentQuery, Integer minPosts, Integer maxPosts, String queryType) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        name = preprocessQuery(name);
        descriptionQuery = preprocessQuery(descriptionQuery);
        pdfContentQuery = preprocessQuery(pdfContentQuery);

        if (name != null && !name.isEmpty()) {
            boolQuery.must(QueryBuilders.matchQuery("name", name));
        }

        if (descriptionQuery != null && !descriptionQuery.isEmpty()) {
            boolQuery.must(QueryBuilders.matchQuery("description", descriptionQuery));
        }

        if (pdfContentQuery != null && !pdfContentQuery.isEmpty()) {
            SearchRequest searchRequest = new SearchRequest("usergroup");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchQuery("content", pdfContentQuery));
            searchRequest.source(searchSourceBuilder);

            try {
                SearchResponse searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
                List<String> matchingGroupIds = new ArrayList<>();
                for (SearchHit hit : searchResponse.getHits().getHits()) {
                    String fileName = (String) hit.getSourceAsMap().get("fileName");
                    matchingGroupIds.add(fileName.replace(".pdf", ""));
                }
                boolQuery.filter(QueryBuilders.termsQuery("id", matchingGroupIds));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (minPosts != null || maxPosts != null) {
            List<UserGroup> allGroups = userGroupRepository.findAll();
            List<UserGroup> filteredGroups = new ArrayList<>();
            for (UserGroup group : allGroups) {
                int postCount = group.getPosts().size();
                if ((minPosts == null || postCount >= minPosts) && (maxPosts == null || postCount <= maxPosts)) {
                    filteredGroups.add(group);
                }
            }
            List<Long> filteredGroupIds = filteredGroups.stream().map(UserGroup::getId).collect(Collectors.toList());
            boolQuery.filter(QueryBuilders.termsQuery("id", filteredGroupIds));
        }

        SearchRequest searchRequest = new SearchRequest("usergroup");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQuery);
        searchRequest.source(searchSourceBuilder);

        SearchResponse response;
        try {
            response = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException("Error executing search request", e);
        }

        return mapSearchHits(response.getHits().getHits());
    }

    private List<UserGroup> mapSearchHits(SearchHit[] searchHits) {
        return Stream.of(searchHits)
                .map(hit -> {
                    UserGroup userGroup = new UserGroup();
                    userGroup.setId(Long.valueOf(hit.getId()));
                    userGroup.setName((String) hit.getSourceAsMap().get("name"));
                    userGroup.setDescription((String) hit.getSourceAsMap().get("description"));
                    // Map other UserGroup fields as necessary
                    return userGroup;
                })
                .collect(Collectors.toList());
    }
}

//
//import com.ibm.icu.text.Transliterator;
//import com.matosic.Facebook.model.UserGroup;
//import com.matosic.Facebook.repository.jpa.UserGroupRepository;
//
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.index.query.BoolQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//@Service
//public class UserGroupSearchService {
//
//    @Autowired
//    private RestHighLevelClient elasticsearchClient;
//    
//    @Autowired
//    private UserGroupRepository userGroupRepository;
//
//    private static final Transliterator toLatin = Transliterator.getInstance("Cyrillic-Latin");
//    private static final Transliterator toLowerCase = Transliterator.getInstance("Lower");
//
//    private String preprocessQuery(String query) {
//        if (query == null) return null;
//        return toLowerCase.transliterate(toLatin.transliterate(query));
//    }
//
//    public List<UserGroup> searchUserGroups(String name, String descriptionQuery, String pdfContentQuery, Integer minPosts, Integer maxPosts, String queryType) {
//        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
//
//        name = preprocessQuery(name);
//        descriptionQuery = preprocessQuery(descriptionQuery);
//        pdfContentQuery = preprocessQuery(pdfContentQuery);
//
//        if (name != null && !name.isEmpty()) {
//            boolQuery.must(QueryBuilders.matchQuery("name", name));
//        }
//
//        if (descriptionQuery != null && !descriptionQuery.isEmpty()) {
//            boolQuery.must(QueryBuilders.matchQuery("description", descriptionQuery));
//        }
//
//        if (pdfContentQuery != null && !pdfContentQuery.isEmpty()) {
//            SearchRequest searchRequest = new SearchRequest("usergroup");
//            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//            searchSourceBuilder.query(QueryBuilders.matchQuery("content", pdfContentQuery));
//            searchRequest.source(searchSourceBuilder);
//
//            try {
//                SearchResponse searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
//                List<String> matchingGroupIds = new ArrayList<>();
//                for (SearchHit hit : searchResponse.getHits().getHits()) {
//                    String fileName = (String) hit.getSourceAsMap().get("fileName");
//                    matchingGroupIds.add(fileName.replace(".pdf", ""));
//                }
//                boolQuery.must(QueryBuilders.termsQuery("id", matchingGroupIds));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (minPosts != null || maxPosts != null) {
//            List<UserGroup> allGroups = userGroupRepository.findAll();
//            List<UserGroup> filteredGroups = new ArrayList<>();
//            for (UserGroup group : allGroups) {
//                int postCount = group.getPosts().size();
//                if ((minPosts == null || postCount >= minPosts) && (maxPosts == null || postCount <= maxPosts)) {
//                    filteredGroups.add(group);
//                }
//            }
//            List<Long> filteredGroupIds = filteredGroups.stream().map(UserGroup::getId).collect(Collectors.toList());
//            boolQuery.must(QueryBuilders.termsQuery("id", filteredGroupIds));
//        }
//
//        SearchRequest searchRequest = new SearchRequest("usergroup");
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        searchSourceBuilder.query(boolQuery);
//        searchRequest.source(searchSourceBuilder);
//
//        SearchResponse response;
//        try {
//            response = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
//        } catch (IOException e) {
//            throw new RuntimeException("Error executing search request", e);
//        }
//
//        return mapSearchHits(response.getHits().getHits());
//    }
//
//
//    private List<UserGroup> mapSearchHits(SearchHit[] searchHits) {
//        return Stream.of(searchHits)
//                     .map(hit -> {
//                         UserGroup userGroup = new UserGroup();
//                         userGroup.setId(Long.valueOf(hit.getId()));
//                         userGroup.setName((String) hit.getSourceAsMap().get("name"));
//                         userGroup.setDescription((String) hit.getSourceAsMap().get("description"));
//                         // Map other UserGroup fields as necessary
//                         return userGroup;
//                     })
//                     .collect(Collectors.toList());
//    }
//}

















//@Service
//public class UserGroupSearchService {
//
//    @Autowired
//    private RestHighLevelClient elasticsearchClient;
//    
//    @Autowired
//    private UserGroupRepository userGroupRepository;
//
//    private static final Transliterator toLatin = Transliterator.getInstance("Cyrillic-Latin");
//    private static final Transliterator toLowerCase = Transliterator.getInstance("Lower");
//
//    private String preprocessQuery(String query) {
//        if (query == null) return null;
//        return toLowerCase.transliterate(toLatin.transliterate(query));
//    }
//
//    public List<UserGroup> searchUserGroups(String name, String descriptionQuery, String pdfContentQuery, Integer minPosts, Integer maxPosts, String queryType) {
//        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
//
//        name = preprocessQuery(name);
//        descriptionQuery = preprocessQuery(descriptionQuery);
//        pdfContentQuery = preprocessQuery(pdfContentQuery);
//
//        if (name != null && !name.isEmpty()) {
//            boolQuery.must(QueryBuilders.matchQuery("name", name));
//        }
//
//        if (descriptionQuery != null && !descriptionQuery.isEmpty()) {
//            boolQuery.must(QueryBuilders.matchQuery("description", descriptionQuery));
//        }
//
//        if (pdfContentQuery != null && !pdfContentQuery.isEmpty()) {
//            SearchRequest searchRequest = new SearchRequest("pdfindex");
//            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//            searchSourceBuilder.query(QueryBuilders.matchQuery("content", pdfContentQuery));
//            searchRequest.source(searchSourceBuilder);
//
//            try {
//                SearchResponse searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
//                List<String> matchingGroupIds = new ArrayList<>();
//                for (SearchHit hit : searchResponse.getHits().getHits()) {
//                    String fileName = (String) hit.getSourceAsMap().get("fileName");
//                    matchingGroupIds.add(fileName.replace(".pdf", ""));
//                }
//                boolQuery.filter(QueryBuilders.termsQuery("id", matchingGroupIds));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (minPosts != null || maxPosts != null) {
//            List<UserGroup> allGroups = userGroupRepository.findAll();
//            List<UserGroup> filteredGroups = new ArrayList<>();
//            for (UserGroup group : allGroups) {
//                int postCount = group.getPosts().size();
//                if ((minPosts == null || postCount >= minPosts) && (maxPosts == null || postCount <= maxPosts)) {
//                    filteredGroups.add(group);
//                }
//            }
//            List<Long> filteredGroupIds = filteredGroups.stream().map(UserGroup::getId).collect(Collectors.toList());
//            boolQuery.filter(QueryBuilders.termsQuery("id", filteredGroupIds));
//        }
//
//        SearchRequest searchRequest = new SearchRequest("usergroup");
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        searchSourceBuilder.query(boolQuery);
//        searchRequest.source(searchSourceBuilder);
//
//        SearchResponse response;
//        try {
//            response = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
//        } catch (IOException e) {
//            throw new RuntimeException("Error executing search request", e);
//        }
//
//        return mapSearchHits(response.getHits().getHits());
//    }
//
//    private List<UserGroup> mapSearchHits(SearchHit[] searchHits) {
//        return Stream.of(searchHits)
//                     .map(hit -> {
//                         UserGroup userGroup = new UserGroup();
//                         userGroup.setId(Long.valueOf(hit.getId()));
//                         userGroup.setName((String) hit.getSourceAsMap().get("name"));
//                         userGroup.setDescription((String) hit.getSourceAsMap().get("description"));
//                         // Map other UserGroup fields as necessary
//                         return userGroup;
//                     })
//                     .collect(Collectors.toList());
//    }
//}
