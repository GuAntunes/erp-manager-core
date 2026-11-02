package br.com.gammacode.erpmanagercore.domain.port.`in`.customer

import br.com.gammacode.erpmanagercore.application.dto.customer.CreateCustomerCommand
import br.com.gammacode.erpmanagercore.domain.model.customer.Customer

interface CreateCustomerUseCase {
	fun execute(command: CreateCustomerCommand): Customer
}
