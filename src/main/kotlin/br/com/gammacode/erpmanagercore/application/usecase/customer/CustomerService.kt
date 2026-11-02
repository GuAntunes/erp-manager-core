package br.com.gammacode.erpmanagercore.application.usecase.customer

import br.com.gammacode.erpmanagercore.application.dto.customer.CreateCustomerCommand
import br.com.gammacode.erpmanagercore.application.dto.customer.GetCustomerQuery
import br.com.gammacode.erpmanagercore.application.dto.customer.UpdateCustomerCommand
import br.com.gammacode.erpmanagercore.domain.model.customer.ContactInfo
import br.com.gammacode.erpmanagercore.domain.model.customer.Customer
import br.com.gammacode.erpmanagercore.domain.model.customer.CustomerId
import br.com.gammacode.erpmanagercore.domain.model.customer.CustomerNotFoundException
import br.com.gammacode.erpmanagercore.domain.port.`in`.PageResult
import br.com.gammacode.erpmanagercore.domain.port.`in`.customer.CreateCustomerUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.customer.DeleteCustomerUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.customer.GetCustomerUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.customer.ListCustomersUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.customer.UpdateCustomerUseCase
import br.com.gammacode.erpmanagercore.domain.port.out.CustomerRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomerService(
	private val customerRepository: CustomerRepository
) : CreateCustomerUseCase, GetCustomerUseCase, ListCustomersUseCase, UpdateCustomerUseCase, DeleteCustomerUseCase {

	@Transactional
	override fun execute(command: CreateCustomerCommand): Customer {
		val customer = Customer(
			id = CustomerId.generate(),
			name = command.name,
			contact = ContactInfo(command.phone, command.email, command.optionalNotes),
			active = command.active
		)
		return customerRepository.save(customer)
	}

	override fun execute(query: GetCustomerQuery): Customer = find(query.id)

	override fun execute(page: Int, size: Int): PageResult<Customer> {
		require(page >= 0) { "Page number must be greater than or equal to 0" }
		require(size in 1..100) { "Page size must be between 1 and 100" }
		return PageResult.of(
			content = customerRepository.findAll(page, size),
			page = page,
			size = size,
			totalElements = customerRepository.count()
		)
	}

	@Transactional
	override fun execute(command: UpdateCustomerCommand): Customer {
		val customer = find(command.id)
		customer.rename(command.name)
		customer.changeContact(ContactInfo(command.phone, command.email, command.optionalNotes))
		if (command.active) customer.activate() else customer.deactivate()
		return customerRepository.save(customer)
	}

	@Transactional
	override fun execute(id: String) {
		val customerId = CustomerId.of(id)
		find(id)
		customerRepository.deleteById(customerId)
	}

	private fun find(id: String): Customer {
		val customerId = CustomerId.of(id)
		return customerRepository.findById(customerId) ?: throw CustomerNotFoundException(id)
	}
}
