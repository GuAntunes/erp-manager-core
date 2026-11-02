package br.com.gammacode.erpmanagercore.infrastructure.adapter.out.persistence.repository

import br.com.gammacode.erpmanagercore.domain.model.sales.Sale
import br.com.gammacode.erpmanagercore.domain.model.sales.SaleId
import br.com.gammacode.erpmanagercore.domain.model.sales.SaleStatus
import br.com.gammacode.erpmanagercore.domain.port.out.SaleRepository
import br.com.gammacode.erpmanagercore.infrastructure.adapter.out.persistence.entity.SaleEntity
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class SaleRepositoryAdapter(
	private val dataRepository: SaleDataJdbcRepository
) : SaleRepository {

	override fun save(sale: Sale): Sale {
		val existing = dataRepository.findById(sale.id.value)
		val entity = SaleEntity.fromDomain(
			sale = sale,
			createdAt = existing.map { it.createdAt }.orElseGet { Instant.now() }
		)
		if (existing.isEmpty) {
			entity.markAsNew()
		}
		return dataRepository.save(entity).toDomain()
	}

	override fun findById(id: SaleId): Sale? =
		dataRepository.findById(id.value).map { it.toDomain() }.orElse(null)

	override fun findAll(page: Int, size: Int, status: SaleStatus?): List<Sale> {
		val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
		val entities = if (status == null) {
			dataRepository.findAllBy(pageable)
		} else {
			dataRepository.findAllByStatus(status.name, pageable)
		}
		return entities.map { it.toDomain() }
	}

	override fun count(status: SaleStatus?): Long =
		if (status == null) dataRepository.count() else dataRepository.countByStatus(status.name)
}
