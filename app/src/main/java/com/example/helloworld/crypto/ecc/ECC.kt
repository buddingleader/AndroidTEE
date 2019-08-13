package com.example.helloworld.crypto.ecc

import java.math.BigInteger
import java.security.spec.ECPoint
import java.security.spec.EllipticCurve
import kotlin.experimental.and

object ECC {
    private val ERROR_EC_POINT = ECPoint(BigInteger("0"), BigInteger("0"))

    fun marshal(curve: EllipticCurve, g: ECPoint): ByteArray {
        val byteLen = (curve.field.fieldSize + 7) shr 3
        val ret = ByteArray(1 + 2 * byteLen)
        ret[0] = 4

        val xBytes = g.affineX.toByteArray()
        xBytes.copyInto(ret, 1 + byteLen - xBytes.size)
        val yBytes = g.affineY.toByteArray()
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

        if (data[0].toInt() != 4) {
            return ERROR_EC_POINT
        }


        var xBytes = UByteArray(1 + byteLen)
        data.copyInto(xBytes, 1, 1, 1 + byteLen)
        val x = BigInteger(xBytes.toByteArray())
        var yBytes = UByteArray(1 + byteLen)
        data.copyInto(yBytes, 1, 1 + byteLen, data.size)
        val y = BigInteger(yBytes.toByteArray())

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

    //    func (curve *CurveParams) ScalarMult(Bx, By *big.Int, k []byte) (*big.Int, *big.Int) {
//	Bz := new(big.Int).SetInt64(1)
//	x, y, z := new(big.Int), new(big.Int), new(big.Int)
//
//	for _, byte := range k {
//		for bitNum := 0; bitNum < 8; bitNum++ {
//			x, y, z = curve.doubleJacobian(x, y, z)
//			if byte&0x80 == 0x80 {
//				x, y, z = curve.addJacobian(Bx, By, Bz, x, y, z)
//			}
//			byte <<= 1
//		}
//	}
//
//	return curve.affineFromJacobian(x, y, z)
//}
    fun scalarMultiply(
        curve: EllipticCurve,
        Bx: BigInteger,
        By: BigInteger,
        k: ByteArray
    ): Pair<BigInteger, BigInteger> {
        val Bz = BigInteger("1")
        var (x, y, z) = Triple(BigInteger("0"), BigInteger("0"), BigInteger("0"))
        for (byte in k) {
            var b = byte
            for (bitNum in 0..8) {
                var (x, y, z) = curve.doubleJacobian(x, y, z)
                if (b and "0x80".toByte() == "0x80".toByte()) {
                    var (x, y, z) = curve.addJacobian(Bx, By, Bz, x, y, z)
                }

                b = (b.toInt() and 0xFF shr 1).toByte()
            }
        }
        return curve.affineFromJacobian(x, y, z)
    }

    //// doubleJacobian takes a point in Jacobian coordinates, (x, y, z), and
//// returns its double, also in Jacobian form.
//func (curve *CurveParams) doubleJacobian(x, y, z *big.Int) (*big.Int, *big.Int, *big.Int) {
//	// See https://hyperelliptic.org/EFD/g1p/auto-shortw-jacobian-3.html#doubling-dbl-2001-b
//	delta := new(big.Int).Mul(z, z)
//	delta.Mod(delta, curve.P)
//	gamma := new(big.Int).Mul(y, y)
//	gamma.Mod(gamma, curve.P)
//	alpha := new(big.Int).Sub(x, delta)
//	if alpha.Sign() == -1 {
//		alpha.Add(alpha, curve.P)
//	}
//	alpha2 := new(big.Int).Add(x, delta)
//	alpha.Mul(alpha, alpha2)
//	alpha2.Set(alpha)
//	alpha.Lsh(alpha, 1)
//	alpha.Add(alpha, alpha2)
//
//	beta := alpha2.Mul(x, gamma)
//
//	x3 := new(big.Int).Mul(alpha, alpha)
//	beta8 := new(big.Int).Lsh(beta, 3)
//	beta8.Mod(beta8, curve.P)
//	x3.Sub(x3, beta8)
//	if x3.Sign() == -1 {
//		x3.Add(x3, curve.P)
//	}
//	x3.Mod(x3, curve.P)
//
//	z3 := new(big.Int).Add(y, z)
//	z3.Mul(z3, z3)
//	z3.Sub(z3, gamma)
//	if z3.Sign() == -1 {
//		z3.Add(z3, curve.P)
//	}
//	z3.Sub(z3, delta)
//	if z3.Sign() == -1 {
//		z3.Add(z3, curve.P)
//	}
//	z3.Mod(z3, curve.P)
//
//	beta.Lsh(beta, 2)
//	beta.Sub(beta, x3)
//	if beta.Sign() == -1 {
//		beta.Add(beta, curve.P)
//	}
//	y3 := alpha.Mul(alpha, beta)
//
//	gamma.Mul(gamma, gamma)
//	gamma.Lsh(gamma, 3)
//	gamma.Mod(gamma, curve.P)
//
//	y3.Sub(y3, gamma)
//	if y3.Sign() == -1 {
//		y3.Add(y3, curve.P)
//	}
//	y3.Mod(y3, curve.P)
//
//	return x3, y3, z3
//}
    fun EllipticCurve.doubleJacobian(
        x: BigInteger,
        y: BigInteger,
        z: BigInteger
    ): Triple<BigInteger, BigInteger, BigInteger> {
        var delta = z.multiply(z)
//            delta.mod()
        return Triple(x, y, z)
    }

    //        // addJacobian takes two points in Jacobian coordinates, (x1, y1, z1) and
//// (x2, y2, z2) and returns their sum, also in Jacobian form.
//func (curve *CurveParams) addJacobian(x1, y1, z1, x2, y2, z2 *big.Int) (*big.Int, *big.Int, *big.Int) {
//	// See https://hyperelliptic.org/EFD/g1p/auto-shortw-jacobian-3.html#addition-add-2007-bl
//	x3, y3, z3 := new(big.Int), new(big.Int), new(big.Int)
//	if z1.Sign() == 0 {
//		x3.Set(x2)
//		y3.Set(y2)
//		z3.Set(z2)
//		return x3, y3, z3
//	}
//	if z2.Sign() == 0 {
//		x3.Set(x1)
//		y3.Set(y1)
//		z3.Set(z1)
//		return x3, y3, z3
//	}
//
//	z1z1 := new(big.Int).Mul(z1, z1)
//	z1z1.Mod(z1z1, curve.P)
//	z2z2 := new(big.Int).Mul(z2, z2)
//	z2z2.Mod(z2z2, curve.P)
//
//	u1 := new(big.Int).Mul(x1, z2z2)
//	u1.Mod(u1, curve.P)
//	u2 := new(big.Int).Mul(x2, z1z1)
//	u2.Mod(u2, curve.P)
//	h := new(big.Int).Sub(u2, u1)
//	xEqual := h.Sign() == 0
//	if h.Sign() == -1 {
//		h.Add(h, curve.P)
//	}
//	i := new(big.Int).Lsh(h, 1)
//	i.Mul(i, i)
//	j := new(big.Int).Mul(h, i)
//
//	s1 := new(big.Int).Mul(y1, z2)
//	s1.Mul(s1, z2z2)
//	s1.Mod(s1, curve.P)
//	s2 := new(big.Int).Mul(y2, z1)
//	s2.Mul(s2, z1z1)
//	s2.Mod(s2, curve.P)
//	r := new(big.Int).Sub(s2, s1)
//	if r.Sign() == -1 {
//		r.Add(r, curve.P)
//	}
//	yEqual := r.Sign() == 0
//	if xEqual && yEqual {
//		return curve.doubleJacobian(x1, y1, z1)
//	}
//	r.Lsh(r, 1)
//	v := new(big.Int).Mul(u1, i)
//
//	x3.Set(r)
//	x3.Mul(x3, x3)
//	x3.Sub(x3, j)
//	x3.Sub(x3, v)
//	x3.Sub(x3, v)
//	x3.Mod(x3, curve.P)
//
//	y3.Set(r)
//	v.Sub(v, x3)
//	y3.Mul(y3, v)
//	s1.Mul(s1, j)
//	s1.Lsh(s1, 1)
//	y3.Sub(y3, s1)
//	y3.Mod(y3, curve.P)
//
//	z3.Add(z1, z2)
//	z3.Mul(z3, z3)
//	z3.Sub(z3, z1z1)
//	z3.Sub(z3, z2z2)
//	z3.Mul(z3, h)
//	z3.Mod(z3, curve.P)
//
//	return x3, y3, z3
//}
    fun EllipticCurve.addJacobian(
        Bx: BigInteger,
        By: BigInteger,
        Bz: BigInteger,
        x: BigInteger,
        y: BigInteger,
        z: BigInteger
    ): Triple<BigInteger, BigInteger, BigInteger> {
        var (x, y, z) = Triple(BigInteger("0"), BigInteger("0"), BigInteger("0"))

        return Triple(x, y, z)
    }

    //        // affineFromJacobian reverses the Jacobian transform. See the comment at the
//// top of the file. If the point is ∞ it returns 0, 0.
//func (curve *CurveParams) affineFromJacobian(x, y, z *big.Int) (xOut, yOut *big.Int) {
//	if z.Sign() == 0 {
//		return new(big.Int), new(big.Int)
//	}
//
//	zinv := new(big.Int).ModInverse(z, curve.P)
//	zinvsq := new(big.Int).Mul(zinv, zinv)
//
//	xOut = new(big.Int).Mul(x, zinvsq)
//	xOut.Mod(xOut, curve.P)
//	zinvsq.Mul(zinvsq, zinv)
//	yOut = new(big.Int).Mul(y, zinvsq)
//	yOut.Mod(yOut, curve.P)
//	return
//}
    fun EllipticCurve.affineFromJacobian(
        x: BigInteger,
        y: BigInteger,
        z: BigInteger
    ): Pair<BigInteger, BigInteger> {

        return Pair(x, y)
    }

}