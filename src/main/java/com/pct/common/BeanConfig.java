package com.pct.common;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfig {

	@Bean
	RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
		messageConverters.add(converter);
		restTemplate.setMessageConverters(messageConverters);
		return restTemplate;
	}

	@Bean
	public RestHighLevelClient restClient() throws MalformedURLException {
		URL elasticsearchUrl = new URL(
				"https://search-dev-es-ms2-3icitsnrjeqn6vtpocxpnguxvm.us-east-1.es.amazonaws.com/");
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("admin", "3tF#$3~*TpXo"));
		RestClientBuilder builder = RestClient
				.builder(new HttpHost(elasticsearchUrl.getHost(), elasticsearchUrl.getPort(), "https"))
				.setRequestConfigCallback(
						requestConfigBuilder -> requestConfigBuilder.setConnectTimeout(5000).setSocketTimeout(120000))
				.setHttpClientConfigCallback(
						httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
		RestHighLevelClient client = new RestHighLevelClient(builder);
		return client;
	}

}
