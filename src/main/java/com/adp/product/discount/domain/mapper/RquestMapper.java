package com.adp.product.discount.domain.mapper;

import org.springframework.stereotype.Component;

import com.adp.product.discount.db.entity.DiscountType;
import com.adp.product.discount.rest.resource.DiscountRequest;
import com.adp.product.discount.rest.resource.DiscountResource;

@Component
public class RquestMapper {

	public DiscountRequest createRequest(DiscountResource discountResource) {

		DiscountType discountType = DiscountType.byName(discountResource.getType())
				.orElseThrow(() -> new IllegalArgumentException("Discount Type not valid"));
		return new DiscountRequest(discountResource.getCode(), discountResource.getItemId(),
				discountResource.getItemType(), discountType, discountResource.getPercent(),
				discountResource.getTotalCost(), discountResource.getTotalCount());

	}

}
