package br.com.gammacode.erpmanagercore.infrastructure.adapter.out.persistence.repository

import br.com.gammacode.erpmanagercore.domain.model.customer.Customer
import br.com.gammacode.erpmanagercore.domain.model.customer.CustomerId
import br.com.gammacode.erpmanagercore.domain.port.out.CustomerRepository
import br.com.gammacode.erpmanagercore.infrastructure.adapter.out.persistence.entity.CustomerEntity
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
class CustomerRepositoryAdapter(
	private val dataRepository: CustomerDataJdbcRepository
) : CustomerRepository {

	override fun save(customer: Customer): Customer {
		val entity = CustomerEntity.fromDomain(customer)
		if (!dataRepository.existsById(customer.id.value)) {
			entity.markAsNew()
		}
		return dataRepository.save(entity).toDomain()
	}

	override fun findById(id: CustomerId): Customer? =
		dataRepository.findById(id.value).map { it.toDomain() }.orElse(null)

	override fun findAll(page: Int, size: Int): List<Customer> {
		val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
		return dataRepository.findAllBy(pageable).map { it.toDomain() }
	}

	override fun count(): Long = dataRepository.count()

	override fun deleteById(id: CustomerId) {
		try {
			dataRepository.deleteById(id.value)
		} catch (ex: DataIntegrityViolationException) {
			throw IllegalStateException("Customer cannot be deleted because it has sales", ex)
		}
	}
}
