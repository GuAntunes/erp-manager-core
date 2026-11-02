package br.com.gammacode.erpmanagercore.application.usecase.sales

import br.com.gammacode.erpmanagercore.application.dto.sales.AddSaleLineCommand
import br.com.gammacode.erpmanagercore.application.dto.sales.ApplySaleDiscountCommand
import br.com.gammacode.erpmanagercore.application.dto.sales.CancelSaleCommand
import br.com.gammacode.erpmanagercore.application.dto.sales.ChangeSaleLineQuantityCommand
import br.com.gammacode.erpmanagercore.application.dto.sales.ConfirmSaleCommand
import br.com.gammacode.erpmanagercore.application.dto.sales.CreateSaleCommand
import br.com.gammacode.erpmanagercore.application.dto.sales.GetSaleQuery
import br.com.gammacode.erpmanagercore.application.dto.sales.ListSalesQuery
import br.com.gammacode.erpmanagercore.application.dto.sales.RemoveSaleLineCommand
import br.com.gammacode.erpmanagercore.domain.model.customer.CustomerId
import br.com.gammacode.erpmanagercore.domain.model.customer.CustomerNotFoundException
import br.com.gammacode.erpmanagercore.domain.model.product.Money
import br.com.gammacode.erpmanagercore.domain.model.product.ProductId
import br.com.gammacode.erpmanagercore.domain.model.product.ProductNotFoundException
import br.com.gammacode.erpmanagercore.domain.model.sales.Sale
import br.com.gammacode.erpmanagercore.domain.model.sales.SaleId
import br.com.gammacode.erpmanagercore.domain.model.sales.SaleNotFoundException
import br.com.gammacode.erpmanagercore.domain.model.sales.SaleStatus
import br.com.gammacode.erpmanagercore.domain.port.`in`.PageResult
import br.com.gammacode.erpmanagercore.domain.port.`in`.sales.AddSaleLineUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.sales.ApplySaleDiscountUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.sales.CancelSaleUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.sales.ChangeSaleLineQuantityUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.sales.ConfirmSaleUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.sales.CreateSaleUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.sales.GetSaleUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.sales.ListSalesUseCase
import br.com.gammacode.erpmanagercore.domain.port.`in`.sales.RemoveSaleLineUseCase
import br.com.gammacode.erpmanagercore.domain.port.out.CustomerRepository
import br.com.gammacode.erpmanagercore.domain.port.out.ProductRepository
import br.com.gammacode.erpmanagercore.domain.port.out.SaleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SaleService(
	private val saleRepository: SaleRepository,
	private val customerRepository: CustomerRepository,
	private val productRepository: ProductRepository
) : CreateSaleUseCase,
	GetSaleUseCase,
	ListSalesUseCase,
	AddSaleLineUseCase,
	ChangeSaleLineQuantityUseCase,
	RemoveSaleLineUseCase,
	ApplySaleDiscountUseCase,
	ConfirmSaleUseCase,
	CancelSaleUseCase {

	@Transactional
	override fun execute(command: CreateSaleCommand): Sale {
		val customer = customerRepository.findById(CustomerId.of(command.customerId))
			?: throw CustomerNotFoundException(command.customerId)
		require(customer.active) { "Customer is inactive" }
		return saleRepository.save(Sale(id = SaleId.generate(), customerId = customer.id))
	}

	override fun execute(query: GetSaleQuery): Sale = find(query.id)

	override fun execute(query: ListSalesQuery): PageResult<Sale> {
		require(query.page >= 0) { "Page number must be greater than or equal to 0" }
		require(query.size in 1..100) { "Page size must be between 1 and 100" }
		val status = query.status?.let(::parseStatus)
		return PageResult.of(
			content = saleRepository.findAll(query.page, query.size, status),
			page = query.page,
			size = query.size,
			totalElements = saleRepository.count(status)
		)
	}

	@Transactional
	override fun execute(command: AddSaleLineCommand): Sale {
		val sale = find(command.saleId)
		val product = productRepository.findById(ProductId.of(command.productId))
			?: throw ProductNotFoundException(command.productId)
		require(product.active) { "Product is inactive" }
		sale.addLine(product.id, product.name, command.quantity, Money.brl(product.price))
		return saleRepository.save(sale)
	}

	@Transactional
	override fun execute(command: ChangeSaleLineQuantityCommand): Sale {
		val sale = find(command.saleId)
		sale.changeQuantity(command.lineNumber, command.quantity)
		return saleRepository.save(sale)
	}

	@Transactional
	override fun execute(command: RemoveSaleLineCommand): Sale {
		val sale = find(command.saleId)
		sale.removeLine(command.lineNumber)
		return saleRepository.save(sale)
	}

	@Transactional
	override fun execute(command: ApplySaleDiscountCommand): Sale {
		val sale = find(command.saleId)
		sale.applyDiscount(Money.of(command.amount, command.currency))
		return saleRepository.save(sale)
	}

	@Transactional
	override fun execute(command: ConfirmSaleCommand): Sale {
		val sale = find(command.saleId)
		sale.confirm()
		return saleRepository.save(sale)
	}

	@Transactional
	override fun execute(command: CancelSaleCommand): Sale {
		val sale = find(command.saleId)
		sale.cancel()
		return saleRepository.save(sale)
	}

	private fun find(id: String): Sale =
		saleRepository.findById(SaleId.of(id)) ?: throw SaleNotFoundException(id)

	private fun parseStatus(status: String): SaleStatus =
		runCatching { SaleStatus.valueOf(status.trim().uppercase()) }.getOrElse {
			throw IllegalArgumentException("Invalid sale status: $status")
		}
}
