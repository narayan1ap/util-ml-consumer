package com.pct.consumer.service.impl.Consumer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pct.utils.dto.ImageDataDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ImageConsumer {

	private static final Logger logger = LoggerFactory.getLogger(ImageConsumer.class);

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private RestHighLevelClient client;

	private String destinationIndex = "device_report_data_staging";

	private List<IndexRequest> indexRequests = new ArrayList<>();

//	@KafkaListener(topics = "externalproces-kafka-process-queue", groupId = "reportconsumer", autoStartup = "true")
//	public void getCargoCameraImageJson(@Payload List<String> uuids, @Headers MessageHeaders messageHeaders)
//			throws Exception {
//		if (ObjectUtils.isNotEmpty(uuids.get(0))) {
//			logger.info("uuids length : " + uuids.size());
//			logger.info("uuid " + uuids);
//			for (String uuid : uuids) {
//				logger.info("uuid : " + uuid);
//			}
//			List<SearchHit> searchHits = createUuidSearchRequest(uuids);
//			for (SearchHit searchHit : searchHits) {
//				String imageUrl = getUriFromCargoCameraTLV(searchHit);
//				if (ObjectUtils.isNotEmpty(imageUrl)) {
//					ImageDataDTO response = null;
//					try {
//						response = getImageDataFromTensorFlow(imageUrl);
//					} catch (Exception e) {
//						createIndexRequestForException(searchHit);
//						logger.error("exception occured for id " + searchHit.getId() + " due to " + e.getMessage());
//					}
//					if (ObjectUtils.isNotEmpty(response)) {
//						createIndexRequest(response, searchHit, indexRequests);
//					}
//				}
//			}
//			if (ObjectUtils.isNotEmpty(indexRequests)) {
//				updateBulkIndexRequests(indexRequests);
//			}
//		}
//	}

	@KafkaListener(topics = "externalproces-kafka-process-queue", groupId = "reportconsumer", autoStartup = "true")
	public void getCargoCameraImageJson(@Payload String uuid, @Headers MessageHeaders messageHeaders) throws Exception {
		if (ObjectUtils.isNotEmpty(uuid)) {
			logger.info("uuids are : " + uuid);
			ObjectMapper mapper = new ObjectMapper();
			List<String> uuids = mapper.readValue(uuid, new TypeReference<List<String>>() {
			});
			logger.info("parsed uuids from kafka : " + uuids + " and size are : " + uuids.size());
			if (ObjectUtils.isNotEmpty(uuids)) {
				List<SearchHit> searchHits = createUuidSearchRequest(uuids);
				for (SearchHit searchHit : searchHits) {
					String imageUrl = getUriFromCargoCameraTLV(searchHit);
					if (ObjectUtils.isNotEmpty(imageUrl)) {
						ImageDataDTO response = null;
						try {
							response = getImageDataFromTensorFlow(imageUrl);
						} catch (Exception e) {
							createIndexRequestForException(searchHit);
							logger.error("exception occured for id " + searchHit.getId() + " due to " + e.getMessage());
						}
						if (ObjectUtils.isNotEmpty(response)) {
							createIndexRequest(response, searchHit, indexRequests);
						}
					}
				}
				if (ObjectUtils.isNotEmpty(indexRequests)) {
					updateBulkIndexRequests(indexRequests);
				}
			}

		}
	}

	private List<SearchHit> createUuidSearchRequest(List<String> uuids) {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.indices(destinationIndex);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.termsQuery("_id", uuids));
		searchRequest.source(searchSourceBuilder);
		List<SearchHit> searchHits = null;
		try {
			SearchResponse searchResponse = null;
			logger.info("started fetching request from ES for uuids");
			logger.info("index========>" + destinationIndex);
			logger.info("query========>" + searchRequest.source().toString());
			searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
			logger.info("completed fetching request from ES for uuids");
			SearchHit[] hits = searchResponse.getHits().getHits();
			logger.info("size is : " + hits.length);
			searchHits = Arrays.asList(hits);
		} catch (IOException e) {
			logger.error("exception occured due to " + e.getMessage());
			e.printStackTrace();
		}
		return searchHits;
	}

	private String getUriFromCargoCameraTLV(SearchHit searchHit) {
		String imageUrl = null;
		String jsonString = searchHit.getSourceAsString();
		JSONObject json = new JSONObject(jsonString);
		if (json.has("cargo_camera_sensor")) {
			logger.info("fetched cargo_camera_sensor tlv");
			if (json.getJSONObject("cargo_camera_sensor").has("uri")) {
				imageUrl = json.getJSONObject("cargo_camera_sensor").getString("uri");
				logger.info("fetched uri from cargo_camera_sensor tlv");
				return imageUrl;
			} else {
				logger.info("not able to fetch uri from cargo_camera_sensor tlv");
				return imageUrl;
			}
		}
		logger.info("not able to fetch cargo_camera_sensor tlv");
		return imageUrl;
	}

	private ImageDataDTO getImageDataFromTensorFlow(String imageUrl) throws Exception {
		// String url = "http://127.0.0.1:5000/image/?file_path=" + imageUrl;
		String url = "http://tensorflow.phillips-connect.net:5000/image/?file_path=" + imageUrl;
		logger.info("started fetching image data from python model");
		ImageDataDTO response = restTemplate.getForObject(url, ImageDataDTO.class);
		logger.info("completed fetching image data from python model");
		return response;
	}

	private void createIndexRequest(ImageDataDTO response, SearchHit searchHit, List<IndexRequest> indexRequests) {
		Map map = searchHit.getSourceAsMap();
		Map cargoCameraSensorTLV = (Map) map.get("cargo_camera_sensor");
		cargoCameraSensorTLV.put("state", response.getState());
		cargoCameraSensorTLV.put("prediction_value", response.getPrediction_value());
		cargoCameraSensorTLV.put("confidence_rating", response.getConfidence_rating());
		String uuid = searchHit.getId();
		IndexRequest indexRequest = new IndexRequest(destinationIndex);// destination
		indexRequest.id(uuid);
		indexRequest.source(map, XContentType.JSON);
		indexRequests.add(indexRequest);
	}

	private void updateBulkIndexRequests(List<IndexRequest> indexRequests) {
		try {
			BulkRequest bulkRequest = new BulkRequest();
			for (IndexRequest indexRequest : indexRequests) {
				bulkRequest.add(indexRequest);
			}
			BulkResponse indexResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
			if (indexResponse.hasFailures()) {
				logger.error("failed while updating ES");
			} else {
				logger.info("total records update for deviceId in index  are " + indexResponse.getItems().length
						+ " and status is " + indexResponse.status());
			}
		} catch (Exception ex) {
			logger.error("exception while updating ES  due to " + ex);
			ex.printStackTrace();
		}
	}

	private void createIndexRequestForException(SearchHit searchHit) {
		Map map = searchHit.getSourceAsMap();
		Map cargoCameraSensorTLV = (Map) map.get("cargo_camera_sensor");
		cargoCameraSensorTLV.put("state", "Excpetion Occured");
		cargoCameraSensorTLV.put("prediction_value", "NA");
		cargoCameraSensorTLV.put("confidence_rating", "NA");
		String uuid = searchHit.getId();
		IndexRequest indexRequest = new IndexRequest(destinationIndex);// destination
		indexRequest.id(uuid);
		indexRequest.source(map, XContentType.JSON);
		indexRequests.add(indexRequest);
	}

}
