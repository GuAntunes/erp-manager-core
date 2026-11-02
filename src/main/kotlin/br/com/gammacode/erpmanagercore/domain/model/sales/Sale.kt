package br.com.gammacode.erpmanagercore.domain.model.sales

import br.com.gammacode.erpmanagercore.domain.model.common.AggregateRoot
import br.com.gammacode.erpmanagercore.domain.model.customer.CustomerId
import br.com.gammacode.erpmanagercore.domain.model.product.Money
import br.com.gammacode.erpmanagercore.domain.model.product.ProductId
import java.math.BigDecimal
import java.time.Instant

class Sale(
	override val id: SaleId,
	val customerId: CustomerId,
	lines: List<SaleLine> = emptyList(),
	discount: Money = Money.brl(BigDecimal.ZERO),
	status: SaleStatus = SaleStatus.DRAFT,
	confirmedAt: Instant? = null
) : AggregateRoot<SaleId>() {

	var status: SaleStatus = status
		private set

	var confirmedAt: Instant? = confirmedAt
		private set

	private var lineItems: List<SaleLine> = lines.toList()

	var discount: Money = discount
		private set

	var subtotal: Money
		private set

	var total: Money
		private set

	val lines: List<SaleLine>
		get() = lineItems.toList()

	init {
		require(!(this.status == SaleStatus.DRAFT && this.confirmedAt != null)) {
			"A draft sale cannot have a confirmation date"
		}
		require(!(this.status == SaleStatus.CONFIRMED && this.confirmedAt == null)) {
			"A confirmed sale must have a confirmation date"
		}
		if (this.status == SaleStatus.CONFIRMED) {
			require(lineItems.isNotEmpty()) { "A confirmed sale must have at least one line" }
		}
		this.confirmedAt?.let { confirmed ->
			require(!confirmed.isAfter(Instant.now().plusSeconds(CLOCK_SKEW_SECONDS))) {
				"Confirmation date cannot be in the future"
			}
		}
		val totals = totalsFor(lineItems, this.discount)
		subtotal = totals.subtotal
		total = totals.total
	}

	fun addLine(productId: ProductId, productNameSnapshot: String, quantity: Int, unitPrice: Money): SaleLine {
		ensureDraft()
		val line = SaleLine(
			lineNumber = (lineItems.maxOfOrNull { it.lineNumber } ?: 0) + 1,
			productId = productId,
			productNameSnapshot = productNameSnapshot,
			quantity = quantity,
			unitPrice = unitPrice
		)
		apply(lineItems + line, discount)
		return line
	}

	fun changeQuantity(lineNumber: Int, quantity: Int) {
		ensureDraft()
		val current = line(lineNumber)
		apply(
			lineItems.map { if (it.lineNumber == lineNumber) current.withQuantity(quantity) else it },
			discount
		)
	}

	fun removeLine(lineNumber: Int) {
		ensureDraft()
		line(lineNumber)
		apply(lineItems.filterNot { it.lineNumber == lineNumber }, discount)
	}

	fun applyDiscount(discount: Money) {
		ensureDraft()
		apply(lineItems, discount)
	}

	fun confirm(confirmedAt: Instant = Instant.now()) {
		require(status == SaleStatus.DRAFT) { "Only a draft sale can be confirmed" }
		require(lineItems.isNotEmpty()) { "A sale must have at least one line to be confirmed" }
		require(!confirmedAt.isAfter(Instant.now().plusSeconds(CLOCK_SKEW_SECONDS))) {
			"Confirmation date cannot be in the future"
		}
		this.status = SaleStatus.CONFIRMED
		this.confirmedAt = confirmedAt
	}

	fun cancel() {
		require(status != SaleStatus.CANCELLED) { "Sale is already cancelled" }
		status = SaleStatus.CANCELLED
	}

	private fun line(lineNumber: Int): SaleLine =
		lineItems.find { it.lineNumber == lineNumber }
			?: throw IllegalArgumentException("Line $lineNumber does not exist")

	private fun ensureDraft() {
		require(status == SaleStatus.DRAFT) { "Only a draft sale can be changed" }
	}

	private fun apply(lines: List<SaleLine>, discount: Money) {
		val totals = totalsFor(lines, discount)
		lineItems = lines
		this.discount = discount
		subtotal = totals.subtotal
		total = totals.total
	}

	private fun totalsFor(lines: List<SaleLine>, discount: Money): SaleTotals {
		require(lines.distinctBy { it.lineNumber }.size == lines.size) { "Line numbers must be unique" }
		val currency = lines.firstOrNull()?.unitPrice?.currency ?: discount.currency
		require(discount.currency == currency) { "Discount currency must match sale currency" }
		require(lines.all { it.unitPrice.currency == currency }) { "All lines must use the same currency" }

		val subtotal = lines.fold(Money.of(BigDecimal.ZERO, currency)) { acc, line ->
			acc.plus(line.lineAmount())
		}
		require(discount.amount.compareTo(subtotal.amount) <= 0) { "Discount cannot exceed subtotal" }
		return SaleTotals(subtotal, subtotal.minus(discount))
	}

	private data class SaleTotals(val subtotal: Money, val total: Money)

	companion object {
		private const val CLOCK_SKEW_SECONDS = 60L
	}
}
