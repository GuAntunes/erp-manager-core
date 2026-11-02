package br.com.gammacode.erpmanagercore.application.dto.product

import java.math.BigDecimal

data class CreateProductCommand(
	val name: String,
	val sku: String,
	val price: BigDecimal,
	val active: Boolean = true
)
