package uk.co.tertiarybrewery.brewcontroller.temperature

import com.pi4j.component.temperature.TemperatureSensor
import com.pi4j.io.w1.W1Master
import uk.co.tertiarybrewery.brewcontroller.loadProperties
import java.util.*
import java.util.logging.Logger

interface ReaderInterface  {
    fun getTemperatures (): CurrentTemps
}

class Reader :ReaderInterface {
    companion object {
        private val log = Logger.getLogger("CONTROLLER")
    }

    var properties = Properties()
    var sensorMap = hashMapOf("not" to "loaded")
    init {
        properties= loadProperties()
        sensorMap=hashMapOf("herms" to properties.getProperty("sensor.herms"), "mash" to properties.getProperty("sensor.mash"), "flow" to properties.getProperty("sensor.flow"))
    }
    override fun getTemperatures(): CurrentTemps {
        val w1Master = W1Master()

        val currentTemps = CurrentTemps(66.0, 65.5, 67.2)
        for(device : TemperatureSensor in (w1Master.getDevices(TemperatureSensor::class.java))) {

            val deviceNameOriginal : String = device.getName()
            val deviceName = deviceNameOriginal.replace("\n", "")
            val hermsSensor = sensorMap.get("herms")
            val mashSensor =sensorMap.get("mash")
            val flowSensor = sensorMap.get("flow")

            when (deviceName) {
                hermsSensor -> currentTemps.herms = device.getTemperature()
                mashSensor -> currentTemps.mash = device.getTemperature()
                flowSensor -> currentTemps.flow = device.getTemperature()
                else -> log.warning("Unknows Sensor:"+deviceName+":")
            }
        }




        return currentTemps;
    }
}

data class CurrentTemps (var mash: Double, var herms: Double, var flow: Double)
