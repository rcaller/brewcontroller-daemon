package uk.co.tertiarybrewery.brewcontroller.reporting

import khttp.post
import uk.co.tertiarybrewery.brewcontroller.temperature.CurrentTemps
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.logging.Logger

interface ReporterInterface {

    fun report(currentTemps: CurrentTemps): TargetResponse
}



class Reporter : ReporterInterface {

    companion object {

        private val log = Logger.getLogger("CONTROLLER")

    }



    var resp = TargetResponse(66.0,0.0, 75.0)
    override fun report(currentTemps: CurrentTemps): TargetResponse {

        val payload = mapOf("mash" to currentTemps.mash,
                                                "flow" to currentTemps.flow,
                                                "herms" to currentTemps.herms,
                                                "hlt" to currentTemps.hlt)
        try {
            val r = post(url="http://localhost:8080/in", json=payload)


            if (r.statusCode == 200) {
                resp = TargetResponse(r.jsonObject.getDouble("pre_warm"),
                        r.jsonObject.getDouble("active"),
                        r.jsonObject.getDouble("hlt"))
            } else {
                log.warning("Report response code " + r.statusCode)
            }
        }
        catch (e: ConnectException) {
            log.warning("No connection to reporting")
        }
        catch (e: SocketTimeoutException) {
            log.warning("Report Timed Out")
        }

        return resp
    }
}

data class TargetResponse (var preWarm: Double, var flow : Double, var hlt: Double)


