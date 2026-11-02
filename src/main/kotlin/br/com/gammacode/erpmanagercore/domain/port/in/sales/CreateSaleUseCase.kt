package br.com.gammacode.erpmanagercore.domain.port.`in`.sales

import br.com.gammacode.erpmanagercore.application.dto.sales.CreateSaleCommand
import br.com.gammacode.erpmanagercore.domain.model.sales.Sale

interface CreateSaleUseCase {
	fun execute(command: CreateSaleCommand): Sale
}
