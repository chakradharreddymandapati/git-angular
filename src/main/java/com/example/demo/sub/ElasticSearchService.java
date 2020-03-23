package com.example.demo.sub;

import java.util.List;

public interface ElasticSearchService {
	public List<SearchResponseDto> search(SearchRequestDto searchReqDto); 
}

