package com.adp.product.discount.domain.mapper;

import java.math.BigDecimal;

import com.adp.product.discount.db.entity.Discount;
import com.adp.product.discount.db.entity.DiscountType;
import com.adp.product.discount.rest.resource.DiscountRequest;
import com.adp.product.discount.rest.resource.DiscountResource;

public class ResourceMapper {
	
	public static DiscountResource mapDiscountResource(Discount discount) {
		BigDecimal totalCost = discount.getType().equals("TOTAL_COST") && discount.getThreshold() != null
				? discount.getThreshold()
				: null;
		Long totalCount = discount.getType().equals("ITEM_COUNT") && discount.getThreshold() != null
				? discount.getThreshold().longValue()
				: null;
		return DiscountResource.builder()
							.code(discount.getCode())
							.itemId(discount.getItemId())
							.itemType(discount.getItemType())
							.type(discount.getType().toString())
							.percent(discount.getPercent())
							.totalCost(totalCost)
							.totalCount(totalCount)
							.build();
	}

	
	public static Discount mapDiscount(DiscountRequest discountRequest) {
		BigDecimal threshold = null;
		threshold = getThresholdValue(discountRequest, threshold);

		return Discount.builder()
						.code(discountRequest.getCode())
						.itemId(discountRequest.getItemId())
						.itemType(discountRequest.getItemType())
						.type(discountRequest.getType().toString())
						.percent(discountRequest.getPercent())
						.threshold(threshold).build();
	}


	private static BigDecimal getThresholdValue(DiscountRequest discountRequest, BigDecimal threshold) {
		if (discountRequest.getType() == DiscountType.ITEM_COUNT) {
			threshold = new BigDecimal(discountRequest.getTotalCount());
		}
		if (discountRequest.getType() == DiscountType.TOTAL_COST) {
			threshold = discountRequest.getTotalCost();
		}
		return threshold;
	}
	
}
