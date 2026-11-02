package br.com.gammacode.erpmanagercore.domain.model.customer

import br.com.gammacode.erpmanagercore.domain.model.common.AggregateRoot
import java.time.Instant

class Customer(
	override val id: CustomerId,
	name: String,
	contact: ContactInfo,
	active: Boolean = true,
	val createdAt: Instant = Instant.now()
) : AggregateRoot<CustomerId>() {

	var name: String = normalizedName(name)
		private set

	var contact: ContactInfo = contact
		private set

	var active: Boolean = active
		private set

	init {
		require(!createdAt.isAfter(Instant.now().plusSeconds(CLOCK_SKEW_SECONDS))) {
			"Creation date cannot be in the future"
		}
	}

	fun rename(name: String) {
		this.name = normalizedName(name)
	}

	fun changeContact(contact: ContactInfo) {
		this.contact = contact
	}

	fun activate() {
		active = true
	}

	fun deactivate() {
		active = false
	}

	companion object {
		private const val MAX_NAME_LENGTH = 200
		private const val CLOCK_SKEW_SECONDS = 60L

		private fun normalizedName(name: String): String {
			val normalized = name.trim()
			require(normalized.isNotBlank()) { "Customer name cannot be blank" }
			require(normalized.length <= MAX_NAME_LENGTH) {
				"Customer name cannot exceed $MAX_NAME_LENGTH characters"
			}
			return normalized
		}
	}
}
