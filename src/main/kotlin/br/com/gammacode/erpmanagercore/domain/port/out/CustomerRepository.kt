package br.com.gammacode.erpmanagercore.domain.port.out

import br.com.gammacode.erpmanagercore.domain.model.customer.Customer
import br.com.gammacode.erpmanagercore.domain.model.customer.CustomerId

interface CustomerRepository {
	fun save(customer: Customer): Customer
	fun findById(id: CustomerId): Customer?
	fun findAll(page: Int, size: Int): List<Customer>
	fun count(): Long
	fun deleteById(id: CustomerId)
}
