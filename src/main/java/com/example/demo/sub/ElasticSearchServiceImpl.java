package com.example.demo.sub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonMappingException;

import io.micrometer.core.instrument.util.StringUtils;

@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {
	private static final Logger logger = LoggerFactory.getLogger(ElasticSearchServiceImpl.class);

	@Value("${velocity_index}")
	private String velocity_index;
	@Value("${acce_index}")
	private String acce_index;
	@Value("${gps_index}")
	private String gps_index;
	@Value("${image_index}")
	private String image_index;
	//Start: Added for fuel and tyre index
	@Value("${fuel_index}")
	private String fuel_index;
	@Value("${tyre_index}")
	private String tyre_index;
	//End: Added for fuel and tyre index
	private String message = "Error occured in search, please try again.";
	
	

	private Map<String, String> indexMaps = new HashMap<>();
	
	@Autowired
	ElasticSearchConfig elasticSearchConfig;

	@Override
	public List<SearchResponseDto> search(SearchRequestDto searchReqDto) {
		System.out.println("hahahha00"+indexMaps);
              System.out.println("searchReqDto"+searchReqDto.getQueryMap());
		logger.info("Inside search() of ElasticSearchServiceImpl...");
		List<SearchResponseDto> searchResponseDtoList = new ArrayList<>();

		RestHighLevelClient restHighLevelClient = elasticSearchConfig.createInstance();
		      System.out.println("restHighLevelClient"+restHighLevelClient);
		try { //Adding try catch to handle unchecked runtime ElasticsearchException thrown by restHighLevelClient
			   System.out.println(searchReqDto.getIndex());
			if (StringUtils.isNotBlank(searchReqDto.getIndex())) {
			
				searchResponseDtoList.add(getSearchResponse(restHighLevelClient,
						getSearchRequest(searchReqDto, searchReqDto.getIndex(), null), searchReqDto.getIndex()));
				
			} else {
				// fetch record from steer_rec index, brake_info, image_metadata, gps_fix
				indexMaps.put(velocity_index, "velocity");
				indexMaps.put(acce_index, "acceleration");
				indexMaps.put(gps_index, "gps");
				indexMaps.put(image_index, "image");
				//Start: Added for fuel and tyre index
				indexMaps.put(fuel_index, "fuel");
				indexMaps.put(tyre_index, "tyre");
				
				System.out.println("hahahha"+indexMaps);
				//End: Added for fuel and tyre index  
				searchResponseDtoList.add(getSearchResponse(restHighLevelClient,
						getSearchRequest(searchReqDto, velocity_index, null), velocity_index));
				searchResponseDtoList.add(getSearchResponse(restHighLevelClient,
						getSearchRequest(searchReqDto, acce_index, null), acce_index));
				searchResponseDtoList.add(getSearchResponse(restHighLevelClient,
						getSearchRequest(searchReqDto, image_index, null), image_index));
				searchResponseDtoList.add(
						getSearchResponse(restHighLevelClient, getSearchRequest(searchReqDto, gps_index, null), gps_index));
				//Start: Added for fuel and tyre index
				searchResponseDtoList.add(
						getSearchResponse(restHighLevelClient, getSearchRequest(searchReqDto, fuel_index, null), fuel_index));
				searchResponseDtoList.add(
						getSearchResponse(restHighLevelClient, getSearchRequest(searchReqDto, tyre_index, null), tyre_index));
				//End: Added for fuel and tyre index
				System.out.println("hahahha123"+searchResponseDtoList);
			}
		}
		catch(ElasticsearchException ex) {
			//System.out.println("Search catch main");
			logger.info(" Inside search() exception block...");
			logger.error(ex.getMessage());
			throw new CustomElasticSearchException(message);
		}
		catch (Exception e) {
			System.out.println("Search catch main");
			// TODO: handle exception
		}

		return searchResponseDtoList;
	}

	private SearchResponseDto getSearchResponse(RestHighLevelClient restHighLevelClient, SearchRequest searchRequest,
			String index) {
                System.out.println("restHighLevelClient"+restHighLevelClient+"searchRequest"+searchRequest+"index"+index);
		SearchResponseDto searchResponseDto = new SearchResponseDto();
		try {
 
			searchResponseDto.setIndex(indexMaps.get(index));
			    System.out.println("indexMaps.get(index)"+indexMaps.get(index));
			    System.out.println("OK");
			List<Object[]> dataList = new ArrayList<>();
			if (true) {
				System.out.println("aggregations");
				Aggregations aggregations = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT)
						.getAggregations();
                   
				Terms terms = aggregations.get("seconds_buckets");
				   System.out.println("terms"+terms);
				List<? extends Bucket> elasticBucket = terms.getBuckets();
				   System.out.println("elasticBucket"+elasticBucket);
				for (int i = 0; i < elasticBucket.size(); i++) {

					if (index.equalsIgnoreCase(gps_index)) {
						Avg averageLat = elasticBucket.get(i).getAggregations().get("averageLat");
						Avg averageLng = elasticBucket.get(i).getAggregations().get("averageLng");
						  System.out.println("averageLat"+averageLat+"averageLng"+averageLng);
						   System.out.println("123"+averageLat.getValue()+averageLng.getValue()+elasticBucket.get(i).getKeyAsNumber());
						Object[] objectArray = { averageLat.getValue(), averageLng.getValue(),
								elasticBucket.get(i).getKeyAsNumber() };
						dataList.add(objectArray);
					} 
					//Start: Added for fuel and tyre index
					else if(index.equalsIgnoreCase(tyre_index)) {
						Avg averageFrntRgt = elasticBucket.get(i).getAggregations().get("averageFrntRgt");
						Avg averageRearRgt = elasticBucket.get(i).getAggregations().get("averageRearRgt");
						Avg averageFrntLft = elasticBucket.get(i).getAggregations().get("averageFrntLft");
						Avg averageRearLft = elasticBucket.get(i).getAggregations().get("averageRearLft");
						System.out.println("averageFrntRgt"+averageFrntRgt+"averageLng"+averageRearRgt+averageFrntLft+averageRearLft);
						   System.out.println("1234"+averageFrntRgt.getValue()+averageRearRgt.getValue()+averageFrntLft.getValue()+averageRearLft.getValue()+elasticBucket.get(i).getKeyAsNumber());
						Object[] objectArray = { averageFrntRgt.getValue(), averageRearRgt.getValue(),averageFrntLft.getValue(), averageRearLft.getValue(),
								elasticBucket.get(i).getKeyAsNumber() };
						dataList.add(objectArray);
					}
					//End: Added for fuel and tyre index
					else {
						Avg averageAge = elasticBucket.get(i).getAggregations().get("average");
						System.out.println("averageAge"+averageAge);
						System.out.println("12345"+averageAge.getValue()+elasticBucket.get(i).getKeyAsNumber() );
						Object[] objectArray = { averageAge.getValue(), elasticBucket.get(i).getKeyAsNumber() };
						dataList.add(objectArray);
					}
					
				}
				searchResponseDto.setSourceMap(dataList);
				  System.out.println("searchResponseDto"+searchResponseDto);
//				searchResponseDto.setAggregations(
//						restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT).getAggregations());
//				searchResponseDto.setSearchResponse(
//						restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT));

//				if (index.equalsIgnoreCase(velocity_index)) {
//					String startTime = null;
//					String endTime = null;
//					Aggregations aggregations = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT)
//							.getAggregations();
//					terms = aggregations.get("seconds_buckets");
//					elasticBucket = terms.getBuckets();
//
//					for (int i = 0; i < elasticBucket.size(); i++) {
//						if (i == 0) {
//							startTime = elasticBucket.get(i).getKeyAsString();
//						}
//						if (i == elasticBucket.size() - 1) {
//							endTime = elasticBucket.get(i).getKeyAsString();
//							TimeIntervalDto timeIntervalDto = new TimeIntervalDto(startTime, endTime);
//							timeIntervals.add(timeIntervalDto);
//
//						}
//						if (i != elasticBucket.size() - 1) {
//							if (Math.abs(elasticBucket.get(i + 1).getKeyAsNumber().intValue()
//									- elasticBucket.get(i).getKeyAsNumber().intValue()) != 1) {
//
//								endTime = elasticBucket.get(i).getKeyAsString();
//								TimeIntervalDto timeIntervalDto = new TimeIntervalDto(startTime, endTime);
//								timeIntervals.add(timeIntervalDto);
//								startTime = elasticBucket.get(i + 1).getKeyAsString();
//							}
//						}
//					}
//
//					searchResponseDto.setTimeIntervals(timeIntervals);
//				}
			} else if (index.equals(image_index)) {

				SearchHits hits = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT).getHits();
				 System.out.println("hits"+hits);
				for (SearchHit hit : hits.getHits()) {
					Map<String, Object> sourceMap = hit.getSourceAsMap();
					System.out.println("sourceMap.keySet().toArray()"+sourceMap.keySet().toArray());
					dataList.add(sourceMap.values().toArray());
				}
				searchResponseDto.setSourceMap(dataList);
			} else {
				
				System.out.println("Else");

				SearchHits hits = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT).getHits();
				  System.out.println("hits1"+hits);
				Object[] header = new Object[0];

				for (SearchHit hit : hits.getHits()) {
					Map<String, Object> sourceMap = hit.getSourceAsMap();
                      System.out.println("sourceMap.keySet().toArray()1"+sourceMap.keySet().toArray());
					if (header.length == 0) {
						header = sourceMap.keySet().toArray();
						 System.out.println("header1"+header);
						dataList.add(0, header);
					}

					dataList.add(sourceMap.values().toArray());
				}

				searchResponseDto.setSourceMap(dataList);
			}
			  System.out.println("searchResponseDto"+searchResponseDto);
			logger.info("Inside getSearchResponse() : " + searchResponseDto);

		} 
		catch (Exception e) {
			System.out.println("Search Catch");
			logger.error("Inside getSearchResponse() Exception is:::" + e);
			e.getMessage();
			if(e.getMessage().contains("index_not_found_exception"))
				 message = "Invalid Index" ;
			throw new CustomElasticSearchException(message);
		}
		return searchResponseDto;
	}

	private SearchRequest getSearchRequest(SearchRequestDto searchReqDto, String index, String type) {

		logger.info("Inside getSearchRequest() .INDEX.." + index);
		logger.info("Inside getSearchRequest() ..TYPE." + type);
               System.out.println("searchReqDto"+searchReqDto+"index"+index+"type"+type);
		SearchRequest searchRequest = new SearchRequest(index);
		if (StringUtils.isNotEmpty(type)) {
			searchRequest.types(type);
		}
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
		RangeQueryBuilder rangeQueryBuilder = null;
		//Adding try catch to handle unchecked runtime ElasticsearchException
		try {
			// Getting a Set of Key-value pairs
			if (StringUtils.isEmpty(searchReqDto.getIndex())) {
				HashMap<String, FilterQuery> queryMap = searchReqDto.getQueryMap();
                      System.out.println("searchReqDto.getQueryMap()"+searchReqDto.getQueryMap());
				logger.info("Inside getSearchRequest() ..queryMap." + queryMap);
				Set entrySet = queryMap.entrySet();

				// Obtaining an iterator for the entry set
				Iterator it = entrySet.iterator();
				// Iterate through HashMap entries(Key-Value pairs)
				logger.info("HashMap Key-Value Pairs : ");
				while (it.hasNext()) {
					Map.Entry me = (Map.Entry) it.next();
					   System.out.println("me"+me.getKey()+me.getValue());
					logger.info("Key is: " + me.getKey() + " & " + " value is: "
							+ ((FilterQuery) me.getValue()).getFieldName());
					logger.info(
							"Key is: " + me.getKey() + " & " + " value is: " + ((FilterQuery) me.getValue()).getOperator());
					logger.info("Key is: " + me.getKey() + " & " + " value is: "
							+ ((FilterQuery) me.getValue()).getFieldValue());
					System.out.println("((FilterQuery) me.getValue()).getFieldValue()"+((FilterQuery) me.getValue()).getFieldName());

					if ((((FilterQuery) me.getValue()).getFieldName().equals("start_time")
							|| ((FilterQuery) me.getValue()).getFieldName().equals("end_time"))) {
						rangeQueryBuilder = QueryBuilders.rangeQuery("header.stamp.secs");
						 System.out.println("rangeQueryBuilder"+rangeQueryBuilder);
					}

//				if (index.equals(image_index) && (((FilterQuery) me.getValue()).getFieldName().equals("start_time")
//						|| ((FilterQuery) me.getValue()).getFieldName().equals("end_time"))) {
//					rangeQueryBuilder = QueryBuilders.rangeQuery("secs");
//				}

					if (index.equals(velocity_index) && ((FilterQuery) me.getValue()).getFieldName().equals("speed")) {
						rangeQueryBuilder = QueryBuilders.rangeQuery(((FilterQuery) me.getValue()).getFieldName());
						 System.out.println("rangeQueryBuilder 1"+rangeQueryBuilder);
					}

					if (rangeQueryBuilder != null) {
						if (((FilterQuery) me.getValue()).getOperator().equals(">")) {
							rangeQueryBuilder.gte(((FilterQuery) me.getValue()).getFieldValue());
						} else if (((FilterQuery) me.getValue()).getOperator().equals("<")) {
							rangeQueryBuilder.lte(((FilterQuery) me.getValue()).getFieldValue());
						} else if (((FilterQuery) me.getValue()).getOperator().equals(">=")) {
							rangeQueryBuilder.gt((new Long(((FilterQuery) me.getValue()).getFieldValue())).longValue());
						} else if (((FilterQuery) me.getValue()).getOperator().equals("<=")) {
							rangeQueryBuilder.lt((new Long(((FilterQuery) me.getValue()).getFieldValue())).longValue());
						}
						System.out.println("rangeQueryBuilder 2"+rangeQueryBuilder);
						boolQueryBuilder.must(rangeQueryBuilder);
					}
				}
				if (StringUtils.isNotBlank(searchReqDto.getSessionid()) && StringUtils.isNotBlank(searchReqDto.getVin())) {
					boolQueryBuilder.should(QueryBuilders.termQuery("sessionid.keyword", searchReqDto.getSessionid()));
					boolQueryBuilder.should(QueryBuilders.termQuery("vin.keyword", searchReqDto.getVin()));
				}
			}
			logger.info("boolQuery : " + boolQueryBuilder);
			   System.out.println("boolQuery123 : " + boolQueryBuilder);
			searchSourceBuilder.query(boolQueryBuilder);
			searchSourceBuilder.from(0);
			    System.out.println("searchSourceBuilder : " + searchSourceBuilder.size(1000));
			if (searchReqDto.getIsPreview()) {
				searchSourceBuilder.size(20);
			} else {
				searchSourceBuilder.size(1000);
			}
			String[] includeFields = null;
			String[] excludeFields = new String[0];
			if (index.equals(velocity_index) || index.equals(acce_index) || index.equalsIgnoreCase(fuel_index) || index.equalsIgnoreCase(tyre_index)) {
//					searchSourceBuilder.sort(new FieldSortBuilder("header.seq").order(SortOrder.ASC));
//					if (index.equals(velocity_index)) {
//						includeFields = new String[] { "speed", "header.stamp.*" };
//					} else if (index.equals(acce_index)) {
//						includeFields = new String[] { "accel_over_ground", "header.stamp.*" };
//					} else if (index.equals(gps_index)) {
//						includeFields = new String[] { "latitude", "longitude", "header.stamp.*" };
//					}
//					searchSourceBuilder.fetchSource(includeFields, excludeFields);
				searchSourceBuilder.fetchSource(false);
				TermsAggregationBuilder aggregation = AggregationBuilders.terms("seconds_buckets")
						.field("header.stamp.secs").size(1000).order(BucketOrder.key(true));
				if (index.equals(velocity_index)) {
					aggregation.subAggregation(AggregationBuilders.avg("average").field("speed"));
				} else if (index.equals(acce_index)) {
					aggregation.subAggregation(AggregationBuilders.avg("average").field("accel_over_ground"));
				} else if (index.equals(gps_index)) {
					aggregation.subAggregation(AggregationBuilders.avg("averageLng").field("longitude"));
					aggregation.subAggregation(AggregationBuilders.avg("averageLat").field("latitude"));
				} 
				//Start: Added for fuel and tyre index
				else if (index.equals(fuel_index)) {
					aggregation.subAggregation(AggregationBuilders.avg("average").field("fuel_level"));
				} else if (index.equals(tyre_index)) {
					aggregation.subAggregation(AggregationBuilders.avg("averageFrntRgt").field("front_right"));
					aggregation.subAggregation(AggregationBuilders.avg("averageRearRgt").field("rear_right"));
					aggregation.subAggregation(AggregationBuilders.avg("averageFrntLft").field("front_left"));
					aggregation.subAggregation(AggregationBuilders.avg("averageRearLft").field("rear_left"));
				}
				//End: Added for fuel and tyre index
				searchSourceBuilder.aggregation(aggregation);
				   System.out.println("searchSourceBuilder 2"+searchSourceBuilder);
			} else if (index.equals(image_index)) {
				includeFields = new String[] { "name" };
				searchSourceBuilder.sort(new FieldSortBuilder("header.seq").order(SortOrder.ASC));
				searchSourceBuilder.fetchSource(includeFields, excludeFields);
				System.out.println("searchSourceBuilder: 3" + searchSourceBuilder);
			}
			
			searchRequest.source(searchSourceBuilder);
			 System.out.println("searchRequest: " + searchRequest);

			logger.info("searchRequest : " + searchRequest);
		}
		catch(ElasticsearchException e) {
			System.out.println("Catch1");
			logger.info(" Inside getSearchRequest() exception block...");
			logger.error(e.getMessage());
			throw new CustomElasticSearchException(message);
		}
		catch(Exception e) {
			System.out.println("Catch2");

			logger.error(e.getMessage());
			throw new CustomElasticSearchException(message);
		}
		
		return searchRequest;
	}

}
