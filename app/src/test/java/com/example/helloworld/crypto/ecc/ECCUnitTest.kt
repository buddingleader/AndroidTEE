package com.example.helloworld.crypto.ecc

import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class ECCUnitTest {
    @Test
    fun isOnCurve_isCorrect() {
        val ok = ECC.isOnCurve(
            ECCP256.getParams().curve,
            BigInteger("42309187002739805951602027765812718539274653581056272841330484659748798544133"),
            BigInteger("41553079035870764182946005789567871934527410236442122824193411214379303279397")
        )
        Assert.assertTrue(ok)
    }
}