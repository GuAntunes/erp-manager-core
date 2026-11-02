package br.com.gammacode.erpmanagercore.domain.port.`in`.customer

import br.com.gammacode.erpmanagercore.application.dto.customer.GetCustomerQuery
import br.com.gammacode.erpmanagercore.domain.model.customer.Customer

interface GetCustomerUseCase {
	fun execute(query: GetCustomerQuery): Customer
}
