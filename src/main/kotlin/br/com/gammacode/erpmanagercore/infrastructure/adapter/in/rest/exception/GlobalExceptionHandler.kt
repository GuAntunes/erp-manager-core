package br.com.gammacode.erpmanagercore.infrastructure.adapter.`in`.rest.exception

import br.com.gammacode.erpmanagercore.domain.model.customer.CustomerNotFoundException
import br.com.gammacode.erpmanagercore.domain.model.product.ProductNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler {

	@ExceptionHandler(ProductNotFoundException::class, CustomerNotFoundException::class)
	fun handleNotFound(ex: RuntimeException, request: WebRequest): ResponseEntity<ErrorResponse> =
		error(HttpStatus.NOT_FOUND, ex.message ?: "Resource not found", request)

	@ExceptionHandler(IllegalArgumentException::class, HttpMessageNotReadableException::class)
	fun handleBadRequest(ex: RuntimeException, request: WebRequest): ResponseEntity<ErrorResponse> =
		error(HttpStatus.BAD_REQUEST, ex.message ?: "Invalid request", request)

	@ExceptionHandler(IllegalStateException::class)
	fun handleConflict(ex: IllegalStateException, request: WebRequest): ResponseEntity<ErrorResponse> =
		error(HttpStatus.CONFLICT, ex.message ?: "Conflict", request)

	@ExceptionHandler(Exception::class)
	fun handleUnexpected(ex: Exception, request: WebRequest): ResponseEntity<ErrorResponse> =
		error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request)

	private fun error(status: HttpStatus, message: String, request: WebRequest): ResponseEntity<ErrorResponse> {
		val body = ErrorResponse(
			timestamp = Instant.now(),
			status = status.value(),
			error = status.reasonPhrase,
			message = message,
			path = request.getDescription(false).removePrefix("uri=")
		)
		return ResponseEntity.status(status).body(body)
	}
}

data class ErrorResponse(
	val timestamp: Instant,
	val status: Int,
	val error: String,
	val message: String,
	val path: String
)
