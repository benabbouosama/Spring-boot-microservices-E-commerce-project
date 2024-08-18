package com.benabbou.microservices.product;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MongoDBContainer;

import static org.hamcrest.Matchers.*;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests {

	@ServiceConnection
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

	@LocalServerPort
	private Integer port;

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	static {
		mongoDBContainer.start();
	}

	@Test
	void shouldCreateProduct() {
		// Sample product data
		String requestBody = "{\n" +
				"    \"name\": \"Organic Bananas\",\n" +
				"    \"description\": \"Fresh and ripe organic bananas.\",\n" +
				"    \"skuCode\": \"ORG-BAN-007\",\n" +
				"    \"price\": 2.49\n" +
				"}";

		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("/products")
				.then()
				.statusCode(201)
				.body("id", Matchers.notNullValue())
				.body("name", Matchers.equalTo("Organic Bananas"))
				.body("description", Matchers.equalTo("Fresh and ripe organic bananas."))
				.body("skuCode", Matchers.equalTo("ORG-BAN-007"))
				.body("price", Matchers.equalTo(2.49F));

	}

	@Test
	void contextLoads() {
		// This test ensures that the application context loads successfully
	}
}
