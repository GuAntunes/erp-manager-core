package br.com.gammacode.erpmanagercore.infrastructure.adapter.out.persistence.entity

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.util.UUID

@Table("sale_lines")
class SaleLineEntity(
	@Column("product_id")
	val productId: UUID,
	@Column("product_name_snapshot")
	val productNameSnapshot: String,
	@Column("quantity")
	val quantity: Int,
	@Column("unit_price")
	val unitPrice: BigDecimal,
	@Column("currency")
	val currency: String
)
