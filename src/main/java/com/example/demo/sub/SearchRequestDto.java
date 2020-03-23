package com.example.demo.sub;

import java.io.Serializable;
import java.util.HashMap;

public class SearchRequestDto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2385374853369481646L;
	
	private double speed;
	private long start_time;
	private long end_time;
	private String queryString="noinput";
	private String vin=null;
	private String sessionid=null;
	private HashMap <String, FilterQuery> queryMap=new HashMap<>();
	private String index;
	private Boolean isPreview=false;
	

	public SearchRequestDto() {
		super();
	}
	
	public SearchRequestDto(float speed, long start_time, long end_time, String queryString) {
		super();
		this.speed=speed;
		this.start_time=start_time;
		this.end_time=end_time;	
		this.setQueryString(queryString);
	}

	/**
	 * @return the speed
	 */
	public double getSpeed() {
		return speed;
	}

	/**
	 * @param speed the speed to set
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
	}

	/**
	 * @return the start_time
	 */
	public long getStart_time() {
		return start_time;
	}

	/**
	 * @param start_time the start_time to set
	 */
	public void setStart_time(long start_time) {
		this.start_time = start_time;
	}

	/**
	 * @return the end_time
	 */
	public long getEnd_time() {
		return end_time;
	}

	/**
	 * @param end_time the end_time to set
	 */
	public void setEnd_time(long end_time) {
		this.end_time = end_time;
	}

	/**
	 * @return the queryString
	 */
	public String getQueryString() {
		return queryString;
	}

	/**
	 * @param queryString the queryString to set
	 */
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	public HashMap<String, FilterQuery> getQueryMap() {
		return queryMap;
	}

	public void setQueryMap(HashMap<String, FilterQuery> queryMap) {
		this.queryMap = queryMap;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public Boolean getIsPreview() {
		return isPreview;
	}

	public void setIsPreview(Boolean isPreview) {
		this.isPreview = isPreview;
	}
	
	

	}
