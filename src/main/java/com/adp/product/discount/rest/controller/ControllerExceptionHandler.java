package com.adp.product.discount.rest.controller;

import java.time.LocalDateTime;
import java.util.logging.Level;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.adp.product.discount.rest.resource.ErrorMessage;

import lombok.extern.java.Log;

@Log
@RestControllerAdvice
public class ControllerExceptionHandler {

	@ExceptionHandler(value = { IllegalArgumentException.class })
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ErrorMessage resourceNotFoundException(IllegalArgumentException ex) {

		log.log(Level.INFO, "Caught exception:" + ex.getMessage(), ex);
		ErrorMessage message = new ErrorMessage(LocalDateTime.now(), ex.getMessage());

		return message;
	}

}
