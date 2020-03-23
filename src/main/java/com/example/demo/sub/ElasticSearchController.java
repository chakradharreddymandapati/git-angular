package com.example.demo.sub;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;




@CrossOrigin("*")
@RestController
public class ElasticSearchController {

	private static final Logger logger = LoggerFactory.getLogger(ElasticSearchController.class);
	
	@Autowired
	private ElasticSearchService elasticSerachService;
	
			
	@PostMapping("/search")
	@ResponseBody
	public List<SearchResponseDto> search(@RequestBody SearchRequestDto searchReqDto) {
		logger.info("Inside search()... ");
		logger.info("Before elasticSerachService.search()... "+searchReqDto.getQueryString());
		String str2 = searchReqDto.getQueryString();
		      System.out.println("str2"+str2+"logger"+logger);
		List<SearchResponseDto> resDto;
		 
		 
				 if(!str2.equalsIgnoreCase("noinput")) {
					 if(!str2.isEmpty()) {
					 searchReqDto.setQueryMap(parseQueryString(str2));
					   
					 }
				 }
				 try {
					 resDto = elasticSerachService.search(searchReqDto);
					 if(resDto.get(0).getSourceMap() != null && resDto.get(0).getSourceMap().isEmpty() ) {
						 throw new CustomElasticSearchException("No Record Found");
					 }
					 
				 }catch (Exception e)
				    {
					 System.out.println("Controller Catch");
				      throw new CustomElasticSearchException(e.getMessage());
				    } 
				 
		
		return resDto;
	}
	
	@PostMapping("/getSourceMapEls")
	@ResponseBody
	public List<Object[]> getHitsSourceEls(@RequestBody SearchRequestDto searchReqDto) {
		logger.info("Inside search()... "+searchReqDto.getIndex());
		System.out.println("Inside search()... "+searchReqDto.getIndex());
		return elasticSerachService.search(searchReqDto).get(0).getSourceMap();
	}
	
	private HashMap <String, FilterQuery> parseQueryString(String queryString) {
		 HashMap <String, FilterQuery> queryMap = new HashMap<String, FilterQuery>();
		 String[] operatorList = {"<",">",">=","<=","="};
		     System.out.println("queryString : "+queryString);
		 String[] arrOfStr = queryString.toLowerCase().split("and");
		 for(int i=0; i < arrOfStr.length; i++ ) {
			 
			 FilterQuery filterQuery = new FilterQuery();
			      System.out.println("arrOfStr[i]"+arrOfStr[i]);
			 logger.info("stringList:"+i+" " + arrOfStr[i]);
			 for(int z=0;z<operatorList.length;z++) {
	    	   if(arrOfStr[i].contains(operatorList[z])) {
	    		   filterQuery.setOperator(operatorList[z]);
	    		   System.out.println("operatorList[z]"+operatorList[z]);
	    		   break;
	    	   }
			 }
			 String[] stringArray = arrOfStr[i].split(filterQuery.getOperator());
			      System.out.println("stringArray"+stringArray);
			 filterQuery.setFieldName(stringArray[0].trim());
			     System.out.println("stringArray[z].trim()"+stringArray[1].trim());
			 if(filterQuery.getFieldName().contains("time")) {
				 DateFormat dfm = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
			      try {
			    	  System.out.println("a");
					long a = dfm.parse(stringArray[1].trim()).getTime();
					
					filterQuery.setFieldValue(""+a);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }else {
			 filterQuery.setFieldValue(stringArray[1].trim());
			 }  
	    	    queryMap.put(""+i, filterQuery);
	    	    logger.info("queryMap===== : " + queryMap);
	    	    logger.info("queryMap=getFieldName()==== : " + queryMap.get(""+i).getFieldName());
	    	    logger.info("queryMap=getOperator()==== : " + queryMap.get(""+i).getOperator());
	    	    logger.info("queryMap=getFieldValue()==== : " + queryMap.get(""+i).getFieldValue());
	    	    System.out.println("finally"+queryMap+"queryMap.get(\"\"+i).getFieldName()"+queryMap.get(""+i).getFieldName()+" queryMap.get(\"\"+i).getOperator()"+ queryMap.get(""+i).getOperator()+"queryMap.get(\"\"+i).getFieldValue()"+queryMap.get(""+i).getFieldValue());
//	    	    System.out.println("queryMap===== : " + queryMap);
//	    	    System.out.println("queryMap=getFieldName()==== : " + queryMap.get(""+i).getFieldName());
//	    	    System.out.println("queryMap=getOperator()==== : " + queryMap.get(""+i).getOperator());
//	    	    System.out.println("queryMap=getFieldValue()==== : " + queryMap.get(""+i).getFieldValue());
	    	   
	    	   }	
		return queryMap; 
	}
}
