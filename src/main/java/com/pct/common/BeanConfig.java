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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfig {

	@Value("${elastic.url}")
	private String elasticUrl;
	
	@Value("${elastic.username}")
	private String elasticUsername;
	
	@Value("${elastic.password}")
	private String elasticPassword;
	
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
		URL elasticsearchUrl = new URL(elasticUrl);
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(elasticUsername, elasticPassword));
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
