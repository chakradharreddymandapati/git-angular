package com.example.demo.sub;

import java.io.Serializable;

public class FilterQuery implements Serializable {
	
	private static final long serialVersionUID = 2385374853434336646L;
	
	private String fieldName;
	private String operator;
	private String fieldValue;
	
	public FilterQuery() {
		super();
	}
	
	public FilterQuery(String fieldName, String operator, String fieldValue) {
		super();
		this.fieldName=fieldName;
		this.operator=operator;
		this.fieldValue=fieldValue;		
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}
	/**
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	/**
	 * @return the operator
	 */
	public String getOperator() {
		return operator;
	}
	/**
	 * @param operator the operator to set
	 */
	public void setOperator(String operator) {
		this.operator = operator;
	}
	/**
	 * @return the fieldValue
	 */
	public String getFieldValue() {
		return fieldValue;
	}
	/**
	 * @param fieldValue the fieldValue to set
	 */
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

}
