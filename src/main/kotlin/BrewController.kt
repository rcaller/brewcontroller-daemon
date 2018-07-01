package uk.co.tertiarybrewery.brewcontroller




import uk.co.tertiarybrewery.brewcontroller.pid.PID
import uk.co.tertiarybrewery.brewcontroller.reporting.Reporter
import uk.co.tertiarybrewery.brewcontroller.temperature.Reader
import uk.co.tertiarybrewery.brewcontroller.tuner.KVTuner

import uk.co.tertiarybrewery.heater.Controller
import java.util.*
import java.util.logging.FileHandler
import java.util.logging.Logger
import java.util.logging.SimpleFormatter
import kotlin.concurrent.fixedRateTimer


private val log = Logger.getLogger("CONTROLLER")


fun main(args: Array<String>) {

    val formatter= SimpleFormatter()

    val fileHandler  = FileHandler("/tmp/brewcontroller.log")
    fileHandler.setFormatter(formatter)
    var properties = loadProperties()
    log.addHandler(fileHandler);

    val tempReader = Reader()
    val reporter = Reporter()
    val pid = PID()
    val heatController = Controller(properties.getProperty("PIN.herms"))
    val kvTuner = KVTuner()

    if (properties.getProperty("tune") == "true") {

        var targetTemp:Double = 45.0
        var p = 0.1
        var brewTimer:Timer = Timer()
        kvTuner.clear()
        pid.setP(p)
        brewTimer = fixedRateTimer(name = "tuningScheduler", initialDelay = 0, period = 10000) {
            log.info("Tuning")
            val currentTemps = tempReader.getTemperatures()
            val targets = reporter.report(currentTemps)
            var currentTemp = currentTemps.flow
            kvTuner.add(currentTemp)
            val heatRatio = pid.calculate(targetTemp, currentTemp)
            heatController.heat(heatRatio)
            if (kvTuner.ready()) {
                brewTimer.purge()
                val freq = kvTuner.analyse()
                if (freq == 0.0) {

                }
                else {
                    log.info("p="+p)
                    log.info("f="+freq)
                    throw Exception("END")
                }
            }
        }
    }
    else {

        fixedRateTimer(name="scheduler", initialDelay=0, period=10000) {
            val currentTemps = tempReader.getTemperatures();
            val targets = reporter.report(currentTemps)
            var targetTemp = targets.flow;
            var currentTemp = currentTemps.flow;
            if (targets.preWarm > 0) {

                targetTemp = targets.preWarm
                currentTemp = currentTemps.herms
                log.info("Prewarming")
            }
            log.info("Target:" + targetTemp + "\nCurrent:" + currentTemp)
            val heatRatio = pid.calculate(targetTemp, currentTemp)
            heatController.heat(heatRatio)
        }

    }
}



