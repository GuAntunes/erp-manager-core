package br.com.gammacode.erpmanagercore.domain.model.product

import java.math.BigDecimal

class Money(amount: BigDecimal, currency: String) {

	val amount: BigDecimal = amount
	val currency: String = currency.trim().uppercase()

	init {
		require(this.amount.signum() >= 0) { "Amount cannot be negative" }
		require(CURRENCY_PATTERN.matches(this.currency)) { "Currency must be an ISO 4217 code" }
	}

	fun plus(other: Money): Money {
		requireSameCurrency(other)
		return Money(amount.add(other.amount), currency)
	}

	fun minus(other: Money): Money {
		requireSameCurrency(other)
		require(amount.compareTo(other.amount) >= 0) { "Amount cannot be negative" }
		return Money(amount.subtract(other.amount), currency)
	}

	fun times(quantity: Int): Money {
		require(quantity >= 0) { "Quantity cannot be negative" }
		return Money(amount.multiply(BigDecimal.valueOf(quantity.toLong())), currency)
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Money) return false
		return amount.compareTo(other.amount) == 0 && currency == other.currency
	}

	override fun hashCode(): Int = 31 * canonicalAmount().hashCode() + currency.hashCode()

	override fun toString(): String = "$currency $amount"

	private fun requireSameCurrency(other: Money) {
		require(currency == other.currency) { "Currency mismatch: $currency and ${other.currency}" }
	}

	private fun canonicalAmount(): BigDecimal {
		val stripped = amount.stripTrailingZeros()
		return if (stripped.scale() < 0) stripped.setScale(0) else stripped
	}

	companion object {
		private val CURRENCY_PATTERN = Regex("^[A-Z]{3}$")

		fun of(amount: BigDecimal, currency: String): Money = Money(amount, currency)

		fun brl(amount: BigDecimal): Money = Money(amount, "BRL")
	}
}
