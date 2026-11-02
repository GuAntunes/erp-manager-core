package br.com.gammacode.erpmanagercore.application.dto.customer

data class CreateCustomerCommand(
	val name: String,
	val phone: String,
	val email: String,
	val optionalNotes: String? = null,
	val active: Boolean = true
)
