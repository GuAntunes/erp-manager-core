package br.com.gammacode.erpmanagercore.domain.model.customer

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CustomerTest {

	@Test
	fun `creates a customer with normalized contact`() {
		val customer = Customer(
			id = CustomerId.generate(),
			name = "  Maria Silva  ",
			contact = ContactInfo(
				phone = " (11) 98888-7766 ",
				email = " Maria@Example.com ",
				optionalNotes = "  "
			)
		)

		assertEquals("Maria Silva", customer.name)
		assertEquals("(11) 98888-7766", customer.contact.phone)
		assertEquals("maria@example.com", customer.contact.email)
		assertNull(customer.contact.optionalNotes)
		assertTrue(customer.active)
	}

	@Test
	fun `contact info is equal by value`() {
		val first = ContactInfo("11988887766", "maria@example.com", "Prefere WhatsApp")
		val second = ContactInfo("11988887766", "MARIA@example.com", "Prefere WhatsApp")

		assertEquals(first, second)
		assertEquals(first.hashCode(), second.hashCode())
	}

	@Test
	fun `rejects invalid contact, blank name and a future creation date`() {
		val id = CustomerId.generate()
		val contact = ContactInfo("11988887766", "maria@example.com")

		assertThrows<IllegalArgumentException> {
			ContactInfo(phone = "123", email = "maria@example.com")
		}
		assertThrows<IllegalArgumentException> {
			ContactInfo(phone = "11988887766", email = "not-an-email")
		}
		assertThrows<IllegalArgumentException> {
			Customer(id, "   ", contact)
		}
		assertThrows<IllegalArgumentException> {
			Customer(id, "Maria", contact, createdAt = Instant.now().plusSeconds(120))
		}
	}

	@Test
	fun `updates contact and activation without changing identity`() {
		val id = CustomerId.of(UUID.randomUUID())
		val customer = Customer(id, "Maria", ContactInfo("11988887766", "maria@example.com"))
		val updatedContact = ContactInfo("1133334444", "maria.silva@example.com", "Ligar após as 18h")

		customer.rename("Maria Souza")
		customer.changeContact(updatedContact)
		customer.deactivate()

		assertEquals(id, customer.id)
		assertEquals("Maria Souza", customer.name)
		assertEquals(updatedContact, customer.contact)
		assertFalse(customer.active)
	}
}
