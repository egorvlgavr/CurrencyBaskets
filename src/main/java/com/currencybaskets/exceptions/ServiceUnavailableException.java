package com.currencybaskets.exceptions;

public class ServiceUnavailableException extends RuntimeException {

	public ServiceUnavailableException(String errorMsg) {
		super(errorMsg);
	}
}
