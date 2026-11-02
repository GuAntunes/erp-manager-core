package br.com.gammacode.erpmanagercore.domain.port.`in`.product

import br.com.gammacode.erpmanagercore.application.dto.product.UpdateProductCommand
import br.com.gammacode.erpmanagercore.domain.model.product.Product

interface UpdateProductUseCase {
	fun execute(command: UpdateProductCommand): Product
}
