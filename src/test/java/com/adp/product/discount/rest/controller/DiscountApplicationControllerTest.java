package com.adp.product.discount.rest.controller;

import static com.adp.product.discount.db.TestDataHelper.from;
import static com.adp.product.discount.db.TestDataHelper.getDiscount;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import com.adp.product.discount.db.entity.Discount;
import com.adp.product.discount.db.entity.DiscountType;
import com.adp.product.discount.db.repository.DiscountRepository;
import com.adp.product.discount.rest.resource.ItemResource;
import com.adp.product.discount.rest.resource.ItemsResource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.http.ContentType;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DiscountApplicationControllerTest {

	@LocalServerPort
	protected int serverPort;

	@Autowired
	protected DiscountRepository discountRepository;
	
	@Autowired
	protected ObjectMapper objectMapper;

	@Test
	void testDiscountCreationWithType() {
		String discountCode = "TYPE_2021";
		String request = "{\n" + "    \"code\": \"" + discountCode + "\",\n" + "    \"itemType\": \"Clothes\",\n"
				+ "    \"type\": \"ITEM_TYPE\",\n" + "    \"percent\": 10,\n" + "    \"totalCost\": 100\n" + "}";

		assertThat(discountRepository.findById(discountCode)).isEmpty();

		given()//
				.baseUri("http://localhost:" + serverPort)//
				.body(request)//
				.contentType(ContentType.JSON)//
				.when()//
				.post("/discounts")//
				.then()//
				.statusCode(200)//
				.log().all() //
				.body("code", Matchers.equalTo(discountCode));

		assertThat(discountRepository.findById(discountCode)).isNotEmpty();

	}
	
	
	
	@Test
	void testDiscountCreationWithCostFails() {
		String discountCode = "COST_2021";
		String request = "{\r\n"
				+ "    \"code\": \"" + discountCode +  "\",\r\n"
				+ "    \"itemId\": \"1\",\r\n"
				+ "    \"itemType\": \"Clothes\",\r\n"
				+ "    \"type\": \"TOTAL_COST\",\r\n"
				+ "    \"percent\": 10\r\n"
				+ "}";

		assertThat(discountRepository.findById(discountCode)).isEmpty();

		given()//
				.baseUri("http://localhost:" + serverPort)//
				.body(request)//
				.contentType(ContentType.JSON)//
				.when()//
				.post("/discounts")//
				.then()//
				.statusCode(400)//
				.log().all() //
				.body("message", Matchers.equalTo("Invalid Value for cost threshold : null"));

		assertThat(discountRepository.findById(discountCode)).isEmpty();

	}
	
	@Test
	void testDiscountDeletion() {
		String discountCode = "DEL";
		discountRepository.save(getDiscount("DEL", DiscountType.ITEM_TYPE, from("10.00"), "CLOTHES", null, null));

		assertThat(discountRepository.findById(discountCode)).isNotEmpty();

		given()//
				.baseUri("http://localhost:" + serverPort)//
				.contentType(ContentType.JSON)//
				.when()//
				.delete("/discount/DEL")//
				.then()//
				.statusCode(200);
		assertThat(discountRepository.findById(discountCode)).isEmpty();

	}
	
	@Test
	void testDicountByType() throws JsonProcessingException {

		discountRepository.save(getDiscount("ABC", DiscountType.ITEM_TYPE, from("10.00"), "CLOTHES", null, null));
		discountRepository
				.save(getDiscount("CDE", DiscountType.TOTAL_COST, from("15.00"), null, null, from("100.00")));

		ItemsResource resource = ItemsResource.builder()
				.itemsResource(List
						.of(ItemResource.builder().id("123").type("CLOTHES").cost(from("50.00")).quantity(1).build()))
				.build();

		String request = objectMapper.writeValueAsString(resource);

		given()//
				.baseUri("http://localhost:" + serverPort)//
				.body(request)//
				.contentType(ContentType.JSON)//
				.when()//
				.post("/discounts/calculate-discount")//
				.then()//
				.statusCode(200)//
				.log().all() //
				.body("discountCode", Matchers.equalTo("ABC")).and()//
				.body("costAfterDiscount", Matchers.comparesEqualTo(from("45.00").floatValue()));

	}

	@Test
	void testDiscountByCount() throws JsonProcessingException {

		discountRepository.save(getDiscount("ABC", DiscountType.ITEM_TYPE, from("10.00"), "CLOTHES", null, null));
		discountRepository
				.save(getDiscount("CDE", DiscountType.TOTAL_COST, from("15.00"), null, null, from("100.00")));
		discountRepository
				.save(getDiscount("FGH", DiscountType.ITEM_COUNT, from("20.00"), null, "123", from("2")));

		ItemsResource resource = ItemsResource.builder()
				.itemsResource(List
						.of(ItemResource.builder().id("123").type("CLOTHES").cost(from("50.00")).quantity(5).build()))
				.build();

		String request = objectMapper.writeValueAsString(resource);

		given()//
				.baseUri("http://localhost:" + serverPort)//
				.body(request)//
				.contentType(ContentType.JSON)//
				.when()//
				.post("/discounts/calculate-discount")//
				.then()//
				.statusCode(200)//
				.log().all() //
				.body("discountCode", Matchers.equalTo("FGH")).and()//
				.body("costAfterDiscount", Matchers.comparesEqualTo(from("200.00").floatValue()));

	}

	@Test
	void testDiscountByCost() throws JsonProcessingException {

		discountRepository.save(getDiscount("ABC", DiscountType.ITEM_TYPE, from("10.00"), "CLOTHES", null, null));
		discountRepository
				.save(getDiscount("CDE", DiscountType.TOTAL_COST, from("15.00"), null, null, from("100.00")));
		Optional<Discount> discount = discountRepository.findById("FGH");
		if(discount.isPresent()) {
			discountRepository.deleteById("FGH");
		}

		ItemsResource resource = ItemsResource.builder()
				.itemsResource(List.of(
						ItemResource.builder().id("123").type("CLOTHES").cost(from("50.00")).quantity(1).build(),
						ItemResource.builder().id("456").type("ELECTRONICS").cost(from("300.00")).quantity(1).build()))
				.build();

		String request = objectMapper.writeValueAsString(resource);

		given()//
				.baseUri("http://localhost:" + serverPort)//
				.body(request)//
				.contentType(ContentType.JSON)//
				.when()//
				.post("/discounts/calculate-discount")//
				.then()//
				.statusCode(200)//
				.log().all() //
				.body("discountCode", Matchers.equalTo("CDE")).and()//
				.body("costAfterDiscount", Matchers.comparesEqualTo(from("305.00").floatValue()));

	}

}