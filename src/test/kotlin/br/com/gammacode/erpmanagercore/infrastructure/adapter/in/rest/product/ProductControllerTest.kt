package br.com.gammacode.erpmanagercore.infrastructure.adapter.`in`.rest.product

import br.com.gammacode.erpmanagercore.PostgresIntegrationTest
import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerTest : PostgresIntegrationTest() {

	@Autowired
	private lateinit var mockMvc: MockMvc

	@Test
	fun `creates reads updates lists and deletes a product`() {
		val created = mockMvc.perform(
			post("/api/v1/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"name":" Notebook ","sku":"nb-01","price":3500.50}""")
		)
			.andExpect(status().isCreated)
			.andExpect(jsonPath("$.name").value("Notebook"))
			.andExpect(jsonPath("$.sku").value("NB-01"))
			.andExpect(jsonPath("$.active").value(true))
			.andReturn()

		val id = JsonPath.read<String>(created.response.contentAsString, "$.id")

		mockMvc.perform(get("/api/v1/products/$id"))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.id").value(id))
			.andExpect(jsonPath("$.price").value(3500.50))

		mockMvc.perform(
			put("/api/v1/products/$id")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"name":"Notebook Pro","sku":"nb-01","price":4000,"active":false}""")
		)
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.name").value("Notebook Pro"))
			.andExpect(jsonPath("$.sku").value("NB-01"))
			.andExpect(jsonPath("$.active").value(false))

		mockMvc.perform(get("/api/v1/products"))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.totalElements").value(1))
			.andExpect(jsonPath("$.content[0].id").value(id))

		mockMvc.perform(delete("/api/v1/products/$id"))
			.andExpect(status().isNoContent)

		mockMvc.perform(get("/api/v1/products/$id"))
			.andExpect(status().isNotFound)
	}

	@Test
	fun `rejects an invalid product and a duplicated sku`() {
		mockMvc.perform(
			post("/api/v1/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"name":"Notebook","sku":"nb-01","price":-1}""")
		)
			.andExpect(status().isBadRequest)

		mockMvc.perform(
			post("/api/v1/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"name":"Notebook","sku":"nb-02","price":10}""")
		)
			.andExpect(status().isCreated)

		mockMvc.perform(
			post("/api/v1/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"name":"Other","sku":"NB-02","price":12}""")
		)
			.andExpect(status().isConflict)

		mockMvc.perform(get("/api/v1/products/not-a-uuid"))
			.andExpect(status().isBadRequest)

		mockMvc.perform(get("/api/v1/products?size=0"))
			.andExpect(status().isBadRequest)
	}
}
