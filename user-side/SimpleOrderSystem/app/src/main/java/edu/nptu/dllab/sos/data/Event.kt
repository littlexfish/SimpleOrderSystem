package edu.nptu.dllab.sos.data

import edu.nptu.dllab.sos.util.SOSVersion

/**
 * The base class of event
 */
@SOSVersion(since = "0.0")
abstract class Event(val event: String)