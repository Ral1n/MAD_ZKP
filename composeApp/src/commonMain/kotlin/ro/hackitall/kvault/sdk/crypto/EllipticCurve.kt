package ro.hackitall.kvault.sdk.crypto

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger

// Curve parameters for y^2 = x^3 + 3
internal val A = FieldElement(0.toBigInteger())
internal val B = FieldElement(3.toBigInteger())

/**
 * Represents a point on the Alt_Bn128 elliptic curve.
 */
internal data class ECPoint(val x: FieldElement?, val y: FieldElement?) {

    // Point addition
    operator fun plus(other: ECPoint): ECPoint {
        if (this.x == null) return other
        if (other.x == null) return this

        if (this == other) { // Point doubling
            if (this.y!!.value == 0.toBigInteger()) return INFINITY
            val m = (FieldElement(3.toBigInteger()) * this.x.pow(2.toBigInteger()) + A) * (FieldElement(2.toBigInteger()) * this.y).inverse()
            val x3 = m.pow(2.toBigInteger()) - (FieldElement(2.toBigInteger()) * this.x)
            val y3 = m * (this.x - x3) - this.y
            return ECPoint(x3, y3)
        } else { // Point addition
            if (this.x == other.x) return INFINITY
            val m = (other.y!! - this.y!!) * (other.x!! - this.x).inverse()
            val x3 = m.pow(2.toBigInteger()) - this.x - other.x
            val y3 = m * (this.x - x3) - this.y
            return ECPoint(x3, y3)
        }
    }

    // Scalar multiplication using the Double-and-Add algorithm
    operator fun times(scalar: BigInteger): ECPoint {
        var current = this
        var result = INFINITY
        var n = scalar
        while (n > 0.toBigInteger()) {
            if (n % 2.toBigInteger() == 1.toBigInteger()) {
                result += current
            }
            current += current
            n /= 2.toBigInteger()
        }
        return result
    }

    companion object {
        val INFINITY = ECPoint(null, null)
        val G = ECPoint(FieldElement(1.toBigInteger()), FieldElement(2.toBigInteger()))

        /**
         * Deserializes a point from a hex string "x,y".
         */
        fun fromHex(hex: String): ECPoint {
            if (hex == "INFINITY") return INFINITY
            val parts = hex.split(",")
            require(parts.size == 2) { "Invalid ECPoint hex format" }
            val x = FieldElement(parts[0].toBigInteger(16))
            val y = FieldElement(parts[1].toBigInteger(16))
            return ECPoint(x, y)
        }
    }

    /**
     * Serializes the point to a hex string "x,y".
     */
    fun toHex(): String {
        if (x == null || y == null) return "INFINITY"
        return "${x.value.toString(16)},${y.value.toString(16)}"
    }
}
