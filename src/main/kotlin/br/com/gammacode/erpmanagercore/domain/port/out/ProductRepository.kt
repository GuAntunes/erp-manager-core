package br.com.gammacode.erpmanagercore.domain.port.out

import br.com.gammacode.erpmanagercore.domain.model.product.Product
import br.com.gammacode.erpmanagercore.domain.model.product.ProductId

interface ProductRepository {
	fun save(product: Product): Product
	fun findById(id: ProductId): Product?
	fun findBySku(sku: String): Product?
	fun findAll(page: Int, size: Int): List<Product>
	fun count(): Long
	fun deleteById(id: ProductId)
}
