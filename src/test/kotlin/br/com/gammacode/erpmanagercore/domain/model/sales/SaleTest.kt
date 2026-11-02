package br.com.gammacode.erpmanagercore.domain.model.sales

import br.com.gammacode.erpmanagercore.domain.model.customer.CustomerId
import br.com.gammacode.erpmanagercore.domain.model.product.Money
import br.com.gammacode.erpmanagercore.domain.model.product.ProductId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SaleTest {

	private val customerId = CustomerId.generate()
	private val notebookId = ProductId.generate()
	private val mouseId = ProductId.generate()

	@Test
	fun `draft sale totals lines and keeps the snapshot`() {
		val sale = Sale(SaleId.generate(), customerId)

		sale.addLine(notebookId, "  Notebook  ", 2, Money.brl(BigDecimal("3500.00")))
		sale.addLine(mouseId, "Mouse", 1, Money.brl(BigDecimal("80.00")))
		sale.applyDiscount(Money.brl(BigDecimal("100.00")))

		assertEquals(SaleStatus.DRAFT, sale.status)
		assertNull(sale.confirmedAt)
		assertEquals(listOf(1, 2), sale.lines.map { it.lineNumber })
		assertEquals("Notebook", sale.lines.first().productNameSnapshot)
		assertEquals(Money.brl(BigDecimal("7080.00")), sale.subtotal)
		assertEquals(Money.brl(BigDecimal("100.00")), sale.discount)
		assertEquals(Money.brl(BigDecimal("6980.00")), sale.total)
	}

	@Test
	fun `changing and removing a line recalculates the total and preserves line numbers`() {
		val sale = Sale(SaleId.generate(), customerId)
		sale.addLine(notebookId, "Notebook", 1, Money.brl(BigDecimal("100.00")))
		sale.addLine(mouseId, "Mouse", 1, Money.brl(BigDecimal("40.00")))
		sale.applyDiscount(Money.brl(BigDecimal("10.00")))

		sale.changeQuantity(1, 3)
		assertEquals(Money.brl(BigDecimal("330.00")), sale.total)

		sale.removeLine(1)
		assertEquals(listOf(2), sale.lines.map { it.lineNumber })
		assertEquals(Money.brl(BigDecimal("30.00")), sale.total)

		val added = sale.addLine(notebookId, "Notebook", 1, Money.brl(BigDecimal("100.00")))
		assertEquals(3, added.lineNumber)
	}

	@Test
	fun `rejects inconsistent lines, discount and currency`() {
		val sale = Sale(SaleId.generate(), customerId)
		sale.addLine(notebookId, "Notebook", 1, Money.brl(BigDecimal("50.00")))

		assertThrows<IllegalArgumentException> { sale.applyDiscount(Money.brl(BigDecimal("50.01"))) }
		assertThrows<IllegalArgumentException> { sale.addLine(mouseId, "Mouse", 1, Money.of(BigDecimal.TEN, "USD")) }
		assertThrows<IllegalArgumentException> { sale.changeQuantity(1, 0) }
		assertThrows<IllegalArgumentException> { sale.removeLine(9) }
		assertThrows<IllegalArgumentException> {
			SaleLine(0, notebookId, "Notebook", 1, Money.brl(BigDecimal.TEN))
		}
		assertThrows<IllegalArgumentException> {
			val duplicated = SaleLine(1, notebookId, "Notebook", 1, Money.brl(BigDecimal.TEN))
			Sale(
				SaleId.generate(),
				customerId,
				lines = listOf(duplicated, duplicated.withLineNumber(1)),
				discount = Money.brl(BigDecimal.ZERO)
			)
		}
		assertThrows<IllegalArgumentException> { Sale(SaleId.generate(), customerId).confirm() }
	}

	@Test
	fun `confirms a draft and blocks later changes`() {
		val sale = Sale(SaleId.generate(), customerId)
		val confirmedAt = Instant.now().minusSeconds(30)
		sale.addLine(notebookId, "Notebook", 1, Money.brl(BigDecimal("100.00")))

		sale.confirm(confirmedAt)

		assertEquals(SaleStatus.CONFIRMED, sale.status)
		assertEquals(confirmedAt, sale.confirmedAt)
		assertThrows<IllegalArgumentException> {
			sale.addLine(mouseId, "Mouse", 1, Money.brl(BigDecimal.TEN))
		}
		assertThrows<IllegalArgumentException> { sale.confirm(confirmedAt) }
	}

	@Test
	fun `cancels a confirmed sale and keeps the confirmation date`() {
		val confirmedAt = Instant.now().minusSeconds(30)
		val line = SaleLine(1, notebookId, "Notebook", 2, Money.brl(BigDecimal("80.00")))
		val sale = Sale(
			id = SaleId.generate(),
			customerId = customerId,
			lines = listOf(line),
			discount = Money.brl(BigDecimal("10.00")),
			status = SaleStatus.CONFIRMED,
			confirmedAt = confirmedAt
		)

		sale.cancel()

		assertEquals(SaleStatus.CANCELLED, sale.status)
		assertEquals(confirmedAt, sale.confirmedAt)
		assertEquals(Money.brl(BigDecimal("150.00")), sale.total)
		assertThrows<IllegalArgumentException> { sale.cancel() }
		assertThrows<IllegalArgumentException> {
			sale.confirm(Instant.now())
		}
	}

	@Test
	fun `two sales with the same id are the same sale`() {
		val id = SaleId.generate()
		val first = Sale(id, customerId)
		val second = Sale(id, CustomerId.generate())

		assertEquals(first, second)
		assertEquals(first.hashCode(), second.hashCode())
		assertTrue(id == SaleId.of(id.value.toString()))
		assertThrows<IllegalArgumentException> { SaleId.of("not-a-uuid") }
	}

	private fun SaleLine.withLineNumber(lineNumber: Int): SaleLine =
		SaleLine(lineNumber, productId, productNameSnapshot, quantity, unitPrice)
}
