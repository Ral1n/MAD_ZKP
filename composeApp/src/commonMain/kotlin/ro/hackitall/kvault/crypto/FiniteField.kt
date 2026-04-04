package ro.hackitall.kvault.crypto

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger

val P = "21888242871839275222246405745257275088696311157297823662689037894645226208583".toBigInteger()

class FieldElement(val value: BigInteger) {

    init {
        require(value >= BigInteger.ZERO && value < P) { "Value must be in range [0, P-1]" }
    }

    operator fun plus(other: FieldElement): FieldElement {
        val res = (this.value + other.value) % P
        return FieldElement(if (res < BigInteger.ZERO) res + P else res)
    }

    operator fun minus(other: FieldElement): FieldElement {
        val res = (this.value - other.value) % P
        return FieldElement(if (res < BigInteger.ZERO) res + P else res)
    }

    operator fun times(other: FieldElement): FieldElement {
        val res = (this.value * other.value) % P
        return FieldElement(if (res < BigInteger.ZERO) res + P else res)
    }

    operator fun div(other: FieldElement): FieldElement {
        return this * other.inverse()
    }

    fun pow(exponent: BigInteger): FieldElement {
        require(exponent >= BigInteger.ZERO) { "Negative exponents are not supported." }
        if (exponent == BigInteger.ZERO) return FieldElement(BigInteger.ONE)

        var res = FieldElement(BigInteger.ONE)
        var base = this
        var exp = exponent
        val two = 2.toBigInteger()

        while (exp > BigInteger.ZERO) {
            if (exp % two == BigInteger.ONE) {
                res = res * base
            }
            base = base * base
            exp /= two
        }
        return res
    }

    fun inverse(): FieldElement {
        if (value == BigInteger.ZERO) throw ArithmeticException("Division by zero: zero has no inverse.")
        // Fermat's Little Theorem: a^(p-2) is the modular inverse of a mod p
        return pow(P - 2.toBigInteger())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FieldElement) return false
        return this.value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun toString(): String {
        return "FieldElement(${value})"
    }
}
