package ro.hackitall.kvault.crypto

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger

val A = FieldElement(0.toBigInteger())
val B = FieldElement(3.toBigInteger())

data class ECPoint(val x: FieldElement?, val y: FieldElement?) {

    init {
        if (x != null && y != null) {
            require(y.pow(2.toBigInteger()) == x.pow(3.toBigInteger()) + A * x + B) {
                "Point is not on the curve"
            }
        }
    }

    operator fun plus(other: ECPoint): ECPoint {
        if (this.x == null) return other
        if (other.x == null) return this

        // Case 1: P1 == P2 (Point Doubling)
        if (this == other) {
            if (this.y!!.value == 0.toBigInteger()) {
                return INFINITY
            }
            // Slope m = (3 * x1^2 + A) / (2 * y1)
            val m = (FieldElement(3.toBigInteger()) * this.x.pow(2.toBigInteger()) + A) * (FieldElement(2.toBigInteger()) * this.y).inverse()
            // x3 = m^2 - 2 * x1
            val x3 = m.pow(2.toBigInteger()) - (FieldElement(2.toBigInteger()) * this.x)
            // y3 = m * (x1 - x3) - y1
            val y3 = m * (this.x - x3) - this.y
            return ECPoint(x3, y3)
        }

        // Case 2: P1 != P2
        // Slope m = (y2 - y1) / (x2 - x1)
        if (this.x == other.x) {
            return INFINITY // P1 + (-P1) = Infinity
        }
        val m = (other.y!! - this.y!!) * (other.x!! - this.x).inverse()
        // x3 = m^2 - x1 - x2
        val x3 = m.pow(2.toBigInteger()) - this.x - other.x
        // y3 = m * (x1 - x3) - y1
        val y3 = m * (this.x - x3) - this.y
        return ECPoint(x3, y3)
    }

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
        val G = ECPoint(
            FieldElement(1.toBigInteger()),
            FieldElement(2.toBigInteger())
        )
    }
}
