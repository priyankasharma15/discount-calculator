package com.adp.product.discount.db.entity;

import java.util.Optional;

public enum DiscountType {

	TOTAL_COST, ITEM_TYPE, ITEM_COUNT;
	
	public static Optional<DiscountType> byName(String name){
		return Optional.ofNullable(DiscountType.valueOf(name));
	}
}
