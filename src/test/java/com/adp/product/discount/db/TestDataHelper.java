package com.adp.product.discount.db;

import java.math.BigDecimal;

import com.adp.product.discount.db.entity.Discount;
import com.adp.product.discount.db.entity.DiscountType;

public class TestDataHelper {

	public static Discount getDiscount(String code, DiscountType type, BigDecimal percent, String itemType,
			String itemId, BigDecimal threshold) {
		return Discount.builder().code(code).type(type.toString()).percent(percent).threshold(threshold)
				.itemType(itemType).itemId(itemId).build();

	}

	public static BigDecimal from(String value) {
		return new BigDecimal(value);
	}
}
