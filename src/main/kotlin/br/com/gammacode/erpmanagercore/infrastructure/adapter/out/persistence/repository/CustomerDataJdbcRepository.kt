package br.com.gammacode.erpmanagercore.infrastructure.adapter.out.persistence.repository

import br.com.gammacode.erpmanagercore.infrastructure.adapter.out.persistence.entity.CustomerEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CustomerDataJdbcRepository :
	CrudRepository<CustomerEntity, UUID>,
	PagingAndSortingRepository<CustomerEntity, UUID> {

	fun findAllBy(pageable: Pageable): List<CustomerEntity>
}
