package br.com.gammacode.erpmanagercore.domain.port.`in`.product

import br.com.gammacode.erpmanagercore.application.dto.product.GetProductQuery
import br.com.gammacode.erpmanagercore.domain.model.product.Product

interface GetProductUseCase {
	fun execute(query: GetProductQuery): Product
}
