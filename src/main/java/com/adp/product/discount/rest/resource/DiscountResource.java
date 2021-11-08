package com.adp.product.discount.rest.resource;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiscountResource {

	private String code;

	private String itemId;

	private String itemType;

	private String type;

	private BigDecimal percent;

	private BigDecimal totalCost;
	
	private Long totalCount;
}
