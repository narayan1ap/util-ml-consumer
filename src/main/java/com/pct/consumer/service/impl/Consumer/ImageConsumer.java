package com.pct.consumer.service.impl.Consumer;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
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

	@KafkaListener(topics = "image-ml")
	public void getCargoCameraImageJson(@Payload String imageJson, @Headers MessageHeaders messageHeaders) {
		JSONObject jsonString = new JSONObject(imageJson);
		String imageUrl = jsonString.getJSONObject("_source").getJSONObject("cargo_camera_sensor").getString("uri");
		logger.debug("imageUrl: " + imageUrl);
		String url = "http://127.0.0.1:5000/image/?file_path=" + imageUrl;
		logger.debug("url: " + url);
		ImageDataDTO response = null;
		Map<String, Object> paramsToBeUpdate = new HashMap<>();
		try {
			logger.info("started fetching image data from python model");
			response = restTemplate.getForObject(url, ImageDataDTO.class);
			logger.info("completed fetching image data from python model");
			paramsToBeUpdate.put("uri", imageUrl);
			paramsToBeUpdate.put("state", response.getState());
			paramsToBeUpdate.put("prediction_value", response.getPrediction_value());
			paramsToBeUpdate.put("confidence_rating", response.getConfidence_rating());
		} catch (Exception e) {
			paramsToBeUpdate.put("state", "Exception Occured");
			paramsToBeUpdate.put("prediction_value", "NA");
			paramsToBeUpdate.put("confidence_rating", "NA");
			logger.error("Exception Occured while fetching image data from python model " + e.getMessage());
		}
		String source = "ctx._source.cargo_camera_sensor.uri = params.uri;ctx._source.cargo_camera_sensor.state = params.state;ctx._source.cargo_camera_sensor.prediction_value = params.prediction_value;ctx._source.cargo_camera_sensor.confidence_rating = params.confidence_rating";
		UpdateByQueryRequest updateByQueryRequest = new UpdateByQueryRequest("image-index-delete-me-later");
		updateByQueryRequest.setQuery(QueryBuilders.matchQuery("_id", jsonString.getString("_id")));
		Script script = new Script(ScriptType.INLINE, "painless", source, paramsToBeUpdate);
		updateByQueryRequest.setScript(script);
		try {
			BulkByScrollResponse bulkResponse = client.updateByQuery(updateByQueryRequest, RequestOptions.DEFAULT);
			logger.debug("updated response id: " + bulkResponse.getTotal());
		} catch (Exception e) {
			logger.error("Exception Occured while updating image data into ES " + e.getMessage());
			//retry
		}
	
	}

}
