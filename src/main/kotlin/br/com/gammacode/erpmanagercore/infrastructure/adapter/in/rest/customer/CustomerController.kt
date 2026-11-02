package br.com.gammacode.erpmanagercore.infrastructure.adapter.`in`.rest.customer

import br.com.gammacode.erpmanagercore.application.dto.customer.GetCustomerQuery
import br.com.gammacode.erpmanagercore.domain.port.`in`.customer.CreateCustomerUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.customer.DeleteCustomerUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.customer.GetCustomerUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.customer.ListCustomersUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.customer.UpdateCustomerUseCase
import br.com.gammacode.erpmanagercore.infrastructure.adapter.`in`.rest.dto.PageResponse
import br.com.gammacode.erpmanagercore.infrastructure.adapter.`in`.rest.dto.customer.CreateCustomerRequest
import br.com.gammacode.erpmanagercore.infrastructure.adapter.`in`.rest.dto.customer.CustomerResponse
import br.com.gammacode.erpmanagercore.infrastructure.adapter.`in`.rest.dto.customer.UpdateCustomerRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/customers")
class CustomerController(
	private val createCustomerUseCase: CreateCustomerUseCase,
	private val getCustomerUseCase: GetCustomerUseCase,
	private val listCustomersUseCase: ListCustomersUseCase,
	private val updateCustomerUseCase: UpdateCustomerUseCase,
	private val deleteCustomerUseCase: DeleteCustomerUseCase
) {

	@PostMapping
	fun create(@RequestBody request: CreateCustomerRequest): ResponseEntity<CustomerResponse> {
		val customer = createCustomerUseCase.execute(request.toCommand())
		return ResponseEntity.status(HttpStatus.CREATED).body(CustomerResponse.fromDomain(customer))
	}

	@GetMapping("/{id}")
	fun get(@PathVariable id: String): CustomerResponse =
		CustomerResponse.fromDomain(getCustomerUseCase.execute(GetCustomerQuery(id)))

	@GetMapping
	fun list(
		@RequestParam(defaultValue = "0") page: Int,
		@RequestParam(defaultValue = "20") size: Int
	): PageResponse<CustomerResponse> {
		val result = listCustomersUseCase.execute(page, size)
		return PageResponse(
			content = result.content.map(CustomerResponse::fromDomain),
			page = result.page,
			size = result.size,
			totalElements = result.totalElements,
			totalPages = result.totalPages,
			first = result.isFirst,
			last = result.isLast
		)
	}

	@PutMapping("/{id}")
	fun update(@PathVariable id: String, @RequestBody request: UpdateCustomerRequest): CustomerResponse =
		CustomerResponse.fromDomain(updateCustomerUseCase.execute(request.toCommand(id)))

	@DeleteMapping("/{id}")
	fun delete(@PathVariable id: String): ResponseEntity<Void> {
		deleteCustomerUseCase.execute(id)
		return ResponseEntity.noContent().build()
	}
}
