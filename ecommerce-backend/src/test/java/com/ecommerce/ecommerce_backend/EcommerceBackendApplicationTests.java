package com.ecommerce.ecommerce_backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EcommerceBackendApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}
	@Test
	void loginTest() throws Exception {

		String loginJson = """
                {
                    "email": "user2@gmail.com",
                    "password": "user123"
                }
                """;

		mockMvc.perform(post("/users/login")
						.contentType("application/json")
						.content(loginJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").exists());
	}
	@Test
	void loginWithWrongPasswordTest() throws Exception {

		String loginJson = """
            {
                "email": "user2@gmail.com",
                "password": "wrongpassword"
            }
            """;

		mockMvc.perform(post("/users/login")
						.contentType("application/json")
						.content(loginJson))
				.andExpect(status().isUnauthorized());
	}
	@Test
	void loginWithUnknownEmailTest() throws Exception {

		String loginJson = """
            {
                "email": "unknown@gmail.com",
                "password": "user123"
            }
            """;

		mockMvc.perform(post("/users/login")
						.contentType("application/json")
						.content(loginJson))
				.andExpect(status().isNotFound());
	}
	@Test
	void getProductTest() throws Exception {

		String loginJson = """
            {
                "email": "user2@gmail.com",
                "password": "user123"
            }
            """;

		MvcResult result = mockMvc.perform(post("/users/login")
						.contentType("application/json")
						.content(loginJson))
				.andExpect(status().isOk())
				.andReturn();

		String response = result.getResponse().getContentAsString();

		String token = new ObjectMapper()
				.readTree(response)
				.get("token")
				.asText();

		mockMvc.perform(get("/products/2")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());
	}
	@Test
	void userCannotAccessAdminOrderEndpointTest() throws Exception {

		String loginJson = """
            {
                "email": "user2@gmail.com",
                "password": "user123"
            }
            """;

		MvcResult result = mockMvc.perform(post("/users/login")
						.contentType("application/json")
						.content(loginJson))
				.andExpect(status().isOk())
				.andReturn();

		String response = result.getResponse().getContentAsString();

		String token = new ObjectMapper()
				.readTree(response)
				.get("token")
				.asText();

		mockMvc.perform(get("/orders/product/2")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isForbidden());
	}
	@Test
	void adminCanAccessOrderByProductTest() throws Exception {

		String loginJson = """
            {
                "email": "test@gmail.com",
                "password": "test123"
            }
            """;

		MvcResult result = mockMvc.perform(post("/users/login")
						.contentType("application/json")
						.content(loginJson))
				.andExpect(status().isOk())
				.andReturn();

		String response = result.getResponse().getContentAsString();

		String token = new ObjectMapper()
				.readTree(response)
				.get("token")
				.asText();

		mockMvc.perform(get("/orders/product/2")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());
	}
	@Test
	void productNotFoundTest() throws Exception {

		String loginJson = """
            {
                "email": "user2@gmail.com",
                "password": "user123"
            }
            """;

		MvcResult result = mockMvc.perform(post("/users/login")
						.contentType("application/json")
						.content(loginJson))
				.andExpect(status().isOk())
				.andReturn();

		String response = result.getResponse().getContentAsString();

		String token = new ObjectMapper()
				.readTree(response)
				.get("token")
				.asText();

		mockMvc.perform(get("/products/9999")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isNotFound());
	}
	@Test
	void orderNotFoundTest() throws Exception {

		String loginJson = """
            {
                "email": "user2@gmail.com",
                "password": "user123"
            }
            """;

		MvcResult result = mockMvc.perform(post("/users/login")
						.contentType("application/json")
						.content(loginJson))
				.andExpect(status().isOk())
				.andReturn();

		String response = result.getResponse().getContentAsString();

		String token = new ObjectMapper()
				.readTree(response)
				.get("token")
				.asText();

		mockMvc.perform(get("/orders/9999")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isNotFound());
	}
	@Test
	void addToCartTest() throws Exception {

		String loginJson = """
            {
                "email": "user2@gmail.com",
                "password": "user123"
            }
            """;

		MvcResult result = mockMvc.perform(post("/users/login")
						.contentType("application/json")
						.content(loginJson))
				.andExpect(status().isOk())
				.andReturn();

		String response = result.getResponse().getContentAsString();

		String token = new ObjectMapper()
				.readTree(response)
				.get("token")
				.asText();

		mockMvc.perform(post("/cart")
						.param("productid", "2")
						.param("quantity", "1")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated());
	}
	@Test
	void cartItemNotFoundTest() throws Exception {

		String loginJson = """
            {
                "email": "user2@gmail.com",
                "password": "user123"
            }
            """;

		MvcResult result = mockMvc.perform(post("/users/login")
						.contentType("application/json")
						.content(loginJson))
				.andExpect(status().isOk())
				.andReturn();

		String response = result.getResponse().getContentAsString();

		String token = new ObjectMapper()
				.readTree(response)
				.get("token")
				.asText();

		mockMvc.perform(get("/cart/9999")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isNotFound());
	}
	@Test
	void updateCartTest() throws Exception {

		String loginJson = """
            {
                "email": "user2@gmail.com",
                "password": "user123"
            }
            """;

		MvcResult result = mockMvc.perform(post("/users/login")
						.contentType("application/json")
						.content(loginJson))
				.andExpect(status().isOk())
				.andReturn();

		String response = result.getResponse().getContentAsString();

		String token = new ObjectMapper()
				.readTree(response)
				.get("token")
				.asText();

		MvcResult cartResult = mockMvc.perform(post("/cart")
						.param("productid", "2")
						.param("quantity", "1")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated())
				.andReturn();

		String cartResponse =
				cartResult.getResponse().getContentAsString();

		Integer cartId = new ObjectMapper()
				.readTree(cartResponse)
				.get("id")
				.asInt();

		mockMvc.perform(put("/cart/" + cartId)
						.param("quantity", "3")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());
	}
	@Test
	void deleteCartTest() throws Exception {

		String loginJson = """
            {
                "email": "user2@gmail.com",
                "password": "user123"
            }
            """;

		MvcResult loginResult = mockMvc.perform(post("/users/login")
						.contentType("application/json")
						.content(loginJson))
				.andExpect(status().isOk())
				.andReturn();

		String loginResponse =
				loginResult.getResponse().getContentAsString();

		String token = new ObjectMapper()
				.readTree(loginResponse)
				.get("token")
				.asText();

		MvcResult cartResult = mockMvc.perform(post("/cart")
						.param("productid", "2")
						.param("quantity", "1")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated())
				.andReturn();

		String cartResponse =
				cartResult.getResponse().getContentAsString();

		Integer cartId = new ObjectMapper()
				.readTree(cartResponse)
				.get("id")
				.asInt();

		mockMvc.perform(delete("/cart/" + cartId)
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isNoContent());
	}
	@Test
	void checkoutTest() throws Exception {

		String loginJson = """
            {
                "email": "user2@gmail.com",
                "password": "user123"
            }
            """;

		MvcResult loginResult = mockMvc.perform(post("/users/login")
						.contentType("application/json")
						.content(loginJson))
				.andExpect(status().isOk())
				.andReturn();

		String loginResponse =
				loginResult.getResponse().getContentAsString();

		String token = new ObjectMapper()
				.readTree(loginResponse)
				.get("token")
				.asText();

		mockMvc.perform(post("/cart")
						.param("productid", "3")
						.param("quantity", "1")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated());

		mockMvc.perform(post("/orders/checkout")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated());
	}
}