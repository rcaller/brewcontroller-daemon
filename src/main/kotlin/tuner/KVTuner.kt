package uk.co.tertiarybrewery.brewcontroller.tuner

import org.apache.commons.math3.complex.Complex
import org.apache.commons.math3.transform.DftNormalization
import org.apache.commons.math3.transform.FastFourierTransformer
import org.apache.commons.math3.transform.TransformType
import java.util.logging.Logger

class KVTuner {
    companion object {
        private val log = Logger.getLogger("CONTROLLER")
    }

    var temps:MutableList<Double> = mutableListOf()

    val fft:FastFourierTransformer = FastFourierTransformer(DftNormalization.STANDARD)
    fun analyse():Double {
        val transformed: Array<Complex> = fft.transform(this.temps.toDoubleArray(), TransformType.FORWARD)
        val realTransformed = transformed.dropLast((this.temps.size/2))

        val maxFreq = realTransformed.maxBy { s -> s.abs() }
        val bucket =  transformed.indexOf(maxFreq)
        val frequency = 0.1 * bucket / this.temps.size
        return frequency

    }
    fun ready():Boolean {

        return temps.size.rem(256) ==0
    }
    fun add(temp: Double) {
      temps.add(temp)
    }
}
