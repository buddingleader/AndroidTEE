package com.example.helloworld.crypto.ecc

import org.junit.Assert
import org.junit.Test
import java.math.BigInteger
import java.security.interfaces.ECPrivateKey

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

    @Test
    fun scalarMultiply_isCorrect() {
        val keypair = ECCP256.generateKeyPair()
        val priv = keypair.private as ECPrivateKey
        val X = BigInteger("42309187002739805951602027765812718539274653581056272841330484659748798544133")
        val Y = BigInteger("41553079035870764182946005789567871934527410236442122824193411214379303279397")
        val D = BigInteger("94648885406075364822723245673535794069561978283666952281426111539877895054624")
        val xy_ = Pair(
            BigInteger("73745403906017313451357070244555708942239857523751679370934122267178636502363"),
            BigInteger("21985117855595945486635913615907126717883150961688861170558468060311212097217")
        )
        var xy = ECC.scalarMultiply(priv.params.curve, X, Y, D.toByteArray().toUByteArray())
        Assert.assertEquals(xy, xy_)
    }

    @Test
    fun doubleJacobian_isCorrect() {
        var xyz = Triple(
            BigInteger("42309187002739805951602027765812718539274653581056272841330484659748798544133"),
            BigInteger("41553079035870764182946005789567871934527410236442122824193411214379303279397"),
            BigInteger("1")
        )
        var xyz1 = Triple(
            BigInteger("67237145650864883383834443854648918535803788730904365130097318868106248282602"),
            BigInteger("33806717067118220641472526727006792245061897594530437138195473721716084252416"),
            BigInteger("83106158071741528365892011579135743869054820472884245648386822428758606558794")
        )
        var xyz2 = Triple(
            BigInteger("107210772288118722837131757944467674132468025633442476218564165238114491069244"),
            BigInteger("110710273482805044395063505927643481575401990718115604767292433205718056206656"),
            BigInteger("76677775835394174287870699280517763859893486281402569994725133796604341297497")
        )
        ECC.run {
            xyz = ECCP256.getParams().curve.doubleJacobian(xyz)
            Assert.assertEquals(xyz, xyz1)
            xyz = ECCP256.getParams().curve.doubleJacobian(xyz)
            Assert.assertEquals(xyz, xyz2)
        }
    }

    @Test
    fun addJacobian_isCorrect() {
        var Bx = BigInteger("42309187002739805951602027765812718539274653581056272841330484659748798544133")
        var By = BigInteger("41553079035870764182946005789567871934527410236442122824193411214379303279397")
        var Bz = BigInteger("1")
        var xyz = Triple(
            BigInteger("67237145650864883383834443854648918535803788730904365130097318868106248282602"),
            BigInteger("33806717067118220641472526727006792245061897594530437138195473721716084252416"),
            BigInteger("83106158071741528365892011579135743869054820472884245648386822428758606558794")
        )
        var xyz1 = Triple(
            BigInteger("78580857281486673185828830562375442062683919526075925241364117152495548244974"),
            BigInteger("52583412628718323529322152523040835401465934113622135712053266422783118936595"),
            BigInteger("101434860140278803061831245226617768199227621366056818746409178829938109879613")
        )
        var xyz_ = Triple(
            BigInteger("22201560216310788397164930365812174070698783079186660465472866809400086240553"),
            BigInteger("112028464236272089453957691346267901043304911323309579811449720643895396017977"),
            BigInteger("58312486731944207773961487810982340771976329874972209498513682226515571477273")
        )
        var xyz2 = Triple(
            BigInteger("45767758313654537876318632877576863712861669065876772001632162202471564996208"),
            BigInteger("14716925453436961866670858125304203160528073308376578583219416934649892238714"),
            BigInteger("34966806931583555208374958505323158494635963897664028523012670717808244989459")
        )
        ECC.run {
            xyz = ECCP256.getParams().curve.addJacobian(Bx, By, Bz, xyz)
            Assert.assertEquals(xyz, xyz1)
            xyz = ECCP256.getParams().curve.addJacobian(Bx, By, Bz, xyz_)
//            println("Bx,By,Bz,x,y,z:$Bx,$By,$Bz,\n${xyz.first},\n${xyz.second},\n${xyz.third}")
            Assert.assertEquals(xyz, xyz2)
        }
    }


    @Test
    fun affineFromJacobian_isCorrect() {
        var xyz = Triple(
            BigInteger("29797835552026002429213061007614653845745773229211710499164102198082646116974"),
            BigInteger("72116014563909304621240964246252752844441425574988747403106895004789207877890"),
            BigInteger("18715514640047039311216169534565671540118023386212887254603851135177812791335")
        )
        var xy_ = Pair(
            BigInteger("80049576834585146863775051164376095977542896576572093817510081061736630764021"),
            BigInteger("12702613043849973666213919544221524936817376008878668629550182564297072929100")
        )
        var xyz1 = Triple(
            BigInteger("22770445338983177973961474234973846061232847563842945539662893043115032718328"),
            BigInteger("100557892672264289060494481733592914329132702404307899242973293427779187313857"),
            BigInteger("105156117142577833148713687281253821183013115075833328129591409524706960319737")
        )
        var xy1_ = Pair(
            BigInteger("73745403906017313451357070244555708942239857523751679370934122267178636502363"),
            BigInteger("21985117855595945486635913615907126717883150961688861170558468060311212097217")
        )
        ECC.run {
            var xy = ECCP256.getParams().curve.affineFromJacobian(xyz)
            Assert.assertEquals(xy, xy_)
            var xy1 = ECCP256.getParams().curve.affineFromJacobian(xyz1)
            Assert.assertEquals(xy1, xy1_)
        }
    }
}