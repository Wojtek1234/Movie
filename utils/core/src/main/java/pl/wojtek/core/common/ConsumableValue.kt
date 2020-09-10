package pl.wojtek.core.common

class ConsumableValue<T>(val data: T) {

    private var consumed = false

    fun consume(block: ConsumableValue<T>.(T) -> Unit) {
        if (!consumed) {
            consumed = true
            block(data)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ConsumableValue<*>) return false

        if (data != other.data) return false
        if (consumed != other.consumed) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data?.hashCode() ?: 0
        result = 31 * result + consumed.hashCode()
        return result
    }

}