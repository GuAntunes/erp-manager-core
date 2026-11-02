package br.com.gammacode.erpmanagercore.infrastructure.adapter.`in`.rest.customer

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
class CustomerControllerTest : PostgresIntegrationTest() {

	@Autowired
	private lateinit var mockMvc: MockMvc

	@Test
	fun `creates reads updates lists and deletes a customer`() {
		val created = mockMvc.perform(
			post("/api/v1/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(
					"""
					{"name":" Maria Silva ","phone":" (11) 98888-7766 ","email":"Maria@Example.com","optionalNotes":"  "}
					""".trimIndent()
				)
		)
			.andExpect(status().isCreated)
			.andExpect(jsonPath("$.name").value("Maria Silva"))
			.andExpect(jsonPath("$.phone").value("(11) 98888-7766"))
			.andExpect(jsonPath("$.email").value("maria@example.com"))
			.andExpect(jsonPath("$.optionalNotes").isEmpty)
			.andExpect(jsonPath("$.active").value(true))
			.andReturn()

		val id = JsonPath.read<String>(created.response.contentAsString, "$.id")

		mockMvc.perform(
			put("/api/v1/customers/$id")
				.contentType(MediaType.APPLICATION_JSON)
				.content(
					"""
					{"name":"Maria Souza","phone":"1133334444","email":"maria.souza@example.com","optionalNotes":"Ligar após as 18h","active":false}
					""".trimIndent()
				)
		)
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.name").value("Maria Souza"))
			.andExpect(jsonPath("$.email").value("maria.souza@example.com"))
			.andExpect(jsonPath("$.optionalNotes").value("Ligar após as 18h"))
			.andExpect(jsonPath("$.active").value(false))

		mockMvc.perform(get("/api/v1/customers"))
			.andExpect(status().isOk)
			.andExpect(jsonPath("$.totalElements").value(1))
			.andExpect(jsonPath("$.content[0].id").value(id))

		mockMvc.perform(delete("/api/v1/customers/$id"))
			.andExpect(status().isNoContent)

		mockMvc.perform(get("/api/v1/customers/$id"))
			.andExpect(status().isNotFound)
	}

	@Test
	fun `rejects an invalid customer`() {
		mockMvc.perform(
			post("/api/v1/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""{"name":"Maria","phone":"11988887766","email":"not-an-email"}""")
		)
			.andExpect(status().isBadRequest)
	}
}
