package libs.errs

@ConsistentCopyVisibility
data class Error private constructor(val code: String, val message: String) {
    companion object {
        private const val SEPARATOR = "||"

        fun of(code: String, message: String) = Error(code, message)

        fun deserialize(serialized: String): Error {
            if (serialized == "A non-empty request body is required.") {
                return GeneralErrors.valueIsRequired("serialized")
            }

            val parts = serialized.split(SEPARATOR, limit = 2)

            require(parts.size >= 2) { "Invalid error serialization: '$serialized'" }

            return Error(parts[0], parts[1])
        }

        fun throwIf(error: Error?) {
            if (error != null) {
                throw DomainInvariantException(error)
            }
        }
    }

    fun serialize(): String = "$code$SEPARATOR$message"

    override fun toString(): String = "Error{code='$code', message='$message'}"
}
