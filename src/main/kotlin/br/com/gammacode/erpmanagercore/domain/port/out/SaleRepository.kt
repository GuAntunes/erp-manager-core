package br.com.gammacode.erpmanagercore.domain.port.out

import br.com.gammacode.erpmanagercore.domain.model.sales.Sale
import br.com.gammacode.erpmanagercore.domain.model.sales.SaleId
import br.com.gammacode.erpmanagercore.domain.model.sales.SaleStatus

interface SaleRepository {
	fun save(sale: Sale): Sale
	fun findById(id: SaleId): Sale?
	fun findAll(page: Int, size: Int, status: SaleStatus?): List<Sale>
	fun count(status: SaleStatus?): Long
}
