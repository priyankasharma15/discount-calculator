package com.adp.product.discount.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.adp.product.discount.db.entity.Discount;

@Component
public class DiscountCalculator {

	private final static BigDecimal BIGDECIMAL_100 = new BigDecimal("100.00");

	public Optional<Entry<Discount, BigDecimal>> calculateBestDiscount(Map<String, BigDecimal> costByItemId,
			Map<String, BigDecimal> costByItemType, List<Discount> discountForCost, List<Discount> discountsByType,
			List<Discount> discountsByCount) {
		Optional<Entry<Discount, BigDecimal>> discountedAmountForCount = getDiscount(discountsByCount, costByItemId,
				Discount::getItemId);
		Optional<Entry<Discount, BigDecimal>> discountedAmountForType = getDiscount(discountsByType, costByItemType,
				Discount::getItemType);

		Optional<Entry<Discount, BigDecimal>> bestDiscountForItemCost = bestDiscountForCost(discountForCost,
				costByItemId);
		return bestDiscount(discountedAmountForCount, discountedAmountForType, bestDiscountForItemCost);

	}

	private Optional<Entry<Discount, BigDecimal>> bestDiscount(Optional<Entry<Discount, BigDecimal>> discountedAmountForCount,
			Optional<Entry<Discount, BigDecimal>> discountedAmountForType, Optional<Entry<Discount, BigDecimal>> bestDiscountForItemCost) {

		Map<Discount, BigDecimal> discountMap = discountMap(discountedAmountForType, discountedAmountForCount,
				bestDiscountForItemCost);

		Optional<Entry<Discount, BigDecimal>> bestDiscount = discountMap.entrySet().stream()
				.sorted(descendingOrderByValue()).findFirst();
		return bestDiscount;
	}

	private Optional<Entry<Discount, BigDecimal>> bestDiscountForCost(List<Discount> discountForCost,
			Map<String, BigDecimal> costByItemId) {
		
		Entry<String, BigDecimal> maxCostItemMap = costByItemId.entrySet().stream().sorted(descendingOrderByValue()).findFirst().orElseThrow(
				()-> new RuntimeException("Item id with Cost should be prsent"));
		BigDecimal maxCostforItemId = maxCostItemMap.getValue();
		Optional<Discount> highestDiscount = discountForCost.stream()//
				.filter(discount-> isGreaterThanOrEq(maxCostforItemId, 
						discount.getThreshold()))			
				.sorted(Comparator.comparing(Discount::getPercent).reversed())
				.findFirst();
				
		return highestDiscount.isPresent() ?
				Optional.of(Map.entry(highestDiscount.get(), highestDiscount.get().getPercent()
						.multiply(maxCostforItemId).divide(BIGDECIMAL_100)))
				: Optional.empty();
				

	}

	private Comparator<Entry<?, BigDecimal>> descendingOrderByValue() {
		return (a, b) -> b.getValue().compareTo(a.getValue());
	}
	
	private boolean isGreaterThanOrEq(BigDecimal a, BigDecimal b) {
		return a.compareTo(b) >= 0;
	}

	private Optional<Entry<Discount, BigDecimal>> getDiscount(List<Discount> discounts,
			Map<String, BigDecimal> costByKey, Function<Discount, String> keyMapper) {

		return discounts.stream()
				.map(discount -> Map.entry(discount, discount.getPercent()
						.multiply(costByKey.get(keyMapper.apply(discount))).divide(BIGDECIMAL_100)))
				.sorted(descendingOrderByValue()).findFirst();
	}

	private Map<Discount, BigDecimal> discountMap(Optional<Entry<Discount, BigDecimal>> bestDiscountForType,
			Optional<Entry<Discount, BigDecimal>> bestDiscountForCount,
			Optional<Entry<Discount, BigDecimal>> bestDiscountForCost) {
		Map<Discount, BigDecimal> discountMap = new HashMap<>();
		bestDiscountForType.ifPresent(entry -> discountMap.put(entry.getKey(), entry.getValue()));
		bestDiscountForCount.ifPresent(entry -> discountMap.put(entry.getKey(), entry.getValue()));
		bestDiscountForCost.ifPresent(entry -> discountMap.put(entry.getKey(), entry.getValue()));

		return discountMap;
	}

}
