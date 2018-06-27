package tuner

import org.junit.Test
import uk.co.tertiarybrewery.brewcontroller.tuner.KVTuner
import kotlin.math.sin
import kotlin.test.assertEquals


internal class TagClientTest {

    @Test
    fun testReadyWithOne() {
        var kvTuner=KVTuner()
        kvTuner.add(1.1)
        assertEquals(false, kvTuner.ready())
    }
    @Test
    fun testReadyWithNo256() {
        var kvTuner=KVTuner()
        repeat(256) {
            kvTuner.add(1.1)
        }
        assertEquals(true, kvTuner.ready())
    }

    @Test
    fun testAnalyseWithFlatData() {
        var kvTuner=KVTuner()
        repeat(256) {
            kvTuner.add(1.2)
        }
        val result = kvTuner.analyse()
        assertEquals(0.0, result)
    }
    @Test
    fun testAnalyseWithsineData() {
        var kvTuner=KVTuner()
        for (i in 0..255) {
            kvTuner.add(sin(i*6.0))
        }
        val result = kvTuner.analyse()
        assertEquals(0.004687500000000001, result)
    }
}