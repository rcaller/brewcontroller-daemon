package uk.co.tertiarybrewery.brewcontroller.pid

import uk.co.tertiarybrewery.brewcontroller.loadProperties
import java.util.*
import java.util.logging.Logger

class PID (name: String) {
    companion object {
        private val log = Logger.getLogger("CONTROLLER")
    }
    var properties = Properties()
    var pid = MiniPID(0.0, 0.0, 0.0)
    init {

        properties= loadProperties()
        if (properties.getProperty("tune") != "true") {
            var proportional = properties.getProperty("$name.PID.proportional").toDouble()
            var integral = properties.getProperty("$name.PID.integral").toDouble()
            var differential = properties.getProperty("$name.PID.differential").toDouble()
            pid = MiniPID(proportional, integral, differential)
        }
        pid.setOutputLimits(0.0, 1.0)
        pid.setMaxIOutput(0.1)
    }
    fun  calculate(targetTemp: Double, currentTemp: Double): Double {
        var result = pid.getOutput(currentTemp, targetTemp)
        
        return result

    }

    fun setP(proportional: Double) {
        pid.setP(proportional)
    }

}
