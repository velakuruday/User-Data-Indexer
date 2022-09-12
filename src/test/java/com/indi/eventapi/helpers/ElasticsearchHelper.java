package com.indi.eventapi.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.indi.eventapi.dto.UserUpdateDto;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

import static java.util.Optional.empty;

@Service
@Slf4j
public class ElasticsearchHelper {

    @Autowired
    private RestHighLevelClient esClient;

    @Autowired
    private ObjectMapper mapper;


    public void deleteAllIndices() {
        DeleteIndexRequest request = new DeleteIndexRequest("*");
        try {
            esClient.indices().delete(request, RequestOptions.DEFAULT);
            esClient.indices().refresh(new RefreshRequest("*"), RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("Delete request failed {}", e.getMessage());
        }
    }

    public Optional<UserUpdateDto> findById(String id) throws IOException {
        GetRequest request = new GetRequest("user_updates_1", id);
        var response1 = esClient.indices().refresh(new RefreshRequest(), RequestOptions.DEFAULT);
        var response = esClient.get(request, RequestOptions.DEFAULT);
        if (!response.isExists()) {
            return empty();
        }
        return Optional.of(mapper.readValue(response.getSourceAsString(), UserUpdateDto.class));
    }
}
