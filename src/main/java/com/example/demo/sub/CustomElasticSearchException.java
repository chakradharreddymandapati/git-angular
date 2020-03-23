package com.example.demo.sub;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CustomElasticSearchException extends RuntimeException {
	public CustomElasticSearchException(String exception) {
		super(exception);
    }

}
