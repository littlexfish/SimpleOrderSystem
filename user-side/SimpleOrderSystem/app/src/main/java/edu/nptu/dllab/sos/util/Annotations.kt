package edu.nptu.dllab.sos.util

/**
 * The annotation record class and function available version
 *
 * @author Little Fish
 * @since 22/10/03
 */
@SOSVersion(since = "0.0")
annotation class SOSVersion(val since: String = "0.0", val until: String = "Inf")
