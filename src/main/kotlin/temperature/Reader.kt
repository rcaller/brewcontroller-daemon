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
    var correctionMap = hashMapOf("not" to "loaded")
    init {
        properties= loadProperties()
        sensorMap=hashMapOf("herms" to properties.getProperty("sensor.herms"),
                            "mash" to properties.getProperty("sensor.mash"),
                            "flow" to properties.getProperty("sensor.flow"),
                            "hlt" to properties.getProperty("sensor.hlt"))
        correctionMap=hashMapOf("herms" to properties.getProperty("correction.herms"),
                                "mash" to properties.getProperty("correction.mash"),
                                "flow" to properties.getProperty("correction.flow"),
                                "hlt" to properties.getProperty("correction.hlt"))
    }
    override fun getTemperatures(): CurrentTemps {
        val w1Master = W1Master()

        val currentTemps = CurrentTemps(66.0, 65.5, 67.2, 66.3)
        for(device : TemperatureSensor in (w1Master.getDevices(TemperatureSensor::class.java))) {

            val deviceNameOriginal : String = device.getName()
            val deviceName = deviceNameOriginal.replace("\n", "")
            val hermsSensor = sensorMap.get("herms")
            val mashSensor =sensorMap.get("mash")
            val flowSensor = sensorMap.get("flow")
            val hltSensor = sensorMap.get("hlt")

            when (deviceName) {
                hermsSensor -> currentTemps.herms = device.getTemperature() + correctionMap.get("herms").toString().toFloat()
                mashSensor -> currentTemps.mash = device.getTemperature() + correctionMap.get("mash").toString().toFloat()
                flowSensor -> currentTemps.flow = device.getTemperature() + correctionMap.get("flow").toString().toFloat()
                hltSensor -> currentTemps.hlt = device.getTemperature() + correctionMap.get("hlt").toString().toFloat()
                else -> log.warning("Unknows Sensor:"+deviceName+":")
            }
        }




        return currentTemps;
    }
}

data class CurrentTemps (var mash: Double, var herms: Double, var flow: Double, var hlt: Double)
