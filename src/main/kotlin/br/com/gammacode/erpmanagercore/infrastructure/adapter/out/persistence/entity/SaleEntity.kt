package br.com.gammacode.erpmanagercore.infrastructure.adapter.out.persistence.entity

import br.com.gammacode.erpmanagercore.domain.model.customer.CustomerId
import br.com.gammacode.erpmanagercore.domain.model.product.Money
import br.com.gammacode.erpmanagercore.domain.model.product.ProductId
import br.com.gammacode.erpmanagercore.domain.model.sales.Sale
import br.com.gammacode.erpmanagercore.domain.model.sales.SaleId
import br.com.gammacode.erpmanagercore.domain.model.sales.SaleLine
import br.com.gammacode.erpmanagercore.domain.model.sales.SaleStatus
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.MappedCollection
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Table("sales")
class SaleEntity(
	@Id
	@Column("id")
	@get:JvmName("getIdValue")
	val id: UUID,
	@Column("customer_id")
	val customerId: UUID,
	@Column("status")
	val status: String,
	@Column("discount_amount")
	val discountAmount: BigDecimal,
	@Column("discount_currency")
	val discountCurrency: String,
	@Column("confirmed_at")
	val confirmedAt: Instant?,
	@Column("created_at")
	val createdAt: Instant,
	@MappedCollection(idColumn = "sale_id", keyColumn = "line_number")
	val lines: Map<Int, SaleLineEntity>
) : Persistable<UUID> {

	@field:Transient
	private var newEntity: Boolean = false

	override fun getId(): UUID = id

	override fun isNew(): Boolean = newEntity

	fun markAsNew(): SaleEntity {
		newEntity = true
		return this
	}

	fun toDomain(): Sale = Sale(
		id = SaleId.of(id),
		customerId = CustomerId.of(customerId),
		lines = lines.map { (lineNumber, line) ->
			SaleLine(
				lineNumber = lineNumber,
				productId = ProductId.of(line.productId),
				productNameSnapshot = line.productNameSnapshot,
				quantity = line.quantity,
				unitPrice = Money.of(line.unitPrice, line.currency)
			)
		}.sortedBy { it.lineNumber },
		discount = Money.of(discountAmount, discountCurrency),
		status = SaleStatus.valueOf(status),
		confirmedAt = confirmedAt
	)

	companion object {
		fun fromDomain(sale: Sale, createdAt: Instant): SaleEntity = SaleEntity(
			id = sale.id.value,
			customerId = sale.customerId.value,
			status = sale.status.name,
			discountAmount = sale.discount.amount,
			discountCurrency = sale.discount.currency,
			confirmedAt = sale.confirmedAt,
			createdAt = createdAt,
			lines = sale.lines.associate { line ->
				line.lineNumber to SaleLineEntity(
					productId = line.productId.value,
					productNameSnapshot = line.productNameSnapshot,
					quantity = line.quantity,
					unitPrice = line.unitPrice.amount,
					currency = line.unitPrice.currency
				)
			}
		)
	}
}
