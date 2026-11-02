package br.com.gammacode.erpmanagercore.infrastructure.adapter.`in`.rest.dto.sales

import br.com.gammacode.erpmanagercore.application.dto.sales.AddSaleLineCommand
import br.com.gammacode.erpmanagercore.application.dto.sales.ApplySaleDiscountCommand
import br.com.gammacode.erpmanagercore.application.dto.sales.ChangeSaleLineQuantityCommand
import br.com.gammacode.erpmanagercore.application.dto.sales.CreateSaleCommand
import br.com.gammacode.erpmanagercore.domain.model.sales.Sale
import br.com.gammacode.erpmanagercore.domain.model.sales.SaleLine
import java.math.BigDecimal
import java.time.Instant

data class CreateSaleRequest(
	val customerId: String
) {
	fun toCommand(): CreateSaleCommand = CreateSaleCommand(customerId)
}

data class AddSaleLineRequest(
	val productId: String,
	val quantity: Int
) {
	fun toCommand(saleId: String): AddSaleLineCommand = AddSaleLineCommand(saleId, productId, quantity)
}

data class ChangeSaleLineQuantityRequest(
	val quantity: Int
) {
	fun toCommand(saleId: String, lineNumber: Int): ChangeSaleLineQuantityCommand =
		ChangeSaleLineQuantityCommand(saleId, lineNumber, quantity)
}

data class ApplySaleDiscountRequest(
	val amount: BigDecimal,
	val currency: String
) {
	fun toCommand(saleId: String): ApplySaleDiscountCommand =
		ApplySaleDiscountCommand(saleId, amount, currency)
}

data class MoneyResponse(
	val amount: BigDecimal,
	val currency: String
)

data class SaleLineResponse(
	val lineNumber: Int,
	val productId: String,
	val productNameSnapshot: String,
	val quantity: Int,
	val unitPrice: MoneyResponse
) {
	companion object {
		fun fromDomain(line: SaleLine): SaleLineResponse = SaleLineResponse(
			lineNumber = line.lineNumber,
			productId = line.productId.value.toString(),
			productNameSnapshot = line.productNameSnapshot,
			quantity = line.quantity,
			unitPrice = MoneyResponse(line.unitPrice.amount, line.unitPrice.currency)
		)
	}
}

data class SaleResponse(
	val id: String,
	val customerId: String,
	val status: String,
	val lines: List<SaleLineResponse>,
	val subtotal: MoneyResponse,
	val discount: MoneyResponse,
	val total: MoneyResponse,
	val confirmedAt: Instant?
) {
	companion object {
		fun fromDomain(sale: Sale): SaleResponse = SaleResponse(
			id = sale.id.value.toString(),
			customerId = sale.customerId.value.toString(),
			status = sale.status.name,
			lines = sale.lines.map(SaleLineResponse::fromDomain),
			subtotal = MoneyResponse(sale.subtotal.amount, sale.subtotal.currency),
			discount = MoneyResponse(sale.discount.amount, sale.discount.currency),
			total = MoneyResponse(sale.total.amount, sale.total.currency),
			confirmedAt = sale.confirmedAt
		)
	}
}
