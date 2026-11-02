package br.com.gammacode.erpmanagercore.infrastructure.adapter.`in`.rest

import br.com.gammacode.erpmanagercore.PostgresIntegrationTest
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiTest : PostgresIntegrationTest() {

	@Autowired
	private lateinit var mockMvc: MockMvc

	@Test
	fun `serves the openapi file and redirects to swagger ui`() {
		mockMvc.perform(get("/openapi.yaml"))
			.andExpect(status().isOk)
			.andExpect(content().string(containsString("ERP Manager Core API")))
			.andExpect(content().string(containsString("/api/v1/products")))
			.andExpect(content().string(containsString("/api/v1/customers")))

		mockMvc.perform(get("/swagger-ui.html"))
			.andExpect(status().isFound)
			.andExpect(redirectedUrl("/swagger-ui/index.html"))

		mockMvc.perform(get("/v3/api-docs/swagger-config"))
			.andExpect(status().isOk)
			.andExpect(content().string(containsString("/openapi.yaml")))
	}
}
