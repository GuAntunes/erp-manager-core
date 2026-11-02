package br.com.gammacode.erpmanagercore.infrastructure.adapter.`in`.rest.dto.product

import br.com.gammacode.erpmanagercore.application.dto.product.CreateProductCommand
import br.com.gammacode.erpmanagercore.application.dto.product.UpdateProductCommand
import br.com.gammacode.erpmanagercore.domain.model.product.Product
import java.math.BigDecimal
import java.time.Instant

data class CreateProductRequest(
	val name: String,
	val sku: String,
	val price: BigDecimal,
	val active: Boolean = true
) {
	fun toCommand(): CreateProductCommand = CreateProductCommand(name, sku, price, active)
}

data class UpdateProductRequest(
	val name: String,
	val sku: String,
	val price: BigDecimal,
	val active: Boolean
) {
	fun toCommand(id: String): UpdateProductCommand = UpdateProductCommand(id, name, sku, price, active)
}

data class ProductResponse(
	val id: String,
	val name: String,
	val sku: String,
	val active: Boolean,
	val price: BigDecimal,
	val createdAt: Instant
) {
	companion object {
		fun fromDomain(product: Product): ProductResponse = ProductResponse(
			id = product.id.value.toString(),
			name = product.name,
			sku = product.sku,
			active = product.active,
			price = product.price,
			createdAt = product.createdAt
		)
	}
}
