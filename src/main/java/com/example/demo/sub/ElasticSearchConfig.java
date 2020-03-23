package com.example.demo.sub;

import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;


@Configuration
public class ElasticSearchConfig extends AbstractFactoryBean {
	
	private static final Logger logger = LoggerFactory.getLogger(ElasticSearchServiceImpl.class);
	
//	@Value("${spring.data.elasticsearch.cluster-nodes}")
//    private String clusterNodes;
    @Value("${spring.data.elasticsearch.cluster-name}")
    private String clusterName;
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    Environment env;
    
    
    
    @Override
    public void destroy() {
        try {
            if (restHighLevelClient != null) {
                restHighLevelClient.close();
            }
        } catch (final Exception e) {
        	logger.error("Error closing ElasticSearch client: ", e);
        	throw new CustomElasticSearchException("Error closing ElasticSearch client.");
        }
    }

    @Override
    public Class<RestHighLevelClient> getObjectType() {
        return RestHighLevelClient.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public RestHighLevelClient createInstance() {
    	
        return buildClient();
    }

    private RestHighLevelClient buildClient() {
       try {
        	
        	logger.info("Connecting to elasticsearch cluster"+env.getProperty("elasticsearch_ip")+"... ");
        	
        	
            restHighLevelClient = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(env.getProperty("elasticsearch_ip"), 9200, "http"),
                            new HttpHost(env.getProperty("elasticsearch_ip"), 9201, "http")));
      
            System.out.println("restHighLevelClient"+restHighLevelClient+logger);
                 
        } catch (ElasticsearchException  e) {
        	logger.error(e.getMessage());
		      throw new CustomElasticSearchException("Unable to connect elasticsearch cluster.");
        }
        return restHighLevelClient;
    }

}