package com.adp.product.discount.rest.resource;

import java.math.BigDecimal;

import com.adp.product.discount.db.entity.DiscountType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class DiscountRequest {

	private String code;

	private String itemId;

	private String itemType;

	private DiscountType type;

	private BigDecimal percent;

	private BigDecimal totalCost;

	private Long totalCount;

}
