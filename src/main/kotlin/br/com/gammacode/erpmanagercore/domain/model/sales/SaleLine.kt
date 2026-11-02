package br.com.gammacode.erpmanagercore.domain.model.sales

import br.com.gammacode.erpmanagercore.domain.model.product.Money
import br.com.gammacode.erpmanagercore.domain.model.product.ProductId

class SaleLine(
	val lineNumber: Int,
	val productId: ProductId,
	productNameSnapshot: String,
	val quantity: Int,
	val unitPrice: Money
) {
	val productNameSnapshot: String = productNameSnapshot.trim()

	init {
		require(lineNumber >= 1) { "Line number must be positive" }
		require(this.productNameSnapshot.isNotBlank()) { "Product name snapshot cannot be blank" }
		require(this.productNameSnapshot.length <= MAX_NAME_LENGTH) {
			"Product name snapshot cannot exceed $MAX_NAME_LENGTH characters"
		}
		require(quantity >= 1) { "Quantity must be at least 1" }
	}

	fun lineAmount(): Money = unitPrice.times(quantity)

	fun withQuantity(quantity: Int): SaleLine =
		SaleLine(lineNumber, productId, productNameSnapshot, quantity, unitPrice)

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is SaleLine) return false
		return lineNumber == other.lineNumber &&
			productId == other.productId &&
			productNameSnapshot == other.productNameSnapshot &&
			quantity == other.quantity &&
			unitPrice == other.unitPrice
	}

	override fun hashCode(): Int {
		var result = lineNumber
		result = 31 * result + productId.hashCode()
		result = 31 * result + productNameSnapshot.hashCode()
		result = 31 * result + quantity
		result = 31 * result + unitPrice.hashCode()
		return result
	}

	override fun toString(): String =
		"SaleLine(lineNumber=$lineNumber, productId=$productId, quantity=$quantity, unitPrice=$unitPrice)"

	companion object {
		private const val MAX_NAME_LENGTH = 200
	}
}
