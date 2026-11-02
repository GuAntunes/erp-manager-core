package br.com.gammacode.erpmanagercore.domain.model.sales

class SaleNotFoundException(id: String) : RuntimeException("Sale with ID $id not found")
