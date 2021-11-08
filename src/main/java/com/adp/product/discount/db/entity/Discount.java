package com.adp.product.discount.db.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "DISCOUNT")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Discount {
	
	@Id
	@Column(name="CODE")
	private String code;
	@Column(name="ITEM_ID")
	private String itemId;
	@Column(name="ITEM_TYPE")
	private String itemType;
	@Column(name="TYPE")
	private String type;
	@Column(name="PERCENT")
	private BigDecimal percent;
	@Column(name="THRESHOLD")
	private BigDecimal threshold;

}
