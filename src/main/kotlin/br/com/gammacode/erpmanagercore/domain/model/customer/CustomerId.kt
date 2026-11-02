package br.com.gammacode.erpmanagercore.domain.model.customer

import br.com.gammacode.erpmanagercore.domain.model.common.AggregateId
import java.util.UUID

class CustomerId private constructor(value: UUID) : AggregateId(value) {

	companion object {
		fun generate(): CustomerId = CustomerId(UUID.randomUUID())

		fun of(value: UUID): CustomerId = CustomerId(value)

		fun of(value: String): CustomerId {
			val parsed = runCatching { UUID.fromString(value.trim()) }.getOrElse {
				throw IllegalArgumentException("CustomerId must be a valid UUID: $value")
			}
			return CustomerId(parsed)
		}
	}
}
