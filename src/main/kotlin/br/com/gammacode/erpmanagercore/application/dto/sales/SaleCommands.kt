package br.com.gammacode.erpmanagercore.application.dto.sales

import java.math.BigDecimal

data class CreateSaleCommand(val customerId: String)

data class GetSaleQuery(val id: String)

data class ListSalesQuery(
	val page: Int,
	val size: Int,
	val status: String? = null
)

data class AddSaleLineCommand(
	val saleId: String,
	val productId: String,
	val quantity: Int
)

data class ChangeSaleLineQuantityCommand(
	val saleId: String,
	val lineNumber: Int,
	val quantity: Int
)

data class RemoveSaleLineCommand(
	val saleId: String,
	val lineNumber: Int
)

data class ApplySaleDiscountCommand(
	val saleId: String,
	val amount: BigDecimal,
	val currency: String
)

data class ConfirmSaleCommand(val saleId: String)

data class CancelSaleCommand(val saleId: String)
