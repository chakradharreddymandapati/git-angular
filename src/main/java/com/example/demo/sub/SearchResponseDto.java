package com.example.demo.sub;

import java.util.List;


public class SearchResponseDto {
	private String index;
	private List<Object[]> sourceMap;
	

	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}

	public List<Object[]> getSourceMap() {
		return sourceMap;
	}
	public void setSourceMap(List<Object[]> sourceMap) {
		this.sourceMap = sourceMap;
	}
	
	
	
	
	
}
