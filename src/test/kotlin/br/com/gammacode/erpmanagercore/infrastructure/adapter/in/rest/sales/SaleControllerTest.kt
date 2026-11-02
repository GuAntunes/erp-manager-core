package br.com.gammacode.erpmanagercore.infrastructure.adapter.`in`.rest.sales

import br.com.gammacode.erpmanagercore.PostgresIntegrationTest
import com.jayway.jsonpath.JsonPath
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SaleControllerTest : PostgresIntegrationTest() {

	@Autowired
	private lateinit var mockMvc: MockMvc

	@Test
	fun `opens a draft, edits lines, confirms and cancels the sale`() {
		val customerId = createCustomer()
		val productId = createProduct(price = "100.00")

		val created = mockMvc.perform(
			post("/api/v1/sales")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"customerId":"$customerId"}""")
		)
			.andExpect(status().isCreated)
			.andExpect(jsonPath("$.status").value("DRAFT"))
			.andExpect(jsonPath("$.customerId").value(customerId))
			.andExpect(jsonPath("$.lines").isEmpty)
			.andExpect(jsonPath("$.confirmedAt").value(nullValue()))
			.andReturn()

		val saleId = JsonPath.read<String>(created.response.contentAsString, "$.id")

		mockMvc.perform(
			post("/api/v1/sales/$saleId/lines")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"productId":"$productId","quantity":2}""")
		)
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.lines[0].lineNumber").value(1))
			.andExpect(jsonPath("$.lines[0].productNameSnapshot").value("Notebook"))
			.andExpect(jsonPath("$.lines[0].unitPrice.amount").value(100.00))
			.andExpect(jsonPath("$.lines[0].unitPrice.currency").value("BRL"))
			.andExpect(jsonPath("$.subtotal.amount").value(200.00))
			.andExpect(jsonPath("$.total.amount").value(200.00))

		mockMvc.perform(
			post("/api/v1/sales/$saleId/lines")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"productId":"$productId","quantity":1}""")
		)
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.lines[1].lineNumber").value(2))

		mockMvc.perform(delete("/api/v1/sales/$saleId/lines/1"))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.lines.length()").value(1))
			.andExpect(jsonPath("$.lines[0].lineNumber").value(2))

		mockMvc.perform(
			patch("/api/v1/sales/$saleId/lines/2")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"quantity":3}""")
		)
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.lines[0].quantity").value(3))
			.andExpect(jsonPath("$.subtotal.amount").value(300.00))

		mockMvc.perform(
			put("/api/v1/sales/$saleId/discount")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"amount":50.00,"currency":"BRL"}""")
		)
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.discount.amount").value(50.00))
			.andExpect(jsonPath("$.total.amount").value(250.00))

		mockMvc.perform(post("/api/v1/sales/$saleId/confirm"))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.status").value("CONFIRMED"))
			.andExpect(jsonPath("$.confirmedAt").isNotEmpty)

		mockMvc.perform(
			post("/api/v1/sales/$saleId/lines")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"productId":"$productId","quantity":1}""")
		)
			.andExpect(status().isBadRequest)

		mockMvc.perform(post("/api/v1/sales/$saleId/cancel"))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.status").value("CANCELLED"))
			.andExpect(jsonPath("$.confirmedAt").isNotEmpty)

		mockMvc.perform(get("/api/v1/sales/$saleId"))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.status").value("CANCELLED"))

		mockMvc.perform(get("/api/v1/sales").param("status", "CANCELLED"))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.totalElements").value(1))
			.andExpect(jsonPath("$.content[0].id").value(saleId))
	}

	@Test
	fun `rejects a missing sale and a discount above the subtotal`() {
		val customerId = createCustomer()
		val productId = createProduct(price = "80.00")
		val missingId = UUID.randomUUID().toString()

		mockMvc.perform(get("/api/v1/sales/$missingId"))
			.andExpect(status().isNotFound)

		mockMvc.perform(
			post("/api/v1/sales")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"customerId":"$missingId"}""")
		)
			.andExpect(status().isNotFound)

		val saleId = JsonPath.read<String>(
			mockMvc.perform(
				post("/api/v1/sales")
					.contentType(MediaType.APPLICATION_JSON)
					.content("""{"customerId":"$customerId"}""")
			)
				.andExpect(status().isCreated)
				.andReturn()
				.response.contentAsString,
			"$.id"
		)

		mockMvc.perform(
			post("/api/v1/sales/$saleId/lines")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"productId":"$missingId","quantity":1}""")
		)
			.andExpect(status().isNotFound)

		mockMvc.perform(
			post("/api/v1/sales/$saleId/lines")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"productId":"$productId","quantity":1}""")
		)
			.andExpect(status().isOk)

		mockMvc.perform(
			put("/api/v1/sales/$saleId/discount")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"amount":80.01,"currency":"BRL"}""")
		)
			.andExpect(status().isBadRequest)
	}

	private fun createCustomer(): String {
		val created = mockMvc.perform(
			post("/api/v1/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"name":"Ana","phone":"11999999999","email":"ana@example.com"}""")
		)
			.andExpect(status().isCreated)
			.andReturn()
		return JsonPath.read(created.response.contentAsString, "$.id")
	}

	private fun createProduct(price: String): String {
		val created = mockMvc.perform(
			post("/api/v1/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"name":"Notebook","sku":"nb-${UUID.randomUUID()}","price":$price}""")
		)
			.andExpect(status().isCreated)
			.andReturn()
		return JsonPath.read(created.response.contentAsString, "$.id")
	}
}
