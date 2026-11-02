package br.com.gammacode.erpmanagercore.domain.port.`in`.sales

import br.com.gammacode.erpmanagercore.application.dto.sales.ApplySaleDiscountCommand
import br.com.gammacode.erpmanagercore.domain.model.sales.Sale

interface ApplySaleDiscountUseCase {
	fun execute(command: ApplySaleDiscountCommand): Sale
}
