package br.com.gammacode.erpmanagercore.domain.model.product

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class MoneyTest {

	@Test
	fun `compares amount by value and normalizes the currency code`() {
		val ten = Money.of(BigDecimal("10.00"), "brl")
		val sameAmount = Money.brl(BigDecimal("10.0"))

		assertEquals("BRL", ten.currency)
		assertEquals(ten, sameAmount)
		assertEquals(ten.hashCode(), sameAmount.hashCode())
		assertNotEquals(ten, Money.of(BigDecimal.TEN, "USD"))
	}

	@Test
	fun `adds, subtracts and multiplies amounts in the same currency`() {
		val ten = Money.brl(BigDecimal("10.00"))

		assertEquals(Money.brl(BigDecimal("25.00")), ten.plus(Money.brl(BigDecimal("15.00"))))
		assertEquals(Money.brl(BigDecimal("4.00")), ten.minus(Money.brl(BigDecimal("6.00"))))
		assertEquals(Money.brl(BigDecimal("30.00")), ten.times(3))
		assertThrows<IllegalArgumentException> { ten.plus(Money.of(BigDecimal.ONE, "USD")) }
		assertThrows<IllegalArgumentException> { ten.minus(Money.brl(BigDecimal("10.01"))) }
	}

	@Test
	fun `rejects a negative amount and an invalid currency`() {
		assertThrows<IllegalArgumentException> {
			Money.of(BigDecimal("-0.01"), "BRL")
		}
		assertThrows<IllegalArgumentException> {
			Money.of(BigDecimal.TEN, "REAL")
		}
	}
}
