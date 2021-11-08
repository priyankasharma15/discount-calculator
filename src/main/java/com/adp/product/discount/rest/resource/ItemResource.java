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
public class ItemResource {

	private String id;
	private String type;
	private BigDecimal cost;
	private Integer quantity;
	
}
