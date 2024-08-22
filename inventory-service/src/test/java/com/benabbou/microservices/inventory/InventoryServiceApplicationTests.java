package com.benabbou.microservices.inventory;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import static org.hamcrest.Matchers.equalTo;


@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryServiceApplicationTests {

	@ServiceConnection
	static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:latest");

	@LocalServerPort
	private Integer port;

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	@Test
	void shouldCheckStockAvailability() {
		RestAssured.given()
				.contentType(ContentType.JSON)
				.when()
				.get("/inventory?skuCode=ABC123&quantity=5")
				.then()
				.statusCode(200)
				.body("inStock", equalTo(false))
				.body("skuCode" , equalTo("ABC123"))
				.body("requestedQuantity" , equalTo(5))
				.body("availableQuantity", equalTo(0)); // Adjust based on your test data
	}

	static {
		postgreSQLContainer.start();
	}


	@Test
	void contextLoads() {
	}

}
