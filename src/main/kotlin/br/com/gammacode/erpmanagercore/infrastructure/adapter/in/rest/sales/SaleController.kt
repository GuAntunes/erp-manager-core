package br.com.gammacode.erpmanagercore.infrastructure.adapter.`in`.rest.sales

import br.com.gammacode.erpmanagercore.application.dto.sales.CancelSaleCommand
import br.com.gammacode.erpmanagercore.application.dto.sales.ConfirmSaleCommand
import br.com.gammacode.erpmanagercore.application.dto.sales.GetSaleQuery
import br.com.gammacode.erpmanagercore.application.dto.sales.ListSalesQuery
import br.com.gammacode.erpmanagercore.application.dto.sales.RemoveSaleLineCommand
import br.com.gammacode.erpmanagercore.domain.port.`in`.sales.AddSaleLineUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.sales.ApplySaleDiscountUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.sales.CancelSaleUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.sales.ChangeSaleLineQuantityUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.sales.ConfirmSaleUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.sales.CreateSaleUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.sales.GetSaleUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.sales.ListSalesUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.sales.RemoveSaleLineUseCase
import br.com.gammacode.erpmanagercore.infrastructure.adapter.`in`.rest.dto.PageResponse
import br.com.gammacode.erpmanagercore.infrastructure.adapter.`in`.rest.dto.sales.AddSaleLineRequest
import br.com.gammacode.erpmanagercore.infrastructure.adapter.`in`.rest.dto.sales.ApplySaleDiscountRequest
import br.com.gammacode.erpmanagercore.infrastructure.adapter.`in`.rest.dto.sales.ChangeSaleLineQuantityRequest
import br.com.gammacode.erpmanagercore.infrastructure.adapter.`in`.rest.dto.sales.CreateSaleRequest
import br.com.gammacode.erpmanagercore.infrastructure.adapter.`in`.rest.dto.sales.SaleResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/sales")
class SaleController(
	private val createSaleUseCase: CreateSaleUseCase,
	private val getSaleUseCase: GetSaleUseCase,
	private val listSalesUseCase: ListSalesUseCase,
	private val addSaleLineUseCase: AddSaleLineUseCase,
	private val changeSaleLineQuantityUseCase: ChangeSaleLineQuantityUseCase,
	private val removeSaleLineUseCase: RemoveSaleLineUseCase,
	private val applySaleDiscountUseCase: ApplySaleDiscountUseCase,
	private val confirmSaleUseCase: ConfirmSaleUseCase,
	private val cancelSaleUseCase: CancelSaleUseCase
) {

	@PostMapping
	fun create(@RequestBody request: CreateSaleRequest): ResponseEntity<SaleResponse> {
		val sale = createSaleUseCase.execute(request.toCommand())
		return ResponseEntity.status(HttpStatus.CREATED).body(SaleResponse.fromDomain(sale))
	}

	@GetMapping("/{id}")
	fun get(@PathVariable id: String): SaleResponse =
		SaleResponse.fromDomain(getSaleUseCase.execute(GetSaleQuery(id)))

	@GetMapping
	fun list(
		@RequestParam(defaultValue = "0") page: Int,
		@RequestParam(defaultValue = "20") size: Int,
		@RequestParam(required = false) status: String?
	): PageResponse<SaleResponse> {
		val result = listSalesUseCase.execute(ListSalesQuery(page, size, status))
		return PageResponse(
			content = result.content.map(SaleResponse::fromDomain),
			page = result.page,
			size = result.size,
			totalElements = result.totalElements,
			totalPages = result.totalPages,
			first = result.isFirst,
			last = result.isLast
		)
	}

	@PostMapping("/{id}/lines")
	fun addLine(@PathVariable id: String, @RequestBody request: AddSaleLineRequest): SaleResponse =
		SaleResponse.fromDomain(addSaleLineUseCase.execute(request.toCommand(id)))

	@PatchMapping("/{id}/lines/{lineNumber}")
	fun changeQuantity(
		@PathVariable id: String,
		@PathVariable lineNumber: Int,
		@RequestBody request: ChangeSaleLineQuantityRequest
	): SaleResponse =
		SaleResponse.fromDomain(changeSaleLineQuantityUseCase.execute(request.toCommand(id, lineNumber)))

	@DeleteMapping("/{id}/lines/{lineNumber}")
	fun removeLine(@PathVariable id: String, @PathVariable lineNumber: Int): SaleResponse =
		SaleResponse.fromDomain(removeSaleLineUseCase.execute(RemoveSaleLineCommand(id, lineNumber)))

	@PutMapping("/{id}/discount")
	fun applyDiscount(@PathVariable id: String, @RequestBody request: ApplySaleDiscountRequest): SaleResponse =
		SaleResponse.fromDomain(applySaleDiscountUseCase.execute(request.toCommand(id)))

	@PostMapping("/{id}/confirm")
	fun confirm(@PathVariable id: String): SaleResponse =
		SaleResponse.fromDomain(confirmSaleUseCase.execute(ConfirmSaleCommand(id)))

	@PostMapping("/{id}/cancel")
	fun cancel(@PathVariable id: String): SaleResponse =
		SaleResponse.fromDomain(cancelSaleUseCase.execute(CancelSaleCommand(id)))
}
