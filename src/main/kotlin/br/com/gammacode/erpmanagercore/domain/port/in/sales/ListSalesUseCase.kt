package br.com.gammacode.erpmanagercore.domain.port.`in`.sales

import br.com.gammacode.erpmanagercore.application.dto.sales.ListSalesQuery
import br.com.gammacode.erpmanagercore.domain.model.sales.Sale
import br.com.gammacode.erpmanagercore.domain.port.`in`.PageResult

interface ListSalesUseCase {
	fun execute(query: ListSalesQuery): PageResult<Sale>
}
