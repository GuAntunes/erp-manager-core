package br.com.gammacode.erpmanagercore.domain.port.`in`.customer

import br.com.gammacode.erpmanagercore.application.dto.customer.UpdateCustomerCommand
import br.com.gammacode.erpmanagercore.domain.model.customer.Customer

interface UpdateCustomerUseCase {
	fun execute(command: UpdateCustomerCommand): Customer
}
