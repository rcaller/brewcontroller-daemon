package uk.co.tertiarybrewery.brewcontroller.pid

import java.util.*
import java.util.logging.Logger

class PID {
    companion object {
        private val log = Logger.getLogger("CONTROLLER")
    }
    val properties = Properties()
    var pid = MiniPID(0.0, 0.0, 0.0)
    init {

        val propStream = PID::class.java.getResourceAsStream("/application.properties")
        properties.load(propStream)
        var proportional = properties.getProperty("PID.proportional").toDouble()
        var integral = properties.getProperty("PID.integral").toDouble()
        var differential =properties.getProperty("PID.differential").toDouble()
        pid = MiniPID(proportional, integral, differential)
        pid.setOutputLimits(0.0, 1.0)
        pid.setMaxIOutput(0.1)
    }
    fun  calculate(targetTemp: Double, currentTemp: Double): Double {
        var result = pid.getOutput(currentTemp, targetTemp)
        
        return result

    }

}
