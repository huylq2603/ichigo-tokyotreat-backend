package com.ichigobackend.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.ichigo.exceptions.CustomException;

/**
 * 
 * @author huylq
 *
 */

public class GeneralResponse<T> {

	@JsonInclude(Include.NON_NULL)
	public T data;
	
	@JsonInclude(Include.NON_NULL)
	public CustomException error;

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public CustomException getError() {
		return error;
	}

	public void setError(CustomException error) {
		this.error = error;
	}
	
	public GeneralResponse(){}
	
	public GeneralResponse(T data){
		this.data = data;
	}

	public GeneralResponse(CustomException error){
		this.error = error;
	}
	
	public GeneralResponse(T data, CustomException error) {
		this.data = data;
		this.error = error;
	}

}
