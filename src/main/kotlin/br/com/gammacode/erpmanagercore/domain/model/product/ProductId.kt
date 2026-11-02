package br.com.gammacode.erpmanagercore.domain.model.product

import br.com.gammacode.erpmanagercore.domain.model.common.AggregateId
import java.util.UUID

class ProductId private constructor(value: UUID) : AggregateId(value) {

	companion object {
		fun generate(): ProductId = ProductId(UUID.randomUUID())

		fun of(value: UUID): ProductId = ProductId(value)

		fun of(value: String): ProductId {
			val parsed = runCatching { UUID.fromString(value.trim()) }.getOrElse {
				throw IllegalArgumentException("ProductId must be a valid UUID: $value")
			}
			return ProductId(parsed)
		}
	}
}
