package br.com.gammacode.erpmanagercore.domain.port.`in`.sales

import br.com.gammacode.erpmanagercore.application.dto.sales.CancelSaleCommand
import br.com.gammacode.erpmanagercore.domain.model.sales.Sale

interface CancelSaleUseCase {
	fun execute(command: CancelSaleCommand): Sale
}
