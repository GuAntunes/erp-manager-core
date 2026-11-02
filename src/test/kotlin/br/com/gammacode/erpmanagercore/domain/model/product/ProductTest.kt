package br.com.gammacode.erpmanagercore.domain.model.product

import br.com.gammacode.erpmanagercore.domain.model.customer.CustomerId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ProductTest {

	@Test
	fun `creates a product with normalized sku and identity equality`() {
		val id = ProductId.generate()
		val product = Product(
			id = id,
			name = "  Notebook  ",
			sku = "nb-01",
			price = BigDecimal("3500.00")
		)

		assertEquals(id, product.id)
		assertEquals("Notebook", product.name)
		assertEquals("NB-01", product.sku)
		assertEquals(BigDecimal("3500.00"), product.price)
		assertTrue(product.active)

		val sameIdentity = Product(id, "Outro", "sku-2", BigDecimal.ONE)
		assertEquals(product, sameIdentity)
		assertEquals(product.hashCode(), sameIdentity.hashCode())
	}

	@Test
	fun `rejects blank name, blank sku, negative price and a future creation date`() {
		val id = ProductId.generate()

		assertThrows<IllegalArgumentException> {
			Product(id, "  ", "SKU-1", BigDecimal.TEN)
		}
		assertThrows<IllegalArgumentException> {
			Product(id, "Notebook", "  ", BigDecimal.TEN)
		}
		assertThrows<IllegalArgumentException> {
			Product(id, "Notebook", "SKU-1", BigDecimal("-1"))
		}
		assertThrows<IllegalArgumentException> {
			Product(id, "Notebook", "SKU-1", BigDecimal.TEN, createdAt = Instant.now().plusSeconds(120))
		}
	}

	@Test
	fun `changes price and activation through the aggregate`() {
		val product = Product(ProductId.generate(), "Notebook", "SKU-1", BigDecimal.TEN)

		product.changePrice(BigDecimal.ZERO)
		product.deactivate()

		assertEquals(BigDecimal.ZERO, product.price)
		assertFalse(product.active)

		assertThrows<IllegalArgumentException> {
			product.changePrice(BigDecimal("-0.01"))
		}
		assertEquals(BigDecimal.ZERO, product.price)
	}

	@Test
	fun `product id keeps the uuid and does not match another aggregate id`() {
		val uuid = UUID.randomUUID()
		val productId = ProductId.of(uuid)

		assertEquals(uuid, productId.value)
		assertEquals(productId, ProductId.of(uuid.toString()))
		assertNotEquals<Any>(productId, CustomerId.of(uuid))
		assertThrows<IllegalArgumentException> { ProductId.of("not-a-uuid") }
		assertThrows<IllegalArgumentException> { ProductId.of(UUID(0L, 0L)) }
	}
}
