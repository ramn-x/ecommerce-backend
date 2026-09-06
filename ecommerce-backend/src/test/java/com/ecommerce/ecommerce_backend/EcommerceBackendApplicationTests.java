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

		String token = getUserToken();

		mockMvc.perform(get("/products/2")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());
	}
	@Test
	void userCannotAccessAdminOrderEndpointTest() throws Exception {

		String token = getUserToken();

		mockMvc.perform(get("/orders/product/2")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isForbidden());
	}
	@Test
	void adminCanAccessOrderByProductTest() throws Exception {

		String token = getAdminToken();

		mockMvc.perform(get("/orders/product/2")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());
	}
	@Test
	void productNotFoundTest() throws Exception {

		String token = getUserToken();

		mockMvc.perform(get("/products/9999")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isNotFound());
	}
	@Test
	void orderNotFoundTest() throws Exception {

		String token = getUserToken();

		mockMvc.perform(get("/orders/9999")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isNotFound());
	}
	@Test
	void addToCartTest() throws Exception {

		String token = getUserToken();

		mockMvc.perform(post("/cart")
						.param("productid", "3")
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

		String token = getUserToken();

		MvcResult cartResult = mockMvc.perform(post("/cart")
						.param("productid", "3")
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

		String token = getUserToken();

		MvcResult cartResult = mockMvc.perform(post("/cart")
						.param("productid", "3")
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

		String token = getUserToken();

		mockMvc.perform(post("/cart")
						.param("productid", "3")
						.param("quantity", "1")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated());

		mockMvc.perform(post("/orders/checkout")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated());
	}
	@Test
	void getOrderByIdTest() throws Exception {

		String token = getUserToken();

		String orderJson = """
            {
                "userId": 6,
                "productId": 3,
                "quantity": 1
            }
            """;

		MvcResult orderResult = mockMvc.perform(post("/orders")
						.contentType("application/json")
						.content(orderJson)
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated())
				.andReturn();

		String orderResponse =
				orderResult.getResponse().getContentAsString();

		Integer orderId = new ObjectMapper()
				.readTree(orderResponse)
				.get("id")
				.asInt();

		mockMvc.perform(get("/orders/" + orderId)
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());
	}
	@Test
	void userCannotAccessAnotherUsersOrderTest() throws Exception {

		// Login as ADMIN / user 4
		String adminLoginJson = """
            {
                "email": "test@gmail.com",
                "password": "test123"
            }
            """;

		MvcResult adminLoginResult = mockMvc.perform(post("/users/login")
						.contentType("application/json")
						.content(adminLoginJson))
				.andExpect(status().isOk())
				.andReturn();

		String adminResponse =
				adminLoginResult.getResponse().getContentAsString();

		String adminToken = new ObjectMapper()
				.readTree(adminResponse)
				.get("token")
				.asText();

		// Create order for user 4
		String orderJson = """
            {
                "userId": 4,
                "productId": 3,
                "quantity": 1
            }
            """;

		MvcResult orderResult = mockMvc.perform(post("/orders")
						.contentType("application/json")
						.content(orderJson)
						.header("Authorization", "Bearer " + adminToken))
				.andExpect(status().isCreated())
				.andReturn();

		String orderResponse =
				orderResult.getResponse().getContentAsString();

		Integer orderId = new ObjectMapper()
				.readTree(orderResponse)
				.get("id")
				.asInt();

		// Login as USER 6
		String userLoginJson = """
            {
                "email": "user2@gmail.com",
                "password": "user123"
            }
            """;

		MvcResult userLoginResult = mockMvc.perform(post("/users/login")
						.contentType("application/json")
						.content(userLoginJson))
				.andExpect(status().isOk())
				.andReturn();

		String userResponse =
				userLoginResult.getResponse().getContentAsString();

		String userToken = new ObjectMapper()
				.readTree(userResponse)
				.get("token")
				.asText();

		// USER 6 tries to access USER 4's order
		mockMvc.perform(get("/orders/" + orderId)
						.header("Authorization", "Bearer " + userToken))
				.andExpect(status().isForbidden());
	}
	@Test
	void updateOrderStatusTest() throws Exception {

		String token = getAdminToken();

		String orderJson = """
            {
                "userId": 4,
                "productId": 3,
                "quantity": 1
            }
            """;

		MvcResult orderResult = mockMvc.perform(post("/orders")
						.contentType("application/json")
						.content(orderJson)
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated())
				.andReturn();

		String orderResponse =
				orderResult.getResponse().getContentAsString();

		Integer orderId = new ObjectMapper()
				.readTree(orderResponse)
				.get("id")
				.asInt();

		mockMvc.perform(put("/orders/" + orderId + "/status")
						.contentType("application/json")
						.content("""
                            {
                                "status": "CONFIRMED"
                            }
                            """)
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());
	}
	@Test
	void invalidOrderStatusTest() throws Exception {

		String token = getAdminToken();

		String orderJson = """
            {
                "userId": 4,
                "productId": 3,
                "quantity": 1
            }
            """;

		MvcResult orderResult = mockMvc.perform(post("/orders")
						.contentType("application/json")
						.content(orderJson)
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated())
				.andReturn();

		String orderResponse =
				orderResult.getResponse().getContentAsString();

		Integer orderId = new ObjectMapper()
				.readTree(orderResponse)
				.get("id")
				.asInt();

		mockMvc.perform(put("/orders/" + orderId + "/status")
						.contentType("application/json")
						.content("""
                            {
                                "status": "SHIPPED"
                            }
                            """)
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isBadRequest());
	}
	@Test
	void completeOrderStatusFlowTest() throws Exception {

		String token = getAdminToken();

		String orderJson = """
            {
                "userId": 4,
                "productId": 3,
                "quantity": 1
            }
            """;

		MvcResult orderResult = mockMvc.perform(post("/orders")
						.contentType("application/json")
						.content(orderJson)
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated())
				.andReturn();

		String orderResponse =
				orderResult.getResponse().getContentAsString();

		Integer orderId = new ObjectMapper()
				.readTree(orderResponse)
				.get("id")
				.asInt();

		mockMvc.perform(put("/orders/" + orderId + "/status")
						.contentType("application/json")
						.content("""
                            {
                                "status": "CONFIRMED"
                            }
                            """)
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());

		mockMvc.perform(put("/orders/" + orderId + "/status")
						.contentType("application/json")
						.content("""
                            {
                                "status": "SHIPPED"
                            }
                            """)
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());

		mockMvc.perform(put("/orders/" + orderId + "/status")
						.contentType("application/json")
						.content("""
                            {
                                "status": "DELIVERED"
                            }
                            """)
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());
	}
	@Test
	void paymentFlowTest() throws Exception {

		String token = getUserToken();

		mockMvc.perform(post("/cart")
						.param("productid", "3")
						.param("quantity", "1")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated());

		MvcResult checkoutResult = mockMvc.perform(
						post("/orders/checkout")
								.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated())
				.andReturn();

		String checkoutResponse =
				checkoutResult.getResponse().getContentAsString();

		Integer orderId = new ObjectMapper()
				.readTree(checkoutResponse)
				.get(0)
				.get("id")
				.asInt();

		MvcResult paymentResult = mockMvc.perform(
						post("/payments/" + orderId)
								.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated())
				.andReturn();

		String paymentResponse =
				paymentResult.getResponse().getContentAsString();

		Integer paymentId = new ObjectMapper()
				.readTree(paymentResponse)
				.get("id")
				.asInt();

		mockMvc.perform(
						put("/payments/" + paymentId + "/status")
								.contentType("application/json")
								.content("""
                                    {
                                        "status": "PAID"
                                    }
                                    """)
								.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());

		mockMvc.perform(
						get("/orders/" + orderId)
								.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("CONFIRMED"));
	}
	private String getUserToken() throws Exception {

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

		return new ObjectMapper()
				.readTree(response)
				.get("token")
				.asText();
	}
	private String getAdminToken() throws Exception {

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

		return new ObjectMapper()
				.readTree(response)
				.get("token")
				.asText();
	}
	@Test
	void invalidPaymentStatusTest() throws Exception {

		String token = getUserToken();

		// Create cart item
		mockMvc.perform(post("/cart")
						.param("productid", "3")
						.param("quantity", "1")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated());

		// Checkout
		MvcResult checkoutResult = mockMvc.perform(
						post("/orders/checkout")
								.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated())
				.andReturn();

		String checkoutResponse =
				checkoutResult.getResponse().getContentAsString();

		Integer orderId = new ObjectMapper()
				.readTree(checkoutResponse)
				.get(0)
				.get("id")
				.asInt();

		// Create payment
		MvcResult paymentResult = mockMvc.perform(
						post("/payments/" + orderId)
								.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated())
				.andReturn();

		String paymentResponse =
				paymentResult.getResponse().getContentAsString();

		Integer paymentId = new ObjectMapper()
				.readTree(paymentResponse)
				.get("id")
				.asInt();

		// PENDING → REFUNDED should fail
		mockMvc.perform(
						put("/payments/" + paymentId + "/status")
								.contentType("application/json")
								.content("""
                                    {
                                        "status": "REFUNDED"
                                    }
                                    """)
								.header("Authorization", "Bearer " + token))
				.andExpect(status().isBadRequest());
	}
	@Test
	void paymentFailedStatusTest() throws Exception {

		String token = getUserToken();

		mockMvc.perform(post("/cart")
						.param("productid", "3")
						.param("quantity", "1")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated());

		MvcResult checkoutResult = mockMvc.perform(
						post("/orders/checkout")
								.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated())
				.andReturn();

		String checkoutResponse =
				checkoutResult.getResponse().getContentAsString();

		Integer orderId = new ObjectMapper()
				.readTree(checkoutResponse)
				.get(0)
				.get("id")
				.asInt();

		MvcResult paymentResult = mockMvc.perform(
						post("/payments/" + orderId)
								.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated())
				.andReturn();

		String paymentResponse =
				paymentResult.getResponse().getContentAsString();

		Integer paymentId = new ObjectMapper()
				.readTree(paymentResponse)
				.get("id")
				.asInt();

		mockMvc.perform(
						put("/payments/" + paymentId + "/status")
								.contentType("application/json")
								.content("""
                                    {
                                        "status": "FAILED"
                                    }
                                    """)
								.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());
	}
	@Test
	void paymentRefundTest() throws Exception {

		String token = getUserToken();

		// Add product to cart
		mockMvc.perform(post("/cart")
						.param("productid", "3")
						.param("quantity", "1")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated());

		// Checkout
		MvcResult checkoutResult = mockMvc.perform(
						post("/orders/checkout")
								.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated())
				.andReturn();

		String checkoutResponse =
				checkoutResult.getResponse().getContentAsString();

		Integer orderId = new ObjectMapper()
				.readTree(checkoutResponse)
				.get(0)
				.get("id")
				.asInt();

		// Create payment
		MvcResult paymentResult = mockMvc.perform(
						post("/payments/" + orderId)
								.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated())
				.andReturn();

		String paymentResponse =
				paymentResult.getResponse().getContentAsString();

		Integer paymentId = new ObjectMapper()
				.readTree(paymentResponse)
				.get("id")
				.asInt();

		// PENDING → PAID
		mockMvc.perform(
						put("/payments/" + paymentId + "/status")
								.contentType("application/json")
								.content("""
                                    {
                                        "status": "PAID"
                                    }
                                    """)
								.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());

		// PAID → REFUNDED
		mockMvc.perform(
						put("/payments/" + paymentId + "/status")
								.contentType("application/json")
								.content("""
                                    {
                                        "status": "REFUNDED"
                                    }
                                    """)
								.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk());
	}
	@Test
	void userCannotUpdateAnotherUsersPaymentTest() throws Exception {

		// Login as USER 4
		String user4LoginJson = """
            {
                "email": "test@gmail.com",
                "password": "test123"
            }
            """;

		MvcResult user4LoginResult = mockMvc.perform(post("/users/login")
						.contentType("application/json")
						.content(user4LoginJson))
				.andExpect(status().isOk())
				.andReturn();

		String user4Response =
				user4LoginResult.getResponse().getContentAsString();

		String user4Token = new ObjectMapper()
				.readTree(user4Response)
				.get("token")
				.asText();

		// Create order for USER 4
		String orderJson = """
            {
                "userId": 4,
                "productId": 3,
                "quantity": 1
            }
            """;

		MvcResult orderResult = mockMvc.perform(post("/orders")
						.contentType("application/json")
						.content(orderJson)
						.header("Authorization", "Bearer " + user4Token))
				.andExpect(status().isCreated())
				.andReturn();

		Integer orderId = new ObjectMapper()
				.readTree(orderResult.getResponse().getContentAsString())
				.get("id")
				.asInt();

		// Create payment for USER 4's order
		MvcResult paymentResult = mockMvc.perform(
						post("/payments/" + orderId)
								.header("Authorization", "Bearer " + user4Token))
				.andExpect(status().isCreated())
				.andReturn();

		Integer paymentId = new ObjectMapper()
				.readTree(paymentResult.getResponse().getContentAsString())
				.get("id")
				.asInt();

		// Login as USER 6
		String user6Token = getUserToken();

		// USER 6 tries to update USER 4's payment
		mockMvc.perform(
						put("/payments/" + paymentId + "/status")
								.contentType("application/json")
								.content("""
                                    {
                                        "status": "PAID"
                                    }
                                    """)
								.header("Authorization", "Bearer " + user6Token))
				.andExpect(status().isForbidden());
	}
	@Test
	void duplicatePaymentTest() throws Exception {

		String token = getUserToken();

		// Add product to cart
		mockMvc.perform(post("/cart")
						.param("productid", "3")
						.param("quantity", "1")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated());

		// Checkout
		MvcResult checkoutResult = mockMvc.perform(
						post("/orders/checkout")
								.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated())
				.andReturn();

		String checkoutResponse =
				checkoutResult.getResponse().getContentAsString();

		Integer orderId = new ObjectMapper()
				.readTree(checkoutResponse)
				.get(0)
				.get("id")
				.asInt();

		// First payment
		mockMvc.perform(
						post("/payments/" + orderId)
								.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated());

		// Second payment → should fail
		mockMvc.perform(
						post("/payments/" + orderId)
								.header("Authorization", "Bearer " + token))
				.andExpect(status().isBadRequest());
	}
	@Test
	void getPaymentByIdTest() throws Exception {

		String token = getUserToken();

		// Create cart item
		mockMvc.perform(post("/cart")
						.param("productid", "3")
						.param("quantity", "1")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated());

		// Checkout
		MvcResult checkoutResult = mockMvc.perform(
						post("/orders/checkout")
								.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated())
				.andReturn();

		String checkoutResponse =
				checkoutResult.getResponse().getContentAsString();

		Integer orderId = new ObjectMapper()
				.readTree(checkoutResponse)
				.get(0)
				.get("id")
				.asInt();

		// Create payment
		MvcResult paymentResult = mockMvc.perform(
						post("/payments/" + orderId)
								.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated())
				.andReturn();

		Integer paymentId = new ObjectMapper()
				.readTree(paymentResult.getResponse().getContentAsString())
				.get("id")
				.asInt();

		// Get payment
		mockMvc.perform(get("/payments/" + paymentId)
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(paymentId))
				.andExpect(jsonPath("$.orderId").value(orderId));
	}
	@Test
	void userCannotAccessAnotherUsersPaymentTest() throws Exception {

		// ADMIN creates an order
		String adminToken = getAdminToken();

		String orderJson = """
            {
                "userId": 4,
                "productId": 3,
                "quantity": 1
            }
            """;

		MvcResult orderResult = mockMvc.perform(
						post("/orders")
								.header("Authorization", "Bearer " + adminToken)
								.contentType("application/json")
								.content(orderJson))
				.andExpect(status().isCreated())
				.andReturn();

		Integer orderId = new ObjectMapper()
				.readTree(orderResult.getResponse().getContentAsString())
				.get("id")
				.asInt();

		// ADMIN creates payment for that order
		MvcResult paymentResult = mockMvc.perform(
						post("/payments/" + orderId)
								.header("Authorization", "Bearer " + adminToken))
				.andExpect(status().isCreated())
				.andReturn();

		Integer paymentId = new ObjectMapper()
				.readTree(paymentResult.getResponse().getContentAsString())
				.get("id")
				.asInt();

		// USER 6 tries to access ADMIN's payment
		String userToken = getUserToken();

		mockMvc.perform(
						get("/payments/" + paymentId)
								.header("Authorization", "Bearer " + userToken))
				.andExpect(status().isForbidden());
	}
	@Test
	void getPaymentByOrderIdTest() throws Exception {

		String token = getUserToken();

		// Add product to cart
		mockMvc.perform(post("/cart")
						.param("productid", "3")
						.param("quantity", "1")
						.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated());

		// Checkout
		MvcResult checkoutResult = mockMvc.perform(
						post("/orders/checkout")
								.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated())
				.andReturn();

		String checkoutResponse =
				checkoutResult.getResponse().getContentAsString();

		Integer orderId = new ObjectMapper()
				.readTree(checkoutResponse)
				.get(0)
				.get("id")
				.asInt();

		// Create payment
		MvcResult paymentResult = mockMvc.perform(
						post("/payments/" + orderId)
								.header("Authorization", "Bearer " + token))
				.andExpect(status().isCreated())
				.andReturn();

		Integer paymentId = new ObjectMapper()
				.readTree(paymentResult.getResponse().getContentAsString())
				.get("id")
				.asInt();

		// Get payment by order ID
		mockMvc.perform(
						get("/payments/order/" + orderId)
								.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(paymentId))
				.andExpect(jsonPath("$.orderId").value(orderId))
				.andExpect(jsonPath("$.status").value("PENDING"));
	}
	@Test
	void userCannotAccessAnotherUsersPaymentByOrderIdTest() throws Exception {

		// USER 1 creates an order
		String user1Token = getAdminToken();

		String orderJson = """
            {
                "userId": 4,
                "productId": 3,
                "quantity": 1
            }
            """;

		MvcResult orderResult = mockMvc.perform(
						post("/orders")
								.header("Authorization", "Bearer " + user1Token)
								.contentType("application/json")
								.content(orderJson))
				.andExpect(status().isCreated())
				.andReturn();

		Integer orderId = new ObjectMapper()
				.readTree(orderResult.getResponse().getContentAsString())
				.get("id")
				.asInt();

		// Create payment for that order
		mockMvc.perform(
						post("/payments/" + orderId)
								.header("Authorization", "Bearer " + user1Token))
				.andExpect(status().isCreated());

		// USER 2 tries to access that payment by order ID
		String user2Token = getUserToken();

		mockMvc.perform(
						get("/payments/order/" + orderId)
								.header("Authorization", "Bearer " + user2Token))
				.andExpect(status().isForbidden());
	}
	@Test
	void paymentNotFoundTest() throws Exception {

		String token = getUserToken();

		mockMvc.perform(
						get("/payments/999999")
								.header("Authorization", "Bearer " + token))
				.andExpect(status().isNotFound());
	}
	@Test
	void unauthenticatedPaymentAccessTest() throws Exception {

		mockMvc.perform(
						get("/payments/1"))
				.andExpect(status().isForbidden());	}
	@Test
	void adminCanAccessPaymentTest() throws Exception {

		String adminToken = getAdminToken();

		String orderJson = """
            {
                "userId": 4,
                "productId": 3,
                "quantity": 1
            }
            """;

		MvcResult orderResult = mockMvc.perform(
						post("/orders")
								.header("Authorization", "Bearer " + adminToken)
								.contentType("application/json")
								.content(orderJson))
				.andExpect(status().isCreated())
				.andReturn();

		Integer orderId = new ObjectMapper()
				.readTree(orderResult.getResponse().getContentAsString())
				.get("id")
				.asInt();

		MvcResult paymentResult = mockMvc.perform(
						post("/payments/" + orderId)
								.header("Authorization", "Bearer " + adminToken))
				.andExpect(status().isCreated())
				.andReturn();

		Integer paymentId = new ObjectMapper()
				.readTree(paymentResult.getResponse().getContentAsString())
				.get("id")
				.asInt();

		mockMvc.perform(
						get("/payments/" + paymentId)
								.header("Authorization", "Bearer " + adminToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(paymentId))
				.andExpect(jsonPath("$.orderId").value(orderId));
	}
	@Test
	void invalidPaymentStatusValueTest() throws Exception {

		String token = getUserToken();

		mockMvc.perform(
						put("/payments/1/status")
								.header("Authorization", "Bearer " + token)
								.contentType("application/json")
								.content("""
                                    {
                                        "status": "INVALID"
                                    }
                                    """))
				.andExpect(status().isBadRequest());
	}
	@Test
	void paymentNotFoundOnStatusUpdateTest() throws Exception {

		String token = getUserToken();

		mockMvc.perform(
						put("/payments/999999/status")
								.header("Authorization", "Bearer " + token)
								.contentType("application/json")
								.content("""
                                    {
                                        "status": "PAID"
                                    }
                                    """))
				.andExpect(status().isNotFound());
	}
	@Test
	void userCannotAccessAnotherUsersCartTest() throws Exception {

		String adminToken = getAdminToken();

		// ADMIN adds product to cart
		MvcResult cartResult = mockMvc.perform(
						post("/cart")
								.param("productid", "3")
								.param("quantity", "1")
								.header("Authorization", "Bearer " + adminToken))
				.andExpect(status().isCreated())
				.andReturn();

		Integer cartId = new ObjectMapper()
				.readTree(cartResult.getResponse().getContentAsString())
				.get("id")
				.asInt();

		// USER tries to access ADMIN's cart
		String userToken = getUserToken();

		mockMvc.perform(
						get("/cart/" + cartId)
								.header("Authorization", "Bearer " + userToken))
				.andExpect(status().isForbidden());
	}
	@Test
	void userCannotUpdateAnotherUsersCartTest() throws Exception {

		String adminToken = getAdminToken();

		MvcResult cartResult = mockMvc.perform(
						post("/cart")
								.param("productid", "3")
								.param("quantity", "1")
								.header("Authorization", "Bearer " + adminToken))
				.andExpect(status().isCreated())
				.andReturn();

		Integer cartId = new ObjectMapper()
				.readTree(cartResult.getResponse().getContentAsString())
				.get("id")
				.asInt();

		String userToken = getUserToken();

		mockMvc.perform(
						put("/cart/" + cartId)
								.param("quantity", "3")
								.header("Authorization", "Bearer " + userToken))
				.andExpect(status().isForbidden());
	}
	@Test
	void userCannotDeleteAnotherUsersCartTest() throws Exception {

		String adminToken = getAdminToken();

		MvcResult cartResult = mockMvc.perform(
						post("/cart")
								.param("productid", "3")
								.param("quantity", "1")
								.header("Authorization", "Bearer " + adminToken))
				.andExpect(status().isCreated())
				.andReturn();

		Integer cartId = new ObjectMapper()
				.readTree(cartResult.getResponse().getContentAsString())
				.get("id")
				.asInt();

		String userToken = getUserToken();

		mockMvc.perform(
						delete("/cart/" + cartId)
								.header("Authorization", "Bearer " + userToken))
				.andExpect(status().isForbidden());
	}
	@Test
	void unauthenticatedCartAccessTest() throws Exception {

		mockMvc.perform(
						get("/cart"))
				.andExpect(status().isForbidden());
	}
	@Test
	void unauthenticatedOrderAccessTest() throws Exception {

		mockMvc.perform(
						get("/orders"))
				.andExpect(status().isForbidden());
	}
	@Test
	void adminCanAccessAnotherUsersOrderTest() throws Exception {

		String adminToken = getAdminToken();

		String orderJson = """
            {
                "userId": 6,
                "productId": 3,
                "quantity": 1
            }
            """;

		MvcResult orderResult = mockMvc.perform(
						post("/orders")
								.header("Authorization", "Bearer " + adminToken)
								.contentType("application/json")
								.content(orderJson))
				.andExpect(status().isCreated())
				.andReturn();

		Integer orderId = new ObjectMapper()
				.readTree(orderResult.getResponse().getContentAsString())
				.get("id")
				.asInt();

		mockMvc.perform(
						get("/orders/" + orderId)
								.header("Authorization", "Bearer " + adminToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(orderId));
	}
	@Test
	void invalidProductPriceTest() throws Exception {

		String adminToken = getAdminToken();

		String productJson = """
            {
                "name": "Invalid Product",
                "price": -100,
                "description": "Test product",
                "quantity": 10
            }
            """;

		mockMvc.perform(
						post("/products")
								.header("Authorization", "Bearer " + adminToken)
								.contentType("application/json")
								.content(productJson))
				.andExpect(status().isBadRequest());
	}
	@Test
	void invalidProductQuantityTest() throws Exception {

		String adminToken = getAdminToken();

		String productJson = """
            {
                "name": "Invalid Quantity Product",
                "price": 100,
                "description": "Test product",
                "quantity": -1
            }
            """;

		mockMvc.perform(
						post("/products")
								.header("Authorization", "Bearer " + adminToken)
								.contentType("application/json")
								.content(productJson))
				.andExpect(status().isBadRequest());
	}
	@Test
	void invalidProductNameTest() throws Exception {

		String adminToken = getAdminToken();

		String productJson = """
            {
                "name": "",
                "price": 100,
                "description": "Test product",
                "quantity": 10
            }
            """;

		mockMvc.perform(
						post("/products")
								.header("Authorization", "Bearer " + adminToken)
								.contentType("application/json")
								.content(productJson))
				.andExpect(status().isBadRequest());
	}
	@Test
	void userCannotCreateProductTest() throws Exception {

		String userToken = getUserToken();

		String productJson = """
            {
                "name": "Unauthorized Product",
                "price": 100,
                "description": "Test product",
                "quantity": 10
            }
            """;

		mockMvc.perform(
						post("/products")
								.header("Authorization", "Bearer " + userToken)
								.contentType("application/json")
								.content(productJson))
				.andExpect(status().isForbidden());
	}
	@Test
	void userCannotUpdateProductTest() throws Exception {

		String userToken = getUserToken();

		String productJson = """
            {
                "name": "Unauthorized Update",
                "price": 500,
                "description": "Test product",
                "quantity": 10
            }
            """;

		mockMvc.perform(
						put("/products/3")
								.header("Authorization", "Bearer " + userToken)
								.contentType("application/json")
								.content(productJson))
				.andExpect(status().isForbidden());
	}

}