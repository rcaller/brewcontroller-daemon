package uk.co.tertiarybrewery.brewcontroller

import java.io.File
import java.util.*

internal object Resources

fun loadProperties() : Properties {
    val properties = Properties()
    var file = File("/etc/brewcontroller.conf")
    if (file.exists()) {
        val propStream = file.inputStream()
        properties.load(propStream)
    }
    else {
        val propStream = Resources.javaClass.getResourceAsStream("/application.properties")
        properties.load(propStream)
    }
    return properties
}