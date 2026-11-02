package br.com.gammacode.erpmanagercore.infrastructure.adapter.`in`.rest.product

import br.com.gammacode.erpmanagercore.application.dto.product.GetProductQuery
import br.com.gammacode.erpmanagercore.domain.port.`in`.product.CreateProductUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.product.DeleteProductUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.product.GetProductUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.product.ListProductsUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.product.UpdateProductUseCase
import br.com.gammacode.erpmanagercore.infrastructure.adapter.`in`.rest.dto.PageResponse
import br.com.gammacode.erpmanagercore.infrastructure.adapter.`in`.rest.dto.product.CreateProductRequest
import br.com.gammacode.erpmanagercore.infrastructure.adapter.`in`.rest.dto.product.ProductResponse
import br.com.gammacode.erpmanagercore.infrastructure.adapter.`in`.rest.dto.product.UpdateProductRequest
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
@RequestMapping("/api/v1/products")
class ProductController(
	private val createProductUseCase: CreateProductUseCase,
	private val getProductUseCase: GetProductUseCase,
	private val listProductsUseCase: ListProductsUseCase,
	private val updateProductUseCase: UpdateProductUseCase,
	private val deleteProductUseCase: DeleteProductUseCase
) {

	@PostMapping
	fun create(@RequestBody request: CreateProductRequest): ResponseEntity<ProductResponse> {
		val product = createProductUseCase.execute(request.toCommand())
		return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponse.fromDomain(product))
	}

	@GetMapping("/{id}")
	fun get(@PathVariable id: String): ProductResponse =
		ProductResponse.fromDomain(getProductUseCase.execute(GetProductQuery(id)))

	@GetMapping
	fun list(
		@RequestParam(defaultValue = "0") page: Int,
		@RequestParam(defaultValue = "20") size: Int
	): PageResponse<ProductResponse> {
		val result = listProductsUseCase.execute(page, size)
		return PageResponse(
			content = result.content.map(ProductResponse::fromDomain),
			page = result.page,
			size = result.size,
			totalElements = result.totalElements,
			totalPages = result.totalPages,
			first = result.isFirst,
			last = result.isLast
		)
	}

	@PutMapping("/{id}")
	fun update(@PathVariable id: String, @RequestBody request: UpdateProductRequest): ProductResponse =
		ProductResponse.fromDomain(updateProductUseCase.execute(request.toCommand(id)))

	@DeleteMapping("/{id}")
	fun delete(@PathVariable id: String): ResponseEntity<Void> {
		deleteProductUseCase.execute(id)
		return ResponseEntity.noContent().build()
	}
}
