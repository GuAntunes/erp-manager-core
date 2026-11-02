package br.com.gammacode.erpmanagercore.domain.model.common

abstract class AggregateRoot<T : AggregateId> {
	abstract val id: T

	final override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || this::class != other::class) return false
		return id == (other as AggregateRoot<*>).id
	}

	final override fun hashCode(): Int = id.hashCode()

	override fun toString(): String = "${this::class.simpleName}(id=$id)"
}
