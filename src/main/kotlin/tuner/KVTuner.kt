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

        // Is this actually an oscillation, ratio of top two frequencies
        val topTwo = realTransformed.sortedByDescending { s -> s.abs() }.subList(0,2)
        val ratio = topTwo[0].abs()/topTwo[1].abs()
        if (ratio < 1.05 ) {
            return 0.0;
        }

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

    fun clear() {
        temps = mutableListOf()
    }
}
