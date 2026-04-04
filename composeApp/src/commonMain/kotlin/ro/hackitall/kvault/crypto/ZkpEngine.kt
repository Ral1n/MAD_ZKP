package ro.hackitall.kvault.crypto

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.toBigInteger
import kotlin.random.Random

class ZkpEngine {

    data class ZkpProof(val r: ECPoint, val z: BigInteger)

    fun prove(s: BigInteger, p: ECPoint): ZkpProof {
        // 1. Generate a random nonce k
        val random = Random(123) // Using a seed for deterministic behavior in this example
        val randomBytes = random.nextBytes(32) // 256 bits
        val k = BigInteger.fromByteArray(randomBytes, sign = Sign.POSITIVE)

        // 2. Compute commitment r = k * G
        val r = ECPoint.G * k

        // 3. Generate challenge c = hash(r, p, G)
        val c = hash(r, p, ECPoint.G)

        // 4. Compute response z = k + c * s
        val z = k + c * s

        return ZkpProof(r, z)
    }

    fun verify(p: ECPoint, proof: ZkpProof): Boolean {
        val (r, z) = proof

        // 1. Generate challenge c = hash(r, p, G)
        val c = hash(r, p, ECPoint.G)

        // 2. Verify if z * G == r + c * p
        val leftSide = ECPoint.G * z
        val rightSide = r + (p * c)

        return leftSide == rightSide
    }

    // A simple hash function for demonstration purposes.
    // In a real application, use a secure cryptographic hash function like SHA-256.
    private fun hash(vararg points: Any): BigInteger {
        val concatenated = points.joinToString("") { point ->
            when (point) {
                is ECPoint -> (point.x?.value?.toString(16) ?: "") + (point.y?.value?.toString(16) ?: "")
                else -> point.toString()
            }
        }
        // Simple hash: take the first 32 hex chars (128 bits) and convert to BigInteger
        val hexHash = concatenated.encodeToByteArray().take(32).joinToString("") {
            it.toUByte().toString(16).padStart(2, '0')
        }
        return hexHash.toBigInteger(16)
    }
}
