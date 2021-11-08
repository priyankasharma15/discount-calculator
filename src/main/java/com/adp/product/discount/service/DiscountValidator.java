package com.adp.product.discount.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.adp.product.discount.db.entity.DiscountType;
import com.adp.product.discount.rest.resource.DiscountRequest;
import com.adp.product.discount.rest.resource.ItemResource;
import com.adp.product.discount.rest.resource.ItemsResource;

@Component
public class DiscountValidator {

	public void validateMandatoryFields(DiscountRequest discountRequest) {
		String code = discountRequest.getCode();
		BigDecimal percent = discountRequest.getPercent();
		checkNotNullOrEmpty(code, "Discount Code");
		checkNotNullOrEmpty(percent, "Discount percent");

		if (discountRequest.getType() == (DiscountType.ITEM_TYPE)) {
			validateDiscountRequestForItemType(discountRequest);
		}
		if (discountRequest.getType() == (DiscountType.ITEM_COUNT)) {
			validateDiscountRequestForItemCount(discountRequest);
		}
		if (discountRequest.getType() == (DiscountType.TOTAL_COST)) {
			validateDiscountRequestForTotalCost(discountRequest);
		}

	}

	public void validateMandatoryFieldsForBestDiscount(ItemsResource itemsResource) {
		itemsResource.getItemsResource().stream().forEach(this::checkMandatoryFields);
	}

	private void checkMandatoryFields(ItemResource resource) {
		checkNotNullOrEmpty(resource.getId(), "Item Id");
		checkNotNullOrEmpty(resource.getType(), "Item Type");
		checkNotNullOrEmpty(resource.getCost(), "Item Cost");
		checkNotNullOrEmpty(resource.getQuantity(), "Item Quantity");
	}

	private void checkNotNullOrEmpty(BigDecimal value, String message) {
		if (value == null || !(value.compareTo(BigDecimal.ZERO) == 1)) {
			throw new IllegalArgumentException("Invalid Value for " + message + " : " + value);
		}

	}
	
	private void checkNotNullOrEmpty(Integer value, String message) {
		if (value == null || value == 0) {
			throw new IllegalArgumentException("Invalid Value for " + message + " : " + value);
		}

	}

	private void checkNotNullOrEmpty(String value, String message) {
		if (value == null || value.isEmpty()) {
			throw new IllegalArgumentException("Invalid Value for " + message + " : " + value);
		}

	}

	private void validateDiscountRequestForItemType(DiscountRequest discountRequest) {
		checkNotNullOrEmpty(discountRequest.getItemType(), "Item Type");
	}

	private void validateDiscountRequestForTotalCost(DiscountRequest discountRequest) {

		checkNotNullOrEmpty(discountRequest.getTotalCost(), "cost threshold");
	}

	private void validateDiscountRequestForItemCount(DiscountRequest discountRequest) {

		BigDecimal countThreshold = new BigDecimal(discountRequest.getTotalCount());
		checkNotNullOrEmpty(discountRequest.getItemId(), "Item Id");

		checkNotNullOrEmpty(countThreshold, "count threshold");

	}

}
