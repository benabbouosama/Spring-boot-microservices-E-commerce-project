package com.benabbou.microservices.order;

import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderServiceApplicationTests {

	@ServiceConnection
	static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:latest");


	@LocalServerPort
	private Integer port;

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	static {
		postgreSQLContainer.start();
	}

	@Test
	void shouldPlaceOrder() {
		// Register a parser for text/plain responses
		RestAssured.registerParser("text/plain", Parser.TEXT);

		String requestBody = "{\n" +
				"  \"orderNumber\": 12345,\n" +
				"  \"skuCode\": \"ABC123\",\n" +
				"  \"price\": 99.99,\n" +
				"  \"quantity\": 2,\n" +
				"  \"userDetails\": {\n" +
				"    \"email\": \"null@example.com\",\n" +
				"    \"firstName\": null,\n" +
				"    \"lastName\": null,\n" +
				"    \"phoneNumber\": \"000-0000\",\n" +
				"    \"addressLine1\": \"123 Placeholder St\",\n" +
				"    \"addressLine2\": \"Apt 0\",\n" +
				"    \"city\": null,\n" +
				"    \"state\": null,\n" +
				"    \"postalCode\": \"00000\",\n" +
				"    \"country\": null\n" +
				"  }\n" +
				"}\n";

		RestAssured.given()
				.contentType("application/json")
				.body(requestBody)
				.when()
				.post("/orders")
				.then()
				.statusCode(201)
				.body(org.hamcrest.Matchers.containsString("Order placed successfully. Order Number:"));
	}



	@Test
	void contextLoads() {
	}

}
