package com.matosic.Facebook.service;

import com.ibm.icu.text.Transliterator;
import com.matosic.Facebook.model.Post;
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
public class PostSearchService {

    @Autowired
    private RestHighLevelClient elasticsearchClient;

    private static final Transliterator toLatin = Transliterator.getInstance("Cyrillic-Latin");
    private static final Transliterator toLowerCase = Transliterator.getInstance("Lower");

    private String preprocessQuery(String query) {
        if (query == null) return null;
        return toLowerCase.transliterate(toLatin.transliterate(query));
    }

    public List<Post> searchPosts(String contentQuery, String pdfContentQuery, Integer minLikes, Integer maxLikes, String queryType) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        contentQuery = preprocessQuery(contentQuery);
        pdfContentQuery = preprocessQuery(pdfContentQuery);


        if (contentQuery != null && !contentQuery.isEmpty()) {
            boolQuery.must(QueryBuilders.matchQuery("content", contentQuery));
        }

        if (pdfContentQuery != null && !pdfContentQuery.isEmpty()) {
            SearchRequest searchRequest = new SearchRequest("post");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchQuery("content", pdfContentQuery));
            searchRequest.source(searchSourceBuilder);

            try {
                SearchResponse searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
                List<String> matchingPostIds = new ArrayList<>();
                for (SearchHit hit : searchResponse.getHits().getHits()) {
                    String fileName = (String) hit.getSourceAsMap().get("fileName");
                    matchingPostIds.add(fileName.replace(".pdf", ""));
                }
                boolQuery.must(QueryBuilders.termsQuery("id", matchingPostIds));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (minLikes != null || maxLikes != null) {
            // Add logic for filtering by likes count
            List<Post> allPosts = getAllPosts(); // Assuming a method to fetch all posts
            List<Post> filteredPosts = new ArrayList<>();
            for (Post post : allPosts) {
                int likesCount = post.getReactions().size() + post.getComments().stream().mapToInt(comment -> comment.getReactions().size()).sum();
                if ((minLikes == null || likesCount >= minLikes) && (maxLikes == null || likesCount <= maxLikes)) {
                    filteredPosts.add(post);
                }
            }
            List<Long> filteredPostIds = filteredPosts.stream().map(Post::getId).collect(Collectors.toList());
            boolQuery.must(QueryBuilders.termsQuery("id", filteredPostIds));
        }

        SearchRequest searchRequest = new SearchRequest("post");
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

    private List<Post> getAllPosts() {
        // Fetch all posts logic here (e.g., from a database or another repository)
        return new ArrayList<>(); // Placeholder
    }

    private List<Post> mapSearchHits(SearchHit[] searchHits) {
        return Stream.of(searchHits)
                     .map(hit -> {
                         Post post = new Post();
                         post.setId(Long.valueOf(hit.getId()));
                         post.setContent((String) hit.getSourceAsMap().get("content"));
//                         post.setTitle((String) hit.getSourceAsMap().get("title"));
                         // Map other Post fields as necessary
                         return post;
                     })
                     .collect(Collectors.toList());
    }
}
