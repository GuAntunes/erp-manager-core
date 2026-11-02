package br.com.gammacode.erpmanagercore.domain.port.`in`.product

import br.com.gammacode.erpmanagercore.application.dto.product.CreateProductCommand
import br.com.gammacode.erpmanagercore.domain.model.product.Product

interface CreateProductUseCase {
	fun execute(command: CreateProductCommand): Product
}
