package br.com.gammacode.erpmanagercore.domain.port.`in`.product

import br.com.gammacode.erpmanagercore.domain.model.product.Product
import br.com.gammacode.erpmanagercore.domain.port.`in`.PageResult

interface ListProductsUseCase {
	fun execute(page: Int, size: Int): PageResult<Product>
}
