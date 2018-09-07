package uk.co.tertiarybrewery.heater

import com.pi4j.io.gpio.*
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent
import com.pi4j.io.gpio.event.GpioPinListenerDigital
import java.util.logging.Logger

interface ControllerInterface {
    fun heat(onRatio: Double) : Unit

}

class Controller(val gpioPin: String) : ControllerInterface {

    companion object {
        private val log = Logger.getLogger("CONTROLLER")
    }

    val gpio : GpioController
    val heatPin : GpioPinDigitalOutput
    init {
        gpio = GpioFactory.getInstance()
        heatPin = gpio.provisionDigitalOutputPin(Pins.valueOf(gpioPin).pin, "Heater", PinState.LOW)
        heatPin.setShutdownOptions(true, PinState.LOW)
        heatPin.addListener(GpioPinListenerDigital() {
            fun handleGpioPinDigitalStateChangeEvent(event: GpioPinDigitalStateChangeEvent) {
                log.warning("Heat State Change: "+event.getState())
            }
        })

    }

    override fun heat(onRatio: Double) {
        log.info("On for "+onRatio)
        val roundedRatio = Math.round(onRatio * 100.0) / 100.0
        when (roundedRatio) {
            1.0 -> heatPin.high()
            0.0 -> heatPin.low()
            else -> heatPin.pulse((roundedRatio*10000).toLong())
        }

    }

    fun off() {
        heatPin.low()
    }
}

enum class Pins (val pin: Pin) {
    PIN_29(RaspiPin.GPIO_21),
    PIN_31(RaspiPin.GPIO_22),
    PIN_33(RaspiPin.GPIO_23),
    PIN_35(RaspiPin.GPIO_24),
    PIN_37(RaspiPin.GPIO_25),
    PIN_32(RaspiPin.GPIO_26),
    PIN_36(RaspiPin.GPIO_27),
    PIN_38(RaspiPin.GPIO_28),
    PIN_40(RaspiPin.GPIO_29)
}