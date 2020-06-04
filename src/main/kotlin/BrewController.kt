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
import kotlin.math.round


private val log = Logger.getLogger("CONTROLLER")


fun main(args: Array<String>) {

    val formatter= SimpleFormatter()

    val fileHandler  = FileHandler("/tmp/brewcontroller.log")
    fileHandler.setFormatter(formatter)
    var properties = loadProperties()
    log.addHandler(fileHandler);

    val tempReader = Reader()
    val reporter = Reporter()
    val pid = PID("HERMS")
    var hltPid = PID("HLT")
    val heatController = Controller(properties.getProperty("PIN.herms"))
    val hltController = Controller(properties.getProperty("PIN.hlt"));


    if (properties.getProperty("tune") == "true") {

        tune( pid, tempReader, reporter, heatController, 0.1)
    }
    else {

        fixedRateTimer(name="scheduler", initialDelay=0, period=10000) {
            val currentTemps = tempReader.getTemperatures();
            val targets = reporter.report(currentTemps)
            var targetTemp = targets.flow;
            var currentTemp = currentTemps.flow;
            var hltTarget = targets.hlt;
            var hltCurrent = currentTemps.hlt;
            if (targets.preWarm > 0) {

                targetTemp = targets.preWarm
                currentTemp = currentTemps.herms
                log.info("Prewarming")
            }
            log.info("HERMS - Target:" + targetTemp + "\nCurrent:" + currentTemp)
            val heatRatio = pid.calculate(targetTemp, currentTemp)
            heatController.heat(heatRatio)
            log.info("HLT - Target:" + hltTarget + "\nCurrent:" + hltCurrent)
            val hltRatio = hltPid.calculate(hltTarget, hltCurrent)
            hltController.heat(hltRatio)
        }

    }
}

private fun tune( pid: PID, tempReader: Reader, reporter: Reporter, heatController: Controller, p: Double) {
    log.warning("Stabilising")

    heatController.off()
    Thread.sleep(300000)
    val kvTuner = KVTuner()
    val temps = tempReader.getTemperatures()
    var targetTemp: Double = round(temps.flow + 5)
    log.info("Target Temp = "+targetTemp)
    var brewTimer: Timer = Timer()
    kvTuner.clear()
    pid.setP(p)
    brewTimer = fixedRateTimer(name = "tuningScheduler", initialDelay = 0, period = 10000) {
        log.info("Tuning p="+p.toString())
        val currentTemps = tempReader.getTemperatures()
        val targets = reporter.report(currentTemps)
        val currentTemp = currentTemps.flow
        kvTuner.add(currentTemp)
        val heatRatio = pid.calculate(targetTemp, currentTemp)
        heatController.heat(heatRatio)
        if (kvTuner.ready()) {
            brewTimer.purge()
            brewTimer.cancel()
            val freq = kvTuner.analyse()
            if (freq == 0.0) {
                kvTuner.clear()
                tune( pid, tempReader, reporter, heatController, p + 0.1)
            } else {
                log.info("p=" + p)
                log.info("f=" + freq)
                throw Exception("END")
            }
        }
    }
}



