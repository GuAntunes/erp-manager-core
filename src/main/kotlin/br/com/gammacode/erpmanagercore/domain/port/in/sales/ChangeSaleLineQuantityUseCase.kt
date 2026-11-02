package br.com.gammacode.erpmanagercore.domain.port.`in`.sales

import br.com.gammacode.erpmanagercore.application.dto.sales.ChangeSaleLineQuantityCommand
import br.com.gammacode.erpmanagercore.domain.model.sales.Sale

interface ChangeSaleLineQuantityUseCase {
	fun execute(command: ChangeSaleLineQuantityCommand): Sale
}
