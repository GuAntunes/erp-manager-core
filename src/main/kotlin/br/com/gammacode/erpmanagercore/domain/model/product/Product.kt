package br.com.gammacode.erpmanagercore.domain.model.product

import br.com.gammacode.erpmanagercore.domain.model.common.AggregateRoot
import java.math.BigDecimal
import java.time.Instant

class Product(
	override val id: ProductId,
	name: String,
	sku: String,
	price: BigDecimal,
	active: Boolean = true,
	val createdAt: Instant = Instant.now()
) : AggregateRoot<ProductId>() {

	var name: String = normalizedName(name)
		private set

	var sku: String = normalizedSku(sku)
		private set

	var price: BigDecimal = price
		private set

	var active: Boolean = active
		private set

	init {
		validatePrice(this.price)
		require(!createdAt.isAfter(Instant.now().plusSeconds(CLOCK_SKEW_SECONDS))) {
			"Creation date cannot be in the future"
		}
	}

	fun rename(name: String) {
		this.name = normalizedName(name)
	}

	fun changeSku(sku: String) {
		this.sku = normalizedSku(sku)
	}

	fun changePrice(price: BigDecimal) {
		validatePrice(price)
		this.price = price
	}

	fun activate() {
		active = true
	}

	fun deactivate() {
		active = false
	}

	companion object {
		private const val MAX_NAME_LENGTH = 200
		private const val MAX_SKU_LENGTH = 64
		private const val CLOCK_SKEW_SECONDS = 60L

		private fun normalizedName(name: String): String {
			val normalized = name.trim()
			require(normalized.isNotBlank()) { "Product name cannot be blank" }
			require(normalized.length <= MAX_NAME_LENGTH) {
				"Product name cannot exceed $MAX_NAME_LENGTH characters"
			}
			return normalized
		}

		private fun normalizedSku(sku: String): String {
			val normalized = sku.trim().uppercase()
			require(normalized.isNotBlank()) { "SKU cannot be blank" }
			require(normalized.length <= MAX_SKU_LENGTH) {
				"SKU cannot exceed $MAX_SKU_LENGTH characters"
			}
			return normalized
		}

		private fun validatePrice(price: BigDecimal) {
			require(price.signum() >= 0) { "Price cannot be negative" }
		}
	}
}
