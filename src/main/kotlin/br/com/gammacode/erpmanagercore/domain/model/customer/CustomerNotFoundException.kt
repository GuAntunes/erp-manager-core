package br.com.gammacode.erpmanagercore.domain.model.customer

class CustomerNotFoundException(id: String) : RuntimeException("Customer with ID $id not found")
