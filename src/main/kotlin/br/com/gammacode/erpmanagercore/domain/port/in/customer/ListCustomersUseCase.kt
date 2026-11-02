package br.com.gammacode.erpmanagercore.domain.port.`in`.customer

import br.com.gammacode.erpmanagercore.domain.model.customer.Customer
import br.com.gammacode.erpmanagercore.domain.port.`in`.PageResult

interface ListCustomersUseCase {
	fun execute(page: Int, size: Int): PageResult<Customer>
}
