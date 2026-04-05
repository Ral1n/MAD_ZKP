package ro.hackitall.kvault.sdk.crypto

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger

// The prime for the Alt_Bn128 curve
internal val P = "21888242871839275222246405745257275088696311157297823662689037894645226208583".toBigInteger()

/**
 * Represents an element in the finite field F_p.
 * All arithmetic operations are performed modulo P.
 */
internal class FieldElement(val value: BigInteger) {
    init {
        require(value >= 0.toBigInteger() && value < P) { "Value must be in range [0, P-1]" }
    }

    operator fun plus(other: FieldElement): FieldElement = FieldElement((this.value + other.value) % P)

    operator fun minus(other: FieldElement): FieldElement {
        val result = (this.value - other.value) % P
        return if (result < 0.toBigInteger()) FieldElement(result + P) else FieldElement(result)
    }

    operator fun times(other: FieldElement): FieldElement = FieldElement((this.value * other.value) % P)

    fun pow(exponent: BigInteger): FieldElement {
        var res = FieldElement(1.toBigInteger())
        var base = this
        var exp = exponent
        while (exp > 0.toBigInteger()) {
            if (exp % 2.toBigInteger() == 1.toBigInteger()) res *= base
            base *= base
            exp /= 2.toBigInteger()
        }
        return res
    }

    fun inverse(): FieldElement = pow(P - 2.toBigInteger()) // Using Fermat's Little Theorem

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FieldElement) return false
        return this.value == other.value
    }
    override fun hashCode(): Int = value.hashCode()
}
