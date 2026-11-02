package br.com.gammacode.erpmanagercore.domain.model.sales

import br.com.gammacode.erpmanagercore.domain.model.common.AggregateId
import java.util.UUID

class SaleId private constructor(value: UUID) : AggregateId(value) {

	companion object {
		fun generate(): SaleId = SaleId(UUID.randomUUID())

		fun of(value: UUID): SaleId = SaleId(value)

		fun of(value: String): SaleId {
			val parsed = runCatching { UUID.fromString(value.trim()) }.getOrElse {
				throw IllegalArgumentException("SaleId must be a valid UUID: $value")
			}
			return SaleId(parsed)
		}
	}
}
