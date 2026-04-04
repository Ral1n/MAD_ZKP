package org.example.project

object DocumentProcessor {
    fun processIdCard(rawText: String): IdResult {
        // 1. Căutăm CNP-ul (pentru România: 13 cifre)
        val cnpRegex = Regex("""\b[1-9]\d{12}\b""")
        val cnp = cnpRegex.find(rawText)?.value

        // 2. Verificăm vârsta din CNP
        val isOver18 = cnp?.let { checkAgeFromCnp(it) } ?: false

        // 3. Căutăm autoritatea (Ex: SPCLEP, POLITIA, etc.)
        val authorityRegex = Regex("""EMIS DE\s+([A-Z\s.-]+)""", RegexOption.IGNORE_CASE)
        val authority = authorityRegex.find(rawText)?.groupValues?.get(1)?.trim() ?: "Necunoscut"

        return IdResult(
            isAdult = isOver18,
            issuingAuthority = authority,
            isValidAuthority = authority.contains("SPCLEP") || authority.contains("POLITIA")
        )
    }

    private fun checkAgeFromCnp(cnp: String): Boolean {
        // Exemplu simplificat: Primele cifre după S (gen/secol) sunt YYMMDD
        // S YY MM DD ...
        val s = cnp[0].digitToInt()
        val yy = cnp.substring(1, 3).toInt()
        val yearPrefix = when(s) {
            1, 2 -> 1900
            5, 6 -> 2000
            else -> 1900
        }
        val birthYear = yearPrefix + yy
        val currentYear = 2026 // Suntem în 2026 conform instrucțiunilor tale
        return (currentYear - birthYear) >= 18
    }
}

data class IdResult(val isAdult: Boolean, val issuingAuthority: String, val isValidAuthority: Boolean)