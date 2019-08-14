package com.example.helloworld.crypto.ecc

import java.math.BigInteger
import java.security.spec.ECPoint
import java.security.spec.EllipticCurve

object ECC {
    val ERROR_EC_POINT = ECPoint(BigInteger("0"), BigInteger("0"))
    val U_BYTE_ARRAY_SIZE = 33

    fun marshal(curve: EllipticCurve, g: ECPoint): UByteArray {
        val byteLen = (curve.field.fieldSize + 7) shr 3
        val ret = UByteArray(1 + 2 * byteLen)
        // uncompressed point
        ret[0] = 4.toUByte()

        // copy xBytes into ret
        var xBytes = g.affineX.toByteArray().toUByteArray()
        if (xBytes.size == U_BYTE_ARRAY_SIZE) {
            xBytes = xBytes.copyOfRange(1, U_BYTE_ARRAY_SIZE)
        }
        xBytes.copyInto(ret, 1 + byteLen - xBytes.size)

        // copy yBytes into ret
        var yBytes = g.affineY.toByteArray().toUByteArray()
        if (yBytes.size == U_BYTE_ARRAY_SIZE) {
            yBytes = yBytes.copyOfRange(1, U_BYTE_ARRAY_SIZE)
        }
        yBytes.copyInto(ret, 1 + 2 * byteLen - yBytes.size)
        return ret
    }

    // unmarshal converts a point, serialized by Marshal, into an x, y pair.
    // It is an error if the point is not in uncompressed form or is not on the curve.
    // On error, ECPoint = ERROR_EC_POINT.
    fun unmarshal(curve: EllipticCurve, data: UByteArray): ECPoint {
        val byteLen = (curve.field.fieldSize + 7) shr 3
        if (data.size != 1 + 2 * byteLen) {
            return ERROR_EC_POINT
        }

        // uncompressed form
        if (data[0].toInt() != 4) {
            return ERROR_EC_POINT
        }

        // get x from data
        var xBytes = UByteArray(1 + byteLen)
        data.copyInto(xBytes, 1, 1, 1 + byteLen)
        val x = BigInteger(xBytes.toByteArray())

        // get y from data
        var yBytes = UByteArray(1 + byteLen)
        data.copyInto(yBytes, 1, 1 + byteLen, data.size)
        val y = BigInteger(yBytes.toByteArray())

        // check x and y
        val p = getGoLangP(curve)
        if (x >= p || y >= p) {
            return ERROR_EC_POINT
        }
        if (!isOnCurve(curve, x, y)) {
            return ERROR_EC_POINT
        }

        return ECPoint(x, y)
    }

    private fun getGoLangP(curve: EllipticCurve): BigInteger {
        return curve.a.add(BigInteger("3"))
    }

    fun isOnCurve(curve: EllipticCurve, x: BigInteger, y: BigInteger): Boolean {
        // y² = x³ - 3x + b
        var y2 = y.multiply(y)
        val curveP = getGoLangP(curve)
        y2 = y2.mod(curveP)

        var x3 = x.multiply(x)
        x3 = x3.multiply(x)

        var threeX = x.shl(1)
        threeX = threeX.add(x)

        x3 = x3.subtract(threeX)
        x3 = x3.add(curve.b)
        x3 = x3.mod(curveP)

        return x3.compareTo(y2) == 0
    }

    fun scalarMultiply(
        curve: EllipticCurve,
        Bx: BigInteger,
        By: BigInteger,
        s: UByteArray
    ): Pair<BigInteger, BigInteger> {
        var k = s
        if (k.size == U_BYTE_ARRAY_SIZE) {
            k = k.copyOfRange(1, U_BYTE_ARRAY_SIZE)
        }

        val Bz = BigInteger.ONE
        var xyz = Triple(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO)
        for (byte in k) {
            var b = byte
            for (bitNum in 0..7) {
                xyz = curve.doubleJacobian(xyz)
                if (b and 0x80.toUByte() == 0x80.toUByte()) {
                    xyz = curve.addJacobian(Bx, By, Bz, xyz)
                }

                b = (b.toInt().shl(1)).toUByte()
            }
        }

        return curve.affineFromJacobian(xyz)
    }

    // doubleJacobian takes a point in Jacobian coordinates, (x, y, z), and
    // returns its double, also in Jacobian form.
    fun EllipticCurve.doubleJacobian(xyz: Triple<BigInteger, BigInteger, BigInteger>): Triple<BigInteger, BigInteger, BigInteger> {
        val (x, y, z) = xyz
        val p = getGoLangP(this)
        // See https://hyperelliptic.org/EFD/g1p/auto-shortw-jacobian-3.html#doubling-dbl-2001-b
        var delta = z.multiply(z)
        delta = delta.mod(p)
        var gamma = y.multiply(y)
        gamma = gamma.mod(p)
        var alpha = x.subtract(delta)
        if (alpha.signum() == -1) {
            alpha = alpha.add(p)
        }
        var alpha2 = x.add(delta)
        alpha = alpha.multiply(alpha2)
        alpha2 = alpha.add(BigInteger.ZERO)
        alpha = alpha.shiftLeft(1)
        alpha = alpha.add(alpha2)

        var beta = x.multiply(gamma)

        var x3 = alpha.multiply(alpha)
        var beta8 = beta.shiftLeft(3)
        beta8 = beta8.mod(p)
        x3 = x3.subtract(beta8)
        if (x3.signum() == -1) {
            x3 = x3.add(p)
        }
        x3 = x3.mod(p)

        var z3 = y.add(z)
        z3 = z3.multiply(z3)
        z3 = z3.subtract(gamma)
        if (z3.signum() == -1) {
            z3 = z3.add(p)
        }
        z3 = z3.subtract(delta)
        if (z3.signum() == -1) {
            z3 = z3.add(p)
        }
        z3 = z3.mod(p)

        beta = beta.shiftLeft(2)
        beta = beta.subtract(x3)
        if (beta.signum() == -1) {
            beta = beta.add(p)
        }
        var y3 = alpha.multiply(beta)

        gamma = gamma.multiply(gamma)
        gamma = gamma.shiftLeft(3)
        gamma = gamma.mod(p)

        y3 = y3.subtract(gamma)
        if (y3.signum() == -1) {
            y3 = y3.add(p)
        }
        y3 = y3.mod(p)

        return Triple(x3, y3, z3)
    }

    // addJacobian takes two points in Jacobian coordinates, (x1, y1, z1) and
// (x2, y2, z2) and returns their sum, also in Jacobian form.
    fun EllipticCurve.addJacobian(
        x1: BigInteger,
        y1: BigInteger,
        z1: BigInteger,
        xyz: Triple<BigInteger, BigInteger, BigInteger>
    ): Triple<BigInteger, BigInteger, BigInteger> {
        val (x2, y2, z2) = xyz
        val p = getGoLangP(this)
        // See https://hyperelliptic.org/EFD/g1p/auto-shortw-jacobian-3.html#addition-add-2007-bl
        var (x3, y3, z3) = Triple(BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO)
        if (z1.signum() == 0) {
            x3 = x2.add(BigInteger.ZERO)
            y3 = y2.add(BigInteger.ZERO)
            z3 = z3.add(BigInteger.ZERO)
            return Triple(x3, y3, z3)
        }
        if (z2.signum() == 0) {
            x3 = x1.add(BigInteger.ZERO)
            y3 = y1.add(BigInteger.ZERO)
            z3 = z1.add(BigInteger.ZERO)
            return Triple(x3, y3, z3)
        }

        var z1z1 = z1.multiply(z1)
        z1z1 = z1z1.mod(p)
        var z2z2 = z2.multiply(z2)
        z2z2 = z2z2.mod(p)

        var u1 = x1.multiply(z2z2)
        u1 = u1.mod(p)
        var u2 = x2.multiply(z1z1)
        u2 = u2.mod(p)
        var h = u2.subtract(u1)
        var xEqual = h.signum() == 0
        if (h.signum() == -1) {
            h = h.add(p)
        }
        var i = h.shiftLeft(1)
        i = i.multiply(i)
        var j = h.multiply(i)

        var s1 = y1.multiply(z2)
        s1 = s1.multiply(z2z2)
        s1 = s1.mod(p)
        var s2 = y2.multiply(z1)
        s2 = s2.multiply(z1z1)
        s2 = s2.mod(p)
        var r = s2.subtract(s1)
        if (r.signum() == -1) {
            r = r.add(p)
        }
        var yEqual = r.signum() == 0
        if (xEqual && yEqual) {
            return this.doubleJacobian(Triple(x1, y1, z1))
        }
        r = r.shiftLeft(1)
        var v = u1.multiply(i)

        x3 = r.add(BigInteger.ZERO)
        x3 = x3.multiply(x3)
        x3 = x3.subtract(j)
        x3 = x3.subtract(v)
        x3 = x3.subtract(v)
        x3 = x3.mod(p)

        y3 = r.add(BigInteger.ZERO)
        v = v.subtract(x3)
        y3 = y3.multiply(v)
        s1 = s1.multiply(j)
        s1 = s1.shiftLeft(1)
        y3 = y3.subtract(s1)
        y3 = y3.mod(p)

        z3 = z1.add(z2)
        z3 = z3.multiply(z3)
        z3 = z3.subtract(z1z1)
        z3 = z3.subtract(z2z2)
        z3 = z3.multiply(h)
        z3 = z3.mod(p)

        return Triple(x3, y3, z3)
    }

    // affineFromJacobian reverses the Jacobian transform. See the comment at the
    // top of the file. If the point is ∞ it returns 0, 0.
    fun EllipticCurve.affineFromJacobian(
        xyz: Triple<BigInteger, BigInteger, BigInteger>
    ): Pair<BigInteger, BigInteger> {
        val (x, y, z) = xyz
        val p = getGoLangP(this)

        if (z.signum() == 0) {
            return Pair(BigInteger.ZERO, BigInteger.ZERO)
        }

        var zinv = z.modInverse(p)
        var zinvsq = zinv.multiply(zinv)

        var xOut = x.multiply(zinvsq)
        xOut = xOut.mod(p)
        zinvsq = zinvsq.multiply(zinv)
        var yOut = y.multiply(zinvsq)
        yOut = yOut.mod(p)
        return Pair(xOut, yOut)
    }
}