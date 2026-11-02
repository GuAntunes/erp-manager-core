package br.com.gammacode.erpmanagercore.domain.port.`in`.sales

import br.com.gammacode.erpmanagercore.application.dto.sales.GetSaleQuery
import br.com.gammacode.erpmanagercore.domain.model.sales.Sale

interface GetSaleUseCase {
	fun execute(query: GetSaleQuery): Sale
}
