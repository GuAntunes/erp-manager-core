package br.com.gammacode.erpmanagercore.domain.model.customer

class ContactInfo(
	phone: String,
	email: String,
	optionalNotes: String? = null
) {
	val phone: String = phone.trim()
	val email: String = email.trim().lowercase()
	val optionalNotes: String? = optionalNotes?.trim()?.ifBlank { null }

	init {
		val digits = this.phone.count { it.isDigit() }
		require(digits in MIN_PHONE_DIGITS..MAX_PHONE_DIGITS) {
			"Phone must contain between $MIN_PHONE_DIGITS and $MAX_PHONE_DIGITS digits"
		}
		require(this.email.length <= MAX_EMAIL_LENGTH && EMAIL_PATTERN.matches(this.email)) {
			"Invalid email: $email"
		}
		require(this.optionalNotes == null || this.optionalNotes.length <= MAX_NOTES_LENGTH) {
			"Notes cannot exceed $MAX_NOTES_LENGTH characters"
		}
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is ContactInfo) return false
		return phone == other.phone && email == other.email && optionalNotes == other.optionalNotes
	}

	override fun hashCode(): Int {
		var result = phone.hashCode()
		result = 31 * result + email.hashCode()
		result = 31 * result + (optionalNotes?.hashCode() ?: 0)
		return result
	}

	override fun toString(): String = "ContactInfo(phone=$phone, email=$email, optionalNotes=$optionalNotes)"

	companion object {
		private const val MIN_PHONE_DIGITS = 8
		private const val MAX_PHONE_DIGITS = 15
		private const val MAX_EMAIL_LENGTH = 254
		private const val MAX_NOTES_LENGTH = 1000
		private val EMAIL_PATTERN = Regex("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")
	}
}
