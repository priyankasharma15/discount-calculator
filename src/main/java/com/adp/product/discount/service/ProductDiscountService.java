package com.adp.product.discount.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adp.product.discount.db.entity.Discount;
import com.adp.product.discount.db.repository.DiscountRepository;
import com.adp.product.discount.domain.mapper.ResourceMapper;
import com.adp.product.discount.rest.resource.DiscountRequest;
import com.adp.product.discount.rest.resource.DiscountResource;
import com.adp.product.discount.rest.resource.ItemResource;
import com.adp.product.discount.rest.resource.ItemsResource;

@Service
public class ProductDiscountService {

	@Autowired
	DiscountRepository discountRepository;
	@Autowired
	DiscountValidator discountValidator;
	@Autowired
	DiscountCalculator discountCalculator;

	public DiscountResource createNewDiscount(DiscountRequest discountRequest) {
		discountValidator.validateMandatoryFields(discountRequest);

		Optional<Discount> discountCode = getDiscountByCode(discountRequest.getCode());

		discountCode.ifPresent(code -> {
			throw new IllegalArgumentException("Discount Code already exist");
		});

		var entity = discountRepository.save(ResourceMapper.mapDiscount(discountRequest));
		return ResourceMapper.mapDiscountResource(entity);

	}

	public void deleteDiscountCode(String code) {
		Optional<Discount> discountCode = getDiscountByCode(code);
		var discount = discountCode.orElseThrow(() -> new IllegalArgumentException("Discount code does not exist"));
		discountRepository.delete(discount);
	}

	private Optional<Discount> getDiscountByCode(String code) {
		return discountRepository.findById(code);
	}

	private List<Discount> getDiscountByType(Set<String> itemTypes) {
		return discountRepository.findByItemTypes(itemTypes);
	}

	public DiscountedItemResource getBestDiscount(ItemsResource itemsResource) {
		discountValidator.validateMandatoryFieldsForBestDiscount(itemsResource);
		final Map<String, BigDecimal> costByItemId = new HashMap<>();
		final Map<String, BigDecimal> costByItemType = new HashMap<>();
		itemsResource.getItemsResource().stream()
				.forEach(item -> addTotalCostWithTypeAndId(item, costByItemId, costByItemType));

		var totalCost = costByItemId.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

		Optional<Entry<Discount, BigDecimal>> bestDiscount = discountCalculator.calculateBestDiscount(costByItemId,
				costByItemType, getDiscountByCost(), getDiscountByType(costByItemType.keySet()),
				getDiscountByCount(costByItemId.keySet()));

		return bestDiscount.isPresent()
				? new DiscountedItemResource(bestDiscount.get().getKey().getCode(),
						totalCost.subtract(bestDiscount.get().getValue()))
				: new DiscountedItemResource(null, BigDecimal.ZERO);
	}

	private List<Discount> getDiscountByCount(Set<String> itemIds) {
		return discountRepository.findByItemIds(itemIds);
	}

	private List<Discount> getDiscountByCost() {
		return discountRepository.findByTypeCost();
	}

	private void addTotalCostWithTypeAndId(ItemResource itemResource, Map<String, BigDecimal> costByItemId,
			Map<String, BigDecimal> costByItemType) {
		costByItemId.put(itemResource.getId(),
				itemResource.getCost().multiply(new BigDecimal(itemResource.getQuantity())));
		costByItemType.put(itemResource.getType(),
				itemResource.getCost().multiply(new BigDecimal(itemResource.getQuantity())));
	}

}
