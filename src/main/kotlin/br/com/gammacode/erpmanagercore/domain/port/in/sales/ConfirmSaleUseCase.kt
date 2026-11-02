package br.com.gammacode.erpmanagercore.domain.port.`in`.sales

import br.com.gammacode.erpmanagercore.application.dto.sales.ConfirmSaleCommand
import br.com.gammacode.erpmanagercore.domain.model.sales.Sale

interface ConfirmSaleUseCase {
	fun execute(command: ConfirmSaleCommand): Sale
}
