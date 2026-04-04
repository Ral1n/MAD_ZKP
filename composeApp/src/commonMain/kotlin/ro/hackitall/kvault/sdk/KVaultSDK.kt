package ro.hackitall.kvault.sdk

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import ro.hackitall.kvault.sdk.crypto.ECPoint

/**
 * An internal data class representing the Zero-Knowledge Proof.
 * It is not exposed to the public API.
 */
internal data class ZkpProof(val r: ECPoint, val z: BigInteger) {
    companion object {
        /**
         * Deserializes a proof from a hex string "r_x,r_y,z".
         */
        fun fromHex(hex: String): ZkpProof {
            val parts = hex.split(",")
            require(parts.size == 3) { "Invalid ZKP proof hex format" }
            val r = ECPoint.fromHex("${parts[0]},${parts[1]}")
            val z = parts[2].toBigInteger(16)
            return ZkpProof(r, z)
        }
    }

    /**
     * Serializes the proof to a hex string "r_x,r_y,z".
     */
    fun toHex(): String {
        val rHex = r.toHex().replace(",", "")
        return "$rHex,${z.toString(16)}"
    }
}

/**
 * The public API for the K-Vault SDK.
 * This object provides a high-level interface for verifying Zero-Knowledge Proofs.
 */
object KVaultSDK {

    /**
     * Verifies an age proof using the Schnorr protocol verification equation.
     *
     * @param proofHex The ZKP proof, serialized as a hex string.
     * @param publicKeyHex The user's public key, serialized as a hex string.
     * @param challengeHex The challenge (e.g., a session ID), as a hex string.
     * @return `true` if the proof is valid, `false` otherwise.
     */
    fun verifyAgeProof(proofHex: String, publicKeyHex: String, challengeHex: String): Boolean {
        return try {
            // 1. Deserialize all components from hex strings
            val proof = ZkpProof.fromHex(proofHex)
            val p = ECPoint.fromHex(publicKeyHex)
            val c = challengeHex.toBigInteger(16)

            // 2. Perform the verification using the core cryptographic logic
            // Equation: z * G == R + c * P
            val leftSide = ECPoint.G * proof.z
            val rightSide = proof.r + (p * c)

            // 3. The proof is valid if both sides of the equation are equal
            leftSide == rightSide
        } catch (_: Exception) {
            // If any error occurs during deserialization or calculation, the proof is invalid.
            false
        }
    }
}
