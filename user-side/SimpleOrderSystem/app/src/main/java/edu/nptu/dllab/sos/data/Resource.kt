package edu.nptu.dllab.sos.data

import edu.nptu.dllab.sos.util.SOSVersion

/**
 * The resource contains info from server
 *
 * @author Little Fish
 * @since 22/10/03
 */
@SOSVersion(since = "0.0")
data class Resource(val path: String, val id: Int, val position: String, val sha256: String)
