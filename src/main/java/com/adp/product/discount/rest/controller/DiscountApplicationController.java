package com.adp.product.discount.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.adp.product.discount.domain.mapper.RquestMapper;
import com.adp.product.discount.rest.resource.DiscountRequest;
import com.adp.product.discount.rest.resource.DiscountResource;
import com.adp.product.discount.rest.resource.ItemsResource;
import com.adp.product.discount.service.DiscountedItemResource;
import com.adp.product.discount.service.ProductDiscountService;

import lombok.extern.java.Log;

@Log
@RestController
public class DiscountApplicationController {
	@Autowired
	private RquestMapper requestMapper;
	@Autowired
	private ProductDiscountService productDiscountService;

	@PostMapping(value = "/discounts/calculate-discount", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<DiscountedItemResource> getAllParkingPlace(
			@RequestBody ItemsResource itemsResource) {
		log.info("calculating discount for:" + itemsResource);
		DiscountedItemResource discountedItemResource = productDiscountService.getBestDiscount(itemsResource);
		log.info("response :" + discountedItemResource);
		return new ResponseEntity<DiscountedItemResource>(discountedItemResource, HttpStatus.OK);
	}

	@PostMapping(value = "/discounts", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<DiscountResource> createDiscount(
			@RequestBody DiscountResource discountResource) {
		log.info("received request:" + discountResource);
		DiscountRequest discountRequest = requestMapper.createRequest(discountResource);
		var resource = productDiscountService.createNewDiscount(discountRequest);
		log.info("response:" + resource);
		return new ResponseEntity<DiscountResource>(resource, HttpStatus.OK);
	}

	@DeleteMapping(value = "/discount/{code}")
	public void removeDiscount(@PathVariable String code) {

		productDiscountService.deleteDiscountCode(code);
	}

}
