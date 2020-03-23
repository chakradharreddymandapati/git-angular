package com.example.demo.sub;

public class TimeIntervalDto {
private String startTime;
private String endTime;


public TimeIntervalDto() {
	super();
}
public TimeIntervalDto(String startTime, String endTime) {
	super();
	this.startTime = startTime;
	this.endTime = endTime;
}
public String getStartTime() {
	return startTime;
}
public void setStartTime(String startTime) {
	this.startTime = startTime;
}
public String getEndTime() {
	return endTime;
}
public void setEndTime(String endTime) {
	this.endTime = endTime;
}

}
