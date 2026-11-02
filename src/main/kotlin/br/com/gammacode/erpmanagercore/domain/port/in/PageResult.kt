package br.com.gammacode.erpmanagercore.domain.port.`in`

data class PageResult<T>(
	val content: List<T>,
	val page: Int,
	val size: Int,
	val totalElements: Long,
	val totalPages: Int,
	val isFirst: Boolean,
	val isLast: Boolean
) {
	companion object {
		fun <T> of(content: List<T>, page: Int, size: Int, totalElements: Long): PageResult<T> {
			val totalPages = if (size > 0) ((totalElements + size - 1) / size).toInt() else 0
			return PageResult(
				content = content,
				page = page,
				size = size,
				totalElements = totalElements,
				totalPages = totalPages,
				isFirst = page == 0,
				isLast = totalPages == 0 || page >= totalPages - 1
			)
		}
	}
}
