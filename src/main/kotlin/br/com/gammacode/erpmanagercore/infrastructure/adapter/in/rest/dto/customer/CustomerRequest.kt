package br.com.gammacode.erpmanagercore.infrastructure.adapter.`in`.rest.dto.customer

import br.com.gammacode.erpmanagercore.application.dto.customer.CreateCustomerCommand
import br.com.gammacode.erpmanagercore.application.dto.customer.UpdateCustomerCommand
import br.com.gammacode.erpmanagercore.domain.model.customer.Customer
import java.time.Instant

data class CreateCustomerRequest(
	val name: String,
	val phone: String,
	val email: String,
	val optionalNotes: String? = null,
	val active: Boolean = true
) {
	fun toCommand(): CreateCustomerCommand = CreateCustomerCommand(name, phone, email, optionalNotes, active)
}

data class UpdateCustomerRequest(
	val name: String,
	val phone: String,
	val email: String,
	val optionalNotes: String? = null,
	val active: Boolean
) {
	fun toCommand(id: String): UpdateCustomerCommand =
		UpdateCustomerCommand(id, name, phone, email, optionalNotes, active)
}

data class CustomerResponse(
	val id: String,
	val name: String,
	val phone: String,
	val email: String,
	val optionalNotes: String?,
	val active: Boolean,
	val createdAt: Instant
) {
	companion object {
		fun fromDomain(customer: Customer): CustomerResponse = CustomerResponse(
			id = customer.id.value.toString(),
			name = customer.name,
			phone = customer.contact.phone,
			email = customer.contact.email,
			optionalNotes = customer.contact.optionalNotes,
			active = customer.active,
			createdAt = customer.createdAt
		)
	}
}
