package br.com.gammacode.erpmanagercore.domain.port.`in`.sales

import br.com.gammacode.erpmanagercore.application.dto.sales.RemoveSaleLineCommand
import br.com.gammacode.erpmanagercore.domain.model.sales.Sale

interface RemoveSaleLineUseCase {
	fun execute(command: RemoveSaleLineCommand): Sale
}
