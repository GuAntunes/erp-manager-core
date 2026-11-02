package br.com.gammacode.erpmanagercore.infrastructure.adapter.out.persistence.entity

import br.com.gammacode.erpmanagercore.domain.model.customer.ContactInfo
import br.com.gammacode.erpmanagercore.domain.model.customer.Customer
import br.com.gammacode.erpmanagercore.domain.model.customer.CustomerId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import java.util.UUID

@Table("customers")
class CustomerEntity(
	@Id
	@Column("id")
	@get:JvmName("getIdValue")
	val id: UUID,
	@Column("name")
	val name: String,
	@Column("phone")
	val phone: String,
	@Column("email")
	val email: String,
	@Column("optional_notes")
	val optionalNotes: String?,
	@Column("active")
	val active: Boolean,
	@Column("created_at")
	val createdAt: Instant
) : Persistable<UUID> {

	@field:Transient
	private var newEntity: Boolean = false

	override fun getId(): UUID = id

	override fun isNew(): Boolean = newEntity

	fun markAsNew(): CustomerEntity {
		newEntity = true
		return this
	}

	fun toDomain(): Customer = Customer(
		id = CustomerId.of(id),
		name = name,
		contact = ContactInfo(phone, email, optionalNotes),
		active = active,
		createdAt = createdAt
	)

	companion object {
		fun fromDomain(customer: Customer): CustomerEntity = CustomerEntity(
			id = customer.id.value,
			name = customer.name,
			phone = customer.contact.phone,
			email = customer.contact.email,
			optionalNotes = customer.contact.optionalNotes,
			active = customer.active,
			createdAt = customer.createdAt
		)
	}
}
