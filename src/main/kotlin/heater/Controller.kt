package uk.co.tertiarybrewery.heater

import com.pi4j.io.gpio.GpioFactory
import com.pi4j.io.gpio.PinState
import com.pi4j.io.gpio.RaspiPin
import java.util.logging.Logger

interface ControllerInterface {
    fun heat(onRatio: Double) : Unit

}

class Controller : ControllerInterface {

    val gpio = GpioFactory.getInstance()
    val heatPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28, "Heater", PinState.LOW)
    init {
        heatPin.setShutdownOptions(true, PinState.LOW)
    }
    companion object {
        private val log = Logger.getLogger("CONTROLLER")
    }
    override fun heat(onRatio: Double) {
        log.info("On for "+onRatio)
        roundedRatio = Math.round(onRatio * 100.0) / 100.0
        when (roundedRatio) {
            1.0 -> heatPin.high()
            0.0 -> heatPin.low()
            else -> heatPin.pulse((roundedRatio*10000).toLong())
        }

    }
}