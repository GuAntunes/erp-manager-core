package br.com.gammacode.erpmanagercore.infrastructure.adapter.out.persistence.entity

import br.com.gammacode.erpmanagercore.domain.model.product.Product
import br.com.gammacode.erpmanagercore.domain.model.product.ProductId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Table("products")
class ProductEntity(
	@Id
	@Column("id")
	@get:JvmName("getIdValue")
	val id: UUID,
	@Column("name")
	val name: String,
	@Column("sku")
	val sku: String,
	@Column("active")
	val active: Boolean,
	@Column("price")
	val price: BigDecimal,
	@Column("created_at")
	val createdAt: Instant
) : Persistable<UUID> {

	@field:Transient
	private var newEntity: Boolean = false

	override fun getId(): UUID = id

	override fun isNew(): Boolean = newEntity

	fun markAsNew(): ProductEntity {
		newEntity = true
		return this
	}

	fun toDomain(): Product = Product(
		id = ProductId.of(id),
		name = name,
		sku = sku,
		price = price,
		active = active,
		createdAt = createdAt
	)

	companion object {
		fun fromDomain(product: Product): ProductEntity = ProductEntity(
			id = product.id.value,
			name = product.name,
			sku = product.sku,
			active = product.active,
			price = product.price,
			createdAt = product.createdAt
		)
	}
}
