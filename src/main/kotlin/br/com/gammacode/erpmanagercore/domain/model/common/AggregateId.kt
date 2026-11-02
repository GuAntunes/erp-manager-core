package br.com.gammacode.erpmanagercore.domain.model.common

import java.util.UUID

abstract class AggregateId(val value: UUID) {

	init {
		require(value != NIL_UUID) { "Aggregate ID cannot be nil" }
	}

	final override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || this::class != other::class) return false
		return value == (other as AggregateId).value
	}

	final override fun hashCode(): Int = 31 * this::class.hashCode() + value.hashCode()

	final override fun toString(): String = value.toString()

	companion object {
		private val NIL_UUID = UUID(0L, 0L)
	}
}
