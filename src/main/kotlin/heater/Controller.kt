package uk.co.tertiarybrewery.heater

import java.util.logging.Logger
import com.pi4j.io.gpio.GpioFactory
import com.pi4j.io.gpio.RaspiPin
import com.pi4j.io.gpio.PinState
import java.util.*

interface ControllerInterface {
    fun heat(onRatio: Double) : Unit

}

class Controller : ControllerInterface {

    val gpio = GpioFactory.getInstance()
    val heatPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28, "Heater", PinState.LOW);

    companion object {
        private val log = Logger.getLogger("CONTROLLER")
    }
    override fun heat(onRatio: Double) {
        log.info("On for "+onRatio)
        heatPin.pulse((onRatio*10000).toLong())
    }
}