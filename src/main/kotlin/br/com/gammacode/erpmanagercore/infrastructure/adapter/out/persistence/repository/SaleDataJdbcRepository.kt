package br.com.gammacode.erpmanagercore.infrastructure.adapter.out.persistence.repository

import br.com.gammacode.erpmanagercore.infrastructure.adapter.out.persistence.entity.SaleEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SaleDataJdbcRepository :
	CrudRepository<SaleEntity, UUID>,
	PagingAndSortingRepository<SaleEntity, UUID> {

	fun findAllBy(pageable: Pageable): List<SaleEntity>

	fun findAllByStatus(status: String, pageable: Pageable): List<SaleEntity>

	fun countByStatus(status: String): Long
}
