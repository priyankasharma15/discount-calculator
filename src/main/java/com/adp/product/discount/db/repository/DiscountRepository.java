package com.adp.product.discount.db.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.adp.product.discount.db.entity.Discount;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, String> {

	@Query(value="select * from Discount where type='ITEM_COUNT' and ITEM_ID in (:itemIds) ", nativeQuery = true)
	List<Discount> findByItemIds(Set<String> itemIds);
	
	@Query(value="select * from Discount where type='TOTAL_COST'",nativeQuery = true)
	List<Discount> findByTypeCost();

	@Query(value="select * from Discount where type='ITEM_TYPE' and ITEM_TYPE in (:itemType)" , nativeQuery = true)
	List<Discount> findByItemTypes(Set<String> itemType);
}
