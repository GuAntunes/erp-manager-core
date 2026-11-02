package br.com.gammacode.erpmanagercore.application.usecase.product

import br.com.gammacode.erpmanagercore.application.dto.product.CreateProductCommand
import br.com.gammacode.erpmanagercore.application.dto.product.GetProductQuery
import br.com.gammacode.erpmanagercore.application.dto.product.UpdateProductCommand
import br.com.gammacode.erpmanagercore.domain.model.product.Product
import br.com.gammacode.erpmanagercore.domain.model.product.ProductId
import br.com.gammacode.erpmanagercore.domain.model.product.ProductNotFoundException
import br.com.gammacode.erpmanagercore.domain.port.`in`.PageResult
import br.com.gammacode.erpmanagercore.domain.port.`in`.product.CreateProductUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.product.DeleteProductUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.product.GetProductUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.product.ListProductsUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.product.UpdateProductUseCase
import br.com.gammacode.erpmanagercore.domain.port.out.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
	private val productRepository: ProductRepository
) : CreateProductUseCase, GetProductUseCase, ListProductsUseCase, UpdateProductUseCase, DeleteProductUseCase {

	@Transactional
	override fun execute(command: CreateProductCommand): Product {
		val product = Product(
			id = ProductId.generate(),
			name = command.name,
			sku = command.sku,
			price = command.price,
			active = command.active
		)
		ensureSkuAvailable(product)
		return productRepository.save(product)
	}

	override fun execute(query: GetProductQuery): Product = find(query.id)

	override fun execute(page: Int, size: Int): PageResult<Product> {
		require(page >= 0) { "Page number must be greater than or equal to 0" }
		require(size in 1..100) { "Page size must be between 1 and 100" }
		return PageResult.of(
			content = productRepository.findAll(page, size),
			page = page,
			size = size,
			totalElements = productRepository.count()
		)
	}

	@Transactional
	override fun execute(command: UpdateProductCommand): Product {
		val product = find(command.id)
		product.rename(command.name)
		product.changeSku(command.sku)
		product.changePrice(command.price)
		if (command.active) product.activate() else product.deactivate()
		ensureSkuAvailable(product)
		return productRepository.save(product)
	}

	@Transactional
	override fun execute(id: String) {
		val productId = ProductId.of(id)
		find(id)
		productRepository.deleteById(productId)
	}

	private fun find(id: String): Product {
		val productId = ProductId.of(id)
		return productRepository.findById(productId) ?: throw ProductNotFoundException(id)
	}

	private fun ensureSkuAvailable(product: Product) {
		val existing = productRepository.findBySku(product.sku)
		if (existing != null && existing.id != product.id) {
			throw IllegalStateException("SKU already exists: ${product.sku}")
		}
	}
}
