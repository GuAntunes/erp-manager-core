package br.com.gammacode.erpmanagercore.domain.model.product

class ProductNotFoundException(id: String) : RuntimeException("Product with ID $id not found")
