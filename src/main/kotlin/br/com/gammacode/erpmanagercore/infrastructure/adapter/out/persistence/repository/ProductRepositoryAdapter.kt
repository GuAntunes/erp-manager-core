package br.com.gammacode.erpmanagercore.infrastructure.adapter.out.persistence.repository

import br.com.gammacode.erpmanagercore.domain.model.product.Product
import br.com.gammacode.erpmanagercore.domain.model.product.ProductId
import br.com.gammacode.erpmanagercore.domain.port.out.ProductRepository
import br.com.gammacode.erpmanagercore.infrastructure.adapter.out.persistence.entity.ProductEntity
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
class ProductRepositoryAdapter(
	private val dataRepository: ProductDataJdbcRepository
) : ProductRepository {

	override fun save(product: Product): Product {
		val entity = ProductEntity.fromDomain(product)
		if (!dataRepository.existsById(product.id.value)) {
			entity.markAsNew()
		}
		return try {
			dataRepository.save(entity).toDomain()
		} catch (ex: DuplicateKeyException) {
			throw IllegalStateException("SKU already exists: ${product.sku}", ex)
		}
	}

	override fun findById(id: ProductId): Product? =
		dataRepository.findById(id.value).map { it.toDomain() }.orElse(null)

	override fun findBySku(sku: String): Product? =
		dataRepository.findBySku(sku)?.toDomain()

	override fun findAll(page: Int, size: Int): List<Product> {
		val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
		return dataRepository.findAllBy(pageable).map { it.toDomain() }
	}

	override fun count(): Long = dataRepository.count()

	override fun deleteById(id: ProductId) {
		dataRepository.deleteById(id.value)
	}
}
