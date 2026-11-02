package br.com.gammacode.erpmanagercore.application.dto.customer

data class UpdateCustomerCommand(
	val id: String,
	val name: String,
	val phone: String,
	val email: String,
	val optionalNotes: String? = null,
	val active: Boolean
)
