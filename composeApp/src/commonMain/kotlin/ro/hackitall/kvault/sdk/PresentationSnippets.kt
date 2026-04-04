package ro.hackitall.kvault.sdk

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import ro.hackitall.kvault.sdk.crypto.ECPoint
import kotlin.random.Random

/**
 * This file contains code examples for presentation slides.
 * It demonstrates how both a verifier and a prover would operate.
 */
@Suppress("unused")
object PresentationSnippets {

    /**
     * Example A (The Verifier / e.g., Glovo's Backend)
     *
     * How a merchant calls the SDK to check if a user is over 18.
     */
    fun verifierExample() {
        println("--- Verifier Side (e.g., Glovo) ---")

        // These would be received from the user's wallet during a transaction.
        val userPublicKeyHex = "c1a7b72a...,e3f4c1a..." // User's public identity
        val proofFromUserHex = "d1e2f3a4...,b5c6d7e8...,a1b2c3d4..." // The proof of age
        val challengeHex = "Session12345".encodeToByteArray().toHexString() // A unique challenge for this transaction

        // Call the SDK to verify the proof
        val isAgeVerified = KVaultSDK.verifyAgeProof(
            proofHex = proofFromUserHex,
            publicKeyHex = userPublicKeyHex,
            challengeHex = challengeHex
        )

        if (isAgeVerified) {
            println("SUCCESS: User's age is verified. Allow purchase.")
        } else {
            println("FAILURE: Proof is invalid. Deny purchase.")
        }
    }

    /**
     * Example B (The Prover / The K-Vault Wallet)
     *
     * How the wallet generates the proof to send back to the merchant.
     * NOTE: This logic resides in the wallet, not the SDK. The SDK only verifies.
     */
    fun proverExample() {
        println("\n--- Prover Side (K-Vault Wallet) ---")

        // --- Wallet's Internal State (securely stored) ---
        val userSecretKey: BigInteger = "a_very_secret_number_stored_securely".toBigInteger(36)
        val userPublicKey: ECPoint = ECPoint.G * userSecretKey

        // --- Data from the Merchant ---
        val challengeHex = "Session12345".encodeToByteArray().toHexString()
        val challenge = challengeHex.toBigInteger(16)

        // --- Proof Generation (Schnorr Protocol) ---
        // 1. Generate a random secret nonce 'k'
        val k = BigInteger.fromByteArray(Random.nextBytes(32), com.ionspin.kotlin.bignum.integer.Sign.POSITIVE)

        // 2. Compute the commitment point 'r'
        val r = ECPoint.G * k

        // 3. Compute the response 'z'
        // z = k + c * s
        val z = k + challenge * userSecretKey

        // 4. Create the proof object
        val proof = ZkpProof(r = r, z = z)

        // 5. Serialize and send back to the merchant
        val proofToSendHex = proof.toHex()
        val publicKeyToSendHex = userPublicKey.toHex()

        println("Sending to merchant:")
        println("  Public Key: $publicKeyToSendHex")
        println("  Proof: $proofToSendHex")
        println("  Challenge: $challengeHex")

        // --- The verifier would then use these values ---
        val isProofValidOnWalletSide = KVaultSDK.verifyAgeProof(proofToSendHex, publicKeyToSendHex, challengeHex)
        println("Self-verification check: $isProofValidOnWalletSide")
    }
}

// Helper to convert ByteArray to Hex String for challenges
private fun ByteArray.toHexString() = joinToString("") { it.toUByte().toString(16).padStart(2, '0') }
