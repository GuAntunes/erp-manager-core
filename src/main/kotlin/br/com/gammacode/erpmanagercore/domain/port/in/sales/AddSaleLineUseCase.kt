package br.com.gammacode.erpmanagercore.domain.port.`in`.sales

import br.com.gammacode.erpmanagercore.application.dto.sales.AddSaleLineCommand
import br.com.gammacode.erpmanagercore.domain.model.sales.Sale

interface AddSaleLineUseCase {
	fun execute(command: AddSaleLineCommand): Sale
}
